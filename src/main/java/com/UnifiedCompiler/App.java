package com.UnifiedCompiler;

import java.io.File;

public class App {
    public static void main(String[] args) {
        // Define the input directory
        File inputDir = new File("input");

        // Check if the directory exists
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Error: 'input/' directory not found!");
            return;
        }

        // Find the first .cpp file in the directory
        File cppFile = findCppFile(inputDir);
        if (cppFile == null) {
            System.out.println("No .cpp file found in the 'input/' directory.");
            return;
        }

        // Execute the compilation and execution process
        Executor executor = new Executor();
        String result = executor.compileAndRunCpp(cppFile.getAbsolutePath());
        System.out.println(result);
    }

    // Method to find the first .cpp file in the directory
    private static File findCppFile(File directory) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".cpp"));
        return (files != null && files.length > 0) ? files[0] : null;
    }
}
