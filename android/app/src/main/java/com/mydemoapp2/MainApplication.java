package com.mydemoapp2;

import android.app.Application;

import com.mydemoapp2.JSBundleManager;
import com.mydemoapp2.JSBundleManager.JSBundleManagerFrequency;
import com.mydemoapp2.JSBundleManager.JSBundleManagerUpdateType;
import com.mydemoapp2.JSBundleManager;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;



import	android.content.res.AssetManager;

public class MainApplication extends Application implements ReactApplication {

    private JSBundleManager updater;
    private String file;

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {



    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage());
//          JSBundleManagerActivity.getBundleManager(getApplicationContext()));
    }

     @Override
     public String getJSBundleFile() {
       return JSBundleManagerActivity.getBundleManager(getApplicationContext()).getLatestJSCodeLocation();
   }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
  }
}
