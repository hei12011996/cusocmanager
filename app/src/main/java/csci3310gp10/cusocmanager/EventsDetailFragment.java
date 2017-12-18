package csci3310gp10.cusocmanager;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
public class EventsDetailFragment extends Fragment{

    private EventsItemAdapter adapter;
    private ListView newsListView;

    public EventsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        Bundle args = getArguments();
        News news = args.getParcelable("item");
        ArrayList<News> fullNewsList = new ArrayList<>();
        fullNewsList.add(news);

        newsListView = (ListView) view.findViewById(R.id.newsList);

        boolean detailPage = true; //true if it is detailed page
        adapter = new EventsItemAdapter(getContext(), 0, fullNewsList, detailPage);
        newsListView.setAdapter(adapter);
        return view;
    }
}
