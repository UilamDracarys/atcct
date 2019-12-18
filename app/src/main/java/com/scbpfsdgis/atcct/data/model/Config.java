package com.scbpfsdgis.atcct.data.model;

public class Config {

    public static final String TABLE_CFG = "T_CFG";

    public static final String COL_COOR_NAME = "CFG_COOR";
    public static final String COL_LOGIN_DATE = "CFG_LOGIN";
    public static final String COL_LOGOUT_DATE = "CFG_LOGOUT";

    private String cfgCoor;
    private String cfgLogin;
    private String cfgLogout;

    public String getCfgCoor() {
        return cfgCoor;
    }

    public void setCfgCoor(String cfgCoor) {
        this.cfgCoor = cfgCoor;
    }

    public String getCfgLogin() {
        return cfgLogin;
    }

    public void setCfgLogin(String cfgLogin) {
        this.cfgLogin = cfgLogin;
    }

    public String getCfgLogout() {
        return cfgLogout;
    }

    public void setCfgLogout(String cfgLogout) {
        this.cfgLogout = cfgLogout;
    }
}
