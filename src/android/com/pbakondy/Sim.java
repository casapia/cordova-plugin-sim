package com.pbakondy;

import static by.chemerisuk.cordova.support.ExecutionThread.WORKER;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

public class Sim extends ReflectiveCordovaPlugin {
    private static final String TAG = "CordovaPluginSim";
    private CallbackContext requestPermissionCallback;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void pluginInitialize() {
//        requestPermissionLauncher = cordova.getActivity().registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//            requestPermissionCallback.sendPluginResult(new PluginResult(PluginResult.Status.OK, isGranted));
//        });

        requestPermissionLauncher = cordova.getActivity().registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (requestPermissionCallback != null) {
                        requestPermissionCallback.sendPluginResult(new PluginResult(PluginResult.Status.OK, isGranted));
                        requestPermissionCallback = null;
                    }
                });
    }

    @CordovaMethod(WORKER)
    private void shouldShowRequestPermissionRationale(CallbackContext callbackContext) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,
                ActivityCompat.shouldShowRequestPermissionRationale(cordova.getActivity(),
                        Manifest.permission.READ_PHONE_STATE)));
    }

    @CordovaMethod(WORKER)
    private void requestReadPermission(CallbackContext callbackContext) {
        try {
            this.requestPermissionCallback = callbackContext;
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
            Log.d(TAG, "requestReadPermission Launched");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    @CordovaMethod(WORKER)
    private void hasReadPermission(CallbackContext callbackContext) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,
                ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED));
    }

    @CordovaMethod(WORKER)
    private void getSimInfoLite(CallbackContext callbackContext) {
        try {
            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            String mcc = "";
            String mnc = "";
            String simOperator = manager.getSimOperator();
            if (simOperator.length() >= 3) {
                mcc = simOperator.substring(0, 3);
                mnc = simOperator.substring(3);
            }
            JSONObject result = new JSONObject();
            result.put("carrierName", manager.getSimOperatorName());
            result.put("countryCode", manager.getSimCountryIso());
            result.put("mcc", mcc);
            result.put("mnc", mnc);
            result.put("dataActivity", manager.getDataActivity());
            result.put("phoneType", manager.getPhoneType());
            result.put("simState", manager.getSimState());
            result.put("isNetworkRoaming", manager.isNetworkRoaming());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                result.put("phoneCount", manager.getActiveModemCount());
            }
//            result.put("activeSubscriptionInfoCount", subscriptionManager.getActiveSubscriptionInfoCount());
            result.put("activeSubscriptionInfoCountMax", subscriptionManager.getActiveSubscriptionInfoCountMax());
            result.put("defaultDataSubscriptionId", SubscriptionManager.getDefaultDataSubscriptionId());
            callbackContext.success(result);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    @CordovaMethod(WORKER)
    private void getSimInfo(CallbackContext callbackContext) {
        try {
            if (ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                callbackContext.error("Permission must be granted to get sim info");
                return;
            }

            Context context = this.cordova.getActivity().getApplicationContext();
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            String mcc = "";
            String mnc = "";
            String simOperator = manager.getSimOperator();
            if (simOperator.length() >= 3) {
                mcc = simOperator.substring(0, 3);
                mnc = simOperator.substring(3);
            }
            JSONObject result = new JSONObject();
            result.put("carrierName", manager.getSimOperatorName());
            result.put("countryCode", manager.getSimCountryIso());
            result.put("mcc", mcc);
            result.put("mnc", mnc);
            result.put("dataActivity", manager.getDataActivity());
            result.put("phoneType", manager.getPhoneType());
            result.put("simState", manager.getSimState());
            result.put("isNetworkRoaming", manager.isNetworkRoaming());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                result.put("phoneCount", manager.getActiveModemCount());
            }
            result.put("activeSubscriptionInfoCount", subscriptionManager.getActiveSubscriptionInfoCount());
            result.put("activeSubscriptionInfoCountMax", subscriptionManager.getActiveSubscriptionInfoCountMax());
            result.put("defaultDataSubscriptionId", SubscriptionManager.getDefaultDataSubscriptionId());

            List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
            JSONArray sims = new JSONArray();
            for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {
                CharSequence carrierName = subscriptionInfo.getCarrierName();
                JSONObject simData = new JSONObject();
                simData.put("carrierName", carrierName.toString());
                simData.put("displayName", subscriptionInfo.getDisplayName().toString());
                simData.put("countryCode", subscriptionInfo.getCountryIso());
                simData.put("mcc", subscriptionInfo.getMcc());
                simData.put("mnc", subscriptionInfo.getMnc());
                simData.put("isNetworkRoaming", subscriptionManager.isNetworkRoaming(subscriptionInfo.getSimSlotIndex()));
                simData.put("isDataRoaming", (subscriptionInfo.getDataRoaming() == 1));
                simData.put("simSlotIndex", subscriptionInfo.getSimSlotIndex());
                simData.put("phoneNumber", subscriptionInfo.getNumber());
                // Using these device identifiers is not recommended other than for high value
                // fraud prevention and advanced telephony use-cases. For advertising use-cases,
                // use AdvertisingIdClient$Info#getId and for analytics, use InstanceId#getId.
                // Issue id: HardwareIds
                //simData.put("deviceId", manager.getDeviceId(simSlotIndex));
                simData.put("simSerialNumber", subscriptionInfo.getIccId());
                simData.put("subscriptionId", subscriptionInfo.getSubscriptionId());
                sims.put(simData);
            }
            result.put("cards", sims);
            callbackContext.success(result);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }
}
