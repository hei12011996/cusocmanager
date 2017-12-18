package csci3310gp10.cusocmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 18/12/2017.
 */

public class NewsItemAdapter extends ArrayAdapter<News> {

    private Context context;
    private ArrayList<News> newsList = new ArrayList<>();

    public NewsItemAdapter(Context context, int textViewResourceId, ArrayList<News> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.newsList = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        News news = newsList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_news_item, null);

        TextView newsTitle = (TextView) view.findViewById(R.id.newsTitle);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);
        TextView imageUrl = (TextView) view.findViewById(R.id.imageUrl);

        //display trimmed excerpt for description
        int descriptionLength = news.getDescription().length();
        if(descriptionLength >= 100){
            String descriptionTrim = news.getDescription().substring(0, 100) + "...";
            description.setText(descriptionTrim);
        }else{
            description.setText(news.getDescription());
        }

        //set price and rental attributes
        newsTitle.setText(String.valueOf(news.getNewsTitle()));
        description.setText(String.valueOf(news.getDescription()));
        imageUrl.setText(String.valueOf(news.getImageUrl()));
        timestamp.setText(String.valueOf(news.getTimeStamp()));

        return view;
    }
}
