package com.ufma;


public class VMTranslator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: VMTranslator <inputFile.vm>");
            return;
        }

        String inputFile = args[0];
        String outputFile = inputFile.replace(".vm", ".asm");

        try {
            // Instancia o Parser para ler o arquivo .vm
            Parser parser = new Parser(inputFile);
            // Instancia o CodeWriter para gravar no arquivo .asm
            CodeWriter codeWriter = new CodeWriter(outputFile);

            // Processa cada comando no arquivo de entrada
            while (parser.hasMoreCommands()) {
                parser.advance(); // Move para o próximo comando

                switch (parser.commandType()) {
                    case Parser.C_ARITHMETIC:
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    case Parser.C_PUSH:
                        codeWriter.writePush(parser.arg1(), parser.arg2());
                        break;
                    case Parser.C_POP:
                        codeWriter.writePop(parser.arg1(), parser.arg2());
                        break;
                    default:
                        throw new UnsupportedOperationException("Command not supported: " + parser.commandType());
                }
            }

            // Fecha o arquivo de saída
            codeWriter.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
