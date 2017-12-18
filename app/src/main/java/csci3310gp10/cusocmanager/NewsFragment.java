package csci3310gp10.cusocmanager;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements RequestTaskResult<ArrayList<News>>{

    private ArrayList<News> fullNewsList = new ArrayList<>();
    private NewsItemAdapter adapter;
    private ListView newsListView;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsListView = (ListView) view.findViewById(R.id.newsList);
        getFullNewsListFromAPI();
        return view;
    }

    private void getFullNewsListFromAPI() {
        if (! isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else {
            MakeNewsRequestTask updateTask = new MakeNewsRequestTask(this.getActivity(), "getAll", "News_List");
            updateTask.newsListResult = this;
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
    public void taskFinish(ArrayList<News> results){
        fullNewsList = new ArrayList<>(results);

        adapter = new NewsItemAdapter(getContext(), 0, fullNewsList);
        newsListView.setAdapter(adapter);
    }
}
