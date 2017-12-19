package csci3310gp10.cusocmanager;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FeedbackItemAdapter extends ArrayAdapter<Feedback> {

    private Context context;
    private ArrayList<Feedback> feedbackList = new ArrayList<>();
    private boolean detailed;

    public FeedbackItemAdapter(Context context, int textViewResourceId, ArrayList<Feedback> objects, boolean detailed) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.feedbackList = objects;
        this.detailed = detailed;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        Feedback feedback = feedbackList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_feedback_item, null);

        TextView feedbackTitle = (TextView) view.findViewById(R.id.feedbackTitle);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);

        //display trimmed excerpt for description
        int descriptionLength = feedback.getDescription().length();
        if(descriptionLength >= 100 && detailed == false){
            String descriptionTrim = feedback.getDescription().substring(0, 100) + "...\nclick to view full text";
            description.setText(descriptionTrim);
        }else{
            description.setText(feedback.getDescription());
        }


        //set price and rental attributes
        feedbackTitle.setText(String.valueOf(feedback.getTitle()));

        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(Long.valueOf(feedback.getTimeStamp()));
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        timestamp.setText("Sent at: " + date);

        return view;
    }
}


