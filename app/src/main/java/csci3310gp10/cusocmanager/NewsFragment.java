package csci3310gp10.cusocmanager;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements RequestTaskResult<ArrayList<News>>{

    private ArrayList<News> fullNewsList = new ArrayList<>();
    private NewsItemAdapter adapter;
    private ListView newsListView;

    private FloatingActionButton myFab;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        NavigationView navigationView = (NavigationView) this.getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        newsListView = (ListView) view.findViewById(R.id.newsList);
        getFullNewsListFromAPI();

        NavActivity checkLogin = (NavActivity) this.getActivity();
        Boolean hasLogin = checkLogin.getLoginStatus();
        myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        if(hasLogin == true) {
            myFab.show();
        }
        else {
            myFab.hide();
        }

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

        boolean detailPage = false; //false if this is newsFeed, not detailed ver
        adapter = new NewsItemAdapter(getContext(), 0, fullNewsList, false);
        newsListView.setAdapter(adapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News)parent.getItemAtPosition(position);

                NewsDetailFragment fragment = new NewsDetailFragment();
                Bundle args = new Bundle();
                args.putString("mode", "view");
                args.putParcelable("item", news);
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = NewsFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.news_detail_fragment));
                fragmentTransaction.addToBackStack(getString(R.string.news_detail_fragment));
                fragmentTransaction.commit();
            }
        });

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsEditFragment fragment = new NewsEditFragment();
                Bundle args = new Bundle();
                args.putString("mode", "create");
                args.putInt("last_row", fullNewsList.size());
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = NewsFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.news_edit_fragment));
                fragmentTransaction.addToBackStack(getString(R.string.news_edit_fragment));
                fragmentTransaction.commit();
            }
        });
    }
}
