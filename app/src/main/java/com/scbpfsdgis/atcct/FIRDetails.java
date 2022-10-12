package com.scbpfsdgis.atcct;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.guna.libmultispinner.MultiSelectionSpinner;
import com.itextpdf.text.pdf.PdfPCell;
import com.scbpfsdgis.atcct.Utils.ExifUtil;
import com.scbpfsdgis.atcct.Utils.Utils;
import com.scbpfsdgis.atcct.data.model.Config;
import com.scbpfsdgis.atcct.data.model.Contact;
import com.scbpfsdgis.atcct.data.model.FIR;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ConfigRepo;
import com.scbpfsdgis.atcct.data.repo.ContactRepo;
import com.scbpfsdgis.atcct.data.repo.FIRRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.File;
import java.text.ParseException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.scbpfsdgis.atcct.MainActivity.requestcode;

public class FIRDetails extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener, android.view.View.OnClickListener {

    private View mLayout;
    String firID, farmCode, action;
    Farms farm;
    Owners owner;
    ArrayList<String> ownersList = new ArrayList<>();
    private int mHour, mMinute, mDay, mMonth, mYear;
    Time timeStart, timeEnd, timeRFRAvail;
    Date rfrAvail;
    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    SimpleDateFormat dispFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    FloatingActionButton fab;

