package csci3310gp10.cusocmanager;

/**
 * Created by User on 18/12/2017.
 */

public class News {
    private String newsTitle;
    private String description;
    private String imageUrl;
    private String timestamp;

    public News() {

    }

    public News(String t, String d, String i) {
        this.newsTitle = t;
        this.description = d;
        this.imageUrl = i;
        this.timestamp = "20170107235959";
    }

    public String getNewsTitle() {
        return this.newsTitle;
    }
    public String getDescription() {
        return this.description;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getTimeStamp() {
        return this.timestamp;
    }
}
