package com.scbpfsdgis.atcct;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

public class OwnerDetails extends AppCompatActivity {
    String ownerID, action;
    EditText etOwnerName, etMobile, etEmail, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_details);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        ownerID = intent.getStringExtra("ownerID");
        action = intent.getStringExtra("action");
        System.out.println("Received owner ID: " + ownerID);
        init(ownerID);

    }

    private void init(String ownerID) {

        etOwnerName = findViewById(R.id.etOwnerName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);

        if (action.equalsIgnoreCase("New Owner")) {
            etOwnerName.setText("");
            etMobile.setText("");
            etEmail.setText("");
            etAddress.setText("");
        } else {
            OwnersRepo oRepo = new OwnersRepo();
            Owners owner = oRepo.getOwnerByID(ownerID, "M");
            etOwnerName.setText(owner.getOwnerName());
            etMobile.setText(owner.getOwnerMobile());
            etEmail.setText(owner.getOwnerEmail());
            etAddress.setText(owner.getOwnerAddress());
        }
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
        OwnersRepo ownersRepo = new OwnersRepo();
        Owners owner = new Owners();
        String ownerName = etOwnerName.getText().toString().trim();
        String ownerMobile = etMobile.getText().toString().trim();
        String ownerEmail = etEmail.getText().toString().trim();
        String ownerAddress = etAddress.getText().toString().trim();

        if (!action.equalsIgnoreCase("New Owner")) {
            owner = ownersRepo.getOwnerByID(ownerID, "M");
            if (!ownerName.equalsIgnoreCase(owner.getOwnerName())) {
                System.out.println("Owner name changed to " + ownerName);
            } else {
                System.out.println("Owner name not changed.");
                ownerName = null;
            }

            if (!ownerMobile.equalsIgnoreCase(owner.getOwnerMobile())) {
                System.out.println("Owner mobile changed to " + ownerMobile);
            } else {
                System.out.println("Owner mobile not changed.");
                ownerMobile = null;
            }

            if (!ownerEmail.equalsIgnoreCase(owner.getOwnerEmail())) {
                System.out.println("Owner email changed to " + ownerEmail);
            } else {
                System.out.println("Owner email not changed.");
                ownerEmail = null;
            }

            if (!ownerAddress.equalsIgnoreCase(owner.getOwnerAddress())) {
                System.out.println("Owner email changed to " + ownerAddress);
            } else {
                System.out.println("Owner email not changed.");
                ownerAddress = null;
            }
        }

        owner.setOwnerID(ownerID);
        owner.setOwnerName(ownerName);
        owner.setOwnerMobile(ownerMobile);
        owner.setOwnerEmail(ownerEmail);
        owner.setOwnerAddress(ownerAddress);

        if (action.equalsIgnoreCase("New Owner")) {
            ownersRepo.insert(owner, "Owner");
        } else {
            if (!ownersRepo.isChgExist(ownerID)) {
                ownersRepo.insert(owner, "Change");
            } else {
                ownersRepo.updateChange(owner);
            }
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
