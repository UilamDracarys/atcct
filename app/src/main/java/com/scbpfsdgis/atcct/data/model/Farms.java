package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Farms {

    public static final String TABLE_FARMS = "T_FARMS";
    public static final String TABLE_MASTERTBL = "T_MASTER";
    public static final String TABLE_FARM_CHANGES = "T_FARM_CHGS";

    //Farms Table
    public static final String COL_FARMCODE = "FARMCODE";
    public static final String COL_FARMNAME = "FARM_NAME";
    public static final String COL_BASE = "FARM_BASE";
    public static final String COL_STATUS = "FARM_STATUS";
    public static final String COL_OWNERID = "FARM_OWNERID";
    public static final String COL_REMARKS = "FARM_REMARKS";

    private String farmCode;
    private String farmName;
    private String farmBase;
    private String farmStatus;
    private String farmOwnerID;
    private String farmRemarks;

    public String getFarmCode() {
            return farmCode;
        }

    public void setFarmCode(String farmCode) {
        this.farmCode = farmCode;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmBase() {
        return farmBase;
    }

    public void setFarmBase(String farmBase) {
        this.farmBase = farmBase;
    }

    public String getFarmOwnerID() {
        return farmOwnerID;
    }

    public void setFarmOwnerID(String farmOwnerID) {
        this.farmOwnerID = farmOwnerID;
    }

    public String getFarmRemarks() {
        return farmRemarks;
    }

    public void setFarmRemarks(String farmRemarks) {
        this.farmRemarks = farmRemarks;
    }

    public String getFarmStatus() {
        return farmStatus;
    }

    public void setFarmStatus(String farmStatus) {
        this.farmStatus = farmStatus;
    }

    public int getIdxByItem(String[] array, String att) {
        int idx = 0;
        for (int i=0; i <array.length; i++) {
            if(array[i].contains(att)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public int getIdxByCode(String[] array, String att) {
        int idx = 0;
        for (int i=0; i <array.length; i++) {
            if(array[i].contains("(" + att + ")")) {
                idx = i;
                break;
            }
        }
        return idx;
    }

}
