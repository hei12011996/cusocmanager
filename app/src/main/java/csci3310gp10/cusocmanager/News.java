package csci3310gp10.cusocmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 18/12/2017.
 */

public class News implements Parcelable {
    private Integer row;
    private String title;
    private String description;
    private String image_url;
    private String timestamp;

    public static final Creator CREATOR = new Creator(){
        @Override
        public News createFromParcel(Parcel source) {
            News news = new News();
            news.setRow(source.readInt());
            news.setTitle(source.readString());
            news.setDescription(source.readString());
            news.setImageUrl(source.readString());
            news.setTimeStamp(source.readString());
            return news;
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public News() {
        this.row = null;
        this.title = null;
        this.description = null;
        this.image_url = null;
        this.timestamp = null;
    }

    public News(Integer row, String title, String description, String image_url, String timestamp) {
        this.row = row;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.timestamp = timestamp;
        this.timestamp = "20170107235959";
    }

    public Integer getRow() {
        return this.row;
    }
    public String getTitle() {
        return this.title;
    }
    public String getDescription() {
        return this.description;
    }
    public String getImageUrl() {
        return this.image_url;
    }
    public String getTimeStamp() {
        return this.timestamp;
    }

    public void setRow(Integer row) {
        this.row = row;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(row);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image_url);
        dest.writeString(timestamp);
    }
}
