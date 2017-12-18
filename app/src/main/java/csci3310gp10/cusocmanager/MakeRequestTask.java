package csci3310gp10.cusocmanager;

/**
 * Created by KaHei on 18/12/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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
public class MakeRequestTask extends AsyncTask<Void, Void, ArrayList<Member>> {
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    public RequestTaskResult<ArrayList<Member>> memberListResult = null;

    MakeRequestTask(GoogleAccountCredential credential, Context context) {
        this.context = context;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
    @Override
    protected ArrayList<Member> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
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
    private ArrayList<Member> getDataFromApi() throws IOException {
        String spreadsheetId = "18LYcI3_Z9lr4N4ZKWXVpy-76ZtxBFwX6p6_E9QYv361";
        String range = "Member_List";
        ArrayList<Member> results = new ArrayList<Member>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
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

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<Member> output) {
        this.memberListResult.taskFinish(output);
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