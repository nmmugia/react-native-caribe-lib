package com.mrp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.PackageList;
import com.facebook.hermes.reactexecutor.HermesExecutorFactory;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.ReactApplication;
import com.androidbroadcastreceivereventreminder.RNAndroidBroadcastReceiverEventReminderPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.jamesisaac.rnbackgroundtask.BackgroundTaskPackage;
import com.horcrux.svg.SvgPackage;
import com.dieam.reactnativepushnotification.ReactNativePushNotificationPackage;
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
import com.peel.react.TcpSocketsModule;
import com.oblador.vectoricons.VectorIconsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.react.NavigationReactNativeHost;
import com.reactnativenavigation.react.ReactGateway;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class MainApplication extends NavigationApplication {


    ScanDevice sm;
    @Override
    protected ReactGateway createReactGateway() {
        ReactNativeHost host = new NavigationReactNativeHost(this, isDebug(), createAdditionalReactPackages()) {
            @Override
            protected String getJSMainModuleName() {
                return "index";
            }
        };
        return new ReactGateway(this, isDebug(), host);
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    protected List<ReactPackage> getPackages() {
        // Add additional packages you require here
        // No need to add RnnPackage and MainReactPackage
        return Arrays.<ReactPackage>asList(
                // eg. new VectorIconsPackage()
                new VectorIconsPackage(), new AsyncStoragePackage(), new ReactNativePushNotificationPackage(),
                new SvgPackage(), new BackgroundTaskPackage(), new TcpSocketsModule());
    }

    @Override
    public List<ReactPackage> createAdditionalReactPackages() {
        return getPackages();
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReactInstanceManager mReactInstanceManager = getReactNativeHost().getReactInstanceManager();
            ReactApplicationContext mContext = (ReactApplicationContext) mReactInstanceManager.getCurrentReactContext();
            WritableMap params = Arguments.createMap();
            String barcodeStr;
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            barcodeStr = new String(barocode, 0, barocodelen);
            params.putString("code", barcodeStr);
            Toast.makeText(context, barcodeStr, Toast.LENGTH_SHORT).show();
            sendEvent(mContext ,"scanCode", params);
            sm.stopScan();
        }
    };

    public void sendEvent(ReactApplicationContext mContext, String eventName, @Nullable WritableMap params) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
        BackgroundTaskPackage.useContext(this);
        sm = new ScanDevice();
        sm.setOutScanMode(0);
        IntentFilter filter = new IntentFilter();
        filter.addAction("scan.rcv.message");
        registerReceiver(mScanReceiver, filter);
    }
}
