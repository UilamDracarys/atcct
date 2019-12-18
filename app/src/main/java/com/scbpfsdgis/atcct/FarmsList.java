package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.scbpfsdgis.atcct.Utils.SearchableAdapter;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;

import java.util.ArrayList;
import java.util.HashMap;

public class FarmsList extends AppCompatActivity {
    TextView tvFarmCode;
    private View mLayout;
    private Menu menu;
    SearchableAdapter adapter;
    ArrayList<HashMap<String, String>> farmsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farms_list);
        mLayout = findViewById(R.id.farms_list_layout);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        loadFarms();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadFarms();
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void loadFarms() {
        final FarmsRepo repo = new FarmsRepo();
        farmsList = repo.getFarmsList();

        ListView lv = findViewById(R.id.farmsList);
        lv.setFastScrollEnabled(true);
        if (farmsList.size() != 0) {
            lv = findViewById(R.id.farmsList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    if (b != null) {
                        tvFarmCode = view.findViewById(R.id.farmCode);
                        String farmCode = tvFarmCode.getText().toString();
                        System.out.println("Selected Farm: " + farmCode);
                        Farms farm = repo.getFarmByID(farmCode, "M");
                        Intent objIndent;
                        objIndent = new Intent(getApplicationContext(), FarmDetails.class);
                        objIndent.putExtra("farmCode", farm.getFarmCode());
                        startActivity(objIndent);
                    }
                }
            });
            adapter = new SearchableAdapter(FarmsList.this,
                    farmsList,
                    R.layout.farms_list_item,
                    new String[]{"farmCode", "farmName", "planter"},
                    new int[]{R.id.farmCode, R.id.farmName, R.id.pltrName});
            lv.setAdapter(adapter);
        } else {
            adapter = null;
            lv.setAdapter(adapter);
        }
    }


}
