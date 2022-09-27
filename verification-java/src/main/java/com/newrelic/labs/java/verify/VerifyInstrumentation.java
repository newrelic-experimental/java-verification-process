package com.newrelic.labs.java.verify;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
//import org.eclipse.jgit.api.errors.CanceledException;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.api.errors.InvalidConfigurationException;
//import org.eclipse.jgit.api.errors.InvalidRemoteException;
//import org.eclipse.jgit.api.errors.NoHeadException;
//import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
//import org.eclipse.jgit.api.errors.RefNotFoundException;
//import org.eclipse.jgit.api.errors.TransportException;
//import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
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

		File clonedDir = new File("cloned-repos");
		File localPath = new File(clonedDir,repoName);
		if (knownRepos.contains(repoName)) {
			try {
				Git result = Git.open(localPath);
				PullResult pullResult = result.pull().call();
				logger.info("Result of pull for "+repoName+" was "+pullResult);
			} catch (Exception e) {
				logger.error("Failed to pull repo "+repoName, e);
				// Exit process if clone or pull fails
				return 1;
			}

		} else {
			try {
				Git result = Git.cloneRepository().setURI(cloneUrl).setDirectory(localPath).call();
				logger.info("Result of clone for repo "+repoName+" is "+result);
			} catch (Exception e) {
				logger.error("Failed to clone repo "+repoName, e);
				// Exit process if clone or pull fails
				return 1;
			}
		}

		// Runs gradlew command and changes working directory into specified cloned repo 
		ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(localPath).connect();

		try {
			BuildLauncher build = connection.newBuild();
			build.forTasks("checkForDependencies","verifyInstrumentation");
			File output = new File("output-logs/command-output" + index + ".log");
			FileOutputStream fos = new FileOutputStream(output);
			build.setStandardOutput(fos);
			build.setStandardError(fos);
			ResultHandler<Void> handler = new ResultHandler<Void>() {

				@Override
				public void onFailure(GradleConnectionException failure) {
					logger.error("Gradle Tasks for repo "+repoName+" failed", failure);
				}

				@Override
				public void onComplete(Void result) {
					logger.info("Repo "+ repoName + " finished processing");
				}
			};
			build.run(handler);
		} finally {
			connection.close();
		}

		return 0;
	}


	/*
    Generate process to delete newly created directory from cloned repo,
    For repos that need to be skipped, cannot run verify command
	 */
	public void deleteRepo(String repoName) throws IOException, InterruptedException {
		
		File repoDir = new File(new File("cloned-repos"), repoName);
		FileUtils.deleteDirectory(repoDir);

	}


}
