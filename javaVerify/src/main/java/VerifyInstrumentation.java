import java.io.File;
import java.io.IOException;

public class VerifyInstrumentation {

    /*
        Clone repo given by cloneUrl from query,
        Change directory and run verifyInstrumentation command,
        Store output log in separate file to parse through for violation
     */
    public static void cloneVerifyProcess(String repoName, String cloneUrl) throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Run git clone here and to clone directory locally using cloneUrl
        processBuilder.command("/bin/sh", "-c", "git clone " + cloneUrl);

        Process process1 = processBuilder.start();

        int exitCode1 = process1.waitFor();
        System.out.println("\nClone exited with error code : " + exitCode1);

        // Runs gradlew command and changes working directory into cloned repo
        processBuilder.command("/bin/sh", "-c", "./gradlew checkForDependencies");
        processBuilder.directory(new File(repoName));

        Process process2 = processBuilder.start();

        int exitCode2 = process2.waitFor();
        System.out.println("\nExited with error code : " + exitCode2);

        processBuilder.command("/bin/sh", "-c", "./gradlew verifyInstrumentation");



        // This code redirects and writes the output to a log file in this project directory
        processBuilder.redirectErrorStream(true);
        File log = new File("test-output.log");
        processBuilder.redirectOutput(log);

        Process process3 = processBuilder.start();

        // This code reads the output and prints it to the console
        /*BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        */

        int exitCode3 = process3.waitFor();
        System.out.println("\nVerify exited with error code : " + exitCode3);
    }

    /*
        Generate process to delete newly created directory from cloned repo,
        For repos that ran the verify command
        Save space for future cloned directories
     */
    public static void deleteVerifiedRepo(String repoName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        //change working directory
        processBuilder.command("/bin/sh", "-c", "cd ..");
        Process process1 = processBuilder.start();

        process1.waitFor();
        //printing and storing of exitCode may not be needed

        //delete repo's local directory with remove command
        processBuilder.command("/bin/sh", "-c", "rm -r " + repoName);

        Process process2 = processBuilder.start();

        int exitCode = process2.waitFor();
        System.out.println("\nDeletion exited with error code : " + exitCode);

    }

    /*
        Generate process to delete newly created directory from cloned repo,
        For repos that need to be skipped or cannot run verify command
     */
    public static void deleteRepo(String repoName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/sh", "-c", "rm -r " + repoName);
        Process process = processBuilder.start();
        process.waitFor();
    }


}
