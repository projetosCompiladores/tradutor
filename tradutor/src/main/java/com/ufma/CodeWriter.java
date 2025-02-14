package com.ufma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter implements AutoCloseable {
    private BufferedWriter writer;
    private String moduleName;
    private int synCount;
    private int returnCounter;

    public CodeWriter(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
        synCount = 0;
        returnCounter = 0;
    }

    public void setFileName(String fileName) {
        moduleName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
    }

    private String registerName(String segment, int index) {
        switch (segment) {
            case "local":
                return "LCL";
            case "argument":
                return "ARG";
            case "this":
                return "THIS";
            case "that":
                return "THAT";
            case "pointer":
                return "R" + (3 + index);
            case "temp":
                return "R" + (5 + index);
            default:
                return moduleName + "." + index;
        }
    }

    public void writeInit() throws IOException {
        write("@256");
        write("D=A");
        write("@SP");
        write("M=D");
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) throws IOException {
        write("(" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        write("@" + label);
        write("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        write("@SP");
        write("AM=M-1");
        write("D=M");
        write("@" + label);
        write("D;JNE");
    }

    public void writeFunction(String functionName, int numLocals) throws IOException {
        write("(" + functionName + ")");
        for (int i = 0; i < numLocals; i++) {
            writePush("constant", 0);
        }
    }

    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            case "add":
                writeArithmeticAdd();
                break;
            case "sub":
                writeArithmeticSub();
                break;
            case "neg":
                writeArithmeticNeg();
                break;
            case "eq":
                writeArithmeticEq();
                break;
            case "gt":
                writeArithmeticGt();
                break;
            case "lt":
                writeArithmeticLt();
                break;
            case "and":
                writeArithmeticAnd();
                break;
            case "or":
                writeArithmeticOr();
                break;
            default:
                writeArithmeticNot();
                break;
        }
    }

    private void writeArithmeticAdd() throws IOException {
        write("@SP // add");
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("A=A-1");
        write("M=D+M");
    }

    private void writeArithmeticSub() throws IOException {
        write("@SP // sub");
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("A=A-1");
        write("M=M-D");
    }

    private void writeArithmeticNeg() throws IOException {
        write("@SP // neg");
        write("A=M");
        write("A=A-1");
        write("M=-M");
    }

    private void writeArithmeticAnd() throws IOException {
        write("@SP // and");
        write("AM=M-1");
        write("D=M");
        write("A=A-1");
        write("M=D&M");
    }

    private void writeArithmeticOr() throws IOException {
        write("@SP // or");
        write("AM=M-1");
        write("D=M");
        write("A=A-1");
        write("M=D|M");
    }

    private void writeArithmeticNot() throws IOException {
        write("@SP // not");
        write("A=M");
        write("A=A-1");
        write("M=!M");
    }

    private void writeArithmeticEq() throws IOException {
        String label = "JEQ_" + moduleName + "_" + synCount++;
        write("@SP // eq");
        write("AM=M-1");
        write("D=M");
        write("@SP");
        write("AM=M-1");
        write("D=M-D");
        write("@" + label);
        write("D;JEQ");
        write("D=1");
        write("(" + label + ")");
        write("D=D-1");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    private void writeArithmeticGt() throws IOException {
        String labelTrue = "JGT_TRUE_" + moduleName + "_" + synCount;
        String labelFalse = "JGT_FALSE_" + moduleName + "_" + synCount++;
        write("@SP // gt");
        write("AM=M-1");
        write("D=M");
        write("@SP");
        write("AM=M-1");
        write("D=M-D");
        write("@" + labelTrue);
        write("D;JGT");
        write("D=0");
        write("@" + labelFalse);
        write("0;JMP");
        write("(" + labelTrue + ")");
        write("D=-1");
        write("(" + labelFalse + ")");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    private void writeArithmeticLt() throws IOException {
        String labelTrue = "JLT_TRUE_" + moduleName + "_" + synCount;
        String labelFalse = "JLT_FALSE_" + moduleName + "_" + synCount++;
        write("@SP // lt");
        write("AM=M-1");
        write("D=M");
        write("@SP");
        write("AM=M-1");
        write("D=M-D");
        write("@" + labelTrue);
        write("D;JLT");
        write("D=0");
        write("@" + labelFalse);
        write("0;JMP");
        write("(" + labelTrue + ")");
        write("D=-1");
        write("(" + labelFalse + ")");
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    public void writePush(String segment, int index) throws IOException {
        if ("constant".equals(segment)) {
            write("@" + index + " // push " + segment + " " + index);
            write("D=A");
            write("@SP");
            write("A=M");
            write("M=D");
            write("@SP");
            write("M=M+1");
        } else if ("static".equals(segment) || "temp".equals(segment) || "pointer".equals(segment)) {
            write("@" + registerName(segment, index) + " // push " + segment + " " + index);
            write("D=M");
            write("@SP");
            write("A=M");
            write("M=D");
            write("@SP");
            write("M=M+1");
        } else {
            write("@" + registerName(segment, 0) + " // push " + segment + " " + index);
            write("D=M");
            write("@" + index);
            write("A=D+A");
            write("D=M");
            write("@SP");
            write("A=M");
            write("M=D");
            write("@SP");
            write("M=M+1");
        }
    }

    public void writePop(String segment, int index) throws IOException {
        if ("static".equals(segment) || "temp".equals(segment) || "pointer".equals(segment)) {
            write("@SP // pop " + segment + " " + index);
            write("M=M-1");
            write("A=M");
            write("D=M");
            write("@" + registerName(segment, index));
            write("M=D");
        } else {
            write("@" + registerName(segment, 0) + " // pop " + segment + " " + index);
            write("D=M");
            write("@" + index);
            write("D=D+A");
            write("@R13");
            write("M=D");
            write("@SP");
            write("M=M-1");
            write("A=M");
            write("D=M");
            write("@R13");
            write("A=M");
            write("M=D");
        }
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        String returnLabel = "RETURN_LABEL" + returnCounter++;
        write("@" + returnLabel);
        write("D=A");
        writePushD();
        writePushSegment("LCL");
        writePushSegment("ARG");
        writePushSegment("THIS");
        writePushSegment("THAT");
        write("@SP");
        write("D=M");
        write("@" + (5 + numArgs));
        write("D=D-A");
        write("@ARG");
        write("M=D");
        write("@SP");
        write("D=M");
        write("@LCL");
        write("M=D");
        writeGoto(functionName);
        write("(" + returnLabel + ")");
    }

    public void writeReturn() throws IOException {
        write("@LCL");
        write("D=M");
        write("@R13");
        write("M=D");
        write("@5");
        write("A=D-A");
        write("D=M");
        write("@R14");
        write("M=D");
        write("@SP");
        write("AM=M-1");
        write("D=M");
        write("@ARG");
        write("A=M");
        write("M=D");
        write("@ARG");
        write("D=M+1");
        write("@SP");
        write("M=D");
        writeRestoreSegment("THAT", 1);
        writeRestoreSegment("THIS", 2);
        writeRestoreSegment("ARG", 3);
        writeRestoreSegment("LCL", 4);
        write("@R14");
        write("A=M");
        write("0;JMP");
    }

    private void writePushD() throws IOException {
        write("@SP");
        write("A=M");
        write("M=D");
        write("@SP");
        write("M=M+1");
    }

    private void writePushSegment(String segment) throws IOException {
        write("@" + segment);
        write("D=M");
        writePushD();
    }

    private void writeRestoreSegment(String segment, int offset) throws IOException {
        write("@R13");
        write("D=M");
        write("@" + offset);
        write("A=D-A");
        write("D=M");
        write("@" + segment);
        write("M=D");
    }

    private void write(String s) throws IOException {
        writer.write(s + "\n");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
