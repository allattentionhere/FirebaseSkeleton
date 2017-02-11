package com.allattentionhere.show.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by krupenghetiya on 11/02/17.
 */

@IgnoreExtraProperties
public class User {

    private String loginprovider_id;
    private String email;
    private String profileImageUrl;
    private String first_name;
    private String last_name;
    private String login_provider;
    private String push_token;
    private String firebase_userid;
    private Object time_stamp;

    public User() {
    }

    public User(String loginprovider_id, String email, String profileImageUrl, String first_name, String last_name) {
        this.loginprovider_id = loginprovider_id;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Object getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Object time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getLoginprovider_id() {
        return loginprovider_id;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getLogin_provider() {
        return login_provider;
    }

    public String getPush_token() {
        return push_token;
    }

    public String getFirebase_userid() {
        return firebase_userid;
    }

    public void setLoginprovider_id(String loginprovider_id) {
        this.loginprovider_id = loginprovider_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setLogin_provider(String login_provider) {
        this.login_provider = login_provider;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public void setFirebase_userid(String firabase_userid) {
        this.firebase_userid = firabase_userid;
    }
}
