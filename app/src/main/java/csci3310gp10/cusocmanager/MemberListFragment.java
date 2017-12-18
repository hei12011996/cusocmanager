package csci3310gp10.cusocmanager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberListFragment extends Fragment implements RequestTaskResult<ArrayList<Member>>{
    private GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private ArrayList<Member> fullMemberList = new ArrayList<>();
    private String[] memberBasicInfoList;
    private ListView listView;
    private EditText inputSearch;
    CustomArrayAdapter<String> adapter;

    public MemberListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        mCredential = GoogleAccountCredential.usingOAuth2(
                this.getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();

        listView = (ListView) view.findViewById(R.id.member_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                MemberDetailFragment fragment = new MemberDetailFragment();
                Bundle args = new Bundle();
                args.putParcelable("member", fullMemberList.get(Arrays.asList(memberBasicInfoList).indexOf(listView.getItemAtPosition(position))));
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = MemberListFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        });
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if(!fullMemberList.isEmpty()){
                    String text = cs.toString().toLowerCase(Locale.getDefault());
                    adapter.getFilter().filter(text);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
         * Attempt to call the API, after verifying that all the preconditions are
         * satisfied. The preconditions are: Google Play Services installed, an
         * account was selected and the device currently has online access. If any
         * of the preconditions are not satisfied, the app will prompt the user as
         * appropriate.
         */
    private void getResultsFromApi() {
        if (! isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else {
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
    public void taskFinish(ArrayList<Member> results){
        fullMemberList = new ArrayList<>(results);
        memberBasicInfoList = new String[results.size()];
        for(int i = 0; i < fullMemberList.size(); i++){
            Member member = fullMemberList.get(i);
            memberBasicInfoList[i] =  member.getChineseName() + ", " + member.getEnglishName() + ", " + member.getSID();
        }
        adapter = new CustomArrayAdapter<>(MemberListFragment.this.getActivity(), R.layout.list_item, R.id.member_basic_info, memberBasicInfoList);
        listView.setAdapter(adapter);
    }
}