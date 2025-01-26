package com.ufma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter writer;

    /**
     * Constructor
     * Abre o arquivo .asm para escrita.
     *
     * @param filename o nome do arquivo .asm
     */
    public CodeWriter(String filename) {
        try {
            writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            System.err.println("Hack VM Translator Output File I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Escreve o código assembly para comandos aritméticos/lógicos.
     *
     * @param command o comando aritmético ou lógico
     */
    public void writeArithmetic(String command) {
        try {
            writer.write("// " + command + "\n");
            switch (command) {
                case "add":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "M=M+D\n"
                    );
                    break;
                case "sub":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "M=M-D\n"
                    );
                    break;
                case "neg":
                    writer.write(
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-M\n"
                    );
                    break;
                case "eq":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "@EQUAL\n" +
                            "D;JEQ\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=0\n" +
                            "(EQUAL)\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n"
                    );
                    break;
                case "gt":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "@GREATER\n" +
                            "D;JGT\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=0\n" +
                            "(GREATER)\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n"
                    );
                    break;
                case "lt":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "@LESS\n" +
                            "D;JLT\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=0\n" +
                            "(LESS)\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n"
                    );
                    break;
                case "and":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "M=M&D\n"
                    );
                    break;
                case "or":
                    writer.write(
                            "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "M=M|D\n"
                    );
                    break;
                case "not":
                    writer.write(
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=!M\n"
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Invalid arithmetic command: " + command);
            }
        } catch (IOException e) {
            System.err.println("Hack VM Translator Output File I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Escreve o código assembly para o comando push.
     *
     * @param segment o segmento a ser acessado
     * @param index o índice do segmento
     */
    public void writePush(String segment, int index) {
        try {
            writer.write("// push " + segment + " " + index + "\n");
            if (segment.equals("constant")) {
                writer.write(
                        "@" + index + "\n" +
                        "D=A\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n"
                );
            } else {
                throw new UnsupportedOperationException("Segment not implemented: " + segment);
            }
        } catch (IOException e) {
            System.err.println("Hack VM Translator Output File I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Escreve o código assembly para o comando pop.
     *
     * @param segment o segmento a ser acessado
     * @param index o índice do segmento
     */
    public void writePop(String segment, int index) {
        try {
            writer.write("// pop " + segment + " " + index + "\n");
            throw new UnsupportedOperationException("Pop command not implemented yet.");
        } catch (IOException e) {
            System.err.println("Hack VM Translator Output File I/O Exception: " + e.getMessage());
        }
    }

    /**
     * Fecha o arquivo de saída.
     */
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Hack VM Translator Output File I/O Exception: " + e.getMessage());
        }
    }
}
