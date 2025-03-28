import java.io.File;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class FileProcessor {
    private final CommandExecutor executor;
    
    public FileProcessor() {
        this.executor = new CommandExecutor();
    }

    public void processFile(String filePath) {
        try {
            validateFilePath(filePath);
            filePath = new File(filePath).getAbsolutePath();

            FileType fileType = FileType.fromFileName(filePath);
            if (fileType == FileType.UNSUPPORTED) {
                System.err.println("Error: Only .cpp and .py files are supported");
                return;
            }

            if (!checkDependencies(fileType)) return;

            CodeFile codeFile = createFileObject(fileType, filePath);
            executeFile(codeFile);
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private void validateFilePath(String filePath) throws Exception {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new Exception("File path cannot be empty");
        }
    }

    private boolean checkDependencies(FileType fileType) {
        try {
            String result = switch (fileType) {
                case PYTHON -> executor.executeCommand("python --version", 5);
                case CPP -> executor.executeCommand("g++ --version", 5);
                default -> "";
            };
            System.out.println("Using " + fileType + ": " + result.split("\n")[0]);
            return true;
        } catch (Exception e) {
            System.err.println("Dependency check failed: " + e.getMessage());
            return false;
        }
    }

    private CodeFile createFileObject(FileType fileType, String filePath) {
        return switch (fileType) {
            case PYTHON -> new PyFile(filePath, executor);
            case CPP -> new CppFile(filePath, executor);
            default -> null;
        };
    }

    private void executeFile(CodeFile codeFile) throws Exception {
        if (!codeFile.exists()) {
            throw new Exception("File not found: " + codeFile.filePath);
        }
        System.out.println("Note: Press Ctrl+Z (Windows) or Ctrl+D (Unix) to stop input.");

        Scanner scanner = new Scanner(System.in);
        CompletableFuture.runAsync(() -> {
            try {
                codeFile.execute(scanner);
                System.out.println("Process completed. Press Enter to continue...");
            } catch (Exception e) {
                System.err.println("Execution failed: " + e.getMessage());
            }
        }).join();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter file path (.cpp or .py): ");
            new FileProcessor().processFile(scanner.nextLine());
        } finally {
            scanner.close();
        }
    }
}

enum FileType {
    PYTHON, CPP, UNSUPPORTED;
    
    public static FileType fromFileName(String fileName) {
        if (fileName.toLowerCase().endsWith(".py")) return PYTHON;
        if (fileName.toLowerCase().endsWith(".cpp")) return CPP;
        return UNSUPPORTED;
    }
}