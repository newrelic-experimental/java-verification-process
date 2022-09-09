package com.newrelic.labs.java.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VerifyInstrumentation {

    /*
    Clone repo given by cloneUrl from query,
    Change directory and run verifyInstrumentation command,
    Store output log in separate file to parse through for violation
     */
    public int cloneVerifyProcess(String repoName, String cloneUrl, int index, List<String> knownRepos) throws InterruptedException, IOException {
        Logger logger = LoggerFactory.getLogger(VerifyInstrumentation.class);

        ProcessBuilder initBuilder1 = new ProcessBuilder();
        ProcessBuilder initBuilder2 = new ProcessBuilder();

        ProcessBuilder processBuilder = new ProcessBuilder();

        /* check if already cloned, then do git pull, otherwise continue with clone process */
        Process process1;
        int exitCode1;
        if (knownRepos.contains(repoName)) {
            // run git pull for known cloned repo
            initBuilder1.command("/bin/sh", "-c", "git pull");
            initBuilder1.directory(new File("cloned-repos/" + repoName));
            process1 = initBuilder1.start();
            exitCode1 = process1.waitFor();
            logger.info("\n{} Pull exited with error code : {}", repoName, exitCode1);
        } else {
            // Run git clone here and to clone directory locally into cloned-repos directory
            initBuilder2.command("/bin/sh", "-c", "git clone " + cloneUrl);
            initBuilder2.directory(new File("cloned-repos"));
            process1 = initBuilder2.start();
            exitCode1 = process1.waitFor();
            logger.info("\n{} Clone exited with error code : {}", repoName, exitCode1);

        }

        // Exit process if clone or pull fails
        if (exitCode1 != 0)
            return 1;

        // Runs gradlew command and changes working directory into specified cloned repo
        processBuilder.command("/bin/sh", "-c", "./gradlew checkForDependencies");
        processBuilder.directory(new File("cloned-repos/" + repoName));

        Process process2 = processBuilder.start();

        int exitCode2 = process2.waitFor();
        logger.info("\n{} Check exited with error code : {}", repoName, exitCode2);

        processBuilder.command("/bin/sh", "-c", "./gradlew verifyInstrumentation");



        // This code redirects and writes the output to a log file in this project directory;
        // different log file for each repo because parallel thread processes
        processBuilder.redirectErrorStream(true);
        File log = new File("output-logs/command-output" + index + ".log");
        processBuilder.redirectOutput(log);

        Process process3 = processBuilder.start();
        int exitCode3 = process3.waitFor();
        logger.info("\n{} Verify exited with error code : {}", repoName, exitCode3);

        return 0;
    }


    /*
    Generate process to delete newly created directory from cloned repo,
    For repos that need to be skipped, cannot run verify command
     */
    public void deleteRepo(String repoName) throws IOException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(VerifyInstrumentation.class);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/sh", "-c", "rm -r " + repoName);
        processBuilder.directory(new File("cloned-repos"));
        Process process = processBuilder.start();
        process.waitFor();
    }


}
