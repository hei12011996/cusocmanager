package csci3310gp10.cusocmanager;

/**
 * Created by KaHei on 18/12/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SortRangeRequest;
import com.google.api.services.sheets.v4.model.SortSpec;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeNewsRequestTask extends AsyncTask<Void, Void, ArrayList<News>> {
    private static final String CLIENT_ID = "cu-soc-manager@excellent-tide-188914.iam.gserviceaccount.com";
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    private News news = null;
    private String spreadsheetId = "";
    private String command = "getAll";
    private String range = "News_List";
    private Integer affectedRow = 0;
    public RequestTaskResult<ArrayList<News>> newsListResult = null;

    MakeNewsRequestTask(Context context, String command, String range) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.spreadsheetId = context.getString(R.string.news_list_sheet_id);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, null)
                .setApplicationName("Cu Soc Manager")
                .build();
    }

    MakeNewsRequestTask(Context context, String command, String range, News news) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.news = news;
        this.spreadsheetId = context.getString(R.string.news_list_sheet_id);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        try {
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, getCredentials())
                    .setApplicationName("Cu Soc Manager")
                    .build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private File getTempPkc12File() throws IOException {
        InputStream pkc12Stream = context.getAssets().open("Cu Soc Manager-121ee0a1ad27.p12");
        File tempPkc12File = File.createTempFile("P12File", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);

        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }

    private GoogleCredential getCredentials() throws GeneralSecurityException,
            IOException, URISyntaxException {
        List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
        JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = new ApacheHttpTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(CLIENT_ID)
                .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                .setServiceAccountScopes(SCOPES).build();

        return credential;
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
                    return getFullNewsListFromSheet();
                case "update":
                    return updateNewsToSheet();
                case "create":
                    return appendNewsToSheet();
                case "delete":
                    return removeNewsFromSheet();
                default:
                    return getFullNewsListFromSheet();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
         * Fetch a list of news in a sample spreadsheet:
         * @return List of members
         * @throws IOException
         */
    private ArrayList<News> getFullNewsListFromSheet() throws IOException {
        ArrayList<News> results = new ArrayList<News>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
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
                String event = String.valueOf(row.get(3));
                String timestamp = String.valueOf(row.get(4));

                Boolean isEvent = false;
                if (event.equals("Y")) {
                    isEvent = true;
                }
                News news = new News(i, title, description, image_url, isEvent, timestamp);
                results.add(news);
            }
        }
        return results;
    }

    private ArrayList<News> updateNewsToSheet() throws IOException{
        ArrayList<News> results = new ArrayList<News>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(news.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(news.getRow() + 1) + ":" + String.valueOf(news.getRow() + 1) ;
        UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        results.add(this.news);
        affectedRow = result.getUpdatedRows();
        return results;
    }

    private ArrayList<News> appendNewsToSheet() throws IOException{
        ArrayList<News> results = new ArrayList<News>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(news.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(news.getRow() + 1) + ":" + String.valueOf(news.getRow() + 1) ;
        AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        sortNewsListViaAPI();
        results.add(this.news);
        affectedRow = result.getUpdates().getUpdatedRows();
        return results;
    }

    private ArrayList<News> removeNewsFromSheet() throws IOException{
        ArrayList<News> results = new ArrayList<News>();
        Member empty_member = new Member(news.getRow());
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(empty_member.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(news.getRow() + 1) + ":" + String.valueOf(news.getRow() + 1) ;
        UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        sortNewsListViaAPI();
        results.add(this.news);
        affectedRow = result.getUpdatedRows();
        return results;
    }

    private void sortNewsListViaAPI() throws IOException{
        BatchUpdateSpreadsheetRequest batch_update_request = new BatchUpdateSpreadsheetRequest();
        SortSpec sort_spec = new SortSpec();
        sort_spec.setSortOrder("ASCENDING");
        sort_spec.setDimensionIndex(1);
        GridRange grid_range = new GridRange();
        grid_range.setStartRowIndex(1);
        SortRangeRequest sort_range_request = new SortRangeRequest();
        sort_range_request.setSortSpecs(Arrays.asList(sort_spec));
        sort_range_request.setRange(grid_range);
        Request req = new Request();
        req.setSortRange(sort_range_request);
        batch_update_request.setRequests(Arrays.asList(req));

        BatchUpdateSpreadsheetResponse result = this.mService.spreadsheets().batchUpdate(spreadsheetId, batch_update_request)
                .setKey(context.getString(R.string.google_sheet_API_key))
                .execute();
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
        }
        else {
            Toast.makeText(context, "Request cancelled.", Toast.LENGTH_SHORT).show();
        }
    }
}