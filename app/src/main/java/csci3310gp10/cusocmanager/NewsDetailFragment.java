package csci3310gp10.cusocmanager;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailFragment extends Fragment{

    private NewsItemAdapter adapter;
    private ListView newsListView;

    private String mode = "";
    private Integer last_row = 0;
    private News news = null;

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        //hide fab in detailed page
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.hide();

        Bundle args = getArguments();
        mode = args.getString("mode");
        if(mode.equals("view")) {
            news = args.getParcelable("item");

            ArrayList<News> fullNewsList = new ArrayList<>();
            fullNewsList.add(news);

            newsListView = (ListView) view.findViewById(R.id.newsList);
            boolean detailPage = true; //true if it is detailed page
            adapter = new NewsItemAdapter(getContext(), 0, fullNewsList, detailPage);
            newsListView.setAdapter(adapter);
        }
        else if(mode.equals("create")) {
            news = new News();
            last_row = args.getInt("last_row");
        }

        return view;
    }
}
