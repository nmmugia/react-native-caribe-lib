package com.mrp;

import android.widget.Toast;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.app.Activity;
import android.device.ScanDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.Map;
import java.util.HashMap;

public class CaribeScanner extends ReactContextBaseJavaModule {


  ScanDevice sm;
	private final static String SCAN_ACTION = "scan.rcv.message";
	private String barcodeStr;
  public CaribeScanner(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @ReactMethod
  protected void sendEvent(ReactContext reactContext,
                        String eventName,
                        @Nullable WritableMap params) {
  reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

  WritableMap params = Arguments.createMap();

  @Override
  public String getName() {
    return "CaribeScanner";
  }


  @ReactMethod
  public void show(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }

  private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
    @Override
      public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        byte[] barcode = intent.getByteArrayExtra("barcode");
        int barcodelen = intent.getIntExtra("length", 0);
        byte temp = intent.getByteExtra("barcodeType", (byte) 0);
        barcodeStr = new String(barcode, 0, barcodelen);
        android.util.Log.i("debug", "----codetype--" + temp + barcodeStr);
        System.out.println(barcodeStr);
        Toast.makeText(getReactApplicationContext(), barcodeStr, 100).show();
        sm.stopScan();
      }
  };

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("SHORT", Toast.LENGTH_SHORT);
    constants.put("LONG", Toast.LENGTH_LONG);
    return constants;
  }

}