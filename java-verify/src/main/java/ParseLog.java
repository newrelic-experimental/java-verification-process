import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParseLog {

    /*
    Parse output.log file generated from verifyInstrumentation,
    Return if build successful or failed
    True: success, false: failed
     */
    public static boolean parseForBuild() throws FileNotFoundException {
        Scanner s = new Scanner(new File("output.log"));
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.startsWith("BUILD SUCCESSFUL")) {
                s.close();
                return true;
            } else if (line.startsWith("FAILURE")) {
                s.close();
                return false;
            }
        }
        s.close();
        return false;
    }

    /*
    Return violation lines from failed verifyInstrumentation command,
    Parse through output.log for failure exceptions
     */
    public static String parseForViolation() {
        Scanner s = new Scanner(new File("output.log"));
        String fullViolation = "";
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.startsWith("Execution failed") || line.startsWith("> A failure") || ) {
                fullViolation = fullViolation + line + '\n';
            }
            if (line.contains("Verification FAILED")) {
                fullViolation = fullViolation + line + "\n\n";
            }
        }
        return fullViolation;
    }


}
