package com.mydemoapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mydemoapp2.JSBundleManager.JSBundleManagerUpdateType;
import com.mydemoapp2.JSBundleManager.JSBundleManagerFrequency;

import java.util.ArrayList;


/**
 * @author anbu
 */
public abstract class JSBundleManagerActivity extends AppCompatActivity
        implements JSBundleManager.Interface {

    protected static JSBundleManager updater;
    protected static boolean  _mAlreadyUpdated = false;


    @Override
    public void onDestroy(){
        super.onDestroy();
        updater = null;
      }

    public static JSBundleManager getBundleManager(Context context){
      if(updater == null){
        updater = JSBundleManager.getInstance(context);
        updater.setUpdateMetadataUrl(getUpdateDataUrl())
                      .setMetadataAssetName("metadata.android.json")
                      .setUpdateFrequency(JSBundleManagerFrequency.EACH_TIME)
                      .setUpdateTypesToDownload(JSBundleManagerUpdateType.PATCH)
                      .setHostnameForRelativeDownloadURLs(MainActivity.getMetaDataURL());
      }

      return updater;
    }

    public static String getUpdateDataUrl(){
        return MainActivity.getMetaDataURL()+"/"+MainActivity.getDevice()+"/"+MainActivity.getProductName()+"/"+MainActivity.getUpdateFileName();
    }

    protected String getHostnameForRelativeDownloadURLs() {
        return null;
    }

    protected JSBundleManagerUpdateType getAllowedUpdateType() {
        return JSBundleManagerUpdateType.PATCH;
    }

    protected JSBundleManagerFrequency getUpdateFrequency() {
        return JSBundleManagerFrequency.EACH_TIME;
    }

    @Override
    public void updateFinished() {
//        getReactNativeHost().clear();
        this.recreate();
        _mAlreadyUpdated = true;
    }

    @Override
    public void onNewVersionAvailable(final String version){
        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog);
            alertDialogBuilder.setTitle(R.string.auto_updater_downloaded_title);
            alertDialogBuilder
                    .setMessage("A latest version ("+version+") is available.Do you want to Download it?")
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_action_update)
                    .setPositiveButton(
                            R.string.auto_updater_downloaded_now,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    updater.downloadNewversion(version);
                                }
                            }
                    )
                    .setNegativeButton(
                            R.string.auto_updater_downloaded_later,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bundlesFetched(final ArrayList<VersionDetails> versionList) {

        Dialog dialog = new Dialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ListView modeList = new ListView(this);
//        VersionAdapter<VersionDetails> modeAdapter = new VersionAdapter<VersionDetails>(this,versionList);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View headerView = inflater.inflate(R.layout.listview_header, null, false);
        modeList.addHeaderView(headerView);

        View footerView = inflater.inflate(R.layout.listview_footer, null, false);
        modeList.addFooterView(footerView);


        builder.setView(modeList);
        dialog = builder.create();
        dialog.show();

        final Dialog finalDialog = dialog;
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

//                String version = versionList.get(id).title;
                String version = ((TextView) view.findViewById(R.id.version_list_title)).getText().toString();
                updater.downloadNewversion(version);
                finalDialog.hide();

            }
        });

        Button cancelButton = (Button) footerView.findViewById(R.id.cancel);
        if(cancelButton != null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        finalDialog.hide();
                }
            });
        }



//        modeList.setAdapter(modeAdapter);
    }
}
