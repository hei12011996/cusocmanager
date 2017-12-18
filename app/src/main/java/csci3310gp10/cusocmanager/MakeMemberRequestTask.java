package csci3310gp10.cusocmanager;

/**
 * Created by KaHei on 18/12/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SortRangeRequest;
import com.google.api.services.sheets.v4.model.SortSpec;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeMemberRequestTask extends AsyncTask<Void, Void, ArrayList<Member>> {
    private static final String CLIENT_ID = "cu-soc-manager@excellent-tide-188914.iam.gserviceaccount.com";
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    private Member member = null;
    private String spreadsheetId = "";
    private String command = "getAll";
    private String range = "Member_List";
    private Integer affectedRow = 0;
    public RequestTaskResult<ArrayList<Member>> memberListResult = null;

    MakeMemberRequestTask(Context context, String command, String range) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.spreadsheetId = context.getString(R.string.member_list_sheet_id);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, null)
                .setApplicationName("Cu Soc Manager")
                .build();
    }

    MakeMemberRequestTask(Context context, String command, String range, Member member) {
        this.context = context;
        this.command = command;
        this.range = range;
        this.member = member;
        this.spreadsheetId = context.getString(R.string.member_list_sheet_id);
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
    protected ArrayList<Member> doInBackground(Void... params) {
        try {
            switch(command){
                case "getAll":
                    return getFullMemberListFromSheet();
                case "update":
                    return updateMemberToSheet();
                case "create":
                    return appendMemberToSheet();
                case "delete":
                    return removeMemberFromSheet();
                default:
                    return getFullMemberListFromSheet();
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
    private ArrayList<Member> getFullMemberListFromSheet() throws IOException {
        ArrayList<Member> results = new ArrayList<Member>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
                .setKey(context.getString(R.string.google_sheet_API_key))
                .execute();
        List<List<Object>> values = response.getValues();
        Integer i = 0;
        if (values != null) {
            for (List row : values.subList(1, values.size())) {
                i++;
                String chinese_name = String.valueOf(row.get(0));
                String english_name = String.valueOf(row.get(1));
                String sid = String.valueOf(row.get(2));
                String college = String.valueOf(row.get(3));
                String major_year = String.valueOf(row.get(4));
                String phone = String.valueOf(row.get(5));
                String email = String.valueOf(row.get(6));
                Member member = new Member(i, chinese_name, english_name, sid, college, major_year, phone, email);
                results.add(member);
            }
        }
        return results;
    }

    private ArrayList<Member> updateMemberToSheet() throws IOException{
        ArrayList<Member> results = new ArrayList<Member>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(member.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(member.getRow() + 1) + ":" + String.valueOf(member.getRow() + 1) ;
        UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        results.add(this.member);
        affectedRow = result.getUpdatedRows();
        return results;
    }

    private ArrayList<Member> appendMemberToSheet() throws IOException{
        ArrayList<Member> results = new ArrayList<Member>();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(member.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(member.getRow() + 1) + ":" + String.valueOf(member.getRow() + 1) ;
        AppendValuesResponse result = this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        sortMemberListViaAPI();
        results.add(this.member);
        affectedRow = result.getUpdates().getUpdatedRows();
        return results;
    }

    private ArrayList<Member> removeMemberFromSheet() throws IOException{
        ArrayList<Member> results = new ArrayList<Member>();
        Member empty_member = new Member(member.getRow());
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(empty_member.toArray())
        );
        ValueRange body = new ValueRange().setValues(values);
        this.range = String.valueOf(member.getRow() + 1) + ":" + String.valueOf(member.getRow() + 1) ;
        UpdateValuesResponse result = this.mService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        sortMemberListViaAPI();
        results.add(this.member);
        affectedRow = result.getUpdatedRows();
        return results;
    }

    private void sortMemberListViaAPI() throws IOException{
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
    protected void onPostExecute(ArrayList<Member> output) {
        this.memberListResult.taskFinish(output);
        if (output == null || output.size() == 0) {
            Toast.makeText(context, "No results returned.", Toast.LENGTH_SHORT).show();
        }
        else if (command.equals("update") && affectedRow == 1){
            Toast.makeText(context, "Information of " + member.getEnglishName() + " has been updated.", Toast.LENGTH_SHORT).show();
        }
        else if (command.equals("create") && affectedRow == 1){
            Toast.makeText(context, member.getEnglishName() + " is added to member list successfully.", Toast.LENGTH_SHORT).show();
        }
        else if (command.equals("delete") && affectedRow == 1){
            Toast.makeText(context, "Member " + member.getEnglishName() + " is removed successfully.", Toast.LENGTH_SHORT).show();
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