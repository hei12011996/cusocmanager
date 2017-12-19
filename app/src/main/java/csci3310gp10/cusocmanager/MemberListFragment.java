package csci3310gp10.cusocmanager;

import com.google.api.services.sheets.v4.SheetsScopes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberListFragment extends Fragment implements RequestTaskResult<ArrayList<Member>>{
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    private ArrayList<Member> fullMemberList = new ArrayList<>();
    private String[] memberBasicInfoList;
    private ListView listView;
    private EditText inputSearch;
    private CustomArrayAdapter<String> adapter;

    public MemberListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        getFullMemberListFromAPI();

        listView = (ListView) view.findViewById(R.id.member_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                MemberDetailFragment fragment = new MemberDetailFragment();
                Bundle args = new Bundle();
                args.putParcelable("member", fullMemberList.get(Arrays.asList(memberBasicInfoList).indexOf(listView.getItemAtPosition(position))));
                args.putString("mode", "view");
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = MemberListFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.member_detail_fragment));
                fragmentTransaction.addToBackStack(getString(R.string.member_detail_fragment));
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberDetailFragment fragment = new MemberDetailFragment();
                Bundle args = new Bundle();
                args.putString("mode", "create");
                args.putInt("last_row", fullMemberList.size());
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = MemberListFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.member_detail_fragment));
                fragmentTransaction.addToBackStack(getString(R.string.member_detail_fragment));
                fragmentTransaction.commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void getFullMemberListFromAPI() {
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
