package com.scbpfsdgis.atcct;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.widget.EditText;

import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

public class OwnerDetails extends AppCompatActivity {
    String ownerID;
    EditText etOwnerName, etMobile, etBusiness, etContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_details);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        ownerID = intent.getStringExtra("ownerID");

        init(ownerID);

    }

    private void init(String ownerID) {
        etOwnerName = findViewById(R.id.etOwnerName);
        etMobile = findViewById(R.id.etMobile);
        etBusiness = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etAddress);

        OwnersRepo oRepo = new OwnersRepo();
        Owners owner = oRepo.getOwnerByID(ownerID);
        etOwnerName.setText(owner.getOwnerName());
        etMobile.setText(owner.getOwnerMobile());
        etBusiness.setText(owner.getOwnerEmail());
        etContact.setText(owner.getOwnerAddress());
    }
}
