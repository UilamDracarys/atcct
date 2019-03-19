package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Owners {
    public static final String TABLE_OWNERS = "T_OWNERS";

    //Owners Table
    public static final String COL_OWNERID = "I_OWNER_ID";
    public static final String COL_OWNERNAME = "C_OWNER_NAME";
    public static final String COL_PHONEMOB = "C_PHONE_MOBILE";
    public static final String COL_PHONEBUS = "C_PHONE_BUSINESS";
    public static final String COL_CONTACTPERSON = "C_CONTACT_PERSON";

    private String ownerID;
    private String ownerName;
    private String mobileNo;
    private String businessNo;
    private String contactPrsn;

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getContactPrsn() {
        return contactPrsn;
    }

    public void setContactPrsn(String contactPrsn) {
        this.contactPrsn = contactPrsn;
    }
}
