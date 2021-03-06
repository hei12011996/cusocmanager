package csci3310gp10.cusocmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by User on 18/12/2017.
 */

public class EventsItemAdapter extends ArrayAdapter<News> {

    private Context context;
    private ArrayList<News> newsList = new ArrayList<>();
    private boolean detailed;

    public EventsItemAdapter(Context context, int textViewResourceId, ArrayList<News> objects, boolean detailed) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.newsList = objects;
        this.detailed = detailed;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //get the property we are displaying
        News news = newsList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_news_item, null);
        RelativeLayout allLayout = (RelativeLayout) view.findViewById(R.id.allSection);

        if(news.getIsEvent() == false) {
            allLayout.removeAllViews();
            return view;
        }

        TextView newsTitle = (TextView) view.findViewById(R.id.newsTitle);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);
        ImageView img_add = (ImageView) view.findViewById(R.id.image);

        //display trimmed excerpt for description
        int descriptionLength = news.getDescription().length();
        if(descriptionLength >= 100 && detailed == false){
            String descriptionTrim = news.getDescription().substring(0, 100) + "...\nclick to view full text";
            description.setText(descriptionTrim);
        }else{
            description.setText(news.getDescription());
        }

        RelativeLayout sectionLayout = (RelativeLayout) view.findViewById(R.id.section);
        //set price and rental attributes
        newsTitle.setText(String.valueOf(news.getTitle()));
        if (news.getImageUrl() == "null") {
            sectionLayout.removeAllViews();
        }
        else {
            loadImageFromUrl(news.getImageUrl(), context, img_add);
        }

        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(Long.valueOf(news.getTimeStamp()) * 1000L);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        timestamp.setText("Posted at: " + date);

        return view;
    }

    private void loadImageFromUrl (String url, Context content, ImageView imageView){
        if(url.length() != 0) {
            Picasso.with(context).load(url).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }
}


