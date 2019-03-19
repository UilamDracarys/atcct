package com.scbpfsdgis.atcct.data.model;

/**
 * Created by William on 3/18/2019.
 */

public class Farms {


        public static final String TABLE_FARMS = "T_FARMS";

        //Farms Table
        public static final String COL_FARMCODE = "C_FARMCODE";
        public static final String COL_FIELDGRP = "ATT_FARM_FieldGrp_CODE";
        public static final String COL_BASE = "ATT_FARM_Base_CODE";
        public static final String COL_OWNERID = "I_OWNER_ID";

        private String farmCode;
        private String fieldGrp;
        private String base;
        private String ownerID;

        public String getFarmCode() {
            return farmCode;
        }

        public void setFarmCode(String farmCode) {
            this.farmCode = farmCode;
        }

        public String getFieldGrp() {
            return fieldGrp;
        }

        public void setFieldGrp(String fieldGrp) {
            this.fieldGrp = fieldGrp;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getOwnerID() {
            return ownerID;
        }

        public void setOwnerID(String ownerID) {
            this.ownerID = ownerID;
        }

}
