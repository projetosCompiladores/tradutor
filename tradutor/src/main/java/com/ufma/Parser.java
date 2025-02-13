package com.ufma;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser implements AutoCloseable {
    private BufferedReader reader;
    private String currentCommand;

    public Parser(String fileName) throws IOException {
        reader = new BufferedReader(new FileReader(fileName));
        currentCommand = null;
    }

    public boolean hasMoreCommands() throws IOException {
        return reader.ready();
    }

    public void advance() throws IOException {
        if (hasMoreCommands()) {
            currentCommand = reader.readLine().trim();
            while (currentCommand != null && (currentCommand.isEmpty() || currentCommand.startsWith("//"))) {
                currentCommand = reader.readLine();
                if (currentCommand != null) {
                    currentCommand = currentCommand.trim();
                }
            }
        } else {
            currentCommand = null;
        }
    }

    public String commandType() {
        if (currentCommand.startsWith("push")) {
            return "C_PUSH";
        } else if (currentCommand.startsWith("pop")) {
            return "C_POP";
        } else if (currentCommand.startsWith("label")) {
            return "C_LABEL";
        } else if (currentCommand.startsWith("goto")) {
            return "C_GOTO";
        } else if (currentCommand.startsWith("if-goto")) {
            return "C_IF";
        } else if (currentCommand.startsWith("function")) {
            return "C_FUNCTION";
        } else if (currentCommand.startsWith("call")) {
            return "C_CALL";
        } else if (currentCommand.startsWith("return")) {
            return "C_RETURN";
        } else {
            return "C_ARITHMETIC";
        }
    }

    public String arg1() {
        if (commandType().equals("C_RETURN")) {
            throw new IllegalStateException("arg1() called on C_RETURN command");
        }
        if (commandType().equals("C_ARITHMETIC")) {
            return currentCommand;
        }
        return currentCommand.split(" ")[1];
    }

    public int arg2() {
        if (!(commandType().equals("C_PUSH") || commandType().equals("C_POP") || 
              commandType().equals("C_FUNCTION") || commandType().equals("C_CALL"))) {
            throw new IllegalStateException("arg2() called on invalid command type");
        }
        return Integer.parseInt(currentCommand.split(" ")[2].trim());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
