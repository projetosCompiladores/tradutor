package com.ufma;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <inputFile.vm>");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = inputFileName.replace(".vm", ".asm");

        try (Parser parser = new Parser(inputFileName); CodeWriter codeWriter = new CodeWriter(outputFileName)) {
            while (parser.hasMoreCommands()) {
                parser.advance();
                String commandType = parser.commandType();

                if (commandType.equals("C_ARITHMETIC")) {
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (commandType.equals("C_PUSH") || commandType.equals("C_POP")) {
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

