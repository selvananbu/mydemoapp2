package com.mydemoapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.NativeModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author anbu
 */

public class JSBundleManager  implements ReactPackage{

    public static final String RNAU_SHARED_PREFERENCES = "React_Native_Auto_Updater_Shared_Preferences";
    public static final String RNAU_STORED_VERSION = "React_Native_Auto_Updater_Stored_Version";
    private final String RNAU_LAST_UPDATE_TIMESTAMP = "React_Native_Auto_Updater_Last_Update_Timestamp";
    private final String RNAU_STORED_JS_FILENAME = "main.android.jsbundle";
    private final String RNAU_STORED_JS_FOLDER = "JSCode";

    public enum JSBundleManagerFrequency {
        EACH_TIME, DAILY, WEEKLY
    }

    public enum JSBundleManagerUpdateType {
        MAJOR, MINOR, PATCH
    }

    private static JSBundleManager ourInstance;
    private String updateMetadataUrl;
    private String metadataAssetName;
    private JSBundleManagerFrequency updateFrequency = JSBundleManagerFrequency.EACH_TIME;
    private JSBundleManagerUpdateType updateType = JSBundleManagerUpdateType.MINOR;
    private Context context;
    private boolean showProgress = true;
    private String hostname;
    private Interface activity;



    public static JSBundleManager getInstance(Context context) {
        if(ourInstance == null || context == null || context.getApplicationInfo() == null){
            ourInstance = new JSBundleManager();
            ourInstance.context = context;
        }
        return ourInstance;
    }

    private JSBundleManager() {
        ourInstance = this;
    }

    public JSBundleManager setUpdateMetadataUrl(String url) {
        this.updateMetadataUrl = url;
        return this;
    }

    public JSBundleManager setMetadataAssetName(String metadataAssetName) {
        this.metadataAssetName = metadataAssetName;
        return this;
    }

    public JSBundleManager setUpdateFrequency(JSBundleManagerFrequency frequency) {
        this.updateFrequency = frequency;
        return this;
    }

    public JSBundleManager setUpdateTypesToDownload(JSBundleManagerUpdateType updateType) {
        this.updateType = updateType;
        return this;
    }

    public JSBundleManager setHostnameForRelativeDownloadURLs(String hostnameForRelativeDownloadURLs) {
        this.hostname = hostnameForRelativeDownloadURLs;
        return this;
    }


    public JSBundleManager setParentActivity(Interface activity) {
        this.activity = activity;
        return this;
    }

    public JSBundleManager checkForUpdates() {
        if (this.shouldCheckForUpdates()) {
            this.showProgressToast(R.string.auto_updater_checking);
            FetchMetadataTask task = new FetchMetadataTask();
            task.execute(this.updateMetadataUrl);
        }
        return this;
    }

