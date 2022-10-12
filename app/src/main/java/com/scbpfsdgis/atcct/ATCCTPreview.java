package com.scbpfsdgis.atcct;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.scbpfsdgis.atcct.Utils.Utils;
import com.scbpfsdgis.atcct.data.SignatureActivity;
import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.AuthRepRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ATCCTPreview extends AppCompatActivity {

    String signatory;
    private static final int PERMISSION_REQUEST_WRITESTORAGE = 0;
    TextView tvOwnerName, tvOwnerMobile, tvOwnerEmail, tvOwnerAddress, tvPmtMethod, tvPickupPt, tvAccName, tvAccNo,
            tvBankDetails, tvRemarks, tvNotes, tvTNC, tvAuthRep, tvSignatory;
    CheckBox chkConform, chkSignatory;
    TableRow rPickupPt, rAccName, rAccNo, rBank, rRemarks;
    String[] notes = {
            "1. Payment per 1 full wagon load at Php 625 fixed rate.\n\n",
            "2. For changes in payee information as declared above, a letter of authorization should be provided duly signed by the owner with valid ID’S attached.\n\n",
            "3. Payment Processing – maximum 7 business days after the weekly collection cut-off (Monday-Sunday).\n\n",
            "4. Regular schedule for releasing of checks at the transloading site offices will be nominated by Biopower.\n\n",
            "5. If unclaimed at TLS, Check/Cheque may be claimed at Bacolod office 6th Floor, PNB Building, 10th Lacson St., Bacolod City, Neg. Occ. 6100."
    };
    String[] tnc = {
            "1. Preference for cane trash collection shall be given to fields with entire canes rows harvested.\n\n",
            "2. In case of adverse weather conditions and trash cannot be collected, Biopower is not obligated to make any payment for a particular field or group of fields.\n\n",
            "3. Cane trash collection shall be solely at the discretion of Biopower, and payments shall be made based on actual cane trash collected from harvested fields.\n\n",
            "4. In case collecting per field is not completed at cut-off date, partial payment shall be made in proportion to actual area collected.\n\n",
            "5. Owner name stated above shall be the payee name (unless explicitly stated in the remarks) for the entire validity of this agreement. Modifications will only be allowed for reasons such as death of owner/payee and change of ownership."
    };
    String conformity = "The above terms and conditions are understood and agreed upon between Biopower and I, as supplier of cane trash. This shall be valid until otherwise voided by either party";
    Button sign;
    String atccNo, fileName, imgPath, signed;
    ImageView signImage;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atcctpreview);
