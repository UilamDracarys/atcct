package com.scbpfsdgis.atcct.data.model;

import com.itextpdf.kernel.pdf.filespec.PdfStringFS;

public class SMS {
    public static final int REPLACE = 0;
    public static final int APPEND = 1;

    public static final String TABLE_SMS =  "T_SMS";

    public static final String COL_ID = "SMS_ID";
    public static final String COL_DATE = "SMS_DATE";
    public static final String COL_FARM = "SMS_FARM";
    public static final String COL_FIELD = "SMS_FIELD";
    public static final String COL_TOTAL_AREA = "SMS_TOTAL_AREA";
    public static final String COL_STATUS = "SMS_STATUS";
    public static final String COL_AREA = "SMS_AREA";
    public static final String COL_CLUSTER = "SMS_CLUSTER";
    public static final String COL_RMKS = "SMS_REMARKS";

    private String smsID;
    private String smsDate;
    private String smsFarm;
    private String smsField;
    private int smsTotalArea;
    private String smsStatus;
    private int smsArea;
    private String smsCluster;
    private String smsRemarks;

    public int getSmsTotalArea() {
        return smsTotalArea;
    }

    public void setSmsTotalArea(int smsTotalArea) {
        this.smsTotalArea = smsTotalArea;
    }

    public int getSmsArea() {
        return smsArea;
    }

    public void setSmsArea(int smsArea) {
        this.smsArea = smsArea;
    }

    public String getSmsID() {
        return smsID;
    }

    public void setSmsID(String smsID) {
        this.smsID = smsID;
    }

    public String getSmsDate() {
        return smsDate;
    }

    public void setSmsDate(String smsDate) {
        this.smsDate = smsDate;
    }

    public String getSmsFarm() {
        return smsFarm;
    }

    public void setSmsFarm(String smsFarm) {
        this.smsFarm = smsFarm;
    }

    public String getSmsField() {
        return smsField;
    }

    public void setSmsField(String smsField) {
        this.smsField = smsField;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }

    public String getSmsCluster() {
        return smsCluster;
    }

    public void setSmsCluster(String smsCluster) {
        this.smsCluster = smsCluster;
    }

    public String getSmsRemarks() {
        return smsRemarks;
    }

    public void setSmsRemarks(String smsRemarks) {
        this.smsRemarks = smsRemarks;
    }
}