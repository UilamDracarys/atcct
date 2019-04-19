package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 4/17/2019.
 */

public class AuthRep {

    public static final String TABLE_AUTHREP = "T_AUTH_REP";

    public static final String COL_OWNER_ID = Owners.COL_OWNERID;
    public static final String COL_AR_NAME = "AR_FULLNAME";
    public static final String COL_AR_REL = "AR_RELATION";
    public static final String COL_AR_IDTYPE = "AR_IDTYPE";

    private String ownerID;
    private String arFullName;
    private String arRelation;
    private String arIDType;

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getArFullName() {
        return arFullName;
    }

    public void setArFullName(String arFullName) {
        this.arFullName = arFullName;
    }

    public String getArRelation() {
        return arRelation;
    }

    public void setArRelation(String arRelation) {
        this.arRelation = arRelation;
    }

    public String getArIDType() {
        return arIDType;
    }

    public void setArIDType(String arIDType) {
        this.arIDType = arIDType;
    }
}
