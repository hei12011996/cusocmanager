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
public class MakeMemberRequestTask extends AsyncTask<Void, Void, ArrayList<Member>> {
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Context context = null;
    private String command = "getAll";
    private String range = "Member_List";
    public RequestTaskResult<ArrayList<Member>> memberListResult = null;

    MakeMemberRequestTask(Context context, String command, String range) {
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
    protected ArrayList<Member> doInBackground(Void... params) {
        try {
            switch(command){
                case "getAll":
                    return getFullMemberListFromAPI();
                default:
                    return getFullMemberListFromAPI();
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
    private ArrayList<Member> getFullMemberListFromAPI() throws IOException {
        String member_list_sheet_id = context.getString(R.string.member_list_sheet_id);
        ArrayList<Member> results = new ArrayList<Member>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(member_list_sheet_id, range)
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