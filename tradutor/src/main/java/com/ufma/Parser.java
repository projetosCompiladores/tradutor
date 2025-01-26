package com.ufma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {
    private BufferedReader reader = null;
    private String line = null;
    private String currentCommand = null;

    public static final int C_ARITHMETIC = 0; // arithmetic command
    public static final int C_PUSH = 1;      // push command
    public static final int C_POP = 2;       // pop command
    public static final int C_LABEL = 3;     // label command
    public static final int C_GOTO = 4;      // goto command
    public static final int C_IF = 5;        // if-goto command
    public static final int C_FUNCTION = 6;  // function command
    public static final int C_RETURN = 7;    // return command
    public static final int C_CALL = 8;      // call command

    /**
     * Constructor
     * Open the input file and get ready to parse it.
     *
     * @param filename the name of the file to open
     */
    public Parser(String filename) {
        // Ensure the input file exists
        Path file = Paths.get(filename);
        InputStream input;
        try {
            // Open the file for reading
            if (!Files.exists(file) || !Files.isReadable(file)) {
                throw new IOException("File does not exist or is not readable: " + filename);
            }
            input = Files.newInputStream(file);
            this.reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException e) {
            System.err.println("Hack VM Translator Input File I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Check if there are more commands in the file.
     *
     * @return boolean true if there are more commands, false otherwise
     */
    boolean hasMoreCommands() {
        try {
            while ((line = reader.readLine()) != null) {
                // Remove comments
                line = line.replaceAll("//.*", "");

                // Remove leading and trailing whitespace
                line = line.trim();

                if (line.isEmpty()) continue; // Ignore empty lines
                return true;
            }
        } catch (IOException e) {
            System.err.println("Hack VM Translator Input File I/O Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Advance to the next command in the file.
     */
    void advance() {
        currentCommand = line; // 'line' already contains the next command
    }

    /**
     * Get the type of the current command.
     *
     * @return int the type of the command
     */
    int commandType() {
        if (currentCommand.startsWith("push"))
            return C_PUSH;
        else if (currentCommand.startsWith("pop"))
            return C_POP;
        else if (currentCommand.startsWith("label"))
            return C_LABEL;
        else if (currentCommand.startsWith("if-goto"))
            return C_IF;
        else if (currentCommand.startsWith("goto"))
            return C_GOTO;
        else if (currentCommand.startsWith("function"))
            return C_FUNCTION;
        else if (currentCommand.startsWith("return"))
            return C_RETURN;
        else if (currentCommand.startsWith("call"))
            return C_CALL;
        else
            return C_ARITHMETIC; // add, sub, neg, eq, gt, lt, and, or, not
    }

    /**
     * Get the first argument of the current command.
     *
     * @return String the first argument
     */
    public String arg1() {
        if (commandType() == C_RETURN)
            throw new IllegalArgumentException("Command is a return command");
        else if (commandType() == C_ARITHMETIC)
            return currentCommand;
        else
            return currentCommand.split("\\s+")[1]; // Split by whitespace
    }

    /**
     * Get the second argument of the current command.
     *
     * @return int the second argument
     */
    public int arg2() {
        if (commandType() != C_PUSH && commandType() != C_POP && commandType() != C_FUNCTION && commandType() != C_CALL)
            throw new IllegalArgumentException("Command is not a push, pop, function, or call command");
        return Integer.parseInt(currentCommand.split("\\s+")[2]); // Split by whitespace
    }

    /**
     * Close the input file.
     */
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("I/O Exception: " + e.getMessage());
        }
    }
}
