import java.util.Scanner;
import java.util.regex.Pattern;

public class PyFile extends CodeFile {
    private static final int RUN_TIMEOUT = 60;
    private static final Pattern ERROR_PATTERN = 
        Pattern.compile(".*\\b(error|exception|traceback)\\b.*", Pattern.CASE_INSENSITIVE);

    public PyFile(String filePath, CommandExecutor executor) {
        super(filePath, executor);
    }

    @Override
    public void execute(Scanner scanner) throws Exception {
        System.out.println("Executing Python script...");
        String output = executor.executeCommandInteractive("python -u " + filePath, scanner, RUN_TIMEOUT);

        if (ERROR_PATTERN.matcher(output).find()) {
            throw new Exception("Python error detected in output");
        }
    }
}
