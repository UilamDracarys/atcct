package com.scbpfsdgis.atcct;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.AuthRep;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.AuthRepRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ATCCTDetails extends AppCompatActivity {


    Button addAR;
    TextView tvOwner, tvOwnerDetails;
    Spinner spnPmtMethod, spnPickup;
    EditText etAccName, etAccNo, etBankName, etBankAdd, etRemarks;
    ArrayList<HashMap<String, String>> arList;
    ArrayList<HashMap<String, String>> farmsList;
    String action;
    String ownerID;
    String atcctNo;
    ListView lvAuthRep;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcct_details);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        mLayout = findViewById(R.id.atcct_detail_layout);
        setSupportActionBar(myToolbar);

        tvOwner = findViewById(R.id.tvOwnerName);
        tvOwnerDetails = findViewById(R.id.tvOwnerDetails);
        spnPmtMethod = findViewById(R.id.spnPmtMethod);
        spnPickup = findViewById(R.id.spnPickupPt);
        etAccName = findViewById(R.id.etAccName);
        etAccNo = findViewById(R.id.etAccNo);
        etBankName = findViewById(R.id.etBankName);
        etBankAdd = findViewById(R.id.etBankAdd);
        etRemarks = findViewById(R.id.etRemarks);
        addAR = findViewById(R.id.btnAddAR);
        lvAuthRep = findViewById(R.id.arList);
        arList = new ArrayList<>();
        farmsList = new ArrayList<>();
        spnPmtMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initPmtDetails(spnPmtMethod.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        addAR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lvAuthRep.getCount() < 3) {
                    showDialog(0);
                } else {
                    Toast.makeText(ATCCTDetails.this, "Maximum number of Authorized Representatives exceeded. Can no longer add.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        lvAuthRep.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                PopupMenu popup = new PopupMenu(ATCCTDetails.this, view);
                popup.inflate(R.menu.popup_menu);
                MenuItem edit = popup.getMenu().getItem(0);
                edit.setVisible(false);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                arList.remove(position);
                                refreshARList();
                                return true;
                            default:
                                return onOptionsItemSelected(item);
                        }
                    }
                });
                popup.show();
                return true;
            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        getSupportActionBar().setTitle(action);

        ATCCRepo atccRepo = new ATCCRepo();
        ATCC atcc = null;
        atcctNo = "";

        OwnersRepo ownersRepo = new OwnersRepo();
        Owners owner = null;
        String ownerName = "";

        Farms farms = new Farms();
        AuthRepRepo authRepRepo = new AuthRepRepo();

        String accName = "",
                accNo = "",
                bankName = "",
                bankAddress = "",
                remarks = "";
        int selPmtMethod;

        if (action.equalsIgnoreCase("New ATCCT")) {
            ownerID = intent.getStringExtra("ownerID");
        } else {
            atcctNo = intent.getStringExtra("atccNo");
            atcc = atccRepo.getATCCByNo(atcctNo);
            ownerID = atcc.getOwnerID();
            accName = atcc.getAccName();
            accNo = atcc.getAccNo();
            bankName = atcc.getBankName();
            bankAddress = atcc.getBankAdd();
            remarks = atcc.getRemarks();

            spnPmtMethod.setSelection(farms.getIdxByItem(getResources().getStringArray(R.array.pmtMethod), atcc.getPmtMethod()));
            selPmtMethod = spnPmtMethod.getSelectedItemPosition();
            initPmtDetails(selPmtMethod);
            if (selPmtMethod == 1) {
                spnPickup.setSelection(farms.getIdxByItem(getResources().getStringArray(R.array.pickupPt), atcc.getPickupPt()));
            } else {
                etAccName.setText(accName);
                etAccNo.setText(accNo);
                etBankName.setText(bankName);
                etBankAdd.setText(bankAddress);
            }
            etRemarks.setText(remarks);
            arList = authRepRepo.getAuthRepForOwner(ownerID);
            for (int i = 0; i < arList.size(); i++) {
                System.out.println("AuthRep" + i + ": " + arList.get(i).get(0) + ", " + arList.get(i).get(1) + ", " + arList.get(i).get(2));
            }
            refreshARList();
        }

        owner = ownersRepo.getOwnerByID(ownerID, "M");
        ownerName = owner.getOwnerName();

        tvOwner.setText(ownerName);
        tvOwnerDetails.setText("Mobile No.: " + owner.getOwnerMobile() + "\n" +
                "Email: " + owner.getOwnerEmail() + "\n" +
                "Address: " + owner.getOwnerAddress() + "\n" +
                "Farms: \n" + ownersRepo.consolFarms(ownerID));


    }

    private void initPmtDetails(int sel) {
        if (sel == 1) {
            spnPickup.setVisibility(View.VISIBLE);
            etAccName.setVisibility(View.GONE);
            etAccNo.setVisibility(View.GONE);
            etBankName.setVisibility(View.GONE);
            etBankAdd.setVisibility(View.GONE);
        } else if (sel == 2) {
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

        ATCCRepo repo = new ATCCRepo();
        ATCC atcc = new ATCC();
        AuthRep authRep = new AuthRep();
        AuthRepRepo arRepo = new AuthRepRepo();

        String pmtMethod = spnPmtMethod.getSelectedItem().toString();
        String pickUpPt = "", accName = "", accNo = "", bankName = "", bankAdd = "";
        SimpleDateFormat yearDF = new SimpleDateFormat("yy", Locale.getDefault());
        SimpleDateFormat dateDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DecimalFormat ownIDFormat = new DecimalFormat("00000");
        String ownID;
        if (ownerID.startsWith("N-")) {
            ownID = ownerID;
        } else {
            ownID = ownIDFormat.format(Integer.parseInt(ownerID));
        }
        String atccNo = yearDF.format(new Date()) + "-" + ownID;
        String createDate = dateDF.format(new Date());
        String remarks = etRemarks.getText().toString();
        int arListSize = arList.size();

        if (spnPmtMethod.getSelectedItemPosition() == 2) {
            pickUpPt = "N/A";
        } else {
            pickUpPt = spnPickup.getSelectedItem().toString();
        }
        if (spnPmtMethod.getSelectedItemPosition() == 1) {
            accName = "N/A";
            accNo = "N/A";
            bankName = "N/A";
            bankAdd = "N/A";
        } else {
            accName = etAccName.getText().toString();
            accNo = etAccNo.getText().toString();
            bankName = etBankName.getText().toString();
            bankAdd = etBankAdd.getText().toString();
        }

        if (isValid()) {
            atcc.setAtccNo(atccNo);
            atcc.setOwnerID(ownerID);
            atcc.setPmtMethod(pmtMethod);
            atcc.setPickupPt(pickUpPt);
            atcc.setAccName(accName);
            atcc.setAccNo(accNo);
            atcc.setBankName(bankName);
            atcc.setBankAdd(bankAdd);
            atcc.setDteCreated(createDate);
            atcc.setRemarks(remarks);

            if (action.equalsIgnoreCase("New ATCCT")) {
                repo.insert(atcc);
                if (arListSize > 0) {
                    for (int i = 0; i < arListSize; i++) {
                        authRep.setOwnerID(ownerID);
                        authRep.setArFullName(arList.get(i).get("ARName"));
                        authRep.setArRelation(arList.get(i).get("ARRel"));
                        authRep.setArIDType(arList.get(i).get("ARIDType"));
                        arRepo.insert(authRep);
                    }
                }
                Toast.makeText(this, "ATCCT No. " + atccNo + " created on " + createDate, Toast.LENGTH_SHORT).show();
            } else {
                atcc.setAtccNo(atcctNo);
                atcc.setDteModified(createDate);
                repo.updateATCCT(atcc, "");
                if (arListSize > 0) {
                    arRepo.delete(ownerID);
                    for (int i = 0; i < arListSize; i++) {
                        authRep.setOwnerID(ownerID);
                        authRep.setArFullName(arList.get(i).get("ARName"));
                        authRep.setArRelation(arList.get(i).get("ARRel"));
                        authRep.setArIDType(arList.get(i).get("ARIDType"));
                        arRepo.insert(authRep);
                    }
                }  else {
                    arRepo.delete(ownerID);
                }
                Toast.makeText(this, "ATCCT No. " + atccNo + " modified on " + createDate, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private Boolean isValid() {
        if (spnPmtMethod.getSelectedItemPosition() == 0) {
            Snackbar.make(mLayout, "Select a payment method.", Snackbar.LENGTH_SHORT).show();
            spnPmtMethod.requestFocus();
            return false;
        }

        if (spnPmtMethod.getSelectedItemPosition() == 1 && spnPickup.getSelectedItemPosition() == 0) {
            Snackbar.make(mLayout, "Select a pickup destination.", Snackbar.LENGTH_SHORT).show();
            spnPmtMethod.requestFocus();
            return false;
        }

        if (spnPmtMethod.getSelectedItemPosition() == 2) {
            if (etAccName.getText().toString().isEmpty()) {
                Snackbar.make(mLayout, "Account name required.", Snackbar.LENGTH_SHORT).show();
                etAccName.requestFocus();
                return false;
            }

            if (etAccNo.getText().toString().isEmpty()) {
                Snackbar.make(mLayout, "Account no. required.", Snackbar.LENGTH_SHORT).show();
                etAccNo.requestFocus();
                return false;
            }

            if (etBankName.getText().toString().isEmpty()) {
                Snackbar.make(mLayout, "Bank name required.", Snackbar.LENGTH_SHORT).show();
                etBankName.requestFocus();
                return false;
            }

            if (etBankAdd.getText().toString().isEmpty()) {
                Snackbar.make(mLayout, "Bank address/branch required.", Snackbar.LENGTH_SHORT).show();
                etBankAdd.requestFocus();
                return false;
            }
            return true;
        }

        return true;
    }

    private void refreshARList() {
        lvAuthRep = findViewById(R.id.arList);
        lvAuthRep.setFastScrollEnabled(true);
        ListAdapter adapter;
        adapter = new SimpleAdapter(ATCCTDetails.this, arList, R.layout.ar_list_item, new String[]{"ARName", "ARRelID"}, new int[]{R.id.arName, R.id.arRelation});
        lvAuthRep.setAdapter(adapter);
    }

    private void addAR(String name, String rel, String id) {
        HashMap<String, String> authRep = new HashMap<>();
        authRep.put("ARName", name);
        authRep.put("ARRel", rel);
        authRep.put("ARIDType", id);
        authRep.put("ARRelID", rel + " | " + id);
        arList.add(authRep);
    }


}
