# UnifiedCompiler

UnifiedCompiler is a lightweight tool designed for competitive programmers to compile, execute, test, and debug C++ code efficiently. It supports automatic test case handling and AI-powered error analysis.

## Features
- Compile and execute single-file C++ programs.
- Automatically fetch input from `input.txt` and store output in `output.txt`.
- Save compiled executables in the `output/` folder.
- AI-powered debugging and error analysis.
- Modularized code for better handling.

## Prerequisites
- Java 8 or higher
- g++ (GCC Compiler)
- Maven (for dependency management)

## How to Run
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/UnifiedCompiler.git
   cd UnifiedCompiler
   ```

2. Compile the Java files:
   ```sh
   javac -d bin src/main/java/com/UnifiedCompiler/*.java
   ```

3. Run the application:
   ```sh
   java -cp bin com.UnifiedCompiler.App input/sample.cpp
   ```

4. To provide custom input:
   - Place the input data in `buffer/input.txt`.
   - The program will read from `buffer/input.txt` and generate `buffer/output.txt`.


## Contributing
Pull requests are welcome! Feel free to contribute by improving features, fixing bugs, or adding new functionalities.

## License
This project is licensed under the MIT License.

