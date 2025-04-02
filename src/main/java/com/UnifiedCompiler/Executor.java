package com.UnifiedCompiler;

import java.io.*;

public class Executor {

    public String compileAndRunCpp(String fileName) {
        try {
            // Create the output directory for the executable if it doesn't exist
            File exeOutputDir = new File("output");
            if (!exeOutputDir.exists()) {
                exeOutputDir.mkdirs();
            }

            // Extract filename without extension and build the executable path
            String cppFileName = new File(fileName).getName();
            String exeFileName = cppFileName.replace(".cpp", ".exe");
            String exePath = "output" + File.separator + exeFileName;

            // Compile the C++ file
            ProcessBuilder compileBuilder = new ProcessBuilder("g++", fileName, "-o", exePath);
            compileBuilder.redirectErrorStream(true);
            Process compile = compileBuilder.start();
            String compileOutput = readProcessOutput(compile.getInputStream());

            if (compile.waitFor() != 0) {
                return "Compilation Error:\n" + compileOutput;
            }

            // Prepare to run the executable and provide input from BufferHandler
            ProcessBuilder runBuilder = new ProcessBuilder(exePath);
            runBuilder.redirectErrorStream(true);
            Process run = runBuilder.start();

            // Write input to the process if available
            String inputForProcess = BufferHandler.getInput();
            if (!inputForProcess.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(run.getOutputStream()))) {
                    writer.write(inputForProcess);
                    writer.flush();
                }
            }

            // Read process output
            String runOutput = readProcessOutput(run.getInputStream());
            
            // Save the run output to file
            OutputHandler.saveOutput(runOutput);

            return "Execution Output:\n" + runOutput;
        } catch (IOException | InterruptedException e) {
            return "Execution Error: " + e.getMessage();
        }
    }

    private String readProcessOutput(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
    }
}
