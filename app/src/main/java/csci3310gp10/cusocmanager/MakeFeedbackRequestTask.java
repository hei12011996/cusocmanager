package csci3310gp10.cusocmanager;

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
public class MakeFeedbackRequestTask extends AsyncTask<Void, Void, ArrayList<Feedback>> {
    private static final String CLIENT_ID = "cu-soc-manager@excellent-tide-188914.iam.gserviceaccount.com";
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    private Feedback feedback = null;
    private String spreadsheetId = "";
    private String command = "getAll";
    private String range = "Feedback_List";
    private Integer affectedRow = 0;
    public RequestTaskResult<ArrayList<Feedback>> feedbackListResult = null;

    MakeFeedbackRequestTask(Context context, String command, String range) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.spreadsheetId = context.getString(R.string.feedback_list_sheet_id);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, null)
                .setApplicationName("Cu Soc Manager")
                .build();
    }

    MakeFeedbackRequestTask(Context context, String command, String range, Feedback feedback) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.feedback = feedback;
        this.spreadsheetId = context.getString(R.string.feedback_list_sheet_id);
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
    protected ArrayList<Feedback> doInBackground(Void... params) {
        try {
            switch(command){
                case "getAll":
                    return getFullFeedbackListFromSheet();
                case "update":
                    return updateFeedbackToSheet();
                case "create":
                    return appendFeedbackToSheet();
                default:
                    return getFullFeedbackListFromSheet();
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
    private ArrayList<Feedback> getFullFeedbackListFromSheet() throws IOException {
        String member_list_sheet_id = context.getString(R.string.feedback_list_sheet_id);
        ArrayList<Feedback> results = new ArrayList<Feedback>();
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
                String timestamp = String.valueOf(row.get(2));
                Feedback feedback = new Feedback(i, title, description, timestamp);
                results.add(feedback);
            }
        }
        return results;
    }

    private ArrayList<Feedback> updateFeedbackToSheet() throws IOException{
        ArrayList<Feedback> results = new ArrayList<Feedback>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(feedback.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(feedback.getRow() + 1) + ":" + String.valueOf(feedback.getRow() + 1) ;
        UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .setKey(context.getString(R.string.google_sheet_API_key))
                .execute();
        results.add(this.feedback);
        affectedRow = result.getUpdatedCells();
        return results;
    }

    private ArrayList<Feedback> appendFeedbackToSheet() throws IOException{
        ArrayList<Feedback> results = new ArrayList<Feedback>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(feedback.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(feedback.getRow() + 1) + ":" + String.valueOf(feedback.getRow() + 1) ;
        AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .setKey(context.getString(R.string.google_sheet_API_key))
                .execute();
        sortMemberListViaSheet();
        results.add(this.feedback);
        affectedRow = result.getUpdates().getUpdatedCells();
        return results;
    }

    private void sortMemberListViaSheet() throws IOException{
        BatchUpdateSpreadsheetRequest batch_update_request = new BatchUpdateSpreadsheetRequest();
        SortSpec sort_spec = new SortSpec();
        sort_spec.setSortOrder("DESCENDING");
        sort_spec.setDimensionIndex(2);
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
    protected void onPostExecute(ArrayList<Feedback> output) {
        this.feedbackListResult.taskFinish(output);
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