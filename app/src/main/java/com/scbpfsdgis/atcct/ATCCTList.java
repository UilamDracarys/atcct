package com.scbpfsdgis.atcct;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ATCCTList extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_WRITESTORAGE = 0;
    private Menu menu;

    SpinnerDialog spnPlanter;
    ArrayList<String> ownersList = new ArrayList<>();
    String selPlanter;
    String ownerID;
    private View mLayout;
    ATCCRepo repo;
    TextView tvAtccNo;
    String atccNo, fileName;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcct_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLayout = findViewById(R.id.atcct_list_layout);

        button = new Button(this);
        button.setText("Delete");

        loadOwners();

        repo = new ATCCRepo();
        spnPlanter = new SpinnerDialog(ATCCTList.this, ownersList, "Select Planter");
        spnPlanter.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                selPlanter = s;
                if (selPlanter != null) {
                    ownerID = selPlanter.substring(selPlanter.indexOf("[") + 1, selPlanter.indexOf("]"));
                    if (repo.isATCCExist(ownerID)) {
                        Snackbar.make(mLayout, "ATCCT already exists for selected owner: " + selPlanter, Snackbar.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Selected planter: " + selPlanter);
                        newATCC(selPlanter);
                    }

                } else {
                    System.out.println("No planter selected.");
                }
            }
        });
        loadATCCTs("All");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        this.menu = menu;
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
                return true;
            case R.id.chk_all:
                item.setChecked(true);
                loadATCCTs("All");
                return true;
            case R.id.chk_signed:
                item.setChecked(true);
                loadATCCTs("Signed");
                return true;
            case R.id.chk_forsig:
                item.setChecked(true);

                loadATCCTs("To Sign");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newATCC(String owner) {
        Intent intent = new Intent(this, ATCCTDetails.class);
        intent.putExtra("action", "New ATCCT");
        intent.putExtra("ownerID", owner.substring(owner.indexOf("[") + 1, owner.indexOf("]")));
        startActivity(intent);
    }

    private void loadOwners() {
        final OwnersRepo oRepo = new OwnersRepo();
        ArrayList<String> list = oRepo.getOwnersForSpinner();
        ownersList = list;
    }

    private void loadATCCTs(String filter) {
        final ATCCRepo repo = new ATCCRepo();
        ArrayList<HashMap<String, String>> atccList = repo.getATCCForList(filter);

        ListView lv = findViewById(R.id.lstATCCT);
        lv.setFastScrollEnabled(true);
        ListAdapter adapter;
        if (atccList.size() != 0) {
            lv = findViewById(R.id.lstATCCT);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    if (b != null) {
                        tvAtccNo = view.findViewById(R.id.atccNo);
                        atccNo = tvAtccNo.getText().toString();
                        ATCC atcc = repo.getATCCByNo(atccNo);


                        //createATCCTPDF();
                        if (atcc.getFileName() != null) {
                            if (atcc.getFileName().equalsIgnoreCase("")) {
                                Intent objIntent;
                                objIntent = new Intent(getApplicationContext(), ATCCTPreview.class);
                                objIntent.putExtra("atccNo", atcc.getAtccNo());
                                startActivity(objIntent);
                            } else {
                                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                                File file = new File(atcc.getFileName());
                                if (!file.exists()) {
                                    Toast.makeText(getApplicationContext(), "File " + fileName + " not found. It may have been moved or deleted.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Uri apkURI = FileProvider.getUriForFile(
                                        ATCCTList.this,
                                        getApplicationContext()
                                                .getPackageName() + ".provider", file);
                                objIntent.setDataAndType(apkURI, "application/pdf");
                                objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(objIntent);
                            }
                        } else {
                            Intent objIntent;
                            objIntent = new Intent(getApplicationContext(), ATCCTPreview.class);
                            objIntent.putExtra("atccNo", atcc.getAtccNo());
                            startActivity(objIntent);
                        }

                    }
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                    tvAtccNo = view.findViewById(R.id.atccNo);
                    atccNo = tvAtccNo.getText().toString();

                    final ATCC atcc = repo.getATCCByNo(atccNo);

                    PopupMenu popup = new PopupMenu(ATCCTList.this, view);
                    popup.inflate(R.menu.popup_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {


                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    StringBuilder message = new StringBuilder();
                                    if (atcc.getFileName() != null ) {
                                        if (atcc.getFileName().equalsIgnoreCase("")) {
                                            message.append("You are about to delete ATCCT No. " + atccNo + ". Press continue to proceed.\n");
                                        } else {
                                            message.append("ATCCT No. " + atccNo + " is not yet signed. Press continue to proceed.\n");
                                        }
                                    } else {
                                        message.append("ATCCT No. " + atccNo + " is not yet signed. Press continue to proceed.\n");
                                    }
                                    message.append("NOTE: Only ATCCT record will be deleted but not the generated PDFs.");
                                    new AlertDialog.Builder(view.getContext())
                                            .setTitle("Delete")
                                            .setMessage(
                                                    message)
                                            .setIcon(
                                                    getResources().getDrawable(
                                                            android.R.drawable.ic_dialog_alert
                                                    ))
                                            .setPositiveButton(
                                                    "Continue",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            repo.delete(atccNo);
                                                            onRestart();
                                                            Toast.makeText(view.getContext(), "ATCCT successfully deleted. ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                            .setNegativeButton(
                                                    "Cancel",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                        }
                                                    }).show();
                                    return true;
                                case R.id.action_edit:
                                    if (atcc.getFileName() != null) {
                                        if (atcc.getFileName().equalsIgnoreCase("")) {
                                            Intent objIntent;
                                            objIntent = new Intent(getApplicationContext(), ATCCTDetails.class);
                                            objIntent.putExtra("atccNo", atcc.getAtccNo());
                                            objIntent.putExtra("action", "Edit ATCCT");
                                            startActivity(objIntent);
                                        } else {
                                            new AlertDialog.Builder(view.getContext())
                                                    .setTitle("Edit")
                                                    .setMessage("This ATCCT has already been signed. Do you want to continue editing this and sign again?")
                                                    .setIcon(
                                                            getResources().getDrawable(
                                                                    android.R.drawable.ic_dialog_alert
                                                            ))
                                                    .setPositiveButton(
                                                            "Yes",
                                                            new DialogInterface.OnClickListener() {

                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {
                                                                    Intent objIntent;
                                                                    objIntent = new Intent(getApplicationContext(), ATCCTDetails.class);
                                                                    objIntent.putExtra("atccNo", atcc.getAtccNo());
                                                                    objIntent.putExtra("action", "Edit ATCCT");
                                                                    startActivity(objIntent);
                                                                }
                                                            })
                                                    .setNegativeButton(
                                                            "Cancel",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {
                                                                }
                                                            }).show();
                                        }

                                    } else {
                                        Intent objIntent;
                                        objIntent = new Intent(getApplicationContext(), ATCCTDetails.class);
                                        objIntent.putExtra("atccNo", atcc.getAtccNo());
                                        objIntent.putExtra("action", "Edit ATCCT");
                                        startActivity(objIntent);
                                    }
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
            adapter = new SimpleAdapter(ATCCTList.this, atccList, R.layout.atcct_list_item, new String[]{"ATCCNo", "OwnerID", "OwnerName", "ATCCTDetails", "DateSigned"}, new int[]{R.id.atccNo, R.id.ownerID, R.id.ownerName, R.id.atcctDetails, R.id.isSigned});

            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        invalidateOptionsMenu();
        onCreateOptionsMenu(this.menu);
        loadATCCTs("All");
    }

}
