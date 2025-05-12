package org.example.client;

import org.example.config.Config;
import org.example.util.ExamCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExamClient {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ExecutorService executor;
    private volatile boolean isRunning;

    public ExamClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.executor = Executors.newFixedThreadPool(2);
        this.isRunning = true;
    }

    public void start() {
        try {
            if (!connectToServer()) {
                return;
            }

            executor.submit(this::readServerResponses);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while (isRunning && (input = userInput.readLine()) != null) {
                out.println(input);
            }

        } catch (IOException e) {
            System.err.println("Error during exam: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private boolean connectToServer() {
        try {
            System.out.println("Łączenie z serwerem egzaminacyjnym...");
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to exam server at " + serverAddress + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
            return false;
        }
    }

    private void readServerResponses() {
        try {
            String response;
            while (isRunning && (response = in.readLine()) != null) {
                if (response.equals(ExamCommands.TIMEOUT_COMMAND)) {
                    System.out.println("\nCzas na odpowiedź minął! Przechodzimy do następnego pytania.");
                } else if (response.equals(ExamCommands.NEXT_QUESTION_COMMAND)) {
                    System.out.print("> ");
                } else if (response.startsWith("Wynik testu:")) {
                    System.out.println(response);
                    isRunning = false;
                    break;
                } else if (response.equals("Test zakończony przedwcześnie.")) {
                    System.out.println(response);
                } else {
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Utracono połączenie z serwerem: " + e.getMessage());
            }
        } finally {
            shutdown();
        }

        System.exit(0);
    }

    private void shutdown() {
        isRunning = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = Config.getServerAddress();
        int serverPort = Config.getServerPort();
        ExamClient client = new ExamClient(serverAddress, serverPort);
        client.start();
    }
}
