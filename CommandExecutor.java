import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandExecutor {
    private static final int DEFAULT_TIMEOUT = 10;

    public String executeCommand(String command) throws Exception {
        return executeCommand(command, DEFAULT_TIMEOUT);
    }

    public String executeCommand(String command, int timeoutSeconds) throws Exception {
        Process process = createProcess(command);
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        Thread outputThread = readStream(process.getInputStream(), output);
        Thread errorThread = readStream(process.getErrorStream(), error);

        outputThread.start();
        errorThread.start();

        if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new Exception("Command timed out after " + timeoutSeconds + " seconds");
        }

        outputThread.join();
        errorThread.join();

        String errors = error.toString().trim();
        if (!errors.isEmpty()) {
            throw new Exception(errors);
        }

        if (process.exitValue() != 0) {
            throw new Exception("Process failed with exit code " + process.exitValue());
        }

        return output.toString().trim();
    }

    public String executeCommandInteractive(String command, Scanner scanner, int timeoutSeconds) throws Exception {
        Process process = createProcess(command);
        AtomicBoolean hasErrors = new AtomicBoolean(false);
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();

       
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                if (!isPipeError(e)) {
                    errorOutput.append("Output Error: ").append(e.getMessage());
                    hasErrors.set(true);
                }
            }
        });

    
        Thread errorThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("PROGRAM ERROR: " + line);
                    errorOutput.append(line).append("\n");
                    hasErrors.set(true);
                }
            } catch (Exception e) {
                if (!isPipeError(e)) {
                    errorOutput.append("Error Stream Error: ").append(e.getMessage());
                    hasErrors.set(true);
                }
            }
        });

        outputThread.start();
        errorThread.start();

        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(process.getOutputStream()))) {
            while (process.isAlive()) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equals("\u0004") || input.equals("\u001A")) break;
                    writer.write(input);
                    writer.newLine();
                    writer.flush();
                } else break;
            }
        } catch (Exception e) {
            if (!isPipeError(e)) {
                errorOutput.append("Input Error: ").append(e.getMessage());
                hasErrors.set(true);
            }
        }

        if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new Exception("Execution timed out after " + timeoutSeconds + " seconds");
        }

        outputThread.join(2000);
        errorThread.join(2000);

        if (hasErrors.get()) {
            throw new Exception(errorOutput.toString().trim());
        }

        if (process.exitValue() != 0) {
            throw new Exception("Process exited with code " + process.exitValue());
        }

        return output.toString();
    }

    private Process createProcess(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("/bin/sh", "-c", command);
        }
        return builder.start();
    }

    private Thread readStream(InputStream stream, StringBuilder output) {
        return new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                output.append("Stream Error: ").append(e.getMessage());
            }
        });
    }

    private boolean isPipeError(Exception e) {
        return e.getMessage() != null && 
               e.getMessage().toLowerCase().contains("pipe");
    }
}
