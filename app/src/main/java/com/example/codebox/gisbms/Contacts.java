package com.example.codebox.gisbms;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String activityTile;
    private ArrayList<String> contactsList;
    private ListView contactListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeEmergency);
        setContentView(R.layout.activity_contacts);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        activityTile = intent.getStringExtra("title");
        setTitle(activityTile);
//        Toast.makeText(this, ""+activityTile, Toast.LENGTH_SHORT).show();


        if (activityTile.equals("Ambulance")){
            contactsList = new ArrayList<String>();
            contactsList.add("Service1 0989787612");
            contactsList.add("Service2 0989787613");
        }else{
            contactsList = new ArrayList<String>();
            contactsList.add("Service1 1989787612");
            contactsList.add("Service2 1989787613");
        }

        ArrayAdapter<String> contactItem = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
        contactListView = (ListView) findViewById(R.id.contactListView);
        contactListView.setAdapter(contactItem);
        contactListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String totalString = contactsList.get(position);
        String[] part = totalString.split(" ");

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+part[1]));
        startActivity(intent);

        Toast.makeText(this, "Calling", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
