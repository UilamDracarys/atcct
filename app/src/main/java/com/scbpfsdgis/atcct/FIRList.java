package com.scbpfsdgis.atcct;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.Utils.SearchableAdapter;
import com.scbpfsdgis.atcct.data.model.FIR;
import com.scbpfsdgis.atcct.data.repo.FIRRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class FIRList extends AppCompatActivity {
    TextView tvFIRID;
    private View mLayout;
    private Menu menu;
    SearchableAdapter adapter;
    ArrayList<HashMap<String, String>> firList;
    ArrayList<String> farmsList = new ArrayList<>();
    FloatingActionButton fab;
    String selFarm, farmCode, firID;
    SpinnerDialog spnFarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir_list);
        mLayout = findViewById(R.id.fir_list_layout);

        loadFarms();
        init();
        loadFIRs();
    }

    private void init() {
        fab = findViewById(R.id.fabNewFIR);
        spnFarms = new SpinnerDialog(FIRList.this, farmsList, "Select Farm");
        spnFarms.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                selFarm = s;
                if (selFarm != null) {
                    farmCode = selFarm.substring(selFarm.indexOf("[") + 1, selFarm.indexOf("]"));
                    System.out.println("SelFarm " + farmCode);
                    newFIR(farmCode);
                } else {
                    System.out.println("No farm selected.");
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnFarms.showSpinerDialog();
            }
        });
    }

    private void loadFarms() {
        final FarmsRepo repo = new FarmsRepo();
        farmsList = repo.getFarmsForSpinner();
    }

    private void newFIR(String farmCode) {
        FIRRepo repo = new FIRRepo();
        Intent intent = new Intent(this, FIRDetails.class);

        intent.putExtra("title", "New FIR");
        intent.putExtra("farmCode", farmCode);
        intent.putExtra("firID", repo.generateFIRID());
        intent.putExtra("type", "New");
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadFIRs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_back, menu);
        this.menu = menu;
        MenuItem chkAll = menu.findItem(R.id.chk_all);
        MenuItem chkSigned = menu.findItem(R.id.chk_signed);
        MenuItem chkToSign = menu.findItem(R.id.chk_forsig);
        MenuItem exportATCCT = menu.findItem(R.id.action_exportATCCT);

        chkAll.setVisible(false);
        chkSigned.setVisible(false);
        chkToSign.setVisible(false);
        exportATCCT.setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                } else {
                    Toast.makeText(getApplicationContext(), "FIR list is empty.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        return true;
    }

    private void loadFIRs() {
        final FIRRepo repo = new FIRRepo();
        firList = repo.getFIRList();

        /*Collections.sort(firList, new Comparator<HashMap<String, String>>()
        {
            @Override
            public int compare(HashMap<String, String> a, HashMap<String, String> b)
            {
                return a.get("farmName").compareTo(b.get("farmName"));
            }
        });*/

        ListView lv = findViewById(R.id.firList);
        lv.setFastScrollEnabled(true);
        if (firList.size() != 0) {
            lv = findViewById(R.id.firList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    if (b != null) {
                        tvFIRID = view.findViewById(R.id.firID);
                        firID = tvFIRID.getText().toString();
                        System.out.println("FIR ID is: " + firID);
                        FIR fir = repo.getFIRByID(firID);

                        if (fir.getFirPath() != null) {
                            if (fir.getFirPath().equalsIgnoreCase("")) {
                                Intent objIntent;
                                objIntent = new Intent(getApplicationContext(), FIRPreview.class);
                                objIntent.putExtra("firID", fir.getFirID());
                                startActivity(objIntent);
                            } else {
                                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                                File file = new File(fir.getFirPath());
                                if (!file.exists()) {
                                    Toast.makeText(getApplicationContext(), "File " + file.getName() + " not found. It may have been moved or deleted.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Uri apkURI = FileProvider.getUriForFile(
                                        FIRList.this,
                                        getApplicationContext()
                                                .getPackageName() + ".provider", file);
                                objIntent.setDataAndType(apkURI, "application/pdf");
                                objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(objIntent);
                            }

                        } else {
                            Intent objIntent;
                            objIntent = new Intent(getApplicationContext(), FIRPreview.class);
                            objIntent.putExtra("firID", fir.getFirID());
                            startActivity(objIntent);
                        }

                    }
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                    tvFIRID = view.findViewById(R.id.firID);
                    firID = tvFIRID.getText().toString();

                    final FIR fir = repo.getFIRByID(firID);

                    PopupMenu popup = new PopupMenu(FIRList.this, view);
                    popup.inflate(R.menu.popup_menu_fir);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {


                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    new AlertDialog.Builder(view.getContext())
                                            .setTitle("Delete")
                                            .setMessage(
                                                    "You are about to delete this FIR with ID No. " + firID + ". Press CONTINUE to proceed.")
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
                                                            repo.delete(firID);
                                                            onRestart();
                                                            Toast.makeText(view.getContext(), "FIR successfully deleted. ", Toast.LENGTH_SHORT).show();
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
                                    if (fir.getFirPath() != null) {
                                        if (fir.getFirPath().equalsIgnoreCase("")) {
                                            Intent objIntent;
                                            objIntent = new Intent(getApplicationContext(), FIRDetails.class);
                                            objIntent.putExtra("farmCode", fir.getFirFarmCode());
                                            objIntent.putExtra("firID", fir.getFirID());
                                            objIntent.putExtra("type", "Edit");
                                            startActivity(objIntent);
                                        } else {
                                            new AlertDialog.Builder(view.getContext())
                                                    .setTitle("Edit")
                                                    .setMessage("This FIR has already been exported to PDF. Do you want to continue editing this and export again?")
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
                                                                    objIntent = new Intent(getApplicationContext(), FIRDetails.class);
                                                                    objIntent.putExtra("farmCode", fir.getFirFarmCode());
                                                                    objIntent.putExtra("firID", fir.getFirID());
                                                                    objIntent.putExtra("type", "Edit");
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
                                        objIntent = new Intent(getApplicationContext(), FIRDetails.class);
                                        objIntent.putExtra("farmCode", fir.getFirFarmCode());
                                        objIntent.putExtra("firID", fir.getFirID());
                                        objIntent.putExtra("type", "Edit");
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
            adapter = new SearchableAdapter(FIRList.this,
                    firList,
                    R.layout.fir_list_item,
                    new String[]{"ID", "farmNameFIR", "IDFldNo"},
                    new int[]{R.id.firID, R.id.farmCode, R.id.fldNo});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
    }


}
