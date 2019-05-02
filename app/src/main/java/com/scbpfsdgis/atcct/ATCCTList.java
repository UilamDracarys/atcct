package com.scbpfsdgis.atcct;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.AuthRep;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.AuthRepRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ATCCTList extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_WRITESTORAGE = 0;

    SpinnerDialog spnPlanter;
    ArrayList<String> ownersList = new ArrayList<>();
    String selPlanter;
    String ownerID;
    private View mLayout;
    ATCCRepo repo;
    TextView tvAtccNo;
    String atccNo, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcct_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLayout = findViewById(R.id.atcct_list_layout);

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
        loadATCCTs();
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
        intent.putExtra("ownerID", owner.substring(owner.indexOf("[") + 1, owner.indexOf("]")));
        startActivity(intent);
    }

    private void loadOwners() {
        final OwnersRepo oRepo = new OwnersRepo();
        ArrayList<String> list = oRepo.getOwnersForSpinner();
        ownersList = list;
    }

    private void loadATCCTs() {
        final ATCCRepo repo = new ATCCRepo();
        ArrayList<HashMap<String, String>> atccList = repo.getATCCForList();

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
                        Intent objIndent;
                        objIndent = new Intent(getApplicationContext(), ATCCTPreview.class);
                        objIndent.putExtra("atccNo", atcc.getAtccNo());
                        startActivity(objIndent);
                    }
                }
            });
            adapter = new SimpleAdapter(ATCCTList.this, atccList, R.layout.atcct_list_item, new String[]{"ATCCNo", "OwnerID", "OwnerName", "ATCCTDetails"}, new int[]{R.id.atccNo, R.id.ownerID, R.id.ownerName, R.id.atcctDetails});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadATCCTs();
    }




}
