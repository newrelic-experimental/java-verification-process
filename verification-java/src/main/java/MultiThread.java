public class MultiThread extends Thread {
    /*
    Run project's processes on different threads to maximize efficiency
     */
    @Override
    public void run() {
        //clone and verify of all repos
    }

    //Framework code for multi-threading
    /*
     ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        int newStart = 0;
        for (int i = 0; i < 5; ++i) {
            Runnable worker = new testMultiThread(newStart, 5);
            executor.execute(worker);
            newStart+=5;
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("Threads terminated.");
     */
}
