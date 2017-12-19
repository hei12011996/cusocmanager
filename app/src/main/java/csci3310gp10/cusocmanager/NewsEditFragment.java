package csci3310gp10.cusocmanager;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsEditFragment extends Fragment implements RequestTaskResult<ArrayList<News>>{
    private String action = "";
    private String mode = "";
    private Integer last_row = 0;
    private News news = null;
    Button submit_button = null;
    EditText title = null;
    EditText description = null;
    EditText url = null;
    CheckBox checkbox_isEvent = null;

    public NewsEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_edit, container, false);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        mode = args.getString("mode");
        if(mode.equals("edit")){
            news = args.getParcelable("item");
        }
        else if (mode.equals("create")){
            news = new News();
            last_row = args.getInt("last_row");
        }
        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        url = (EditText) view.findViewById(R.id.url);
        checkbox_isEvent = (CheckBox) view.findViewById(R.id.checkbox_isEvent);
        submit_button = (Button) view.findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveInfoToNews();
                pushNewsToSheet();
            }
        });
        if (mode.equals("edit")){
            insertNewsInfo();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void insertNewsInfo(){
        title.setText(news.getTitle());
        description.setText(news.getDescription());
        url.setText(news.getImageUrl());
        checkbox_isEvent.setChecked(news.getIsEvent());
    }

    private void saveInfoToNews(){
        news.setTitle(title.getText().toString());
        news.setDescription(description.getText().toString());
        news.setImageUrl(url.getText().toString());
        news.setIsEvent(checkbox_isEvent.isChecked());
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

    private void pushNewsToSheet(){
        if (! isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else if(news.getTitle().length() == 0 || news.getDescription().length() == 0){
            Toast.makeText(this.getActivity(), "Please fill in the necessary information!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(mode.equals("edit")){
                MakeNewsRequestTask updateTask = new MakeNewsRequestTask(this.getActivity(), "update", "News_List", news);
                updateTask.newsListResult = this;
                updateTask.execute();
            }
            else if (mode.equals("create")){
                news.setRow(last_row + 1);
                long currentTime = System.currentTimeMillis();
                System.out.println(currentTime);
                news.setTimeStamp(String.valueOf(currentTime));
                MakeNewsRequestTask createTask = new MakeNewsRequestTask(this.getActivity(), "create", "News_List", news);
                createTask.newsListResult = this;
                createTask.execute();
            }
        }
    }

    @Override
    public void taskFinish(ArrayList<News> results){
        NewsFragment fragment = new NewsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = NewsEditFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


}
