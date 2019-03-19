package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Users {

    public static final String TABLE_USERS = "T_USERS";

    //Users Table
    public static final String COL_USRID = "USERID";
    public static final String COL_USRNAME = "USERNAME";
    public static final String COL_USRFULLNAME = "NAME";
    public static final String COL_USRPOST = "POSITION";
    public static final String COL_USRDEPT = "DEPT";
    public static final String COL_USRPW = "PASSWORD";

    private String userID;
    private String userName;
    private String userFullname;
    private String userPost;
    private String userDept;
    private String userPW;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getUserPost() {
        return userPost;
    }

    public void setUserPost(String userPost) {
        this.userPost = userPost;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }
}
