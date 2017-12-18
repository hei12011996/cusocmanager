package csci3310gp10.cusocmanager;

import android.os.Parcel;
import android.os.Parcelable;


public class Feedback implements Parcelable {
    private Integer row;
    private String title;
    private String description;
    private String timestamp;

    public static final Creator CREATOR = new Creator(){
        @Override
        public Feedback createFromParcel(Parcel source) {
            Feedback feedback = new Feedback();
            feedback.setRow(source.readInt());
            feedback.setTitle(source.readString());
            feedback.setDescription(source.readString());
            feedback.setTimeStamp(source.readString());
            return feedback;
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };

    public Feedback() {
        this.row = null;
        this.title = null;
        this.description = null;
        this.timestamp = null;
    }

    public Feedback(Integer row, String title, String description, String timestamp) {
        this.row = row;
        this.title = title;
        this.description = description;
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
    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object[] toArray(){
        return new Object[] {this.title, this.description, this.timestamp};
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
        dest.writeString(timestamp);
    }
}
