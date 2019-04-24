package com.scbpfsdgis.atcct.data.model;

import java.sql.Date;

/**
 * Created by William on 3/18/2019.
 */

public class ATCC {

    public static final String TABLE_ATCC = "T_ATCC";

    //ATCC Table
    public static final String COL_ATCCNO = "ATCC_NO";
    public static final String COL_OWNERID = Owners.COL_OWNERID;
    public static final String COL_PMTMETHOD = "PAYMENT_METHOD";
    public static final String COL_PICKUPPOINT = "PICKUP_PT";
    public static final String COL_ACCNAME = "ACC_NAME";
    public static final String COL_ACCNO = "ACC_NO";
    public static final String COL_BANKNAME = "BANK_NAME";
    public static final String COL_BANKADD = "BANK_ADDRESS";
    public static final String COL_REMARKS = "REMARKS";
    public static final String COL_DTECREATED = "DATE_CREATED";
    public static final String COL_DTESIGNED = "DATE_SIGNED";

    private String atccNo;
    private String ownerID;
    private String pmtMethod;
    private String pickupPt;
    private String accName;
    private String accNo;
    private String bankName;
    private String bankAdd;
    private String Remarks;
    private String dteCreated;
    private String dteSigned;


    public String getPickupPt() {
        return pickupPt;
    }

    public void setPickupPt(String pickupPt) {
        this.pickupPt = pickupPt;
    }

    public String getAtccNo() {
        return atccNo;
    }

    public void setAtccNo(String atccNo) {
        this.atccNo = atccNo;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getPmtMethod() {
        return pmtMethod;
    }

    public void setPmtMethod(String pmtMethod) {
        this.pmtMethod = pmtMethod;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAdd() {
        return bankAdd;
    }

    public void setBankAdd(String bankAdd) {
        this.bankAdd = bankAdd;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getDteCreated() {
        return dteCreated;
    }

    public void setDteCreated(String dteCreated) {
        this.dteCreated = dteCreated;
    }

    public String getDteSigned() {
        return dteSigned;
    }

    public void setDteSigned(String dteSigned) {
        this.dteSigned = dteSigned;
    }


    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }
}
