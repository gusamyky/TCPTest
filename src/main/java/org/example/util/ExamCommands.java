package org.example.util;

public final class ExamCommands {
    // Client commands
    public static final String SKIP_COMMAND = "pomijam";
    public static final String END_COMMAND = "koniec";
    
    // Server commands
    public static final String TIMEOUT_COMMAND = "TIMEOUT";
    public static final String NEXT_QUESTION_COMMAND = "MOVING_TO_NEXT_QUESTION";

    //Prevent instantiation
    private ExamCommands() {}
} 