//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
        mLayout = findViewById(R.id.linearLayout);

        Intent intent = getIntent();
        atccNo = intent.getStringExtra("atccNo");
        imgPath = intent.getStringExtra("imagePath");
        signed = intent.getStringExtra("signed");


        getSupportActionBar().setTitle("ATCCT NO. " + atccNo);

        ATCCRepo atccRepo = new ATCCRepo();
        ATCC atcc = atccRepo.getATCCByNo(atccNo);
        OwnersRepo ownersRepo = new OwnersRepo();

        final Owners owner = ownersRepo.getOwnerByID(atcc.getOwnerID(), "M");

        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvOwnerMobile = findViewById(R.id.tvOwnerMobile);
        tvOwnerEmail = findViewById(R.id.tvOwnerEmail);
        tvOwnerAddress = findViewById(R.id.tvAddress);
        tvPmtMethod = findViewById(R.id.tvPmtMethod);
        tvPickupPt = findViewById(R.id.tvPickupPt);
        tvAccName = findViewById(R.id.tvAccName);
        tvAccNo = findViewById(R.id.tvAccNo);
        tvBankDetails = findViewById(R.id.tvBankDetails);
        tvRemarks = findViewById(R.id.tvRemarks);
        tvNotes = findViewById(R.id.tvNotes);
        tvTNC = findViewById(R.id.tvTNC);
        tvAuthRep = findViewById(R.id.tvAuthRep);
        chkConform = findViewById(R.id.tvConform);
        rAccName = findViewById(R.id.rAccName);
        rAccNo = findViewById(R.id.rAccNo);
        rBank = findViewById(R.id.rBankDetails);
        rPickupPt = findViewById(R.id.rPickup);
        rRemarks = findViewById(R.id.rRemarks);
        sign = findViewById(R.id.sign);
        signImage = findViewById(R.id.signature);
        chkSignatory = findViewById(R.id.chkSignatory);
        tvSignatory = findViewById(R.id.signatory);

        tvPmtMethod.setText(atcc.getPmtMethod());
        if (atcc.getPmtMethod().equalsIgnoreCase(getResources().getStringArray(R.array.pmtMethod)[1])) {
            rAccName.setVisibility(View.GONE);
            rAccNo.setVisibility(View.GONE);
            rBank.setVisibility(View.GONE);
            tvPickupPt.setText(atcc.getPickupPt());
        } else {
            rPickupPt.setVisibility(View.GONE);
            tvAccName.setText(atcc.getAccName());
            tvAccNo.setText(atcc.getAccNo());
            tvBankDetails.setText(atcc.getBankName() + " (" + atcc.getBankAdd() + ")");
        }

        if (atcc.getRemarks().isEmpty()) {
            rRemarks.setVisibility(View.GONE);
        } else {
            tvRemarks.setText(atcc.getRemarks());
        }

        for (int i = 0; i < notes.length; i++) {
            tvNotes.append(notes[i]);
        }

        for (int i = 0; i < tnc.length; i++) {
            tvTNC.append(tnc[i]);
        }

        AuthRepRepo authRepRepo = new AuthRepRepo();
        ArrayList<HashMap<String, String>> authReps = authRepRepo.getAuthRepForOwner(atcc.getOwnerID());

        if (authReps.size() > 0) {
            for (int i = 0; i < authReps.size(); i++) {
                tvAuthRep.append(i + 1 + ". " + authReps.get(i).get("ARName") + ", " + authReps.get(i).get("ARRel") + " (" + authReps.get(i).get("ARIDType") + ")\n");
            }
        } else {
            tvAuthRep.setText("***NO NOMINATED AUTHORIZED REPRESENTATIVES***");
        }

        chkConform.setText(conformity);
        if (signed != null) {
            chkConform.setChecked(true);
        } else {
            chkConform.setChecked(false);
        }
        chkConform.setTextColor(tvNotes.getTextColors());
        sign.setEnabled(false);

        chkConform.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chkConform.isChecked()) {
                    sign.setEnabled(true);
                } else {
                    sign.setEnabled(false);
                }

            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ATCCTPreview.this, SignatureActivity.class);
                i.putExtra("atccNo", atccNo);
                System.out.println("Sign ATCC No. " + atccNo);
                startActivity(i);
                finish();

            }
        });

        chkSignatory.setText("Signatory is owner.");
        chkSignatory.setChecked(true);
        chkSignatory.setTextColor(tvNotes.getTextColors());

        chkSignatory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!chkSignatory.isChecked()) {
                    showDialog(0);
                } else {
                    tvSignatory.setText(owner.getOwnerName());
                }

            }
        });

        tvSignatory.setText(owner.getOwnerName());
        signatory = owner.getOwnerName();


        tvOwnerName.setText(owner.getOwnerName());
        tvOwnerMobile.setText(owner.getOwnerMobile());
        tvOwnerEmail.setText(owner.getOwnerEmail());
        tvOwnerAddress.setText(owner.getOwnerAddress());


        String image_path = getIntent().getStringExtra("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(image_path);
        System.out.println(image_path);
        /*if (image_path != null) {
            System.out.println("Height: " + bitmap.getHeight());
            System.out.println("Width: " + bitmap.getWidth());
        }*/
        signImage.setImageBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_WRITESTORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
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
                    ActivityCompat.requestPermissions(ATCCTPreview.this,
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
                exportATCC(atccNo, imgPath);
                if (imgPath != null) {
                    File sig = new File(imgPath);
                    sig.delete();
                }
                delSigCache();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            Toast.makeText(this, "ATCCT No. " + atccNo + " successfully generated.\nFilename: " + fileName, Toast.LENGTH_LONG).show();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportATCC(String atccNo, String imagePath) throws Exception {
        ATCCRepo atccRepo = new ATCCRepo();
        ATCC atcc = atccRepo.getATCCByNo(atccNo);
        OwnersRepo oRepo = new OwnersRepo();
        Owners owner = oRepo.getOwnerByID(atcc.getOwnerID(), "M");
        String ownerName = owner.getOwnerName();
        SimpleDateFormat dateForFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        SimpleDateFormat dateSignedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        File exportDir = new File(Environment.getExternalStorageDirectory() + Utils.mainDir + Utils.atcctSubDir, "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
            System.out.println("ATCCT Dir Created");
        }
        fileName = exportDir.getAbsolutePath() + "/" + "ATCCT_" + atccNo + "_"
                + ownerName.replace("\"", "") + "_"
                + dateForFile.format(new Date()) + ".pdf";
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
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
        Font ita9 = new Font();
        ita9.setStyle(Font.ITALIC);
        ita9.setSize(9);
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
        image.scaleAbsolute(150, 27);

        Image sig;
        if (imagePath != null) {
            sig = Image.getInstance(imagePath);
            sig.setAlignment(Image.ALIGN_CENTER);
            sig.scaleAbsolute(100, 30);
        } else {
            sig = null;
        }

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
        noteList.add(new ListItem("Payment per 1 full wagon load at Php 625 fixed rate.", smallFont));
        noteList.add(new ListItem("For changes in payee information as declared above, a letter of authorization should be provided duly signed by the owner with valid ID’S attached.", smallFont));
        noteList.add(new ListItem("Payment Processing – maximum 7 business days after the weekly collection cut-off (Monday-Sunday).", smallFont));
        noteList.add(new ListItem("Regular schedule for releasing of checks at the transloading site offices will be nominated by Biopower.", smallFont));
        noteList.add(new ListItem("If unclaimed at TLS, Check/Cheque may be claimed at Bacolod office 6th Floor, PNB Building, 10th Lacson St., Bacolod City, Neg. Occ. 6100.", smallFont));
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
        noteList.add(new ListItem("Owner name stated above shall be the payee name (unless explicitly stated in the remarks) for the entire validity of this agreement. Modifications will only be allowed for reasons such as death of owner/payee and change of ownership.", smallFont));
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
            cell = new PdfPCell(new Phrase("Name", ita9));
            authRep.addCell(cell);
            cell = new PdfPCell(new Phrase("Relationship to Owner/Planter", ita9));
            authRep.addCell(cell);
            cell = new PdfPCell(new Phrase("ID Presented", ita9));
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

        Font boldUnderline = new Font();
        boldUnderline.setStyle(Font.BOLD | Font.UNDERLINE);
        boldUnderline.setSize(12);

        Paragraph signName = new Paragraph(signatory.toUpperCase(), boldUnderline);
        signName.setAlignment(Paragraph.ALIGN_CENTER);


        Paragraph signature = new Paragraph("Signature over Printed Name of Planter or Authorized Representative", ita9);
        signature.setAlignment(Paragraph.ALIGN_CENTER);

        cell = new PdfPCell();
        cell.addElement(confClause);
        if (sig != null) {
            cell.addElement(sig);
        }
        cell.addElement(signName);
        cell.addElement(signature);
        conform.addCell(cell);


        document.add(title);
        document.add(ownerInfo);
        document.add(paymentInfo);
        document.add(notes);
        document.add(tnc);
        document.add(authRep);
        document.add(conform);
        if (sig != null) {
            Paragraph sigDate = new Paragraph("Date Signed: " + dateSignedFormat.format(new Date()), smallFont);
            sigDate.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(sigDate);
        }

        // Closing the document
        document.close();

        atcc.setDteSigned(dateSignedFormat.format(new Date()));
        atcc.setFileName(fileName);
        atcc.setSignatory(signatory.toUpperCase());

        atccRepo.updateATCCT(atcc, "Signed");

        Intent objIntent = new Intent(Intent.ACTION_VIEW);
        File file = new File(atcc.getFileName());

        Uri apkURI = FileProvider.getUriForFile(
                this,
                getApplicationContext()
                        .getPackageName() + ".provider", file);
        objIntent.setDataAndType(apkURI, "application/pdf");
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(objIntent);

       /* Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File( fileName  );
        intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
        startActivity(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_cancel, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!chkConform.isChecked()) {
                    Toast.makeText(this, "Please agree to the terms of this agreement by ticking the checkbox under planter's conformity clause.", Toast.LENGTH_SHORT).show();
                } else {
                    if (imgPath != null) {
                        createATCCTPDF();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Generate ATCCT")
                                .setMessage(
                                        "Do you want to generate ATCCT without signature?")
                                .setIcon(
                                        getResources().getDrawable(
                                                android.R.drawable.ic_dialog_info))
                                .setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                createATCCTPDF();
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
                }

                return true;
            case R.id.action_cancel:
                delSigCache();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        delSigCache();
        finish();
    }

    private void delSigCache() {
        File sigDir = new File(getCacheDir(), "");
        if (sigDir.isDirectory()) {
            String[] children = sigDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(sigDir, children[i]).delete();
            }
            Log.v("CLR", "Signature cache cleared.");
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Signatory");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Signatory Fullname");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(50, 50, 50, 50);
        ll.addView(input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(ll);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please input a signatory name.", Toast.LENGTH_SHORT).show();
                    chkSignatory.setChecked(true);
                } else {
                    signatory = input.getText().toString();
                    tvSignatory.setText(signatory);
                    chkSignatory.setChecked(false);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                chkSignatory.setChecked(true);
            }
        });
        return builder.create();
    }
}
