package com.newrelic.labs.java.verify;

public class Stats {

	private static int numberOfRepos;
	private static int reposSubmitted = 0;
	private static int reposProcessed = 0;
	private static int reposBeingProcessed = 0;
	
	public static void setNumberOfRepos(int count) {
		numberOfRepos = count;
	}
	
	public static int getNumberOfRepos() {
		return numberOfRepos;
	}
	
	public static void incrementSubmitted() {
		reposSubmitted++;
	}
	
	public static void incrementProcessed() {
		reposProcessed++;
	}
	
	public static int getSubmitted() {
		return reposSubmitted;
	}
	
	public static int getProcessed() {
		return reposProcessed;
	}
	
	public static int getRemaining() {
		return numberOfRepos - reposProcessed;
	}
	
	public static void incrementStarted() {
		reposBeingProcessed++;
	}
	
	public static int getRunning() {
		return reposBeingProcessed;
	}
}
