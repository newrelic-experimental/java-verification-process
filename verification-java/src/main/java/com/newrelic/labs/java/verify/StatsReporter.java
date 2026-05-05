package com.newrelic.labs.java.verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsReporter implements Runnable {
	Logger logger = LoggerFactory.getLogger(StatsReporter.class);

	@Override
	public void run() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Current Proccessing Stats: ");
		
		sb.append("Submitted Repos = "+Stats.getSubmitted() + ", ");
		sb.append("Processed Repos = "+Stats.getProcessed() + ", ");
		sb.append("Repos Being Processed = "+Stats.getRunning() + ", ");
		sb.append("Repos Not Finished = "+Stats.getRemaining());
		sb.append('\n');
		
		logger.info(sb.toString());
	}

}
