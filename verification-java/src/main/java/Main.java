import java.io.FileWriter;
import java.io.IOException;

public class Main {
        public static void main(String[] args) throws IOException, InterruptedException {
        /*
        Create QueryController and run search method to store response of Search API with keyword "newrelic-java";
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
            int total_count = query.getRepoCount();
            String name, cloneUrl;
            VerifyInstrumentation verify = new VerifyInstrumentation();
            Report report = new Report();
            FileWriter writer = report.generateReport();

            for (int i = 0; i < total_count; ++i) {
                name = query.getRepoName(i);
                System.out.println("Verifying " + name + "...");

                // repos to skip for testing purposes only
                if (name.contains("mule") || name.contains("tibco") || name.contains("hystrix") || name.contains("http4s") ||
                        name.contains("micronaut-http")) {
                    verify.deleteRepo(name);
                    continue;
                }

                cloneUrl = query.getCloneUrl(i);

                verify.cloneVerifyProcess(name, cloneUrl);

                // Parse through logged output - collect violation information
                ParseLog parse = new ParseLog();

                if (parse.parseForBuild()) { //skip when build is successful, no violations
                    verify.deleteVerifiedRepo(name);
                    continue;
                }
                String violationResult = parse.parseForViolation();
                //Add violationResult to the report, include name of repo and violations
                report.writeToReport(name, violationResult, writer);
                verify.deleteVerifiedRepo(name); //delete cloned directory locally
            }
            report.closeFile(writer);

        }
}
