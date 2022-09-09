package com.newrelic.labs.java.verify;

/*
In main program, when repackaging the verification-java code for the script,
make sure to include the new dependencies in the script to download jar files!! IMPORTANT!!
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class SNSService {
    private BasicAWSCredentials credentials;

    public SNSService() throws IOException {
        this.credentials = getConfigCredentials();
    }

    public void SNSEmailMessage() throws IOException {
        Logger logger = LoggerFactory.getLogger(SNSService.class);
        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion("us-west-2")
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        String msg = scanReportToString();
        PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-west-2:830139413159:java-verify", msg);
        PublishResult publishResponse = snsClient.publish(publishRequest);

        logger.info("Published Email topic");
    }

    public void SNSShutdownInstance() {
        Logger logger = LoggerFactory.getLogger(SNSService.class);
        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion("us-west-2")
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        String msg = "Shutdown";
        PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-west-2:830139413159:java-verify-shutdown", msg);
        PublishResult publishResponse = snsClient.publish(publishRequest);
        logger.info("Published Shutdown topic");
    }

    public BasicAWSCredentials getConfigCredentials() throws IOException {
        FileReader reader = new FileReader("aws-config.properties");
        Properties p = new Properties();
        p.load(reader);
        String accessKey = p.getProperty("ACCESS_KEY_ID");
        String secretKey = p.getProperty("SECRET_ACCESS_KEY");
        return new BasicAWSCredentials(accessKey,secretKey);
    }

    public String scanReportToString() throws FileNotFoundException {
        Scanner s = new Scanner(new File("report.txt"));
        String report = "Java Verify Report" + "\n";
        while (s.hasNextLine()) {
            report = report + "\n" + s.nextLine();
        }
        s.close();
        return report;
    }
}
