package com.ufma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter implements AutoCloseable {
    private BufferedWriter writer;

    public CodeWriter(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
    }

    public void writeArithmetic(String command) throws IOException {
        writer.write("// " + command + "\n");
        // Implementação de comando...
    }

    public void writePushPop(String commandType, String segment, int index) throws IOException {
        writer.write("// " + commandType + " " + segment + " " + index + "\n");
        // Implementação de push/pop...
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}