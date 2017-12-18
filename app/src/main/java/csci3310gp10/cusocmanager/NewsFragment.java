package csci3310gp10.cusocmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {


    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_news, container, false);
/*
        ArrayList<String> cheeses = new ArrayList<>();
        cheeses.add("Parmesan");
        cheeses.add("Ricotta");
        cheeses.add("Fontina");
        cheeses.add("Mozzarella");

        ArrayAdapter<String> cheeseAdapter =
                new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        cheeses
                );
*/
        ArrayList<News> news = new ArrayList<>();
        news.add(new News(0, "Welcome to Japanese Soc", "this is a very good soc ar, have japanese nui nui", "https://pbs.twimg.com/media/DRGhvyFUMAAROf7.jpg", "123"));
        news.add(new News(1, "Welcome to Japanese Soc", "this is a very good soc ar, have japanese nui nui", "https://pbs.twimg.com/media/CkriQVTVAAQ_Xnf.jpg", "111"));
        ArrayAdapter<News> adapter = new NewsItemAdapter(getContext(), 0, news);

        ListView newsListView = (ListView) view.findViewById(R.id.newsList);
        newsListView.setAdapter(adapter);

        return view;
    }



}
