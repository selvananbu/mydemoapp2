package com.mydemoapp2;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class ListConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_connection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Connections");

        initializeList();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newConnection = new Intent(view.getContext(),NewConnectionActivity.class);
                startActivityForResult(newConnection,2);
            }
        });
    }

    private void initializeList() {

        ArrayList<Connection> versionList = new ArrayList<>();
        versionList.add(new Connection("Name 0","17.10","http://10.128.1.2:6030/android/LIS.helloworld/helloworld_android_17.10.bundle"));
        versionList.add(new Connection("Name 1","17.11","http://10.128.1.2:6030/android/LIS.helloworld/helloworld_android_17.11.bundle"));
        versionList.add(new Connection("Name 2","17.12","http://10.128.1.2:6030/android/LIS.helloworld/helloworld_android_17.12.bundle"));
        versionList.add(new Connection("Name 3","17.13","http://10.128.1.2:6030/android/LIS.helloworld/helloworld_android_17.13.bundle"));
        versionList.add(new Connection("Name 4","17.14","http://10.128.1.2:6030/android/LIS.helloworld/helloworld_android_17.14.bundle"));

        final VersionAdapter<VersionDetails> modeAdapter = new VersionAdapter<VersionDetails>(this,versionList);

        ListView main_listView = (ListView) findViewById(R.id.main_connectList);
        if(main_listView != null)
            main_listView.setAdapter(modeAdapter);



        main_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String version = ((TextView) view.findViewById(R.id.version_list_title)).getText().toString();
                Connection conn = (Connection) modeAdapter.getItem(position);
                JSBundleManagerActivity.getBundleManager(getBaseContext()).downloadNewversion(conn.getVersion());
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            String urlString = data.getStringExtra("URL");
            Connection conn = new Connection();
            conn.setName("");
            conn.setConnectionUrl(urlString);

            addToListView(conn);

        }
    }

    private void addToListView(Connection conn) {

        ListView main_listView = (ListView) findViewById(R.id.main_connectList);
        if(main_listView != null)
        {
           VersionAdapter adapter = (VersionAdapter) main_listView.getAdapter();
            if(adapter!= null){
                adapter.add(conn);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
