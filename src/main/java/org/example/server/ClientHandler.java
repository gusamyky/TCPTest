package org.example.server;

import org.example.config.Config;
import org.example.model.ExamResult;
import org.example.model.Question;
import org.example.model.StudentResponse;
import org.example.util.FileHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final List<Question> questions;
    private PrintWriter out;
    private BufferedReader in;
    private final int ANSWER_TIMEOUT_SECONDS;

    public ClientHandler(Socket clientSocket, List<Question> questions) {
        this.clientSocket = clientSocket;
        this.questions = questions;
        this.ANSWER_TIMEOUT_SECONDS = Config.getAnswerTimeoutSeconds();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Get student ID
            out.println("Podaj swój numer indeksu:");
            String studentId = in.readLine();

            out.println("Witaj, " + studentId + "! Rozpoczynamy test.");
            out.println("Na każde pytanie masz " + ANSWER_TIMEOUT_SECONDS + " sekund.");
            out.println("Aby odpowiedzieć na pytanie, wpisz numer(y) odpowiedzi oddzielone przecinkami, np. 1,3");
            out.println("Aby zakończyć test wcześniej, wpisz 'koniec'");
            out.println("Naciśnij ENTER, aby rozpocząć...");
            in.readLine();

            int correctAnswers = 0;
            ExecutorService executor = Executors.newFixedThreadPool(2);

            for (Question question : questions) {
                out.println(question.toString());
                out.println("Twoja odpowiedź (oddziel liczby przecinkami, np. 1,3):");
                
                // Signal for timeout and response
                CountDownLatch responseLatch = new CountDownLatch(1);
                AtomicBoolean timeoutOccurred = new AtomicBoolean(false);
                
                // Response holder
                String[] responseHolder = new String[1];
                
                // Task to get user input
                Future<?> inputFuture = executor.submit(() -> {
                    try {
                        String input = in.readLine();
                        // Only process the input if we haven't timed out yet
                        if (!timeoutOccurred.get()) {
                            responseHolder[0] = input;
                            responseLatch.countDown(); // Signal that response is received
                        }
                        return input;
                    } catch (IOException e) {
                        // Handle exception
                        return null;
                    }
                });
                
                // Task for timeout
                executor.submit(() -> {
                    try {
                        boolean received = responseLatch.await(ANSWER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        if (!received) {
                            timeoutOccurred.set(true);
                            out.println("TIMEOUT");
                            // Cancel the input task
                            inputFuture.cancel(true);
                            // Clear any pending input - fix for BufferedReader which doesn't have available() method
                            try {
                                // Skip chars by reading them until ready() returns false
                                while (in.ready()) {
                                    in.read();
                                }
                            } catch (IOException ignored) {
                                // Ignore any exceptions during cleanup
                            }
                            // Force countdown to proceed to next question
                            responseLatch.countDown();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
                // Wait for either response or timeout
                responseLatch.await();
                
                String response = responseHolder[0];
                
                if (timeoutOccurred.get() || response == null) {
                    // Record an empty response due to timeout
                    StudentResponse studentResponse = new StudentResponse(
                            studentId, question.getId(), new ArrayList<>());
                    FileHandler.saveStudentResponse(studentResponse);
                    
                    // Send a clear message to client that we're moving to next question
                    out.println("MOVING_TO_NEXT_QUESTION");
                } else if (response.equalsIgnoreCase("koniec")) {
                    out.println("Test zakończony przedwcześnie.");
                    break;
                } else {
                    // Parse student's answer
                    List<Integer> selectedAnswers = parseAnswers(response);
                    
                    // Record the student's response
                    StudentResponse studentResponse = new StudentResponse(
                            studentId, question.getId(), selectedAnswers);
                    FileHandler.saveStudentResponse(studentResponse);

                    // Check if the answer is correct
                    if (question.isCorrectAnswer(selectedAnswers)) {
                        correctAnswers++;
                    }
                }
            }

            executor.shutdownNow();

            // Calculate and save the result
            ExamResult result = new ExamResult(studentId, correctAnswers, questions.size());
            FileHandler.saveExamResult(result);

            // Send result to the client
            out.println(result.toString());

        } catch (IOException | InterruptedException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
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

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }
}

