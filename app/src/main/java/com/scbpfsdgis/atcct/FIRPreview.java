package com.scbpfsdgis.atcct;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.action.PdfTargetDictionary;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.scbpfsdgis.atcct.Utils.ExifUtil;
import com.scbpfsdgis.atcct.Utils.Utils;
import com.scbpfsdgis.atcct.data.model.Contact;
import com.scbpfsdgis.atcct.data.model.FIR;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ContactRepo;
import com.scbpfsdgis.atcct.data.repo.FIRRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.scbpfsdgis.atcct.app.App.getContext;

public class FIRPreview extends AppCompatActivity {

    String firID;
    String obstructions = "";
    String fileName;
    String mapPath;
    private View mLayout;
    ImageView imgMap;
    TextView tvFarmCode, tvFarmName, tvOwnerName, tvContPerson, tvContNum,
            tvFldNo, tvArea, tvObst, tvHarvMeth, tvFlags, tvStart, tvEnd, tvNotes, tvCoor;
    SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm aa", Locale.getDefault());
    ArrayList<HashMap<String, String>> attList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firpreview);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mLayout = findViewById(R.id.linearLayout);

        Intent intent = getIntent();
        firID = intent.getStringExtra("firID");

        getSupportActionBar().setTitle("FIR No. " + firID);

        init();

        FIRRepo fRepo = new FIRRepo();
        FarmsRepo farmsRepo = new FarmsRepo();
        OwnersRepo ownersRepo = new OwnersRepo();
        ContactRepo contactRepo = new ContactRepo();
        FIR fir = fRepo.getFIRByID(firID);
        Farms farm = farmsRepo.getFarmByID(fir.getFirFarmCode(), "M");
        Owners owner = ownersRepo.getOwnerByID(farm.getFarmOwnerID(), "M");
        Contact contact = contactRepo.getContactByFarm(fir.getFirFarmCode());

        attList = fRepo.getAttachments(firID);
        loadAttachments();

        tvFarmCode.setText(fir.getFirFarmCode());
        tvFarmName.setText(farm.getFarmName());
        tvOwnerName.setText(owner.getOwnerName());
        tvContPerson.setText(contact.getContName());
        tvContNum.setText(contact.getContNum());
        tvFldNo.setText(fir.getFirFldNo());
        tvArea.setText(fir.getFirRFRArea() + " has.");
        String[] obstArr;


        if (fir.getFirObst() != null) {
            if (!fir.getFirObst().equalsIgnoreCase("-")) {
                obstArr = fir.getFirObst().split(",");
                System.out.println("Obstructions: " + obstArr.length);
                System.out.println(fir.getFirObst());

                for (int i = 0; i < obstArr.length; i++) {
                    obstructions += getObstruction(obstArr[i].trim());
                    if (i < obstArr.length - 1) {
                        obstructions += "\n";
                    }
                }
            } else {
                obstructions = "-none-";
            }
        }
        System.out.println("-----Obstructions-----");
        System.out.println(obstructions);

        tvObst.setText(obstructions);
        tvHarvMeth.setText(getHarvMeth(fir.getFirHarvMeth()));
        tvFlags.setText(String.valueOf(fir.getFirFlags()));
        tvStart.setText(timeFmt.format(Time.valueOf(fir.getFirStart())));
        tvEnd.setText(timeFmt.format(Time.valueOf(fir.getFirEnd())));
        tvNotes.setText(fir.getFirNotes());
        tvCoor.setText(fir.getFirCoorName());

        File map = new File(fir.getFirMap());
        if (map.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(fir.getFirMap());
            Bitmap orientedBmp = ExifUtil.rotateBitmap(fir.getFirMap(), bmp);
            imgMap.setImageBitmap(orientedBmp);
            mapPath = map.getPath();
        } else {
            mapPath = "";
            Toast.makeText(this, "Map screenshot could not be located. It may have been moved or deleted.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAttachments() {
        RelativeLayout root = findViewById(R.id.firPreview);
        ScrollView scrollView = (ScrollView) root.getChildAt(1);
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        if (attList.size() > 0) {
            for (int i = 0; i < attList.size(); i++) {
                String cap = attList.get(i).get("AttCaption");
                String path = attList.get(i).get("AttPath");
                TextView caption = new TextView(this);
                caption.setTextColor(Color.parseColor("#003d00"));
                caption.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                ImageView attach = new ImageView(this);
                caption.setText(i + 1 + ". " + cap);
                File att = new File(attList.get(i).get("AttPath"));
                if (att.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    Bitmap orientedBmp = ExifUtil.rotateBitmap(path, bmp);
                    attach.setImageBitmap(orientedBmp);
                    attach.setAdjustViewBounds(true);
                    attach.setScaleType(ImageView.ScaleType.FIT_XY);
                    linearLayout.addView(caption);
                    linearLayout.addView(attach);
                } else {
                    caption.setText(i + 1 + ". The attachment with caption \"" + cap + "\" and path \"" + path + "\" could not be located.");
                    caption.setTextColor(Color.RED);
                    caption.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    linearLayout.addView(caption);
                }
            }
        } else {
            TextView textView = new TextView(this);
            textView.setText("***NO ATTACHMENTS***");
            linearLayout.addView(textView);
        }
    }

    private String getObstruction(String code) {
        String obstruction = "";
        switch (code) {
            case "DT":
                obstruction = "Ditches (Pangpang)";
                break;
            case "CN":
                obstruction = "Canals/Creek";
                break;
            case "EP":
                obstruction = "Electric Posts (Low-lying Wires)";
                break;
            case "SB":
                obstruction = "Stones & Boulders";
                break;
            case "TM":
                obstruction = "Termite Mound";
                break;
            case "WL":
                obstruction = "Wooden Ladder";
                break;
            case "TB":
                obstruction = "Tree Branches";
                break;
            case "IR":
                obstruction = "Irrigation & hydrants";
                break;
            case "WLL":
                obstruction = "Well (Tabay)";
                break;
            case "TS":
                obstruction = "Tree Stomps";
                break;
            case "CPT":
                obstruction = "Cane Points (Patdan)";
                break;
            case "CPL":
                obstruction = "Cane Piles";
                break;
            case "CT":
                obstruction = "Cane Trash";
                break;
            case "TT":
                obstruction = "Tire Tracks";
                break;
            case "SLP":
                obstruction = "Slope/Steep (Bakilid)";
                break;
            case "SR":
                obstruction = "Slightly Rolling";
                break;
        }
        System.out.println("obs " + obstruction);
        return obstruction;
    }

    private String getHarvMeth(String code) {
        String harvmeth = "";
        switch (code) {
            case "L":
                harvmeth = "Linear";
                break;
            case "T":
                harvmeth = "Tonnage";
                break;
            case "C":
                harvmeth = "Combination";
                break;
        }
        return harvmeth;
    }

    private void init() {
        tvFarmCode = findViewById(R.id.tvFarmCode);
        tvFarmName = findViewById(R.id.tvFarmName);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvContPerson = findViewById(R.id.tvContPerson);
        tvContNum = findViewById(R.id.tvContNum);
        tvFldNo = findViewById(R.id.tvFldNo);
        tvArea = findViewById(R.id.tvRFRArea);
        tvObst = findViewById(R.id.tvObstructions);
        tvHarvMeth = findViewById(R.id.tvHarvMeth);
        tvFlags = findViewById(R.id.tvFlags);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        tvNotes = findViewById(R.id.tvNotes);
        tvCoor = findViewById(R.id.tvCoor);
        imgMap = findViewById(R.id.imgMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.export, menu);
        return true;
    }

    private void delete() {
        final FIRRepo repo = new FIRRepo();
        new AlertDialog.Builder(this)
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
                                Toast.makeText(getApplicationContext(), "FIR successfully deleted. ", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).show();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exportFIR:
                exportFIR(firID);
                return true;
            case R.id.action_editFIR:
                FIRRepo firRepo = new FIRRepo();
                final FIR fir = firRepo.getFIRByID(firID);
                if (fir.getFirPath() != null) {
                    if (fir.getFirPath().equalsIgnoreCase("")) {
                        Intent objIntent;
                        objIntent = new Intent(getApplicationContext(), FIRDetails.class);
                        objIntent.putExtra("farmCode", fir.getFirFarmCode());
                        objIntent.putExtra("firID", fir.getFirID());
                        objIntent.putExtra("type", "Edit");
                        startActivity(objIntent);
                    } else {
                        new AlertDialog.Builder(getContext())
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

            case R.id.action_deleteFIR:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportFIR(String firID) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            try {

                if (mapPath.equalsIgnoreCase("")) {
                    Toast.makeText(this, "Could not generate FIR PDF without map screenshot. Please edit the FIR first and try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                createFIRPDF(firID);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    private void createFIRPDF(String firID) throws IOException, DocumentException, ParseException {

        FIRRepo firRepo = new FIRRepo();
        FarmsRepo farmsRepo = new FarmsRepo();
        FIR fir = firRepo.getFIRByID(firID);
        Farms farm = farmsRepo.getFarmByID(fir.getFirFarmCode(), "M");
        OwnersRepo ownersRepo = new OwnersRepo();
        Owners owner = ownersRepo.getOwnerByID(farm.getFarmOwnerID(), "M");
        ContactRepo contactRepo = new ContactRepo();
        Contact contact = contactRepo.getContactByFarm(farm.getFarmCode());
        SimpleDateFormat dateForFile = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String farmFieldNo = farm.getFarmName() + "_FldNo-" + fir.getFirFldNo();
        Rectangle longSize = new Rectangle((float) (8.5 * 72), 13 * 72);

        File exportDir = new File(Environment.getExternalStorageDirectory() + Utils.mainDir + Utils.firSubDir, "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
            System.out.println("FIR Dir Created");
        }

        fileName = exportDir.getAbsolutePath() + "/" + "FIR_"
                //+ farmFieldNo.replace("\"", "") + "_"
                + farmFieldNo.replaceAll("[^A-Za-z0-9()_\\- \\[\\]]", " ") + "_"
                + dateForFile.format(new Date()) + "_ID" + firID + ".pdf";
        fir.setFirPath(fileName);
        Document document = new Document(longSize);
        PdfWriter writer;
        writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));

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

        Font hdrFootNote = new Font();
        hdrFootNote.setSize(7);
        hdrFootNote.setStyle(Font.ITALIC);
        hdrFootNote.setColor(BaseColor.DARK_GRAY);

        BaseColor headerFill = new BaseColor(234, 234, 234);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.biopower);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        Image logo = Image.getInstance(byteArray);
        logo.scaleAbsolute(150, 27);

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.legend);
        stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 75, stream);
        byteArray = stream.toByteArray();
        Image lgnd = Image.getInstance(byteArray);
        lgnd.scaleAbsolute(200, 500);

        //writer.getDirectContent().addImage(image, true);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Rev. No. 003", hdrFootNote), longSize.getWidth() - 50, longSize.getHeight() - 50, 0);
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(Constants.GLOBAL_HOST + " pour réussir votre prochain concours."), 400, 800, 0);

        //REV NO.
        Paragraph revNo = new Paragraph("Rev No. 003", hdrFootNote);
        revNo.setAlignment(Paragraph.ALIGN_RIGHT);

        //TITLE
        Paragraph title = new Paragraph("FIELD INSPECTION REPORT\n\n", bold14);
        title.setAlignment(Paragraph.ALIGN_CENTER);

        //Table 1: Farm Information
        PdfPTable farmInfo = new PdfPTable(2);
        farmInfo.setWidthPercentage(100);
        farmInfo.setWidths(new int[]{1, 4});

        PdfPCell cell;
        cell = new PdfPCell(new Phrase("FIR No. " + firID, boldRed));
        cell.setColspan(2);
        farmInfo.addCell(cell);
        cell = new PdfPCell(new Phrase("I. FARM INFORMATION", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        farmInfo.addCell(cell);
        farmInfo.addCell(new Phrase("Farm Code", fldNameFont));
        farmInfo.addCell(new Phrase(farm.getFarmCode(), boldBlue11));
        farmInfo.addCell(new Phrase("Farm Name & Owner", fldNameFont));
        farmInfo.addCell(new Phrase(farm.getFarmName() + " (" + owner.getOwnerName() + ")", boldBlue11));
        farmInfo.addCell(new Phrase("Contact Person & Number", fldNameFont));
        farmInfo.addCell(new Phrase(contact.getContName() + " (" + contact.getContNum() + ")", boldBlue11));

        //Table 2: Field Inspection Details
        PdfPTable firDetails = new PdfPTable(2);
        firDetails.setWidthPercentage(100);
        firDetails.setWidths(new int[]{1, 4});

        Date firDate = new SimpleDateFormat("yyyy-MM-dd").parse(fir.getFirDate());

        cell = new PdfPCell(new Phrase("II. FIELD INSPECTION DETAILS", bold));
        cell.setColspan(2);
        cell.setBackgroundColor(headerFill);
        firDetails.addCell(cell);
        firDetails.addCell(new Phrase("Field No.", fldNameFont));
        firDetails.addCell(new Phrase(fir.getFirFldNo(), boldBlue11));
        firDetails.addCell(new Phrase("RFR Area", fldNameFont));
        firDetails.addCell(new Phrase(fir.getFirRFRArea() + " has.", boldBlue11));
        firDetails.addCell(new Phrase("Obstructions", fldNameFont));
        firDetails.addCell(new Phrase(obstructions, boldBlue11));
        firDetails.addCell(new Phrase("Harvest Method", fldNameFont));
        firDetails.addCell(new Phrase(getHarvMeth(fir.getFirHarvMeth()), boldBlue11));
        firDetails.addCell(new Phrase("No. of Flags", fldNameFont));
        firDetails.addCell(new Phrase(String.valueOf(fir.getFirFlags()), boldBlue11));
        firDetails.addCell(new Phrase("Date Inspected", fldNameFont));
        firDetails.addCell(new Phrase(dateFormat.format(firDate) + " ," + timeFmt.format(Time.valueOf(fir.getFirStart())) + " ~ " + timeFmt.format(Time.valueOf(fir.getFirEnd())), boldBlue11));
        firDetails.addCell(new Phrase("Notes", fldNameFont));
        firDetails.addCell(new Phrase(fir.getFirNotes(), boldBlue11));
        firDetails.addCell(new Phrase("Field Coordinator", fldNameFont));
        firDetails.addCell(new Phrase(fir.getFirCoorName(), boldBlue11));

        //Table 2: Field Inspection Details
        PdfPTable mapHdr = new PdfPTable(1);
        mapHdr.setWidthPercentage(100);

        Image imgMap;
        if (fir.getFirMap() != null) {
           /* Bitmap bmp = BitmapFactory.decodeFile(fir.getFirMap());
            Bitmap orientedBmp = ExifUtil.rotateBitmap(fir.getFirMap(), bmp);

            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            orientedBmp.compress(Bitmap.CompressFormat.PNG, 50, stream3);*/

            imgMap = Image.getInstance(fir.getFirMap());
            imgMap.setAlignment(Image.ALIGN_CENTER);
            imgMap.setScaleToFitHeight(true);
            System.out.println("X: " + imgMap.getAbsoluteX() + ", Y: " + imgMap.getAbsoluteY());

        } else {
            imgMap = null;
        }

        cell = new PdfPCell(new Phrase("III. MAP", bold));
        cell.setBackgroundColor(headerFill);
        mapHdr.addCell(cell);

        document.add(logo);
        document.add(title);
        document.add(farmInfo);
        document.add(firDetails);
        document.add(mapHdr);

        PdfPTable map = new PdfPTable(2);
        map.setWidthPercentage(100);
        map.setWidths(new int[]{1, 4});

        float vertPost = writer.getVerticalPosition(false);
        cell = new PdfPCell(lgnd, true);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        map.addCell(cell).setFixedHeight(vertPost - 36);

        cell = new PdfPCell(imgMap, true);

        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        float pageHeight = document.getPageSize().getHeight();

        float cellHeight = pageHeight - 36 - vertPost;
        System.out.println("Page Size: " + pageHeight);
        System.out.println("Vertical Position: " + vertPost);
        System.out.println("Cell Height: " + cellHeight);
        map.addCell(cell).setFixedHeight(vertPost - 36);

        document.add(map);


        if (attList.size() > 0) {
            document.newPage();
            PdfPTable attHdr = new PdfPTable(1);
            attHdr.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("IV. ATTACHMENTS for " + firID, bold));
            cell.setBackgroundColor(headerFill);

            attHdr.addCell(cell);
            document.add(attHdr);

            Image attach;

            for (int i=0;i<attList.size();i++) {

                String path = attList.get(i).get("AttPath");
                String caption = attList.get(i).get("AttCaption");

                File file = new File(path);

                if (file.exists()) {
                    PdfPTable capTbl = new PdfPTable(1);
                    capTbl.setWidthPercentage(100);

                    PdfPTable attTbl = new PdfPTable(1);
                    attTbl.setWidthPercentage(100);

                    cell = new PdfPCell(new Phrase(caption, boldBlue11));
                    capTbl.addCell(cell);
                    document.add(capTbl);

                    attach = Image.getInstance(path);
                    attach.setAlignment(Image.ALIGN_CENTER);

                    cell = new PdfPCell(attach, true);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    if (i < attList.size() - 1) {
                        System.out.println("Vert Position at " + i + " " + writer.getVerticalPosition(false));
                        attTbl.addCell(cell).setFixedHeight(((writer.getVerticalPosition(false))/2) - 36);
                    } else {
                        System.out.println("Vert Position at " + i + " " + writer.getVerticalPosition(false));
                        attTbl.addCell(cell).setFixedHeight(writer.getVerticalPosition(false) - 36);
                    }
                    document.add(attTbl);
                }

            }
        }
        document.close();

        firRepo.updateFIRPath(fir);

        Intent objIntent = new Intent(Intent.ACTION_VIEW);
        File file = new File(fir.getFirPath());

        Uri apkURI = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider", file);
        objIntent.setDataAndType(apkURI, "application/pdf");
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(objIntent);
        finish();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            System.out.println("Show Request Permission");
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Permission to access storage is required.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(FIRPreview.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MainActivity.PERMISSION_REQUEST_WRITESTORAGE);
                }
            }).show();
        } else {
            System.out.println("Show Request Permission 2");
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting storage permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.PERMISSION_REQUEST_WRITESTORAGE);
        }
    }
}