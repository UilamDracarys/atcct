package com.scbpfsdgis.atcct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;

import java.util.ArrayList;
import java.util.HashMap;

public class FarmsList extends AppCompatActivity {
    TextView tvFarmCode;
    private View mLayout;

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

    private void loadFarms() {
        final FarmsRepo repo = new FarmsRepo();
        ArrayList<HashMap<String, String>> farmsList = repo.getFarmsList();

        ListView lv = findViewById(R.id.farmsList);
        lv.setFastScrollEnabled(true);
        ListAdapter adapter;
        if (farmsList.size() != 0) {
            System.out.println("Farms: " + farmsList.size());
            lv = findViewById(R.id.farmsList);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    Bundle b = intent.getExtras();

                    if (b != null) {
                        tvFarmCode = view.findViewById(R.id.farmCode);
                        System.out.println("Selected Farm: " + tvFarmCode.getText().toString());
                        String farmCode = tvFarmCode.getText().toString();
                        Farms farm = repo.getFarmByID(farmCode, "M");
                        Intent objIndent;
                        objIndent = new Intent(getApplicationContext(), FarmDetails.class);
                        objIndent.putExtra("farmCode", farm.getFarmCode());
                        startActivity(objIndent);
                    }
                }
            });
            adapter = new SimpleAdapter(FarmsList.this,
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
