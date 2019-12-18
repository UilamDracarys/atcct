package com.scbpfsdgis.atcct;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.guna.libmultispinner.MultiSelectionSpinner;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.scbpfsdgis.atcct.MainActivity.requestcode;

public class FIRDetails extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener, android.view.View.OnClickListener {

    private View mLayout;
    String firID, farmCode, action;
    Farms farm;
    Owners owner;
    ArrayList<String> ownersList = new ArrayList<>();
    private int mHour, mMinute;
    Time timeStart, timeEnd;
    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat dispFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());


    //Declare fields
    TextView farmNameCode;
    EditText fldNo, rfrArea, flags, start, end, notes, coor, etContNum;
    Spinner harvMeth;
    MultiSelectionSpinner spnObstructions;
    Button btnSelStart, btnSelEnd, btnSelImg;
    ImageView imgMap;
    String mapPath = "";
    AutoCompleteTextView etContName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir_details);
        mLayout = findViewById(R.id.fir_detail_layout);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        Intent intent = getIntent();
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
            /*timeStart = Time.valueOf(start.getText().toString());
            System.out.println("Edit Start: " + timeStart);
            timeEnd = Time.valueOf(end.getText().toString());
            System.out.println("Edit End: " + timeEnd);
            */
            harvMeth.setSelection(fir.getIdxByCode(getResources().getStringArray(R.array.harvMeth), fir.getFirHarvMeth()));
            String[] obst = fir.getFirObst().split(",");
            if (obst.length != 0 && !obst[0].equals("-")) {
                spnObstructions.setSelection(fir.getIndexArray(obst, getResources().getStringArray(R.array.obstructions)));
            }
            String mapPath = fir.getFirMap();
            File file  = new File(mapPath);
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(fir.getFirMap());
                Bitmap orientedBmp = ExifUtil.rotateBitmap(fir.getFirMap(), bmp);
                imgMap.setImageBitmap(orientedBmp);
            } else {

            }

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
                selectMap();
            }
        });
        btnSelStart.setOnClickListener(this);
        btnSelEnd.setOnClickListener(this);
        imgMap = findViewById(R.id.imgMap);
        etContName = findViewById(R.id.etContName);
        etContNum = findViewById(R.id.etContNum);

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
                Toast.makeText(this, "FIR No. " + firID + " created on " + createDate, Toast.LENGTH_SHORT).show();
            } else {
                fRepo.update(fir);
                fir.setFirPath("");
                fRepo.updateFIRPath(fir);
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

    @Override
    public void onClick(final View v) {
        if (v == btnSelStart || v == btnSelEnd) {
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
                                /*c.setTime(timeStart);
                                c.add(Calendar.MINUTE, 30);
                                timeEnd = new Time(c.getTime().getHours(), c.getTime().getMinutes(), 0);
                                end.setText(timeEnd.getHours() + ":" + timeEnd.getMinutes());*/

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
                            } else {
                                timeEnd = new Time(hourOfDay, minute, 0);
                               /* timeEnd = new Time(hourOfDay, minute, 0);
                                System.out.println("Start: " + timeStart + "\nEnd: " + timeEnd);*/
                                if (timeEnd.before(timeStart)) {
                                    Toast.makeText(getApplicationContext(), "Time end should not be before time start.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                end.setText(timeFmt.format(timeEnd));
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } else {

        }
    }

    private void selectMap() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            Intent fileintent = new Intent(Intent.ACTION_PICK);
            fileintent.setType("image/*");
            try {
                startActivityForResult(Intent.createChooser(fileintent, "Select map"), requestcode);
            } catch (ActivityNotFoundException e) {
                System.out.println("Not found");
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
                selectMap();
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
                //imgMap.setImageURI(uri);


                //Process via ExifUtil to correct orientation
                String filepath = Utils.getActualPath(this, uri);
                Bitmap bmp = BitmapFactory.decodeFile(filepath);
                Bitmap orientedBmp = ExifUtil.rotateBitmap(filepath, bmp);
                imgMap.setImageBitmap(orientedBmp);
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

        if (etContName.getText().toString().equalsIgnoreCase("")) {
            final boolean[] result = {false};
            new AlertDialog.Builder(this)
                    .setTitle("Save")
                    .setMessage(
                            "Do you want to save without contact details?")
                    .setIcon(
                            getResources().getDrawable(
                                    android.R.drawable.ic_dialog_info))
                    .setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result[0] = true;
                                }
                            })
                    .setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result[0] = false;
                                }
                            }).show();
            return result[0];
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


    private void generateFIR() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            try {

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            //Toast.makeText(this, "ATCCT No. " + atccNo + " successfully generated.\nFilename: " + fileName, Toast.LENGTH_LONG).show();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    private void exportPDFFIR() {

    }

}
