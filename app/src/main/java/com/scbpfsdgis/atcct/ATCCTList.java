package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ATCCTList extends AppCompatActivity {

    SpinnerDialog spnPlanter;
    ArrayList<String> ownersList = new ArrayList<>();
    String selPlanter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcct_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadOwners();

        spnPlanter = new SpinnerDialog(ATCCTList.this, ownersList, "Select Planter");
        spnPlanter.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                selPlanter = s;
                if (selPlanter != null) {
                    System.out.println("Selected planter: " + selPlanter);
                    newATCC(selPlanter);
                } else {
                    System.out.println("No planter selected.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                spnPlanter.showSpinerDialog();
                return true;

            case R.id.action_back:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newATCC(String owner) {
        Intent intent = new Intent(this, ATCCTDetails.class);
        intent.putExtra("action", "New ATCCT");
        intent.putExtra("owner", owner);
        intent.putExtra("ownerID", owner.substring(owner.indexOf("[")+1, owner.indexOf("]")));
        startActivity(intent);
    }

    private void loadOwners() {
        final OwnersRepo oRepo = new OwnersRepo();
        ArrayList<String> list = oRepo.getOwnersForSpinner();
        ownersList = list;
    }
}
