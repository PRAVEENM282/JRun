import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CppFile extends CodeFile {
    private static final String OUTPUT_NAME = "temp_executable";
    private static final int COMPILE_TIMEOUT = 30;
    private static final int RUN_TIMEOUT = 60;
    private static final Pattern ERROR_PATTERN = 
        Pattern.compile(".*\\b(invalid|error|fail|exception|expected)\\b.*", Pattern.CASE_INSENSITIVE);

    public CppFile(String filePath, CommandExecutor executor) {
        super(filePath, executor);
    }

    @Override
    public void execute(Scanner scanner) throws Exception {
        String outputPath = getOutputFileName();
        try {
            compileCode(outputPath);
            runProgram(outputPath, scanner);
        } finally {
            cleanUp(outputPath);
        }
    }

    private void compileCode(String outputPath) throws Exception {
        System.out.println("Compiling C++ code...");
        String compileCommand = String.format("g++ -Wall -Wextra %s -o %s", filePath, outputPath);
        executor.executeCommand(compileCommand, COMPILE_TIMEOUT);

        if (!new File(outputPath).exists()) {
            throw new Exception("Compilation failed - no executable created");
        }
    }

    private void runProgram(String outputPath, Scanner scanner) throws Exception {
        System.out.println("Running program...");
        System.out.println("NOTE: Please enter valid input as expected by the program");

        String output = executor.executeCommandInteractive(outputPath, scanner, RUN_TIMEOUT);
        analyzeOutput(output);
    }

    private void analyzeOutput(String output) throws Exception {
        if (ERROR_PATTERN.matcher(output).find()) {
            throw new Exception("Potential error detected in program output");
        }
    }

    private void cleanUp(String outputPath) {
        try {
            File executable = new File(outputPath);
            if (executable.exists() && !executable.delete()) {
                System.err.println("Warning: Could not delete temporary executable");
            }
        } catch (SecurityException e) {
            System.err.println("Warning: Failed to clean up executable - " + e.getMessage());
        }
    }

    private String getOutputFileName() {
        return System.getProperty("os.name").toLowerCase().contains("windows") 
            ? OUTPUT_NAME + ".exe" 
            : "./" + OUTPUT_NAME;
    }
}
