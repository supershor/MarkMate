package com.apify.markmate;

public class sub_list {
    String name;
    String description;
    int start;int end;Boolean has_unique_id;
    public sub_list(String name, String description,int start,int end,Boolean has_unique_id){
        this.name=name;
        this.description=description;
        this.start=start;
        this.end=end;
        this.has_unique_id=has_unique_id;
    }
}
