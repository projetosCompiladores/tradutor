package com.ufma;

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
            codeWriter.writeInit(); // Inicializa o código
            
            while (parser.hasMoreCommands()) {
                parser.advance();
                String commandType = parser.commandType();

                switch (commandType) {
                    case "C_ARITHMETIC":
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    case "C_PUSH":
                        codeWriter.writePush(parser.arg1(), parser.arg2());
                        break;
                    case "C_POP":
                        codeWriter.writePop(parser.arg1(), parser.arg2());
                        break;
                    case "C_LABEL":
                        codeWriter.writeLabel(parser.arg1());
                        break;
                    case "C_GOTO":
                        codeWriter.writeGoto(parser.arg1());
                        break;
                    case "C_IF":
                        codeWriter.writeIf(parser.arg1());
                        break;
                    case "C_FUNCTION":
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case "C_CALL":
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                        break;
                    case "C_RETURN":
                        codeWriter.writeReturn();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}