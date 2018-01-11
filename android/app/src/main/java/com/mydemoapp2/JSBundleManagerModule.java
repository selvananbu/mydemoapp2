package com.mydemoapp2;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by selvaanb on 12/19/2017.
 */

public class JSBundleManagerModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext context;

    public JSBundleManagerModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @Override
    public String getName() {
        return "ReactNativeAutoUpdater";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<String, Object>();
        SharedPreferences prefs = this.context.getSharedPreferences(
               JSBundleManager.RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE
        );
        String version =  prefs.getString(JSBundleManager.RNAU_STORED_VERSION, null);
        constants.put("jsCodeVersion", version);
        return constants;
    }
}
