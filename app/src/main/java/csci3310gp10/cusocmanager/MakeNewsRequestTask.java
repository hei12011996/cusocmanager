package csci3310gp10.cusocmanager;

/**
 * Created by KaHei on 18/12/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeNewsRequestTask extends AsyncTask<Void, Void, ArrayList<News>> {
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    private String command = "getAll";
    private String range = "News_List";
    public RequestTaskResult<ArrayList<News>> newsListResult = null;

    MakeNewsRequestTask(Context context, String command, String range) {
        this.context = context;
        this.command = command;
        this.range = range;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, null)
                .setApplicationName("Cu Soc Manager")
                .build();
    }

    /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
    @Override
    protected ArrayList<News> doInBackground(Void... params) {
        try {
            switch(command){
                case "getAll":
                    return getFullNewsListFromAPI();
                default:
                    return getFullNewsListFromAPI();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * @return List of members
         * @throws IOException
         */
    private ArrayList<News> getFullNewsListFromAPI() throws IOException {
        String member_list_sheet_id = context.getString(R.string.news_list_sheet_id);
        ArrayList<News> results = new ArrayList<News>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(member_list_sheet_id, range)
                .setKey(context.getString(R.string.google_sheet_API_key))
                .execute();
        List<List<Object>> values = response.getValues();
        Integer i = 0;
        if (values != null) {
            for (List row : values.subList(1, values.size())) {
                i++;
                String title = String.valueOf(row.get(0));
                String description = String.valueOf(row.get(1));
                String image_url = String.valueOf(row.get(2));
                String timestamp = String.valueOf(row.get(3));
                News news = new News(i, title, description, image_url, timestamp);
                results.add(news);
            }
        }
        return results;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<News> output) {
        this.newsListResult.taskFinish(output);
        if (output == null || output.size() == 0) {
            Toast.makeText(context, "No results returned.", Toast.LENGTH_SHORT).show();
//            System.out.println("No results returned.");
        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            Toast.makeText(context, "The following error occurred:\n" + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
//            System.out.println("The following error occurred:\n" + mLastError.getMessage());
        }
        else {
            Toast.makeText(context, "Request cancelled.", Toast.LENGTH_SHORT).show();
//            System.out.println("Request cancelled.");
        }
    }
}