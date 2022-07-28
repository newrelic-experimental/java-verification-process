package com.newrelic.labs.java.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class RunVerifyProcess implements Runnable {
    /*
    Run project's processes on different threads to maximize efficiency
     */
    private QueryController query;
    private VerifyInstrumentation verify;
    private int startIndex;
    private Report report;
    private FileWriter writer;
    private CompletableFuture<Boolean> future;

    public RunVerifyProcess(QueryController query, VerifyInstrumentation verify, Report report,
                            FileWriter writer, int startIndex, CompletableFuture<Boolean> future) {
        this.query = query;
        this.verify = verify;
        this.startIndex = startIndex;
        this.report = report;
        this.writer = writer;
        this.future = future;
    }

    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(RunVerifyProcess.class);

        //read in config file to skip over specified repos
        String reposToSkip;
        try {
            FileReader reader = new FileReader("config.properties");
            Properties p = new Properties();
            p.load(reader);
            reposToSkip = p.getProperty("REPOS");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //clone and verify repo for this thread process
        for (int i = startIndex; i < startIndex + 1; ++i) { // can change to execute more repos on one thread
            String name = query.getRepoName(i);
            logger.info("Verifying {} ...", name);

            // repos to skip, do not have verify task in build.gradle
            if (reposToSkip.contains(name)) {
                try {
                    verify.deleteRepo(name);
                    logger.info("Skipped verify for {}", name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            //use cloneUrl to clone and run verify command in repo
            String cloneUrl = query.getCloneUrl(i);
            int checkClone;
            try {
                checkClone = verify.cloneVerifyProcess(name, cloneUrl, i);
                if (checkClone == 1) {
                    logger.info("Unable to clone or build project {}", name);
                    continue;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Parse through logged output - collect violation information
            ParseLog parse = new ParseLog();

            //skip if verifyInstrumentation command is successful, no violations
            try {
                if (parse.parseForBuild(i)) {
                    verify.deleteRepo(name);
                    parse.deleteParsedLog(i);
                    logger.info("Successful verify for {}", name);
                    continue;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //parse output log for violations, store in violationResult
            String violationResult;
            try {
                violationResult = parse.parseForViolation(i);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            //delete output log from this repo
            try {
                parse.deleteParsedLog(i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //Add violationResult to the report, include name of repo and violations
            try {
                report.writeToReport(name, violationResult, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //update CompletableFuture parameter
        future.complete(true);
    }

}