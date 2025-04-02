package com.UnifiedCompiler;

import java.io.*;
import java.util.Scanner;

public class BufferHandler {

    private static final String BUFFER_DIR = "buffers";
    private static final String INPUT_FILE = BUFFER_DIR + File.separator + "input.txt";

    // Ensures that the buffers directory exists
    private static void ensureBufferDir() {
        File dir = new File(BUFFER_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Reads input from file if available; otherwise, prompts the user and saves it
    public static String getInput() {
        ensureBufferDir();
        File inputFile = new File(INPUT_FILE);
        String inputContent = "";

        // Read from file if exists and not empty
        if (inputFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                inputContent = sb.toString().trim();
            } catch (IOException e) {
                System.out.println("Error reading input file: " + e.getMessage());
            }
        }

        // If file is empty, prompt user for input
        if (inputContent.isEmpty()) {
            System.out.println("No input found in " + INPUT_FILE + ". Please type input for the C++ program (finish with an empty line):");
            Scanner scanner = new Scanner(System.in);
            StringBuilder userInput = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                userInput.append(line).append(System.lineSeparator());
            }
            inputContent = userInput.toString().trim();
            // Save the user input to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
                writer.write(inputContent);
            } catch (IOException e) {
                System.out.println("Error writing input to file: " + e.getMessage());
            }
        }
        return inputContent;
    }
}
