[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)


![GitHub forks](https://img.shields.io/github/forks/newrelic-experimental/java-instrumentation-template?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic-experimental/java-instrumentation-template?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic-experimental/java-instrumentation-template?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic-experimental/java-instrumentation-template/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic-experimental/java-instrumentation-template)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic-experimental/java-instrumentation-template)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic-experimental/java-instrumentation-template)


![GitHub issues](https://img.shields.io/github/issues/newrelic-experimental/java-instrumentation-template)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic-experimental/java-instrumentation-template)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic-experimental/java-instrumentation-template)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic-experimental/java-instrumentation-template)


# New Relic Java Instrumentation Verify

> Automated process to run Gradle task verifyInstrumentation on Java instrumentation in Experimental. Generates a report when verify fails for new versions of a framework.

  
## Installation

> To install and run:
> 1. Clone this repo to your local machine
> 2. Run project

## Getting Started

> Once the project is cloned and running, refer to **logger.txt** for an active program log. Once the program has finished, refer to **report.txt** for a report of instrumentations that failed verify.
> 
> Note that report.txt lists the repo name without any other information if a project has build errors or ran the Gradle tasks checkForDependencies or verifyInstrumentation with errors.

## Usage

> This program executes the Gradle task verifyInstrumentation on all repos in Experimental under "newrelic-java". 
>
> Some repos under this query do not have the Gradle task verifyInstrumentation. Modify **config.properties** to specify which repos should be skipped by the program. Use the format shown in the file.

## Building

>Requires a version of **Java 8 or higher** to be installed before running.

## Testing

>No testing currently available.

## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

>We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.


## Contributing

We encourage your contributions to improve New Relic Java Instrumentation Verify! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project. If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company, please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

New Relic Java Instrumentation Verify is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

>[If applicable: [Project Name] also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.]
