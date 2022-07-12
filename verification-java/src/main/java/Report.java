import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Report {
    public FileWriter generateReport() throws IOException {
        File report = new File("report.txt");
        if (report.createNewFile()) {
            System.out.println("File created: " + report.getName());
        }
        FileWriter fileWriter = new FileWriter("report.txt");
        return fileWriter;
    }

    public void writeToReport(String repoName, String violation, FileWriter writer) throws IOException {
        writer.write(repoName + '\n');
        writer.write(violation + "\n\n");
        writer.close();
    }

    public void closeFile(FileWriter writer) throws IOException {
        writer.close();
    }
}
