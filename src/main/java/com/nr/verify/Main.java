package com.nr.verify;

import java.io.IOException;

public class Main {
        public static void main(String[] args) throws IOException {
        /*
        Create com.nr.QueryController and run search method to store response of Search API with keyword "newrelic-java";
        For each repo in response
            Parse through to find clone url
                ex: in response body, find "clone url"
            Create verifyInstrumentation object given repo
            Generate report
         */
            QueryController query = new QueryController();
            query.search();

            /*
            while input stream still has an object (repo), for each repo:
                String name = query.getRepoName(i);
                String cloneUrl = query.getCloneUrl(i);
                Run VerifyInstrumentation cloneVerifyProcess for this repo using cloneUrl
                Get log output
                If violation
                    Collect to store in generated report
                Delete cloned repo by running VerifyInstrumentation deleteRepo
                    Save space
             */

        }
}
