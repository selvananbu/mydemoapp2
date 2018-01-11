package com.mydemoapp2;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;

public abstract class ReactFragment extends Fragment {
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    // This method returns the name of our top-level component to show
    public abstract String getMainComponentName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mReactRootView = new ReactRootView(context);

        mReactInstanceManager =
                ((MainApplication) getActivity().getApplication())
                        .getReactNativeHost()
                        .getReactInstanceManager();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mReactInstanceManager!=null){
            ((MainApplication) getActivity().getApplication()).getReactNativeHost().clear();
        }
    }

    @Override
    public ReactRootView onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return mReactRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReactRootView.startReactApplication(
                mReactInstanceManager,
                getMainComponentName(),
                null
        );
    }

    public void recreateManager(){
        if(mReactInstanceManager!=null){
            ((MainApplication) getActivity().getApplication()).getReactNativeHost().clear();
        }
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getActivity().getApplication())
                .setJSBundleFile(JSBundleManagerActivity.getBundleManager(getActivity().getBaseContext()).getLatestJSCodeLocation())
                .setJSMainModulePath("main.android")
                .addPackage(new MainReactPackage())
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

    }
}
