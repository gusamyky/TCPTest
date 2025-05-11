package org.example.client;

import org.example.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExamClient {

    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Scanner scanner;
    private ExecutorService executor;
    private volatile boolean inExam = false;

    public ExamClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.scanner = new Scanner(System.in);
        this.executor = Executors.newCachedThreadPool();
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = Config.getServerPort();

        System.out.println("Łączenie z serwerem egzaminacyjnym...");
        ExamClient client = new ExamClient(serverAddress, serverPort);
        client.start();
    }

    public void start() {
        try {
            connectToServer();

            AtomicBoolean waitingForInput = new AtomicBoolean(false);
            
            // Thread for handling server messages
            executor.submit(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        // Check if we got a timeout message
                        if (fromServer.equals("TIMEOUT")) {
                            System.out.println("\nCzas na odpowiedź minął! Przechodzimy do następnego pytania.");
                            waitingForInput.set(false);
                            continue;
                        }
                        
                        // Check if we're moving to next question after timeout
                        if (fromServer.equals("MOVING_TO_NEXT_QUESTION")) {
                            continue;
                        }
                        
                        System.out.println(fromServer);
                        
                        // Test is finished when the result is received
                        if (fromServer.contains("Wynik testu")) {
                            inExam = false;
                            break;
                        }
                        
                        // If the server expects an answer
                        if (fromServer.contains("odpowiedź")) {
                            waitingForInput.set(true);
                            System.out.print("> ");
                        } else if ((fromServer.contains("Podaj") || fromServer.contains("Naciśnij ENTER"))) {
                            waitingForInput.set(true);
                            System.out.print("> ");
                        }
                    }
                } catch (IOException e) {
                    if (inExam) {
                        System.err.println("Utracono połączenie z serwerem: " + e.getMessage());
                    }
                }
                return null;
            });
            
            // Thread for handling user input
            inExam = true;
            while (inExam) {
                if (waitingForInput.get()) {
                    String input = scanner.nextLine();
                    out.println(input);
                    waitingForInput.set(false);
                    
                    if (input.equalsIgnoreCase("koniec")) {
                        System.out.println("Zakończono test przedwcześnie.");
                        inExam = false;
                        break;
                    }
                }
                
                // Small delay to prevent CPU spinning
                TimeUnit.MILLISECONDS.sleep(50);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error during exam: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connected to exam server at " + serverAddress + ":" + serverPort);
    }

    private void closeConnection() {
        try {
            if (executor != null) executor.shutdownNow();
            if (scanner != null) scanner.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
