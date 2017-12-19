package csci3310gp10.cusocmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackDetailFragment extends Fragment{

    private FeedbackItemAdapter adapter;
    private ListView feedbackListView;

    public FeedbackDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_list, container, false);
        Bundle args = getArguments();
        Feedback feedback = args.getParcelable("item");
        ArrayList<Feedback> fullFeedbackList = new ArrayList<>();
        fullFeedbackList.add(feedback);

        feedbackListView = (ListView) view.findViewById(R.id.feedbackList);

        boolean detailPage = true; //true if it is detailed page
        adapter = new FeedbackItemAdapter(getContext(), 0, fullFeedbackList, detailPage);
        feedbackListView.setAdapter(adapter);
        return view;
    }
}
