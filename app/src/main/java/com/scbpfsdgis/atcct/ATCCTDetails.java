package com.scbpfsdgis.atcct;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.util.ArrayList;
import java.util.HashMap;

public class ATCCTDetails extends AppCompatActivity {


    Button addAR;
    TextView tvOwner, tvOwnerDetails;
    Spinner spnPmtMethod, spnPickup;
    EditText etAccName, etAccNo, etBankName, etBankAdd;
    ArrayList<HashMap<String, String>> arList;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcct_details);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        String owner = intent.getStringExtra("owner");
        String ownerID = intent.getStringExtra("ownerID");
        getSupportActionBar().setTitle(action);

        tvOwner = findViewById(R.id.tvOwnerName);
        tvOwnerDetails = findViewById(R.id.tvOwnerDetails);
        spnPmtMethod = findViewById(R.id.spnPmtMethod);
        spnPickup = findViewById(R.id.spnPickupPt);
        etAccName = findViewById(R.id.etAccName);
        etAccNo = findViewById(R.id.etAccNo);
        etBankName = findViewById(R.id.etBankName);
        etBankAdd = findViewById(R.id.etBankName);
        addAR = findViewById(R.id.btnAddAR);
        lv = findViewById(R.id.arList);
        arList = new ArrayList<>();

        OwnersRepo repo = new OwnersRepo();
        Owners own = repo.getOwnerByID(ownerID);


        spnPmtMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnPmtMethod.getSelectedItemPosition() == 1) {
                    spnPickup.setVisibility(View.VISIBLE);
                    etAccName.setVisibility(View.GONE);
                    etAccNo.setVisibility(View.GONE);
                    etBankName.setVisibility(View.GONE);
                    etBankAdd.setVisibility(View.GONE);
                } else if (spnPmtMethod.getSelectedItemPosition() == 2) {
                    spnPickup.setVisibility(View.GONE);
                    etAccName.setVisibility(View.VISIBLE);
                    etAccNo.setVisibility(View.VISIBLE);
                    etBankName.setVisibility(View.VISIBLE);
                    etBankAdd.setVisibility(View.VISIBLE);
                } else {
                    spnPickup.setVisibility(View.GONE);
                    etAccName.setVisibility(View.GONE);
                    etAccNo.setVisibility(View.GONE);
                    etBankName.setVisibility(View.GONE);
                    etBankAdd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });


        tvOwner.setText(owner);
        tvOwnerDetails.setText("Mobile No.: " + own.getOwnerMobile() + "\n" +
                "Email: " +  own.getOwnerEmail() + "\n" +
                "Address: " + own.getOwnerAddress());

        addAR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lv.getCount() < 3) {
                    showDialog(0);
                } else {
                    Toast.makeText(ATCCTDetails.this, "Maximum number of Authorized Representatives exceeded. Can no longer add.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void onRestart() {
        super.onRestart();
        refreshARList();
    }

    @SuppressLint("WrongConstant")
    protected Dialog onCreateDialog(int id) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
        ll.setPadding(50, 50, 50, 50);
        final EditText arName = new EditText(this);
        arName.setHint("Fullname");
        final EditText arRelation = new EditText(this);
        arRelation.setHint("Relationship to owner");
        final EditText arID = new EditText(this);
        arID.setHint("ID Type");
        ll.addView(arName);
        ll.addView(arRelation);
        ll.addView(arID);
        alert.setView(ll);
        alert.setTitle("Add Authorized Rep.");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (arName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Name required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (arRelation.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Relationship required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (arID.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "ID Type required", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_SHORT).show();
                addAR(arName.getText().toString(), arRelation.getText().toString(), arID.getText().toString());
                refreshARList();
                arName.setText("");
                arRelation.setText("");
                arID.setText("");
                arName.requestFocus();

            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        return alert.create();
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

    }

    private void refreshARList() {
        ListView lv = findViewById(R.id.arList);
        lv.setFastScrollEnabled(true);
        ListAdapter adapter;
        adapter = new SimpleAdapter(ATCCTDetails.this, arList, R.layout.ar_list_item, new String[]{"arName", "relationID"}, new int[]{R.id.arName, R.id.arRelation});
        lv.setAdapter(adapter);
    }

    private void addAR(String name, String rel, String id) {
        HashMap<String, String> authRep = new HashMap<>();
        authRep.put("arName", name);
        authRep.put("arRel", rel);
        authRep.put("arID", id);
        authRep.put("relationID", rel + " | " + id);
        arList.add(authRep);
    }


}
