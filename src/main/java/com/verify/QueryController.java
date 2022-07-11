package com.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueryController {
    private static JSONArray responseItems;
    private static JSONObject fullResponse;

    /*
        Search for repos within GitHub with name "newrelic-java",
        Read Input Stream into StringBuilder,
        Convert to JSON and return repos in JSONArray of items
     */
    public static void search() throws IOException {

        // Search using HttpUrlConnection and GitHub API with query parameters
        URL url = new URL("https://api.github.com/search/repositories?&per_page=100&sort=updated&q=newrelic-experimental/newrelic-java");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "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");
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
            fullResponse = responseJSONObj;
            //System.out.println(obj);

            //Get items, each repo JSONObject in JSONArray 'items'
            JSONArray items = responseJSONObj.getJSONArray("items");
            responseItems = items;

            //return items;
            //System.out.println(items);

        } else {
            System.out.println("GET request failed");
            //return new JSONArray("Get failed");
        }
    }

    /*
        Parse JSONArray of response items,
        return name of repo associated with object at index
     */
    public static String getRepoName(int index) {
        JSONObject repo = responseItems.getJSONObject(index); //get object at specified index (repo at this index)
        return repo.getString("name"); //get string associated with key value 'name'
        //return "test";
    }

    /*
        Parse JSONArray of response items,
        return clone_url of repo associated with object at index
     */
    public static String getCloneUrl(int index) {
        JSONObject repo = responseItems.getJSONObject(index); //get object at specified index (repo at this index)
        return repo.getString("clone_url"); //get string associated with key value 'clone_url'
        //return "test";
    }

    public static int getRepoCount() {
        return fullResponse.getInt("total_count"); //get total count of repos from the query
        //return 0;
    }
}
