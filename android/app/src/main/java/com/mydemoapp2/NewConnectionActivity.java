package com.mydemoapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewConnectionActivity extends JSBundleManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("New Connection");


    }

    public void onTestConnection(View v) {

        EditText urlText = (EditText) findViewById(R.id.editText);
        String urlString = urlText.getText().toString();
        if(!urlString.isEmpty()) {
                if(!isValidURL(urlString)){
                    JSBundleManagerActivity.getBundleManager(getBaseContext()).showProgressToast(R.string.auto_updater_url_broken);
                }
                else{
                    JSBundleManagerActivity.getBundleManager(getBaseContext()).showProgressToast(R.string.auto_updater_url_sucess);
                }
        }
    }

    public void onConnectPressed(View v){

         EditText urlText = (EditText) findViewById(R.id.editText);
        String urlString = urlText.getText().toString();
        if(!urlString.isEmpty()){
            if(!isValidURL(urlString)){
                JSBundleManagerActivity.getBundleManager(getBaseContext()).showProgressToast(R.string.auto_updater_url_broken);
                return;
            }
            JSBundleManager manager = JSBundleManagerActivity.getBundleManager(getBaseContext());
            if(manager!=null){
//                manager.downloadNewversion(urlString);
                Intent resultIntent =  getIntent();
                resultIntent.putExtra("URL",urlString);
                setResult(RESULT_OK,resultIntent);
                finish();

            }
        }
    }

    private boolean isValidURL(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            else{
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
