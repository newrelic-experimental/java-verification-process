import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    private static final int NTHREADS = 10;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        /*
        Create QueryController and run search method to store response of Search API with keyword "newrelic-java"
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

        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        //create a CompletableFutures list as callback mechanism for when threads are completed
        List<CompletableFuture<Boolean>> listFutures = new ArrayList<>();
        int newStart = 0;
        //For each repo, run on different threads, create CompletableFuture for each Runnable
        for (int i = 0; i < total_count; ++i) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            Runnable worker = new MultiThread(query, verify, report, writer, newStart, future);
            executor.execute(worker);
            listFutures.add(future);
            newStart++; //next thread starts on next repo
        }
        executor.shutdown();

        CompletableFuture allFutures = CompletableFuture.allOf(listFutures.toArray(new CompletableFuture[listFutures.size()]));
        allFutures.get();
        report.closeFile(writer);
        logger.info("Threads terminated");
        System.out.println("\nProcess complete. Clear out logger.txt before running again");

    }
}
