package com.newrelic.labs.java.verify;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    private static final int NTHREADS = 10;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        /*
        Create com.newrelic.labs.java.verify.QueryController and run search method to store response of Search API with keyword "newrelic-java"
         */
        QueryController query = new QueryController();
        query.search();

        //Use logging framework to log program execution/output
        System.out.println("See logger.txt for program log and report.txt for verifyInstrumentation output");
        PropertyConfigurator.configure("log4j.properties");
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Program Log\n");

        System.out.println("\nVerifying...");

        int total_count = query.getRepoCount();
        VerifyInstrumentation verify = new VerifyInstrumentation();
        Report report = new Report();
        FileWriter writer = report.generateReport();

        List<String> knownRepos;
        ProcessBuilder builder = new ProcessBuilder();
        knownRepos = handleCreateDirectories(logger, builder);

        //create fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        //create a CompletableFutures list as callback mechanism for when threads are completed
        List<CompletableFuture<Boolean>> listFutures = new ArrayList<>();


        //For each repo, run on different threads, create CompletableFuture for each Runnable
        for (int i = 0; i < total_count; ++i) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            Runnable worker = new RunVerifyProcess(query, verify, report, writer, i, future, knownRepos);
            executor.execute(worker);
            listFutures.add(future);
            //next thread starts on next repo, index i
        }

        executor.shutdown();

        //callback to check if all CompletableFutures from loop have completed
        CompletableFuture allFutures = CompletableFuture.allOf(listFutures.toArray(new CompletableFuture[listFutures.size()]));
        allFutures.get();
        report.closeFile(writer);
        logger.info("Threads terminated");
        System.out.println("\nProcess complete. Refer to report.txt for report.");

        handleDeleteDirectories(logger, builder);

        // Send report via Amazon SNS to subscriber email using java-verify topic
        SNSService snsService = new SNSService();
        snsService.SNSEmailMessage();
        // Send message to the Shutdown topic, trigger lambda to shut down EC2 instance
        snsService.SNSShutdownInstance();

    }

    public static List<String> handleCreateDirectories(Logger logger, ProcessBuilder builder) throws IOException, InterruptedException {
        File clonedRepoDir = new File("cloned-repos");
        List<String> knownRepos = new ArrayList<String>();


        if (clonedRepoDir.isDirectory()) {
            //Store known cloned repos in a list
            File dir = new File("cloned-repos");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing)
                    knownRepos.add(child.getName());
            }
        } else {
            builder.command("/bin/sh", "-c", "mkdir cloned-repos");
            Process process1 = builder.start();
            int exitCode1 = process1.waitFor();
            logger.info("\nCreated directory for cloned repos, exited with error code : {}", exitCode1);
        }

        builder.command("/bin/sh", "-c", "mkdir output-logs");
        Process process2 = builder.start();
        int exitCode2 = process2.waitFor();
        logger.info("\nCreated directory for verify output logs, exited with error code : {}", exitCode2);

        return knownRepos;
    }

    public static void handleDeleteDirectories(Logger logger, ProcessBuilder builder) throws IOException, InterruptedException {
        //delete output-logs directory to delete all command logs at once
        builder.command("/bin/sh", "-c", "rm -r output-logs");
        Process process3 = builder.start();
        int exitCode3 = process3.waitFor();
        logger.info("\nDeleted output-logs directory, exited with error code : {}", exitCode3);

    }


}
