public class OutputHandler {
    public void displayOutput(String output) {
        if (output == null || output.isEmpty()) {
            System.out.println("No output generated.");
        } else {
            System.out.println("Output:\n" + output);
        }
    }

    public void displayError(String error) {
       
        System.err.println("              ERROR                    ");
        
        System.err.println(error);
       
    }
}