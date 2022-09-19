package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.Utils.SearchableAdapter;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.util.ArrayList;
import java.util.HashMap;

public class OwnersList extends AppCompatActivity {
    TextView tvOwnerID;
    private View mLayout;
    private Menu menu;
    FloatingActionButton newItem;
    SearchableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_list);
        mLayout = findViewById(R.id.owners_list_layout);

        /*Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        newItem = findViewById(R.id.fab);
        newItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newOwner();
            }
        });
        /*getSupportActionBar().setTitle("Owner Details");*/

        loadOwners();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        invalidateOptionsMenu();
        onCreateOptionsMenu(this.menu);
        loadOwners();
    }

    private void loadOwners() {
        final OwnersRepo oRepo = new OwnersRepo();
        ArrayList<HashMap<String, String>> ownersList = oRepo.getOwnersList();
        ListView lv = findViewById(R.id.ownersList);
        lv.setFastScrollEnabled(true);
        if (ownersList.size() != 0) {
            System.out.println("Owners: " + ownersList.size());
            lv = findViewById(R.id.ownersList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    System.out.println("intent.getExtras(): " + intent.getExtras());

                    if (b != null) {
                        tvOwnerID = view.findViewById(R.id.ownerID);
                        String ownerID = tvOwnerID.getText().toString();
                        System.out.println("SelOwnerID: " + ownerID);
                        Intent objIndent;
                        objIndent = new Intent(getApplicationContext(), OwnerDetails.class);
                        objIndent.putExtra("ownerID", ownerID);
                        objIndent.putExtra("action", "Edit Owner");
                        startActivity(objIndent);
                    }
                }
            });
            adapter = new SearchableAdapter(OwnersList.this,
                    ownersList,
                    R.layout.owners_list_item,
                    new String[]{"ownerID", "ownerName", "ownerMobile"},
                    new int[]{R.id.ownerID, R.id.ownerName, R.id.ownerMobile});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

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
                    Toast.makeText(getApplicationContext(), "Planters list is empty. Please download the data first.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        chkAll.setVisible(false);
        chkSigned.setVisible(false);
        chkToSign.setVisible(false);
        exportATCCT.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newOwner() {
        OwnersRepo repo = new OwnersRepo();
        String ownerID = repo.newOwnerID();
        System.out.println("New Owner ID: " + ownerID);
        Intent intent = new Intent(this, OwnerDetails.class);
        intent.putExtra("action", "New Owner");
        intent.putExtra("ownerID", ownerID);
        startActivity(intent);
    }

}