    //Declare fields
    TextView farmNameCode, attHeader;
    EditText fldNo, rfrArea, flags, start, end, notes, coor, etContNum, etRFRAvail, etRFRAvailTime;
    Spinner harvMeth, postHarv;
    MultiSelectionSpinner spnObstructions;
    Button btnSelStart, btnSelEnd, btnSelImg, btnCheckAtt, btnSelRFRAvail, btnSelRFRAvailTime;
    CheckBox chkDayOp;
    ImageView imgMap;
    String mapPath = "";
    int attCount = 0;
    AutoCompleteTextView etContName;
    List<EditText> attCaptions;
    List<ImageView> attachments;
    ArrayList<HashMap<String, String>> attList;
    private boolean add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir_details);

        mLayout = findViewById(R.id.fir_detail_layout);

        Intent intent = getIntent();
        fab = findViewById(R.id.fabAddAtt);
        farmCode = intent.getStringExtra("farmCode");
        firID = intent.getStringExtra("firID");
        action = intent.getStringExtra("type");

        FarmsRepo farmsRepo = new FarmsRepo();
        farm = farmsRepo.getFarmByID(farmCode, "M");

        OwnersRepo ownersRepo = new OwnersRepo();
        owner = ownersRepo.getOwnerByID(farm.getFarmOwnerID(), "M");

        System.out.println("New FIR for FarmCode: " + farmCode + " with ID " + firID);


        init(action, firID);

        Contact contact;
        ContactRepo contactRepo = new ContactRepo();
        contact = contactRepo.getContactByFarm(farmCode);

        etContName.setText(contact.getContName());
        etContNum.setText(contact.getContNum());

        ConfigRepo cfgRepo = new ConfigRepo();
        coor.setText(cfgRepo.getCurCoor());
        setMSSItems(R.array.obstructions, spnObstructions);

        String title;

        if (action.equalsIgnoreCase("Edit")) {
            SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            title = "Edit FIR";
            FIRRepo firRepo = new FIRRepo();
            FIR fir = firRepo.getFIRByID(firID);

            System.out.println("Obstructions: " + fir.getFirObst());
            fldNo.setText(fir.getFirFldNo());
            rfrArea.setText(String.valueOf(fir.getFirRFRArea()));
            flags.setText(String.valueOf(fir.getFirFlags()));
            notes.setText(fir.getFirNotes());
            coor.setText(fir.getFirCoorName());
            start.setText(timeFmt.format(Time.valueOf(fir.getFirStart())));
            timeStart = Time.valueOf(fir.getFirStart());
            timeEnd = Time.valueOf(fir.getFirEnd());
            end.setText(timeFmt.format(Time.valueOf(fir.getFirEnd())));
            mapPath = fir.getFirMap();
            chkDayOp.setChecked(fir.getFirDayOp() != 0);
            System.out.println("RFRAvailUntilDate:\t" + fir.getFirRFRAvail());
            if (fir.getFirRFRAvail() != null) {
                try {
                    rfrAvail = sqlFormat.parse(fir.getFirRFRAvail());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            etRFRAvail.setText(fir.getFirRFRAvail());
            /*timeStart = Time.valueOf(start.getText().toString());
            System.out.println("Edit Start: " + timeStart);
            timeEnd = Time.valueOf(end.getText().toString());
            System.out.println("Edit End: " + timeEnd);
            */
            harvMeth.setSelection(fir.getIdxByCode(getResources().getStringArray(R.array.harvMeth), fir.getFirHarvMeth()));
            String[] obst = fir.getFirObst().split(", ");
            for (int i = 0; i < obst.length; i++) {
                System.out.println("Obstruction " + i + ": " + obst[i]);
            }
            postHarv.setSelection(fir.getIdxByCode(getResources().getStringArray(R.array.postHarvPlan), fir.getFirPostHarv()));
            if (obst.length != 0 && !obst[0].equals("-")) {
                spnObstructions.setSelection(fir.getIndexArray(obst, getResources().getStringArray(R.array.obstructions)));
            }
            mapPath = fir.getFirMap();
            File file = new File(mapPath);
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(fir.getFirMap());
                Bitmap orientedBmp = ExifUtil.rotateBitmap(fir.getFirMap(), bmp);
                imgMap.setImageBitmap(orientedBmp);
            } else {
                mapPath = "";
            }

            attList = firRepo.getAttachments(firID);
            loadAttachments(firID);

        } else {
            title = intent.getStringExtra("title");
        }
        System.out.println("Title Bar: " + title);
        getSupportActionBar().setTitle(title);


    }

    private void setMSSItems(int id, MultiSelectionSpinner mss) {
        String[] items = getResources().getStringArray(id);
        List<String> list = new ArrayList<>(Arrays.asList(items));
        mss.setItems(list);
        mss.setListener(this);
    }

    private void init(String action, String firID) {

        //Initialize fields
        attHeader = findViewById(R.id.attHeader);
        farmNameCode = findViewById(R.id.tvFarmCode);
        fldNo = findViewById(R.id.etFldNo);
        rfrArea = findViewById(R.id.etRFRArea);
        flags = findViewById(R.id.etFlags);
        start = findViewById(R.id.etStart);
        start.setEnabled(false);
        end = findViewById(R.id.etEnd);
        end.setEnabled(false);
        notes = findViewById(R.id.etNotes);
        coor = findViewById(R.id.etCoor);
        harvMeth = findViewById(R.id.spnHarvMeth);
        spnObstructions = findViewById(R.id.spnObstructions);
        btnSelStart = findViewById(R.id.btnSelStart);
        btnSelEnd = findViewById(R.id.btnSelEnd);
        btnSelEnd.setEnabled(false);
        btnSelImg = findViewById(R.id.btnSelImg);
        btnSelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap("");
            }
        });
        btnSelStart.setOnClickListener(this);
        btnSelEnd.setOnClickListener(this);
        btnCheckAtt = findViewById(R.id.btnCheck);
        btnCheckAtt.setOnClickListener(this);
        imgMap = findViewById(R.id.imgMap);
        etContName = findViewById(R.id.etContName);
        etContNum = findViewById(R.id.etContNum);

        chkDayOp = findViewById(R.id.chkDayOp);
        etRFRAvail = findViewById(R.id.etRFRAvail);
        etRFRAvail.setEnabled(false);
        etRFRAvailTime = findViewById(R.id.etRFRAvailTime);
        etRFRAvailTime.setEnabled(false);
        btnSelRFRAvail = findViewById(R.id.btnSelRFRAvail);
        btnSelRFRAvail.setOnClickListener(this);
        btnSelRFRAvailTime = findViewById(R.id.btnSelRFRAvailTime);
        btnSelRFRAvailTime.setEnabled(false);
        btnSelRFRAvailTime.setOnClickListener(this);
        postHarv = findViewById(R.id.spnPostHarv);

        attCaptions = new ArrayList<>();
        attachments = new ArrayList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout firLayout = findViewById(R.id.attachments);
                if (firLayout.getChildCount() >= 2) {
                    Toast.makeText(getApplicationContext(), "Can only attach up to 2 photos.", Toast.LENGTH_SHORT).show();
                } else {
                    selectMap("Att");
                }

            }
        });

        farmNameCode.setText(farm.getFarmName() + " (" + farmCode + ")\n" + owner.getOwnerName() + "\nFIR No.: " + firID);
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
                System.out.println("Saving...");
                save();
                return true;
            case R.id.action_cancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public String getAttCode(String str) {
        return str.substring(str.indexOf("(") + 1, str.indexOf(")"));
    }

    //Save FIR
    private void save() {
        FIR fir = new FIR();
        FIRRepo fRepo = new FIRRepo();
        String createDate = dispFormat.format(new Date());

        if (isValid()) {
            fir.setFirID(firID);
            fir.setFirDate(sqlFormat.format(new Date()));
            fir.setFirFarmCode(farmCode);
            fir.setFirFldNo(fldNo.getText().toString());
            fir.setFirRFRArea(Double.parseDouble(rfrArea.getText().toString()));

            String obst = spnObstructions.getSelectedItemsAsString();
            System.out.println("ObstructionSave: " + obst);
            if (obst.equalsIgnoreCase("")) {
                fir.setFirObst("-");
            } else {
                fir.setFirObst(obst);
            }
            fir.setFirHarvMeth(getAttCode(harvMeth.getSelectedItem().toString()));

            if (flags.getText().toString().equalsIgnoreCase("")) {
                fir.setFirFlags(0);
            } else {
                fir.setFirFlags(Integer.parseInt(flags.getText().toString()));
            }

            fir.setFirStart(timeStart.toString());
            fir.setFirEnd(timeEnd.toString());
            fir.setFirNotes(notes.getText().toString());
            fir.setFirCoorName(coor.getText().toString());
            System.out.println("Day Operation? : " + (chkDayOp.isChecked() ? 1 : 0));
            fir.setFirDayOp(chkDayOp.isChecked() ? 1 : 0);
            System.out.println("Save:\t" + sqlFormat.format(rfrAvail));
            fir.setFirRFRAvail(sqlFormat.format(rfrAvail));
            fir.setFirPostHarv(getAttCode(postHarv.getSelectedItem().toString()));

            updateCoor(fir.getFirCoorName());
            fir.setFirMap(mapPath);

            Contact contact;
            ContactRepo contactRepo = new ContactRepo();
            contact = contactRepo.getContactByFarm(fir.getFirFarmCode());

            if (contact.getContName() == null) {
                contact.setContFarmCode(farmCode);
                contact.setContName(etContName.getText().toString());
                contact.setContNum(etContNum.getText().toString());
                contactRepo.insert(contact);
            } else {
                contact.setContFarmCode(farmCode);
                contact.setContName(etContName.getText().toString());
                contact.setContNum(etContNum.getText().toString());
                contactRepo.update(contact);
            }

            if (action.equalsIgnoreCase("New")) {
                fRepo.insert(fir);
                saveAttachments();
                Toast.makeText(this, "FIR No. " + firID + " created on " + createDate, Toast.LENGTH_SHORT).show();
            } else {
                fRepo.update(fir);
                fir.setFirPath("");
                fRepo.updateFIRPath(fir);
                saveAttachments();
                Toast.makeText(this, "FIR No. " + firID + " successfully updated.", Toast.LENGTH_SHORT).show();
            }
            finish();
            Intent objIntent;
            objIntent = new Intent(getApplicationContext(), FIRPreview.class);
            objIntent.putExtra("firID", fir.getFirID());
            startActivity(objIntent);
        }

    }


    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(final View v) {
        if (v == btnSelStart || v == btnSelEnd || v == btnSelRFRAvailTime) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            final SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            // Launch Time Picker Dialog
            final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            if (v == btnSelStart) {

                                timeStart = new Time(hourOfDay, minute, 0);
                                timeEnd = null;
                                end.setText("");
                                System.out.println("Time Start: " + timeStart.toString());

                                if (end.getText().toString().equalsIgnoreCase("")) {
                                    if (timeEnd != null) {
                                        if (timeStart.after(timeEnd)) {
                                            Toast.makeText(getApplicationContext(), "Time start should not be after time end.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }   
                                start.setText(timeFmt.format(timeStart));
                                btnSelEnd.setEnabled(true);
                            } else if (v == btnSelEnd){
                                timeEnd = new Time(hourOfDay, minute, 0);

                                if (timeEnd.before(timeStart)) {
                                    Toast.makeText(getApplicationContext(), "Time end should not be before time start.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                end.setText(timeFmt.format(timeEnd));
                            } else if (v == btnSelRFRAvailTime) {
                                timeRFRAvail = new Time(hourOfDay, minute, 0);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(rfrAvail);
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                System.out.println("RFRAvailDateTime:\t" + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(cal.getTime()));
                                etRFRAvailTime.setText(timeFmt.format(timeRFRAvail));
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } else if (v == btnCheckAtt) {
            saveAttachments();
        } else if (v == btnSelRFRAvail) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mMonth = c.get(Calendar.MONTH);
            mYear = c.get(Calendar.YEAR);

            System.out.println("Month: " + mMonth + ", Day: " + mDay + ", Year: " + mYear);


            final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, month);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            
                            Date today = new Date();

                            rfrAvail = cal.getTime();

                            if (rfrAvail.before(today)) {
                                Toast.makeText(getApplicationContext(), "Date should not be before today.", Toast.LENGTH_SHORT).show();
                                etRFRAvail.setText("");
                                return;
                            }
                            System.out.println("RFR Avail: " + rfrAvail);
                            etRFRAvail.setText(dispFormat.format(rfrAvail));
                            etRFRAvailTime.setText("");
                            btnSelRFRAvailTime.setEnabled(true);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

    private void selectMap(String type) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            Intent fileintent = new Intent(Intent.ACTION_PICK).setType("image/*");
            if (type.equals("Att")) {
                try {
                    startActivityForResult(Intent.createChooser(fileintent, "Select map"), 2);
                } catch (ActivityNotFoundException e) {
                    System.out.println("Not found");
                }
            } else {
                try {
                    startActivityForResult(Intent.createChooser(fileintent, "Select map"), requestcode);
                } catch (ActivityNotFoundException e) {
                    System.out.println("Not found");
                }
            }
        } else {
            // Permission is missing and must be requested.
            System.out.println("Requesting storage permission");
            requestStoragePermission();
        }
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
                    ActivityCompat.requestPermissions(FIRDetails.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == MainActivity.PERMISSION_REQUEST_WRITESTORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Storage access granted.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                selectMap("Att");
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Storage access denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Request Code: " + requestCode);
        if (data == null) {
            System.out.println("Cancelled");
            return;
        }
        switch (requestCode) {
            case requestcode:

                //Normal setting of image via URI
                Uri uri = data.getData();
                mapPath = Utils.getActualPath(this, uri);

                //Process via ExifUtil to correct orientation
                String filepath = Utils.getActualPath(this, uri);
                Bitmap bmp = BitmapFactory.decodeFile(filepath);
                Bitmap orientedBmp = ExifUtil.rotateBitmap(filepath, bmp);
                imgMap.setImageBitmap(orientedBmp);
                break;
            case 2:

                uri = data.getData();
                addAttachment(uri);

                break;

        }
    }


    private void addAttachment(Uri uri) {
        final LinearLayout firLayout = findViewById(R.id.attachments);

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addAtt = layoutInflater.inflate(R.layout.attachment, null);
        System.out.println("AttID: " + addAtt.getId());
        Button remove = addAtt.findViewById(R.id.removeBtn);

        final View.OnClickListener removeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) addAtt.getParent()).removeView(addAtt);
            }
        };
        remove.setOnClickListener(removeListener);

        String filepath;
        ImageView attImg = addAtt.findViewById(R.id.attImg);
        filepath = Utils.getActualPath(this, uri);
        Bitmap bmp = BitmapFactory.decodeFile(filepath);
        Bitmap orientedBmp = ExifUtil.rotateBitmap(filepath, bmp);
        attImg.setImageBitmap(orientedBmp);
        attImg.setTag(filepath);

        firLayout.addView(addAtt);

    }

    private void loadAttachments(String firID) {
        if (attList.size() > 0) {
            for (int i = 0;i<attList.size();i++){
                String filepath = attList.get(i).get("AttPath");
                String caption = attList.get(i).get("AttCaption");
                File file  = new File(attList.get(i).get("AttPath"));
                if (file.exists()) {

                    final LinearLayout firLayout = findViewById(R.id.attachments);

                    LayoutInflater layoutInflater =
                            (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View addAtt = layoutInflater.inflate(R.layout.attachment, null);
                    Button remove = addAtt.findViewById(R.id.removeBtn);
                    TextInputEditText captionET = addAtt.findViewById(R.id.caption);
                    captionET.setText(caption);

                    final View.OnClickListener removeListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) addAtt.getParent()).removeView(addAtt);
                        }
                    };
                    remove.setOnClickListener(removeListener);

                    ImageView attImg = addAtt.findViewById(R.id.attImg);
                    Bitmap bmp = BitmapFactory.decodeFile(filepath);
                    Bitmap orientedBmp = ExifUtil.rotateBitmap(filepath, bmp);
                    attImg.setImageBitmap(orientedBmp);
                    attImg.setTag(filepath);

                    firLayout.addView(addAtt);
                }

            }
        }
    }

    private void saveAttachments() {

        FIRRepo repo = new FIRRepo();
        LinearLayout firLayout = findViewById(R.id.attachments);
        int attCount = firLayout.getChildCount();
        repo.deleteAtt(firID);

        for (int i = 0; i < attCount; i++) {

            LinearLayout rootLayout = (LinearLayout) firLayout.getChildAt(i);
            ConstraintLayout subLayout1 = (ConstraintLayout) rootLayout.getChildAt(0);
            TextInputLayout subLayout2 = (TextInputLayout) subLayout1.getChildAt(0);
            FrameLayout subLayout3 = (FrameLayout) subLayout2.getChildAt(0);
            TextInputEditText cap = (TextInputEditText) subLayout3.getChildAt(0);
            ImageView img = (ImageView) rootLayout.getChildAt(1);
            System.out.println("Attachment: \n" +
                    "Path: " + img.getTag().toString() + "\n" +
                    "Caption: " + cap.getText().toString());
            repo.insertFIRAtt(firID, img.getTag().toString(), cap.getText().toString());
        }
    }

    private boolean isValid() {
        if (fldNo.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Field no. can't be blank.", Snackbar.LENGTH_SHORT).show();
            fldNo.requestFocus();
            return false;
        }

        if (rfrArea.getText().toString().equalsIgnoreCase("") ||
                Double.parseDouble(rfrArea.getText().toString()) <= 0) {
            Snackbar.make(mLayout, "RFR Area can't be zero or blank.", Snackbar.LENGTH_SHORT).show();
            rfrArea.requestFocus();
            return false;
        }

        if (harvMeth.getSelectedItemPosition() == 0) {
            Snackbar.make(mLayout, "Choose the harvest method.", Snackbar.LENGTH_SHORT).show();
            harvMeth.requestFocus();
            return false;
        }

        if (start.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please set the start time.", Snackbar.LENGTH_SHORT).show();
            btnSelStart.requestFocus();
            return false;
        }

        if (end.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please set the end time.", Snackbar.LENGTH_SHORT).show();
            btnSelEnd.requestFocus();
            return false;
        }

        if (coor.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please enter coordinator name.", Snackbar.LENGTH_SHORT).show();
            btnSelEnd.requestFocus();
            return false;
        }

        if (mapPath.equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please upload QField map screenshot.", Snackbar.LENGTH_SHORT).show();
            btnSelImg.requestFocus();
            return false;
        }

        if (etRFRAvail.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please set the availability of RFR.", Snackbar.LENGTH_SHORT).show();
            btnSelRFRAvail.requestFocus();
            return false;
        }

        if (etRFRAvailTime.getText().toString().equalsIgnoreCase("")) {
            Snackbar.make(mLayout, "Please set the time of RFR Availability.", Snackbar.LENGTH_SHORT).show();
            btnSelRFRAvailTime.requestFocus();
            return false;
        }

        if (postHarv.getSelectedItemPosition() == 0) {
            Snackbar.make(mLayout, "Please select the post harvest plan.", Snackbar.LENGTH_SHORT).show();
            postHarv.requestFocus();
            return false;
        }

        return true;
    }

    private void updateCoor(String coor) {
        ConfigRepo repo = new ConfigRepo();
        Config config = new Config();
        config.setCfgCoor(coor);
        String curCoor = repo.getCurCoor();
        if (!config.getCfgCoor().equalsIgnoreCase(curCoor)) {
            repo.insert(config);
        }
    }

}
