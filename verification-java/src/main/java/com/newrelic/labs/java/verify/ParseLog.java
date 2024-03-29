package com.newrelic.labs.java.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ParseLog {

    /*
    Parse output.log file generated from verifyInstrumentation,
    Return if build successful or failed
    True: success, false: failed
     */
    public static boolean parseForBuild(int index) throws FileNotFoundException {
        Scanner s = new Scanner(new File("output-logs/command-output" + index + ".log"));
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
    public static String parseForViolation(int index) throws FileNotFoundException {
        Scanner s = new Scanner(new File("output-logs/command-output" + index + ".log"));
        String fullViolation = "";
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.startsWith("Execution failed") || line.startsWith("> A failure")) {
                fullViolation = fullViolation + line + '\n';
            }
            if (line.contains("Verification FAILED")) {
                fullViolation = fullViolation + line + "\n-----------\n";
            }
        }
        return fullViolation;
    }

    /*
    Delete log file after it has been parsed, if successful and no fails
    */
    public static void deleteParsedLog(int index) throws IOException, InterruptedException {
    	
    	File logDir = new File("output-logs");
    	
    	File logFile = new File(logDir,"command-output" + index + ".log");
    	logFile.delete();
//        ProcessBuilder processBuilder = new ProcessBuilder();
//        processBuilder.command("/bin/sh", "-c", "rm command-output" + index + ".log");
//        processBuilder.directory(new File("output-logs"));
//        Process process = processBuilder.start();
//        process.waitFor();
    }


}
