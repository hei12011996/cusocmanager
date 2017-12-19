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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment implements RequestTaskResult<ArrayList<Feedback>> {

    public View view;
    public Button submitButton;
    public EditText topic;
    public EditText description;

    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        submitButton = (Button) view.findViewById(R.id.feedback_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = (EditText) view.findViewById(R.id.feedback_topic_edit);
                description = (EditText) view.findViewById(R.id.feedback_description_edit);
                if (topic.getText().toString().equals("") || description.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "\"Topic\" and \"Description\" should not be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Sending feedback...", Toast.LENGTH_SHORT).show();
                    long currentTime = System.currentTimeMillis();
                    Feedback feedback = new Feedback(1, topic.getText().toString(), description.getText().toString(), String.valueOf(currentTime));
                    sendFeedbackToSheet(feedback);
                }
            }
        });

        NavigationView navigationView = (NavigationView) this.getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_feedback);
        return view;
    }

    private void sendFeedbackToSheet(Feedback feedback) {
        if (!isDeviceOnline()) {
            Toast.makeText(this.getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else {
            MakeFeedbackRequestTask createTask = new MakeFeedbackRequestTask(getContext(), "create", "Feedback_List", feedback);
            createTask.feedbackListResult = this;
            createTask.execute();
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

    public void taskFinish(ArrayList<Feedback> results) {
        Toast.makeText(getContext(), "Send feedback successfully", Toast.LENGTH_SHORT).show();
        FeedBackFragment fragment = new FeedBackFragment();
        if(FeedBackFragment.this.getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1){
            FeedBackFragment.this.getActivity().getSupportFragmentManager().popBackStack();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = FeedBackFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, getString(R.string.feedback_fragment));
        fragmentTransaction.addToBackStack(getString(R.string.feedback_fragment));
        fragmentTransaction.commit();
    }

}
