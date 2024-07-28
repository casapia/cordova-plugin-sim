package com.pbakondy;

import static by.chemerisuk.cordova.support.ExecutionThread.WORKER;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

public class Sim extends ReflectiveCordovaPlugin {
    private static final String TAG = "CordovaPluginSim";

    @CordovaMethod(WORKER)
    private void getSimInfo(CallbackContext callbackContext) {
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
}
