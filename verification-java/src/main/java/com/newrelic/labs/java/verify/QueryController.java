package com.newrelic.labs.java.verify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueryController {
    private JSONArray responseItems;
    private JSONObject fullResponse;

    /*
    Search for repos within GitHub with name "newrelic-java",
    Read Input Stream into StringBuilder,
    Convert to JSON and return repos in JSONArray of items
     */
    public void search() throws IOException {

        // Search using HttpUrlConnection and GitHub API with query parameters
//        URL url = new URL("https://api.github.com/search/repositories?q=newrelic-java+org:newrelic-experimental+language:java&per_page=100&sort=updated");
        URL url = new URL("https://api.github.com/search/repositories?q=org:newrelic-experimental+org:newrelic+topic:nrlabs-java-verify+language:java&per_page=100&sort=updated");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // Add authorization credentials if access needed for private repos
        //connection.setRequestProperty("Authorization", "");
        int responseCode = connection.getResponseCode();
        System.out.println("GET response code: " + responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Convert to response from StringBuilder to JSON
            JSONObject responseJSONObj = new JSONObject(response.toString());
            fullResponse = responseJSONObj;;

            //Get items, each repo JSONObject in JSONArray 'items'
            JSONArray items = responseJSONObj.getJSONArray("items");
            responseItems = items;


        } else {
            System.out.println("GET request failed");
        }
    }

    /*
    Parse JSONArray of response items,
    return name of repo associated with object at index
     */
    public String getRepoName(int index) {
        JSONObject repo = responseItems.getJSONObject(index); //get object at specified index (repo at this index)
        return repo.getString("name"); //get string associated with key value 'name'
    }

    /*
    Parse JSONArray of response items,
    return clone_url of repo associated with object at index
     */
    public String getCloneUrl(int index) {
        JSONObject repo = responseItems.getJSONObject(index); //get object at specified index (repo at this index)
        return repo.getString("clone_url"); //get string associated with key value 'clone_url'
    }

    /*
    Get total count of repos in query from JSON response key "total_count"
     */
    public int getRepoCount() {
        return fullResponse.getInt("total_count"); //get total count of repos from the query
    }
}
