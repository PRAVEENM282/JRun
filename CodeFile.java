import java.io.File;
import java.util.Scanner;

public abstract class CodeFile {
    protected final String filePath;
    protected final CommandExecutor executor;

    public CodeFile(String filePath, CommandExecutor executor) {
        this.filePath = filePath;
        this.executor = executor;
    }

    public abstract void execute(Scanner scanner) throws Exception;

    public boolean exists() {
        return new File(filePath).exists();
    }
}
