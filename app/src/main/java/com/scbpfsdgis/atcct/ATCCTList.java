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
    String atccNo;

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
                        System.out.println("ATCC: " + atccNo);

                        createATCCTPDF();

                        Toast.makeText(getApplicationContext(), "ATCCT No. " + atccNo, Toast.LENGTH_SHORT).show();
                        /*Intent objIndent;
                        objIndent = new Intent(getApplicationContext(), FarmDetails.class);
                        objIndent.putExtra("atccNo", atcc.getAtccNo());
                        startActivity(objIndent);*/
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_WRITESTORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Storage access granted.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Storage access denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Permission to access storage is required.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(ATCCTList.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITESTORAGE);
                }
            }).show();
        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting storage permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITESTORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createATCCTPDF() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            try {
                exportATCC(atccNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "ATCCT No. " + atccNo + " successfully generated. ", Toast.LENGTH_SHORT).show();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportATCC(String atccNo) throws Exception {
        ATCCRepo atccRepo = new ATCCRepo();
        ATCC atcc = atccRepo.getATCCByNo(atccNo);
        OwnersRepo oRepo = new OwnersRepo();
        Owners owner = oRepo.getOwnerByID(atcc.getOwnerID());
        String ownerName = owner.getOwnerName();
        SimpleDateFormat dateForFile = new SimpleDateFormat("YYYYMMDD_HHMMSS", Locale.getDefault());

        final File exportDir = new File(Environment.getExternalStorageDirectory() + "/ATCCTMobile/ATCCTs", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(exportDir.getAbsolutePath() + "/" + "ATCCT_" + atccNo + "_"
                + ownerName.replace("\"", "") + "_"
                + dateForFile.format(new Date()) + ".pdf"));
        document.open();

        //Font Settings
        Font bold = new Font();
        bold.setStyle(Font.BOLD);
        Font boldRed = new Font();
        boldRed.setStyle(Font.BOLD);
        boldRed.setColor(BaseColor.RED);
        boldRed.setSize(13);
        Font bold14 = new Font();
        bold14.setStyle(Font.BOLD);
        bold14.setSize(14);
        Font boldIta10 = new Font();
        boldIta10.setStyle(Font.BOLDITALIC);
        boldIta10.setSize(10);
        Font boldBlue11 = new Font();
        boldBlue11.setStyle(Font.BOLDITALIC);
        boldBlue11.setSize(11);
        boldBlue11.setColor(BaseColor.BLUE);
        Font smallFont = new Font();
        smallFont.setSize(9);
        Font fldNameFont = new Font();
        fldNameFont.setSize(10);
        Font ita10Blue = new Font();
        ita10Blue.setSize(10);
        ita10Blue.setColor(BaseColor.BLUE);
        ita10Blue.setStyle(Font.ITALIC);

        BaseColor headerFill = new BaseColor(234, 234, 234);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.biopower);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 75, stream);
        byte[] byteArray = stream.toByteArray();
        Image image = Image.getInstance(byteArray);
        image.scaleAbsolute(150,27);
        /*try {
            image = Image.getInstance(byteArray);
            image.scaleAbsolute(150,27);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        document.add(image);

        //TITLE
        Paragraph title = new Paragraph("AUTHORITY TO COLLECT CANE TRASH\n\n", bold14);
        title.setAlignment(Paragraph.ALIGN_CENTER);

        //Table 1: ATCCT No.
        PdfPTable ownerInfo = new PdfPTable(2);
        ownerInfo.setWidthPercentage(100);
        ownerInfo.setWidths(new int[]{1, 4});

        PdfPCell cell;
        cell = new PdfPCell(new Phrase("ATCCT NO. " + atccNo, boldRed));
        cell.setColspan(2);
        ownerInfo.addCell(cell);
        cell = new PdfPCell(new Phrase("I. OWNER INFORMATION", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        ownerInfo.addCell(cell);
        ownerInfo.addCell(new Phrase("Owner Name", fldNameFont));
        ownerInfo.addCell(new Phrase(owner.getOwnerName(), boldBlue11));
        ownerInfo.addCell(new Phrase("Mobile No.", fldNameFont));
        ownerInfo.addCell(new Phrase(owner.getOwnerMobile(), boldBlue11));
        ownerInfo.addCell(new Phrase("Email", fldNameFont));
        ownerInfo.addCell(new Phrase(owner.getOwnerEmail(), boldBlue11));
        ownerInfo.addCell(new Phrase("Address", fldNameFont));
        ownerInfo.addCell(new Phrase(owner.getOwnerAddress(), boldBlue11));
        ownerInfo.addCell(new Phrase("Farms", fldNameFont));
        ownerInfo.addCell(new Phrase(oRepo.consolFarms(owner.getOwnerID()), boldBlue11));

        //Table 2: PAYMENT INFORMATION
        PdfPTable paymentInfo = new PdfPTable(2);
        paymentInfo.setWidthPercentage(100);
        paymentInfo.setWidths(new int[]{1, 4});

        cell = new PdfPCell(new Phrase("II. PAYMENT INFORMATION", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        paymentInfo.addCell(cell);
        paymentInfo.addCell(new Phrase("Payment Method", fldNameFont));
        paymentInfo.addCell(new Phrase(atcc.getPmtMethod(), boldBlue11));
        if (atcc.getPmtMethod().equalsIgnoreCase(getResources().getStringArray(R.array.pmtMethod)[1])) {
            paymentInfo.addCell(new Phrase("Pickup Point", fldNameFont));
            paymentInfo.addCell(new Phrase(atcc.getPickupPt(), boldBlue11));
        } else {
            paymentInfo.addCell(new Phrase("Account Name", fldNameFont));
            paymentInfo.addCell(new Phrase(atcc.getAccName(), boldBlue11));
            paymentInfo.addCell(new Phrase("Account No.", fldNameFont));
            paymentInfo.addCell(new Phrase(atcc.getAccNo(), boldBlue11));
            paymentInfo.addCell(new Phrase("Bank Name & Address", fldNameFont));
            paymentInfo.addCell(new Phrase(atcc.getBankName() + " (" + atcc.getBankAdd() + ")", boldBlue11));
        }
        if (!atcc.getRemarks().isEmpty()) {
            paymentInfo.addCell(new Phrase("Remarks", fldNameFont));
            paymentInfo.addCell(new Phrase(atcc.getRemarks(), boldBlue11));
        }

        //Table 3: NOTES
        PdfPTable notes = new PdfPTable(1);
        notes.setWidthPercentage(100);

        cell = new PdfPCell(new Phrase("III. NOTES", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        notes.addCell(cell);

        cell = new PdfPCell();
        List noteList = new List(List.ORDERED);
        noteList.setFirst(1);
        noteList.add(new ListItem("Payment per 1 full wagon load Php 625.00 rate subject to 1% withholding tax with applicable exemptions wherein a valid BIR tax exemption certification should be presented.", smallFont));
        noteList.add(new ListItem("For changes in payee information as declared above, a letter of authorization should be provided duly signed by the owner with valid ID’S attached.", smallFont));
        noteList.add(new ListItem("Payment Processing – maximum 7 business days after the weekly collection cut-off (Monday-Sunday).", smallFont));
        noteList.add(new ListItem("Regular schedule for releasing of checks at the transloading site offices will be nominated by Biopower.", smallFont));
        noteList.add(new ListItem("If unclaimed at TLS, Check/Cheque may be claimed at SCBP plant site, San Carlos Ecozone, Brgy. Palampas, San Carlos City.", smallFont));
        cell.addElement(noteList);
        notes.addCell(cell);


        //Table 4: TERMS AND CONDITIONS
        PdfPTable tnc = new PdfPTable(1);
        tnc.setWidthPercentage(100);

        cell = new PdfPCell(new Phrase("IV. TERMS AND CONDITIONS", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        tnc.addCell(cell);

        cell = new PdfPCell();
        noteList = new List(List.ORDERED);
        noteList.setFirst(1);
        noteList.add(new ListItem("Preference for cane trash collection shall be given to fields with entire canes rows harvested.", smallFont));
        noteList.add(new ListItem("In case of adverse weather conditions and trash cannot be collected, Biopower is not obligated to make any payment for a particular field or group of fields.", smallFont));
        noteList.add(new ListItem("Cane trash collection shall be solely at the discretion of Biopower, and payments shall be made based on actual cane trash collected from harvested fields.", smallFont));
        noteList.add(new ListItem("In case collecting per field is not completed at cut-off date, partial payment shall be made in proportion to actual area collected.", smallFont));
        cell.addElement(noteList);
        tnc.addCell(cell);

        //Table 5: AUTHORIZED REPRESENTATIVES
        AuthRepRepo arRepo = new AuthRepRepo();
        ArrayList<HashMap<String, String>> arList = arRepo.getAuthRepForOwner(owner.getOwnerID());

        PdfPTable authRep = new PdfPTable(3);
        authRep.setWidthPercentage(100);

        cell = new PdfPCell(new Phrase("V. LIST OF AUTHORIZED REPRESENTATIVES", bold));
        cell.setColspan(3);
        cell.setBackgroundColor(headerFill);
        authRep.addCell(cell);

        if (arList.size() == 0) {
            Paragraph noRep = new Paragraph("***NO NOMINATED AUTHORIZED REPRESENTATIVES***", ita10Blue);
            noRep.setAlignment(Paragraph.ALIGN_CENTER);
            cell = new PdfPCell();
            cell.addElement(noRep);
            cell.setColspan(3);
            authRep.addCell(cell);
        } else {
            authRep.setWidths(new int[]{1, 1, 1});
            cell = new PdfPCell(new Phrase("Name", boldIta10));
            authRep.addCell(cell);
            cell = new PdfPCell(new Phrase("Relationship to Owner/Planter", boldIta10));
            authRep.addCell(cell);
            cell = new PdfPCell(new Phrase("ID Presented", boldIta10));
            authRep.addCell(cell);
            for (int i = 0; i < arList.size(); i++) {
                cell = new PdfPCell(new Phrase(arList.get(i).get("ARName").toString(), ita10Blue));
                authRep.addCell(cell);
                cell = new PdfPCell(new Phrase(arList.get(i).get("ARRel").toString(), ita10Blue));
                authRep.addCell(cell);
                cell = new PdfPCell(new Phrase(arList.get(i).get("ARIDType").toString(), ita10Blue));
                authRep.addCell(cell);
            }
        }

        //Table 6: PLANTER'S CONFORMITY
        PdfPTable conform = new PdfPTable(1);
        conform.setWidthPercentage(100);

        cell = new PdfPCell(new Phrase("VI. PLANTER'S CONFORMITY", bold));
        cell.setBackgroundColor(headerFill);
        conform.addCell(cell);

        Paragraph confClause = new Paragraph("The above terms and conditions are understood and agreed upon between Biopower and I, as supplier of cane trash. This shall be valid until otherwise voided by either party\n\n\n", smallFont);
        confClause.setFirstLineIndent(30);

        Paragraph signature = new Paragraph("___________________________________________\n" +
                "SIGNATURE OVER PRINTED NAME OF\nPLANTER or AUTHORIZED REPRESENTATIVE", boldIta10);
        signature.setAlignment(Paragraph.ALIGN_CENTER);

        cell = new PdfPCell();
        cell.addElement(confClause);
        cell.addElement(signature);
        conform.addCell(cell);


        document.add(title);
        document.add(ownerInfo);
        document.add(paymentInfo);
        document.add(notes);
        document.add(tnc);
        document.add(authRep);
        document.add(conform);

        // Closing the document
        document.close();
    }

}