    private boolean shouldCheckForUpdates() {
        if (this.updateFrequency == JSBundleManagerFrequency.EACH_TIME) {
            return true;
        }

        SharedPreferences prefs = context.getSharedPreferences(RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        long msSinceUpdate = System.currentTimeMillis() - prefs.getLong(RNAU_LAST_UPDATE_TIMESTAMP, 0);
        int daysSinceUpdate = (int) (msSinceUpdate / 1000 / 60 / 60 / 24);

        switch (this.updateFrequency) {
            case DAILY:
                return daysSinceUpdate >= 1;
            case WEEKLY:
                return daysSinceUpdate >= 7;
            default:
                return true;
        }
    }

    public static String getJSBundleFile(){
      if(ourInstance != null) return ourInstance.getLatestJSCodeLocation();
      return "";
    }

    public String getLatestJSCodeLocation() {

        SharedPreferences prefs = context.getSharedPreferences(RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if(prefs.getString(RNAU_STORED_VERSION, null) == null){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(RNAU_STORED_VERSION, "1.0");
            editor.apply();
        }
        String currentVersionStr = prefs.getString(RNAU_STORED_VERSION, null);

        Version currentVersion;
        try {
            currentVersion = new Version(currentVersionStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String jsonString = this.getStringFromAsset(this.metadataAssetName);
        if (jsonString == null) {
            return null;
        } else {
            String jsCodePath = null;
            try {
                JSONObject assetMetadata = new JSONObject(jsonString);
                String assetVersionStr = assetMetadata.getString("version");
                Version assetVersion = new Version(assetVersionStr);

                if (currentVersion.compareTo(assetVersion) > 0) {
                    File jsCodeDir = context.getDir(RNAU_STORED_JS_FOLDER, Context.MODE_PRIVATE);
                    File jsCodeFile = new File(jsCodeDir, RNAU_STORED_JS_FILENAME);
                    jsCodePath = jsCodeFile.getAbsolutePath();
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(RNAU_STORED_VERSION, currentVersionStr);
                    editor.apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsCodePath;
        }
    }
    private String getStringFromAsset(String assetName) {
        String jsonString = null;
        try {
            InputStream inputStream = this.context.getAssets().open(assetName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    private void verifyMetadata(JSONObject metadata) {
        try {
            Object versionObj = metadata.get("android");
            JSONArray jsonarrayObj = null;

            if(versionObj instanceof JSONArray)
              jsonarrayObj = (JSONArray) versionObj;
            if(jsonarrayObj == null) return;

            if(jsonarrayObj.length() == 1){

                //Single Bundle
                JSONObject rec = jsonarrayObj.getJSONObject(0);
                String version = rec.getString("version");
                String minContainerVersion = rec.getString("minimum_version");

                if (this.shouldDownloadUpdate(minContainerVersion)) {
                    this.showProgressToast(R.string.auto_updater_downloading);
                    downloadNewversion(minContainerVersion);
                }else if(this.shouldDownloadUpdate(version)){
                    ourInstance.activity.onNewVersionAvailable(version);
                }
                else {
                    this.showProgressToast(R.string.auto_updater_up_to_date);
                }
            }
            else {
                //Multiple Bundle
                ArrayList<VersionDetails> versionList = getVersionListFromServer(jsonarrayObj);

                boolean isDownloadRequired = false;
                SharedPreferences prefs = context.getSharedPreferences(RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                String currentVersionStr = prefs.getString(RNAU_STORED_VERSION, null);
                boolean isMinVersionAvailable = false;
                String versionMinimum;

                Iterator<VersionDetails> iterator = versionList.iterator();
                while (iterator.hasNext()) {
                        VersionDetails det = iterator.next();
                        if(!det.title.contains(currentVersionStr)) {
                            isMinVersionAvailable = true;
                            break;
                        }
                }



                if(!isMinVersionAvailable){
                    downloadNewversion(versionList.get(0).title);
                    isDownloadRequired = true;
                }

                if(versionList != null && !isDownloadRequired)
                    ourInstance.activity.bundlesFetched(versionList);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private ArrayList<VersionDetails> getVersionListFromServer(JSONArray jsonarrayObj) throws JSONException {
        ArrayList<VersionDetails> versionList = new ArrayList<>();
        VersionDetails details = null;
        for (int i = 0; i < jsonarrayObj.length(); ++i) {
            JSONObject tempVersion = jsonarrayObj.getJSONObject(i);
            details = new VersionDetails(tempVersion.getString("version"),tempVersion.getString("user"),tempVersion.getString("date"));
            versionList.add(details);
        }
        return versionList;
    }

    public void downloadNewversion(String version){
        String downloadURL = "/android" + "/LIS.helloworld/"+"helloworld_"+"android_"+version+".bundle";           //app name + platform + version + type .bundle
          if (this.hostname == null) {
              this.showProgressToast(R.string.auto_updater_no_hostname);
              System.out.println("No hostname provided for relative downloads. Aborting");
          } else {
              downloadURL = this.hostname + downloadURL;
          }
      FetchUpdateTask updateTask = new FetchUpdateTask();
      updateTask.execute(downloadURL, version);

    }

    private boolean shouldDownloadUpdate(String minContainerVersionStr) {
        boolean shouldDownload = false;

        SharedPreferences prefs = context.getSharedPreferences(RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currentVersionStr = prefs.getString(RNAU_STORED_VERSION, null);

            Version currentVersion = new Version(currentVersionStr);
            Version updateVersion = new Version(minContainerVersionStr);
            switch (this.updateType) {
                case MAJOR:
                    if (currentVersion.compareMajor(updateVersion) < 0) {
                        shouldDownload = true;
                    }
                    break;

                case MINOR:
                    if (currentVersion.compareMinor(updateVersion) < 0) {
                        shouldDownload = true;
                    }
                    break;

                case PATCH:
                    if (currentVersion.compareTo(updateVersion) < 0) {
                        shouldDownload = true;
                    }
                    break;

                default:
                    shouldDownload = true;
                    break;
            }

        //need to check here @Anbu

        return shouldDownload;
    }

    private String getContainerVersion() {
        String version = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public void showProgressToast(int message) {
        if (this.showProgress && context.getResources().getString(message).length() > 0) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
    }

    private class FetchMetadataTask extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... params) {
            String metadataStr;
            JSONObject metadata = null;
            try {
                URL url = new URL(params[0]);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                metadataStr = response.body().string();

                if (!metadataStr.isEmpty()) {
                    metadata = new JSONObject(metadataStr);
                } else {
                    JSBundleManager.this.showProgressToast(R.string.auto_updater_no_metadata);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return metadata;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) {
                JSBundleManager.this.showProgressToast(R.string.auto_updater_invalid_metadata);
            } else {

                JSBundleManager.this.verifyMetadata(jsonObject);
            }
        }
    }

    private class FetchUpdateTask extends AsyncTask<String, Void, String> {

        private PowerManager.WakeLock mWakeLock;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            // mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            // mWakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "";
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();
                File jsCodeDir = context.getDir(RNAU_STORED_JS_FOLDER, Context.MODE_PRIVATE);
                if (!jsCodeDir.exists()) {
                    jsCodeDir.mkdirs();
                }
                File jsCodeFile = new File(jsCodeDir, RNAU_STORED_JS_FILENAME);
                output = new FileOutputStream(jsCodeFile);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }

                    output.write(data, 0, count);
                }

                SharedPreferences prefs = context.getSharedPreferences(RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RNAU_STORED_VERSION, params[1]);
                editor.putLong(RNAU_LAST_UPDATE_TIMESTAMP, new Date().getTime());

                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null){
                    connection.disconnect();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // mWakeLock.release();
            if (result != "") {
                JSBundleManager.this.showProgressToast(R.string.auto_updater_downloading_error);
            } else {
                  JSBundleManager.this.showProgressToast(R.string.auto_updater_downloading_success);
                  ourInstance.activity.updateFinished();
            }
        }
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        List<NativeModule> nativeModules = new ArrayList<>();
        return nativeModules;
    }

    public interface Interface {

        void updateFinished();

        void bundlesFetched(ArrayList<VersionDetails> versionList);

        void onNewVersionAvailable(String version);
    }

    @Override
   public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
       return new ArrayList<>();
   }
}
