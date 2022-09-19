package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.SMS;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class SendUpdate extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    String title, cluster, selFarm;
    EditText mFarmName, mFieldNo, mTotalArea, mArea, mHarvDate, mRmks;
    TextView mSMSPreview, mCharCount;
    Spinner mHarvState;
    CheckBox mErratum;
    Button mReplace, mAppend, mClear;
    ArrayList<HashMap<String, String>> smsBody;
    ArrayList<HashMap<String, String>> smsDetail;
    ArrayList<String> farmsList = new ArrayList<>();
    SpinnerDialog spnFarms;
    Date harvestDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_update);

        /*Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        getSupportActionBar().setTitle(title);

        loadFarms();
        init();

    }

    private void init() {
        mSMSPreview = findViewById(R.id.etSMSPreview);
        mCharCount = findViewById(R.id.txtCharCount);
        mFarmName = findViewById(R.id.etFarmName);
        mFarmName.setOnClickListener(this);
        mFarmName.setKeyListener(null);
        mFieldNo = findViewById(R.id.etFieldNo);
        mTotalArea = findViewById(R.id.etTotalArea);
        mHarvState = findViewById(R.id.spnHarvState);
        mArea = findViewById(R.id.etArea);
        mHarvDate = findViewById(R.id.etHarvDate);
        mHarvDate.setOnClickListener(this);
        mHarvDate.setKeyListener(null);
        mRmks = findViewById(R.id.etRemarks);
        mErratum = findViewById(R.id.chkErratum);
        mReplace = findViewById(R.id.btnReplace);
        mReplace.setOnClickListener(this);
        mAppend = findViewById(R.id.btnAppend);
        mAppend.setOnClickListener(this);
        mClear = findViewById(R.id.btnClear);
        mClear.setOnClickListener(this);


        spnFarms = new SpinnerDialog(SendUpdate.this, farmsList, "Select Farm");
        spnFarms.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                selFarm = s;
                mFarmName.setText(selFarm);
            }
        });

        mHarvState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equalsIgnoreCase("CROP")) {
                    mHarvDate.setVisibility(View.VISIBLE);
                } else {
                    mHarvDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });
    }

    private void loadFarms() {
        final FarmsRepo repo = new FarmsRepo();
        farmsList = repo.getFarmsForSpnSimp();
    }

    @Override
    public void onClick(View v) {
        if (v == mReplace || v == mAppend) {
            if (isValid()) {
                if (v == mReplace) {
                    mSMSPreview.setText(updateSMS(SMS.REPLACE, mSMSPreview.getText().toString()));
                } else if (v == mAppend) {
                    mSMSPreview.setText(updateSMS(SMS.APPEND, mSMSPreview.getText().toString()));
                }
                mCharCount.setText("Character Count: " + mSMSPreview.getText().toString().length() + "\nLines: " + mSMSPreview.getLineCount());
                clearFields();
            }
        } else if (v == mClear) {
            mSMSPreview.setText("");
            clearFields();
        } else if (v == mFarmName) {
            spnFarms.showSpinerDialog();
        } else if (v == mHarvDate) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    SendUpdate.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "Harvest Date");
        }

    }

    private void clearFields() {
        mFarmName.setText("");
        mFieldNo.setText("");
        mTotalArea.setText("");
        mHarvState.setSelection(0);
        mArea.setText("");
        mRmks.setText("");
        mErratum.setChecked(false);
    }

    private String updateSMS(int action, String sms) {
        String smsUpdate = "";
        StringBuilder sb = new StringBuilder();
        switch (action) {
            case SMS.REPLACE:
                smsUpdate = buildSMS().toString();
                break;
            case SMS.APPEND:
                sb.append(sms).append("\n\n---\n\n").append(buildSMS().toString());
                smsUpdate = sb.toString();
                break;
            default:
                break;
        }
        return smsUpdate;
    }

    private StringBuilder buildSMS() {

        StringBuilder sb = new StringBuilder();

        if (mErratum.isChecked()) {
            sb.append("ERRATUM\n\n");
        }
        String farmName = mFarmName.getText().toString();
        String fieldNo = mFieldNo.getText().toString();
        String totalArea = mTotalArea.getText().toString();
        String harvState = mHarvState.getSelectedItem().toString();
        String area = mArea.getText().toString();
        String remarks = mRmks.getText().toString();
        sb.append("Farm: ").append(farmName).append("\n");
        sb.append("Fld: ").append(fieldNo).append("\n");
        sb.append("Total Area: ").append(totalArea).append("\n");
        sb.append("Status: ").append(harvState).append("\n");
        sb.append("Area: ").append(area);
        if (!remarks.equalsIgnoreCase("")) {
            sb.append("\nRmks: ").append(remarks);
        }

        return sb;
    }

    private boolean isValid() {
        String farmName = mFarmName.getText().toString();
        String fieldNo = mFieldNo.getText().toString();
        String totalArea = mTotalArea.getText().toString();
        String harvState = mHarvState.getSelectedItem().toString();
        String area = mArea.getText().toString();
        String harvestDate = mHarvDate.getText().toString();

        if (farmName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please choose a farm.", Toast.LENGTH_SHORT).show();
            mFarmName.requestFocus();
            return false;
        }

        if (fieldNo.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please input field no/s.", Toast.LENGTH_SHORT).show();
            mFieldNo.requestFocus();
            return false;
        }

        if (totalArea.equalsIgnoreCase("") || Double.parseDouble(totalArea) == 0)  {
            Toast.makeText(this, "Please input total area.", Toast.LENGTH_SHORT).show();
            mTotalArea.requestFocus();
            return false;
        }

        if (mHarvState.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please choose a harvest state.", Toast.LENGTH_SHORT).show();
            mHarvState.requestFocus();
            return false;
        }

        if (area.equalsIgnoreCase("") || Double.parseDouble(area) == 0)  {
            Toast.makeText(this, "Please input total area.", Toast.LENGTH_SHORT).show();
            mArea.requestFocus();
            return false;
        }

        if (harvState.equalsIgnoreCase("CROP") && harvestDate.equalsIgnoreCase("")) {
            Toast.makeText(this, "Harvest State is CROP. Please set the harvest date.", Toast.LENGTH_SHORT).show();
            mHarvDate.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        Date date = cal.getTime();
        harvestDate = date;
        DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        mHarvDate.setText(df.format(date));
    }
}