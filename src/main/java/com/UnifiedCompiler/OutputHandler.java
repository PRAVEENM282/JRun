package com.UnifiedCompiler;

import java.io.*;

public class OutputHandler {

    private static final String BUFFER_DIR = "buffers";
    private static final String OUTPUT_FILE = BUFFER_DIR + File.separator + "output.txt";

    // Ensures that the buffers directory exists
    private static void ensureBufferDir() {
        File dir = new File(BUFFER_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Saves the given output to the output file
    public static void saveOutput(String output) {
        ensureBufferDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writer.write(output);
        } catch (IOException e) {
            System.out.println("Error writing output to file: " + e.getMessage());
        }
    }
}
