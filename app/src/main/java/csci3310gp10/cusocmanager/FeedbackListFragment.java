package csci3310gp10.cusocmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by User on 19/12/2017.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackListFragment extends Fragment implements RequestTaskResult<ArrayList<Feedback>>{
    private ProgressBar spinner;
    private ArrayList<Feedback> feedbackList = new ArrayList<>();
    private FeedbackItemAdapter adapter;
    private ListView feedbackListView;

    public FeedbackListFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_list, container, false);
        spinner = (ProgressBar) view.findViewById(R.id.progress_bar);
        feedbackListView = (ListView) view.findViewById(R.id.feedbackList);
        getFullFeedbackListFromAPI();

        NavigationView navigationView = (NavigationView) this.getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_view_feedback);
        return view;
    }

    private void getFullFeedbackListFromAPI() {
        if (!isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else {
            MakeFeedbackRequestTask updateTask = new MakeFeedbackRequestTask(this.getActivity(), "getAll", "Feedback_List");
            updateTask.feedbackListResult = this;
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
    public void taskFinish(ArrayList<Feedback> results){
        spinner.setVisibility(View.GONE);
        feedbackList = new ArrayList<>(results);

        boolean detailPage = false; //false if this is newsFeed, not detailed ver
        adapter = new FeedbackItemAdapter(getContext(), 0, feedbackList, false);
        feedbackListView.setAdapter(adapter);

        feedbackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Feedback feedback = (Feedback) parent.getItemAtPosition(position);

                FeedbackDetailFragment fragment = new FeedbackDetailFragment();
                Bundle args = new Bundle();
                args.putParcelable("item", feedback);
                fragment.setArguments(args);
                android.support.v4.app.FragmentTransaction fragmentTransaction = FeedbackListFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.feedback_detail_fragment));
                fragmentTransaction.addToBackStack(getString(R.string.feedback_detail_fragment));
                fragmentTransaction.commit();
            }
        });
    }

}
