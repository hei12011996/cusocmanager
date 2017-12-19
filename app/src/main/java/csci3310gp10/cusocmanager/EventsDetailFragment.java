package csci3310gp10.cusocmanager;


import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsDetailFragment extends Fragment implements RequestTaskResult<ArrayList<News>>{

    private EventsItemAdapter adapter;
    private ListView newsListView;


    private String mode = "";
    private Integer last_row = 0;
    private News news = null;

    public EventsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        //activate option menu
        NavActivity checkLogin = (NavActivity) this.getActivity();
        Boolean hasLogin = checkLogin.getLoginStatus();
        if (hasLogin == true) {
            setHasOptionsMenu(true);
        }

        Bundle args = getArguments();
        mode = args.getString("mode");
        if(mode.equals("view")) {
            news = args.getParcelable("item");

            ArrayList<News> fullNewsList = new ArrayList<>();
            fullNewsList.add(news);

            newsListView = (ListView) view.findViewById(R.id.newsList);
            boolean detailPage = true; //true if it is detailed page
            adapter = new EventsItemAdapter(getContext(), 0, fullNewsList, detailPage);
            newsListView.setAdapter(adapter);
        }
        else if(mode.equals("create")) {
            news = new News();
            last_row = args.getInt("last_row");
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nav_news_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_news) {
            NewsEditFragment fragment = new NewsEditFragment();
            Bundle args = new Bundle();
            args.putString("mode", "edit");
            args.putParcelable("item", news);
            fragment.setArguments(args);
            android.support.v4.app.FragmentTransaction fragmentTransaction = EventsDetailFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.news_edit_fragment));
            fragmentTransaction.addToBackStack(getString(R.string.news_edit_fragment));
            fragmentTransaction.commit();
        }
        else if (id == R.id.action_delete_news){
            //remove News
            if (! isDeviceOnline()) {
                Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            else{
                MakeNewsRequestTask deleteTask = new MakeNewsRequestTask(this.getActivity(), "delete", "News_List", news);
                deleteTask.newsListResult = this;
                deleteTask.execute();
            }
        }
        return true;
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void taskFinish(ArrayList<News> results){
        EventsFragment fragment = new EventsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = EventsDetailFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
