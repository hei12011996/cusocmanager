package csci3310gp10.cusocmanager;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberDetailFragment extends Fragment implements RequestTaskResult<ArrayList<Member>>{
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    private Member member = null;
    EditText chinese_name_text = null;
    EditText english_name_text = null;
    EditText sid_text = null;
    EditText college_text = null;
    EditText major_year_text = null;
    EditText phone_text = null;
    EditText email_text = null;


    public MemberDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_detail, container, false);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        member = args.getParcelable("member");
        chinese_name_text = (EditText) view.findViewById(R.id.chinese_name_text);
        english_name_text = (EditText) view.findViewById(R.id.english_name_text);
        sid_text = (EditText) view.findViewById(R.id.sid_text);
        college_text = (EditText) view.findViewById(R.id.college_text);
        major_year_text = (EditText) view.findViewById(R.id.major_year_text);
        phone_text = (EditText) view.findViewById(R.id.phone_text);
        email_text = (EditText) view.findViewById(R.id.email_text);
        closeAllTextEdit();
        insertMemberInfo();

        // Inflate the layout for this fragment
        return view;
    }

    private void insertMemberInfo(){
        chinese_name_text.setText(member.getChineseName());
        english_name_text.setText(member.getEnglishName());
        sid_text.setText(member.getSID());
        college_text.setText(member.getCollege());
        major_year_text.setText(member.getMajorYear());
        phone_text.setText(member.getPhone());
        email_text.setText(member.getEmail());
    }

    private void closeAllTextEdit(){
        chinese_name_text.setEnabled(false);
        english_name_text.setEnabled(false);
        sid_text.setEnabled(false);
        college_text.setEnabled(false);
        major_year_text.setEnabled(false);
        phone_text.setEnabled(false);
        email_text.setEnabled(false);
    }

    private void openAllTextEdit(){
        chinese_name_text.setEnabled(true);
        english_name_text.setEnabled(true);
        sid_text.setEnabled(true);
        college_text.setEnabled(true);
        major_year_text.setEnabled(true);
        phone_text.setEnabled(true);
        email_text.setEnabled(true);
    }

    private void saveInfoToMember(){
        member.setChineseName(chinese_name_text.getText().toString());
        member.setEnglishName(english_name_text.getText().toString());
        member.setSID(sid_text.getText().toString());
        member.setCollege(college_text.getText().toString());
        member.setMajorYear(major_year_text.getText().toString());
        member.setPhone(phone_text.getText().toString());
        member.setEmail(email_text.getText().toString());
    }

    private void pushMemberToSheet(){
        if (! isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else{
            MakeMemberRequestTask updateTask = new MakeMemberRequestTask(this.getActivity(), "getAll", "Member_List");
            updateTask.memberListResult = this;
            updateTask.execute();
        }
    }


    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nav_member_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_member) {
            openAllTextEdit();
        }
        else if (id == R.id.action_save_member){
            closeAllTextEdit();
            saveInfoToMember();
            pushMemberToSheet();
        }
        else if (id == R.id.action_delete_member){
        }
        return true;
    }

    @Override
    public void taskFinish(ArrayList<Member> results){
        for(int i = 0; i < results.size(); i++){
            Member member = results.get(i);
            System.out.println(member.getChineseName() + ", " + member.getEnglishName() + ", " + member.getSID());
        }
    }
}
