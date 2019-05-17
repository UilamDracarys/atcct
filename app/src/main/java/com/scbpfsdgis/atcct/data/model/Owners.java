package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Owners {
    public static final String TABLE_OWNERS = "T_OWNERS";
    public static final String TABLE_OWNERS_CHANGES = "T_OWNERS_CHGS";

    //Owners Table
    public static final String COL_OWNERID = "OWNER_ID";
    public static final String COL_OWNERNAME = "OWNER_NAME";
    public static final String COL_OWNERMOB = "OWNER_MOBILE";
    public static final String COL_OWNEREMAIL = "OWNER_EMAIL";
    public static final String COL_OWNERADDRESS = "OWNER_ADDRESS";
    public static final String COL_BASES = "OWNER_BASES";

    private String ownerID;
    private String ownerName;
    private String ownerMobile;
    private String ownerEmail;
    private String ownerAddress;
    private String ownerBases;

    public String getOwnerBases() {
        return ownerBases;
    }

    public void setOwnerBases(String ownerBases) {
        this.ownerBases = ownerBases;
    }

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

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }
}
