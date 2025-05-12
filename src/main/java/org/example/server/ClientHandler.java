package org.example.server;

import org.example.config.Config;
import org.example.model.ExamResult;
import org.example.model.Question;
import org.example.model.StudentResponse;
import org.example.repository.interfaces.ExamResultRepository;
import org.example.repository.RepositoryFactory;
import org.example.repository.interfaces.StudentResponseRepository;
import org.example.util.ExamCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final List<Question> questions;
    private final ExamResultRepository examResultRepository;
    private final StudentResponseRepository studentResponseRepository;
    private PrintWriter out;
    private BufferedReader in;
    private final int answerTimeoutSeconds;
    private final ExecutorService executor;

    public ClientHandler(Socket clientSocket, List<Question> questions) {
        this.clientSocket = clientSocket;
        this.questions = questions;
        this.examResultRepository = RepositoryFactory.getExamResultRepository();
        this.studentResponseRepository = RepositoryFactory.getStudentResponseRepository();
        this.answerTimeoutSeconds = Config.getAnswerTimeoutSeconds();
        this.executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        String studentId = null;
        try {
            initializeConnection();
            studentId = getStudentId();
            sendWelcomeMessage(studentId);
            
            int correctAnswers = processQuestions(studentId);
            sendResults(studentId, correctAnswers);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client handler interrupted: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void initializeConnection() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private String getStudentId() throws IOException {
        out.println("Podaj swój numer indeksu:");
        String studentId = in.readLine();
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid student ID");
        }
        return studentId;
    }

    private void sendWelcomeMessage(String studentId) throws IOException {
        out.println("Witaj, " + studentId + "! Rozpoczynamy test.");
        out.println("Na każde pytanie masz " + answerTimeoutSeconds + " sekund.");
        out.println("Aby odpowiedzieć na pytanie, wpisz numer(y) odpowiedzi oddzielone przecinkami, np. 1,3");
        out.println("Aby pominąć pytanie, wpisz '" + ExamCommands.SKIP_COMMAND + "'");
        out.println("Aby zakończyć test wcześniej, wpisz '" + ExamCommands.END_COMMAND + "'");
        out.println("Naciśnij ENTER, aby rozpocząć...");
        in.readLine();
    }

    private int processQuestions(String studentId) throws IOException, InterruptedException {
        int correctAnswers = 0;
        int totalQuestions = questions.size();
        boolean testEnded = false;
        
        for (int i = 0; i < totalQuestions; i++) {
            if (testEnded) {
                break;
            }
            
            Question question = questions.get(i);
            out.println("\nPytanie " + (i + 1) + " z " + totalQuestions + ":");
            out.println(question.toString());
            out.println("Twoja odpowiedź (oddziel liczby przecinkami, np. 1,3):");
            
            String response;
            do {
                response = getStudentResponse();
                
                if (response == null) {
                    handleTimeout(studentId, question);
                    break;
                } else if (response.equalsIgnoreCase(ExamCommands.END_COMMAND)) {
                    out.println("Test zakończony przedwcześnie.");
                    testEnded = true;
                    break;
                } else if (response.equalsIgnoreCase(ExamCommands.SKIP_COMMAND)) {
                    handleSkippedQuestion(studentId, question);
                    break;
                } else if (!isValidResponse(response)) {
                    out.println("Nieprawidłowa odpowiedź. Wprowadź numery odpowiedzi oddzielone przecinkami (np. 1,3) lub wpisz 'pomijam'/'koniec'.");
                    continue;
                }
                
                correctAnswers += handleAnswer(studentId, question, response);
                break;
            } while (true);

            Thread.sleep(500);
        }

        return correctAnswers;
    }

    private String getStudentResponse() throws InterruptedException {
        CountDownLatch responseLatch = new CountDownLatch(1);
        AtomicBoolean timeoutOccurred = new AtomicBoolean(false);
        String[] responseHolder = new String[1];
        
        Future<?> inputFuture = executor.submit(() -> {
            try {
                String input = in.readLine();
                if (!timeoutOccurred.get()) {
                    responseHolder[0] = input;
                    responseLatch.countDown();
                }
                return input;
            } catch (IOException e) {
                System.err.println("Error reading student response: " + e.getMessage());
                return null;
            }
        });
        
        executor.submit(() -> {
            try {
                if (!responseLatch.await(answerTimeoutSeconds, TimeUnit.SECONDS)) {
                    timeoutOccurred.set(true);
                    out.println(ExamCommands.TIMEOUT_COMMAND);
                    inputFuture.cancel(true);
                    clearPendingInput();
                    responseLatch.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        responseLatch.await();
        return responseHolder[0];
    }

    private boolean isValidResponse(String response) {
        if (response == null) return false;
        response = response.trim();

        // Check for special commands
        if (response.equalsIgnoreCase(ExamCommands.END_COMMAND) || 
            response.equalsIgnoreCase(ExamCommands.SKIP_COMMAND)) {
            return true;
        }

        // Check for valid answer format
        try {
            String[] parts = response.split(",");
            for (String part : parts) {
                int num = Integer.parseInt(part.trim());
                if (num < 1 || num > 4) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clearPendingInput() {
        try {
            while (in.ready()) {
                in.read();
            }
        } catch (IOException ignored) {
            // Ignore cleanup errors
        }
    }

    private void handleTimeout(String studentId, Question question) {
        try {
            StudentResponse response = new StudentResponse(studentId, question.getId(), new ArrayList<>());
            studentResponseRepository.save(response);
            out.println(ExamCommands.NEXT_QUESTION_COMMAND);
        } catch (Exception e) {
            System.err.println("Error saving timeout response: " + e.getMessage());
        }
    }

    private void handleSkippedQuestion(String studentId, Question question) {
        try {
            StudentResponse response = new StudentResponse(studentId, question.getId(), new ArrayList<>());
            studentResponseRepository.save(response);
            out.println(ExamCommands.NEXT_QUESTION_COMMAND);
        } catch (Exception e) {
            System.err.println("Error saving skipped response: " + e.getMessage());
        }
    }

    private int handleAnswer(String studentId, Question question, String response) {
        try {
            List<Integer> selectedAnswers = parseAnswers(response);
            StudentResponse studentResponse = new StudentResponse(studentId, question.getId(), selectedAnswers);
            studentResponseRepository.save(studentResponse);
            return question.isCorrectAnswer(selectedAnswers) ? 1 : 0;
        } catch (Exception e) {
            System.err.println("Error saving student response: " + e.getMessage());
            return 0;
        }
    }

    private void sendResults(String studentId, int correctAnswers) {
        try {
            ExamResult result = new ExamResult(studentId, correctAnswers, questions.size());
            examResultRepository.save(result);
            out.println(result);
        } catch (Exception e) {
            System.err.println("Error saving exam result: " + e.getMessage());
        }
    }

    private List<Integer> parseAnswers(String answersStr) {
        List<Integer> results = new ArrayList<>();
        try {
            if (!answersStr.trim().isEmpty()) {
                String[] parts = answersStr.split(",");
                for (String part : parts) {
                    results.add(Integer.parseInt(part.trim()));
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing student answers: " + e.getMessage());
        }
        return results;
    }

    private void shutdown() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (executor != null) executor.shutdownNow();
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }
}

