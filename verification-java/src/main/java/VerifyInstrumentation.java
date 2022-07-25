import com.newrelic.agent.deps.com.google.common.base.Verify;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class VerifyInstrumentation {

    /*
    Clone repo given by cloneUrl from query,
    Change directory and run verifyInstrumentation command,
    Store output log in separate file to parse through for violation
     */
    public void cloneVerifyProcess(String repoName, String cloneUrl, int index) throws InterruptedException, IOException {
        PropertyConfigurator.configure("log4j.properties");
        Logger logger = LoggerFactory.getLogger(VerifyInstrumentation.class);

        ProcessBuilder initBuilder = new ProcessBuilder();

        ProcessBuilder processBuilder = new ProcessBuilder();

        // Run git clone here and to clone directory locally into cloned-repos directory
        initBuilder.command("/bin/sh", "-c", "git clone " + cloneUrl);
        initBuilder.directory(new File("cloned-repos"));
        Process process1 = initBuilder.start();
        int exitCode1 = process1.waitFor();
        logger.info("\n{} Clone exited with error code : {}", repoName, exitCode1);

        // Runs gradlew command and changes working directory into specified cloned repo
        processBuilder.command("/bin/sh", "-c", "./gradlew checkForDependencies");
        processBuilder.directory(new File("cloned-repos/" + repoName));

        Process process2 = processBuilder.start();

        int exitCode2 = process2.waitFor();
        logger.info("\n{} Check exited with error code : {}", repoName, exitCode2);

        processBuilder.command("/bin/sh", "-c", "./gradlew verifyInstrumentation");



        // This code redirects and writes the output to a log file in this project directory
        // different log file for each repo because parallel thread processes
        processBuilder.redirectErrorStream(true);
        File log = new File("command-output" + index + ".log");
        processBuilder.redirectOutput(log);

        Process process3 = processBuilder.start();


        int exitCode3 = process3.waitFor();
        logger.info("\n{} Verify exited with error code : {}", repoName, exitCode3);
    }


    /*
    Generate process to delete newly created directory from cloned repo,
    For repos that need to be skipped, cannot run verify command, or successful build
     */
    public void deleteRepo(String repoName) throws IOException, InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        Logger logger = LoggerFactory.getLogger(VerifyInstrumentation.class);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/sh", "-c", "rm -r " + repoName);
        processBuilder.directory(new File("cloned-repos"));
        Process process = processBuilder.start();
        process.waitFor();
        logger.info("Skipped or successful verify for {}", repoName);
    }


}
