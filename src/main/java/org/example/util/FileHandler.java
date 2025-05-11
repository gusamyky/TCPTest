package org.example.util;

import org.example.config.Config;
import org.example.model.ExamResult;
import org.example.model.Question;
import org.example.model.StudentResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileHandler {
    private static final String DATA_DIR = Config.getDataDir();
    private static final String QUESTIONS_FILE = DATA_DIR + "/bazaPytan.txt";
    private static final String RESPONSES_FILE = DATA_DIR + "/bazaOdpowiedzi.txt";
    private static final String RESULTS_FILE = DATA_DIR + "/wyniki.txt";

    public static List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        
        try {
            // Create data directory if it doesn't exist
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            Path questionsPath = Paths.get(QUESTIONS_FILE);
            if (!Files.exists(questionsPath)) {
                createSampleQuestionsFile();
            }
            
            List<String> lines = Files.readAllLines(questionsPath);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    questions.add(Question.fromFileFormat(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }
        
        return questions;
    }
    
    public static void saveStudentResponse(StudentResponse response) {
        try {
            // Ensure data directory exists
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            Files.writeString(
                Paths.get(RESPONSES_FILE),
                response.toFileFormat() + "\n",
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error saving student response: " + e.getMessage());
        }
    }
    
    public static void saveExamResult(ExamResult result) {
        try {
            // Ensure data directory exists
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            Files.writeString(
                Paths.get(RESULTS_FILE),
                result.toFileFormat() + "\n",
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error saving exam result: " + e.getMessage());
        }
    }
    
    public static List<StudentResponse> getStudentResponses(String studentId) {
        List<StudentResponse> responses = new ArrayList<>();
        
        try {
            Path responsesPath = Paths.get(RESPONSES_FILE);
            if (Files.exists(responsesPath)) {
                List<String> lines = Files.readAllLines(responsesPath);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        StudentResponse response = StudentResponse.fromFileFormat(line);
                        if (response.getStudentId().equals(studentId)) {
                            responses.add(response);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading student responses: " + e.getMessage());
        }
        
        return responses;
    }
    
    private static void createSampleQuestionsFile() throws IOException {
        List<Question> sampleQuestions = new ArrayList<>();
        
        // Sample questions (20 in total)
        sampleQuestions.add(new Question(1, "Co to jest Java?", 
            List.of("Rodzaj kawy", "Język programowania", "System operacyjny", "Wyspa w Indonezji"), List.of(2)));
            
        sampleQuestions.add(new Question(2, "Które z poniższych są językami programowania?", 
            List.of("Python", "Oracle", "C++", "HTML"), List.of(1, 3)));
            
        sampleQuestions.add(new Question(3, "Co to jest TCP?", 
            List.of("Protokół warstwy transportowej", "Protokół routingu", 
                  "Protokół warstwy aplikacji", "System plików"), List.of(1)));
                  
        sampleQuestions.add(new Question(4, "Jakie są podstawowe paradygmaty programowania obiektowego?", 
            List.of("Dziedziczenie", "Polimorfizm", "Hermetyzacja", "Wszystkie powyższe"), List.of(4)));
            
        sampleQuestions.add(new Question(5, "Co oznacza skrót JVM?", 
            List.of("Java Virtual Machine", "Java Visual Middleware", 
                  "Java Variable Method", "Java Verified Module"), List.of(1)));
                  
        sampleQuestions.add(new Question(6, "Co to jest SQL?", 
            List.of("Język zapytań do baz danych", "Język programowania obiektowego", 
                  "System operacyjny", "Protokół sieciowy"), List.of(1)));
                  
        sampleQuestions.add(new Question(7, "Co to jest HTTP?", 
            List.of("Protocol komunikacyjny do przesyłania dokumentów hipertekstowych", 
                  "System zarządzania bazą danych", "System plików", 
                  "Język programowania"), List.of(1)));
                  
        sampleQuestions.add(new Question(8, "Co to jest algorytm?", 
            List.of("Sekwencja kroków do rozwiązania problemu", "Typ zmiennej", 
                  "Struktura danych", "Biblioteka programistyczna"), List.of(1)));
                  
        sampleQuestions.add(new Question(9, "Które z poniższych są systemami operacyjnymi?", 
            List.of("Windows", "Linux", "macOS", "Wszystkie powyższe"), List.of(4)));
            
        sampleQuestions.add(new Question(10, "Co to jest HTML?", 
            List.of("Język znaczników używany do tworzenia stron internetowych", 
                  "Język programowania", "System baz danych", 
                  "Protokół do transmisji danych"), List.of(1)));
                  
        sampleQuestions.add(new Question(11, "Co to jest cykl życia oprogramowania?", 
            List.of("Proces od koncepcji do wycofania oprogramowania", 
                  "Czas działania programu", "Częstotliwość aktualizacji", 
                  "Metoda testowania"), List.of(1)));
                  
        sampleQuestions.add(new Question(12, "Co to jest kompilator?", 
            List.of("Program tłumaczący kod źródłowy na kod maszynowy", 
                  "Program do pisania kodu", "Typ bazy danych", 
                  "Algorytm sortowania"), List.of(1)));
                  
        sampleQuestions.add(new Question(13, "Które z poniższych są typami danych w Javie?", 
            List.of("int", "boolean", "String", "Wszystkie powyższe"), List.of(4)));
            
        sampleQuestions.add(new Question(14, "Co to jest REST API?", 
            List.of("Interfejs programowania aplikacji oparty na reprezentacyjnym transferze stanu", 
                  "System zarządzania bazą danych", "Język programowania", 
                  "Protokół routingu"), List.of(1)));
                  
        sampleQuestions.add(new Question(15, "Co to jest serwer?", 
            List.of("Komputer lub program świadczący usługi innym komputerom", 
                  "Urządzenie wejścia", "Język programowania", 
                  "System operacyjny"), List.of(1)));
                  
        sampleQuestions.add(new Question(16, "Które z poniższych są wzorcami projektowymi?", 
            List.of("Singleton", "Factory", "Observer", "Wszystkie powyższe"), List.of(4)));
            
        sampleQuestions.add(new Question(17, "Co to jest garbage collector w Javie?", 
            List.of("Mechanizm automatycznego zarządzania pamięcią", 
                  "Narzędzie do czyszczenia kodu", "System plików", 
                  "Protokół sieciowy"), List.of(1)));
                  
        sampleQuestions.add(new Question(18, "Co to jest testowanie jednostkowe?", 
            List.of("Testowanie pojedynczych modułów kodu", "Testowanie całego systemu", 
                  "Testowanie interfejsu użytkownika", 
                  "Testowanie wydajności"), List.of(1)));
                  
        sampleQuestions.add(new Question(19, "Co to jest indeks w bazie danych?", 
            List.of("Struktura przyspieszająca wyszukiwanie danych", 
                  "Typ zmiennej", "Metoda programowania", 
                  "Protokół sieciowy"), List.of(1)));
                  
        sampleQuestions.add(new Question(20, "Co to jest wątek w programowaniu?", 
            List.of("Niezależny ciąg wykonania w programie", 
                  "Błąd w kodzie", "Struktura danych", 
                  "Metoda kompilacji"), List.of(1)));
        
        // Make sure the data directory exists before creating the file
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        
        String content = sampleQuestions.stream()
            .map(Question::toFileFormat)
            .collect(Collectors.joining("\n"));
            
        Files.writeString(Paths.get(QUESTIONS_FILE), content);
    }
}

