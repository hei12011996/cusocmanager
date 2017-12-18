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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberDetailFragment extends Fragment implements RequestTaskResult<ArrayList<Member>>{
    private String action = "";
    private String mode = "";
    private Integer last_row = 0;
    private Member member = null;
    Button submit_button = null;
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
        mode = args.getString("mode");
        if(mode.equals("view")){
            member = args.getParcelable("member");
        }
        else if (mode.equals("create")){
            member = new Member();
            last_row = args.getInt("last_row");
        }
        chinese_name_text = (EditText) view.findViewById(R.id.chinese_name_text);
        english_name_text = (EditText) view.findViewById(R.id.english_name_text);
        sid_text = (EditText) view.findViewById(R.id.sid_text);
        college_text = (EditText) view.findViewById(R.id.college_text);
        major_year_text = (EditText) view.findViewById(R.id.major_year_text);
        phone_text = (EditText) view.findViewById(R.id.phone_text);
        email_text = (EditText) view.findViewById(R.id.email_text);
        submit_button = (Button) view.findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mode.equals("view")){
                    closeAllTextEdit();
                }
                saveInfoToMember();
                pushMemberToSheet();
            }
        });
        if (mode.equals("view")){
            closeAllTextEdit();
            insertMemberInfo();
        }

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
        submit_button.setVisibility(View.GONE);
    }

    private void openAllTextEdit(){
        chinese_name_text.setEnabled(true);
        english_name_text.setEnabled(true);
        sid_text.setEnabled(true);
        college_text.setEnabled(true);
        major_year_text.setEnabled(true);
        phone_text.setEnabled(true);
        email_text.setEnabled(true);
        submit_button.setVisibility(View.VISIBLE);
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
        else if(member.getEnglishName().length() == 0 || member.getSID().length() == 0 || member.getCollege().length() == 0 || member.getMajorYear().length() == 0 || member.getPhone().length() == 0 || member.getEmail().length() == 0){
            Toast.makeText(this.getActivity(), "Please fill in the necessary information!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(mode.equals("view")){
                MakeMemberRequestTask updateTask = new MakeMemberRequestTask(this.getActivity(), "update", "Member_List", member);
                updateTask.memberListResult = this;
                updateTask.execute();
            }
            else if (mode.equals("create")){
                member.setRow(last_row + 1);
                MakeMemberRequestTask updateTask = new MakeMemberRequestTask(this.getActivity(), "create", "Member_List", member);
                updateTask.memberListResult = this;
                updateTask.execute();
            }
        }
    }

    private void removeMember(){
        if (! isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else{
            MakeMemberRequestTask updateTask = new MakeMemberRequestTask(this.getActivity(), "delete", "Member_List", member);
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
            action = "edit";
        }
        else if (id == R.id.action_delete_member){
            if (!mode.equals("create")){
                removeMember();
                action = "delete";
            }
        }
        return true;
    }

    @Override
    public void taskFinish(ArrayList<Member> results){
        if (action.equals("delete")){
            MemberListFragment fragment = new MemberListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = MemberDetailFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        else if (mode.equals("create")){
            MemberListFragment fragment = new MemberListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = MemberDetailFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }
}
