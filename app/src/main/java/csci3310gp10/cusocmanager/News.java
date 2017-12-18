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
    private Boolean isEvent;
    private String timestamp;

    public static final Creator CREATOR = new Creator(){
        @Override
        public News createFromParcel(Parcel source) {
            News news = new News();
            news.setRow(source.readInt());
            news.setTitle(source.readString());
            news.setDescription(source.readString());
            news.setImageUrl(source.readString());
            news.setIsEvent((source.readInt() == 0) ? false : true);
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
        this.isEvent = null;
        this.timestamp = null;
    }

    public News(Integer row, String title, String description, String image_url, Boolean isEvent, String timestamp) {
        this.row = row;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.isEvent = isEvent;
        this.timestamp = timestamp;
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
    public Boolean getIsEvent() { return this.isEvent; }
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
    public void setIsEvent(Boolean isEvent) {this.isEvent = isEvent; }
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
        dest.writeInt((isEvent ? 1:0));
        dest.writeString(timestamp);
    }
}
