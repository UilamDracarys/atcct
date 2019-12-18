package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Contact {

    public static final String TABLE_CONTACT = "T_CONTACT";


    //Owners Table
    public static final String COL_NAME = "CONT_NAME";
    public static final String COL_NUM = "CONT_NUM";
    public static final String COL_FARMCODE = "CONT_FARMCODE";

    private String contName;
    private String contNum;
    private String contFarmCode;


    public String getContName() {
        return contName;
    }

    public void setContName(String contName) {
        this.contName = contName;
    }

    public String getContNum() {
        return contNum;
    }

    public void setContNum(String contNum) {
        this.contNum = contNum;
    }

    public String getContFarmCode() {
        return contFarmCode;
    }

    public void setContFarmCode(String contFarmCode) {
        this.contFarmCode = contFarmCode;
    }
}
