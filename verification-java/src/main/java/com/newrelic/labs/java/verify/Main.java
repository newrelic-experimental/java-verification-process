package com.newrelic.labs.java.verify;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    	
    	long start = System.currentTimeMillis();
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
        knownRepos = handleCreateDirectories(logger);

        //get configured number of threads to run
        FileReader reader = new FileReader("config.properties");
        Properties p = new Properties();
        p.load(reader);
        int n_threads = Integer.parseInt(p.getProperty("NTHREADS"));

        //create fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(n_threads);
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

        //callback to check if all CompletableFutures from loop have completed
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(listFutures.toArray(new CompletableFuture[listFutures.size()]));
        allFutures.get();
        report.closeFile(writer);
        logger.info("Threads terminated");
        System.out.println("\nProcess complete. Refer to report.txt for report.");

        handleDeleteDirectories(logger);
        
        // cleanup
        executor.shutdown();
        
        long end = System.currentTimeMillis();
        
        logger.info("Verify process took "+ TimeUnit.SECONDS.convert(end-start, TimeUnit.MILLISECONDS)+" seconds to complete");

//        try {
//			// Send report via Amazon SNS to subscriber email using java-verify topic
//			SNSService snsService = new SNSService();
//			snsService.SNSEmailMessage();
//			// Send message to the Shutdown topic, trigger lambda to shut down EC2 instance
//			snsService.SNSShutdownInstance();
//		} catch (IOException e) {
//			logger.error("SNS operations failed due to error", e);
//		}

        System.exit(0);
    }

    public static List<String> handleCreateDirectories(Logger logger) throws IOException, InterruptedException {
        File clonedRepoDir = new File("cloned-repos");
        List<String> knownRepos = new ArrayList<String>();


        if (clonedRepoDir.exists() && clonedRepoDir.isDirectory()) {
            //Store known cloned repos in a list
            File dir = new File("cloned-repos");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing)
                    knownRepos.add(child.getName());
            }
        } else {
        	boolean created = clonedRepoDir.mkdirs();
        	if(created) {
        		logger.info("\nCreated directory for cloned repos");
        	} else {
        		logger.info("\nFailed to create directory for cloned repos");
        	}
        }

        File outputDir = new File("output-logs");
        if(!outputDir.exists()) {
        	boolean created = outputDir.mkdirs();
        	if(created) {
                logger.info("\nCreated directory for verify output logs");
        	} else {
                logger.info("\nFailed to create directory for verify output logs");
        	}
        }

        return knownRepos;
    }

    public static void handleDeleteDirectories(Logger logger) throws IOException, InterruptedException {
    	
    	File dir = new File("output-logs");
    	FileUtils.deleteDirectory(dir);
        logger.info("\nDeleted output-logs directory");

    }


}
