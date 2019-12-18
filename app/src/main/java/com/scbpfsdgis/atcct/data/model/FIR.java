package com.scbpfsdgis.atcct.data.model;

public class FIR {

    public static final String TABLE_FIR = "T_FIR";

    public static final String COL_ID = "FIR_ID";
    public static final String COL_FIRDATE = "FIR_DATE";
    public static final String COL_FARMCODE = "FIR_FARMCODE";
    public static final String COL_FLDNO = "FIR_FLDNO";
    public static final String COL_RFR_AREA = "FIR_RFRAREA";
    public static final String COL_OBST = "FIR_OBST";
    public static final String COL_HARVMETH = "FIR_HARVMETH";
    public static final String COL_FLAGS = "FIR_FLAGS";
    public static final String COL_START = "FIR_START";
    public static final String COL_END = "FIR_END";
    public static final String COL_NOTES = "FIR_NOTES";
    public static final String COL_MAP = "FIR_MAP";
    public static final String COL_COORNAME = "FIR_COORNAME";
    public static final String COL_FIRPATH = "FIR_PATH";

    private String firID;
    private String firDate;
    private String firFarmCode;
    private String firFldNo;
    private double firRFRArea;
    private String firObst;
    private String firHarvMeth;
    private int firFlags;
    private String firStart;
    private String firEnd;
    private String firNotes;
    private String firMap;
    private String firCoorName;
    private String firPath;

    public String getFirPath() {
        return firPath;
    }

    public void setFirPath(String firPath) {
        this.firPath = firPath;
    }

    public String getFirCoorName() {
        return firCoorName;
    }

    public void setFirCoorName(String firCoorName) {
        this.firCoorName = firCoorName;
    }

    public String getFirID() {
        return firID;
    }

    public void setFirID(String firID) {
        this.firID = firID;
    }

    public String getFirDate() {
        return firDate;
    }

    public void setFirDate(String firDate) {
        this.firDate = firDate;
    }

    public String getFirFarmCode() {
        return firFarmCode;
    }

    public void setFirFarmCode(String firFarmCode) {
        this.firFarmCode = firFarmCode;
    }

    public String getFirFldNo() {
        return firFldNo;
    }

    public void setFirFldNo(String firFldNo) {
        this.firFldNo = firFldNo;
    }

    public String getFirObst() {
        return firObst;
    }

    public void setFirObst(String firObst) {
        this.firObst = firObst;
    }

    public String getFirHarvMeth() {
        return firHarvMeth;
    }

    public void setFirHarvMeth(String firHarvMeth) {
        this.firHarvMeth = firHarvMeth;
    }

    public double getFirRFRArea() {
        return firRFRArea;
    }

    public void setFirRFRArea(double firRFRArea) {
        this.firRFRArea = firRFRArea;
    }

    public int getFirFlags() {
        return firFlags;
    }

    public void setFirFlags(int firFlags) {
        this.firFlags = firFlags;
    }

    public String getFirStart() {
        return firStart;
    }

    public void setFirStart(String firStart) {
        this.firStart = firStart;
    }

    public String getFirEnd() {
        return firEnd;
    }

    public void setFirEnd(String firEnd) {
        this.firEnd = firEnd;
    }

    public String getFirNotes() {
        return firNotes;
    }

    public void setFirNotes(String firNotes) {
        this.firNotes = firNotes;
    }

    public String getFirMap() {
        return firMap;
    }

    public void setFirMap(String firMap) {
        this.firMap = firMap;
    }

    public int[] getIndexArray(String[] selArray, String[] array) {
        int[] intIdxArray = new int[selArray.length];

        for (int i=0; i<selArray.length;i++) {
            intIdxArray[i] = getIdxByCode(array, selArray[i].trim());
        }
        return intIdxArray;
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
