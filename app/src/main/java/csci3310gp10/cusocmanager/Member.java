package csci3310gp10.cusocmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KaHei on 18/12/2017.
 */

public class Member implements Parcelable {
    private Integer row;
    private String chinese_name;
    private String english_name;
    private String sid;
    private String college;
    private String major_year;
    private String phone;
    private String email;

    public static final Creator CREATOR = new Creator(){
        @Override
        public Member createFromParcel(Parcel source) {
            Member member = new Member();
            member.setRow(source.readInt());
            member.setChineseName(source.readString());
            member.setEnglishName(source.readString());
            member.setSID(source.readString());
            member.setCollege(source.readString());
            member.setMajorYear(source.readString());
            member.setPhone(source.readString());
            member.setEmail(source.readString());
            return member;
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public Member(Integer row){
        this.row = row;
        this.chinese_name = "";
        this.english_name = "";
        this.sid = "";
        this.college = "";
        this.major_year = "";
        this.phone = "";
        this.email = "";
    }

    public Member(){
        this.row = -1;
        this.chinese_name = "";
        this.english_name = "";
        this.sid = "";
        this.college = "";
        this.major_year = "";
        this.phone = "";
        this.email = "";
    }

    public Member(Integer row, String chinese_name, String english_name, String sid, String college, String major_year, String phone, String email){
        this.row = row;
        this.chinese_name = chinese_name;
        this.english_name = english_name;
        this.sid = sid;
        this.college = college;
        this.major_year = major_year;
        this.phone = phone;
        this.email = email;
    }

    public Integer getRow(){
        return this.row;
    }

    public String getChineseName(){
        return this.chinese_name;
    }

    public String getEnglishName(){
        return this.english_name;
    }

    public String getSID(){
        return this.sid;
    }

    public String getCollege(){
        return this.college;
    }

    public String getMajorYear(){
        return this.major_year;
    }

    public String getPhone(){
        return this.phone;
    }

    public String getEmail(){
        return this.email;
    }

    public void setRow(Integer row){
        this.row = row;
    }

    public void setChineseName(String chinese_name){
        this.chinese_name = chinese_name;
    }

    public void setEnglishName(String english_name){
        this.english_name = english_name;
    }

    public void setSID(String sid){
        this.sid = sid;
    }

    public void setCollege(String college){
        this.college = college;
    }

    public void setMajorYear(String major_year){
        this.major_year = major_year;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public Object[] toArray(){
        return new Object[] {this.chinese_name, this.english_name, this.sid, this.college, this.major_year, this.phone, this.email};
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(row);
        dest.writeString(chinese_name);
        dest.writeString(english_name);
        dest.writeString(sid);
        dest.writeString(college);
        dest.writeString(major_year);
        dest.writeString(phone);
        dest.writeString(email);
    }
}
