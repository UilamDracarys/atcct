package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class FarmDetails extends AppCompatActivity {
    SpinnerDialog spnPlanter;

    Spinner spnBase, spnAvail;
    TextView tvFarmCode, tvPlanter, tvCurFarm;
    EditText etFarmName, etComments;
    private View mLayout;
    String farmCode;
    ArrayList<String> ownersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);
        mLayout = findViewById(R.id.farm_detail_layout);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        farmCode = intent.getStringExtra("farmCode");

        loadOwners();
        init(farmCode);
    }

    private void init(String farmCode) {
        //Initialize fields
        spnBase = findViewById(R.id.spnBase);
        spnAvail = findViewById(R.id.spnAvail);
        tvPlanter = findViewById(R.id.tvPlanter);
        tvFarmCode = findViewById(R.id.farmCode);
        etFarmName = findViewById(R.id.etFarmName);
        etComments = findViewById(R.id.etFarmCmt);
        tvCurFarm = findViewById(R.id.currentFarm);

        tvFarmCode.setText(farmCode);
        FarmsRepo repo = new FarmsRepo();
        Farms farm = repo.getFarmByID(farmCode, "M");

        OwnersRepo oRepo = new OwnersRepo();
        Owners owner = oRepo.getOwnerByID(farm.getFarmOwnerID(), "M");

        etFarmName.setText(farm.getFarmName());
        tvPlanter.setText(owner.getOwnerName() + " [" + farm.getFarmOwnerID() + "]");
        int selBase = farm.getIdxByItem(getResources().getStringArray(R.array.bases), farm.getFarmBase());
        spnBase.setSelection(selBase);
        int selAvail = farm.getIdxByCode(getResources().getStringArray(R.array.availability), farm.getFarmStatus());
        spnAvail.setSelection(selAvail);
        spnPlanter = new SpinnerDialog(FarmDetails.this, ownersList, "Select Planter");
        spnPlanter.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                tvPlanter.setText(s);
            }
        });
        tvPlanter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnPlanter.showSpinerDialog();
            }
        });
        etComments.setText(farm.getFarmRemarks());
    }

    private void loadOwners() {
        final OwnersRepo oRepo = new OwnersRepo();
        ArrayList<String> list = oRepo.getOwnersForSpinner();
        ownersList = list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;

            case R.id.action_cancel:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        FarmsRepo repo = new FarmsRepo();
        Farms farm = repo.getFarmByID(farmCode, "O");
        String farmName = etFarmName.getText().toString().trim();
        String base = spnBase.getSelectedItem().toString();
        String statusStr = spnAvail.getSelectedItem().toString();
        String statusCode = statusStr.substring(statusStr.indexOf("(") + 1, statusStr.indexOf(")"));
        String planter = tvPlanter.getText().toString();
        String ownerID = planter.substring(planter.indexOf("[") + 1, planter.indexOf("]"));
        String remarks = etComments.getText().toString().trim();

        if (!farmName.equalsIgnoreCase(farm.getFarmName())) {
            System.out.println("Farm name changed to " + farmName);
        } else {
            System.out.println("Farm name not changed.");
            farmName = null;
        }

        if (!base.equalsIgnoreCase(farm.getFarmBase())) {
            System.out.println("Base changed to " + base);
        } else {
            System.out.println("Base not changed.");
            base = null;
        }

        if (!statusCode.equalsIgnoreCase(farm.getFarmStatus())) {
            System.out.println("Status changed to " + statusCode);
        } else {
            System.out.println("Status not changed.");
            statusCode = null;
        }

        if (!ownerID.equalsIgnoreCase(farm.getFarmOwnerID())) {
            System.out.println("Owner changed to " + ownerID);
        } else {
            System.out.println("Owner not changed.");
            ownerID = null;
        }

        farm.setFarmName(farmName);
        farm.setFarmBase(base);
        farm.setFarmStatus(statusCode);
        farm.setFarmOwnerID(ownerID);
        farm.setFarmRemarks(remarks);

        if (!repo.isChgExist(farmCode)) {
            repo.insertChange(farm);
        } else {
            repo.updateChange(farm);
        }

        farm = repo.getFarmByID("", "O");
        System.out.println("Farm is " + farm.getFarmCode());
        Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String currentFarm(String farmCode) {
        String ownerName = tvPlanter.getText().toString();
        FarmsRepo repo = new FarmsRepo();
        Farms farm = repo.getFarmByID(farmCode, "O");
        return "Farmcode: " + farm.getFarmCode() + "\n" +
                "Farmname: " + farm.getFarmName() + "\n" +
                "Base: " + farm.getFarmBase() + "\n" +
                "Status: " + farm.getFarmStatus() + "\n" +
                "OwnerID: " + farm.getFarmOwnerID() + "\n" +
                "Remarks: " + farm.getFarmRemarks() + "\n" +
                "OwnerID2: " + ownerName.substring(ownerName.indexOf("[") + 1, ownerName.indexOf("]"));
    }


}
