package com.apify.markmate;

public class attendance_data_with_uid {
    String sr_no;
    String uid;
    Boolean present;
    public attendance_data_with_uid(String sr_no, Boolean present,String uid){
        this.uid=uid;
        this.sr_no=sr_no;
        this.present=present;
    }
}