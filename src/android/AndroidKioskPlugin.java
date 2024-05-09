package com.huayu.cordova.plugin.android.kiosk;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;

import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * This class echoes a string called from JavaScript.
 */
public class AndroidKioskPlugin extends CordovaPlugin {
  public final static String SharedPreferencesName = "com.huayu.cordova.plugin.android.kiosk.main.class";
  public final static String SharedPreferencesKey = "ClassName";
  private static CallbackContext ExecingCallbackContext;
  private static final String[] REQUIRED_PERMISSIONS = new String[] {
      Manifest.permission.RECEIVE_BOOT_COMPLETED,
      // Manifest.permission.INTERNET,
      // Manifest.permission.BIND_DEVICE_ADMIN,
  };
  private static final int REQUEST_CODE_PERMISSIONS = 10;

  private DevicePolicyManager mDpm;

  public AndroidKioskPlugin() {
    super();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    ExecingCallbackContext = callbackContext;
    if (action.equals("startKiosk")) {
      setAndroidKioskMode(true, callbackContext);
      return true;
    } else if (action.equals("endKiosk")) {
      setAndroidKioskMode(false, callbackContext);
      return true;
    } else if (action.equals("enableAutoStart")) {
      setAppAutoStart(true, callbackContext);
      return true;
    } else if (action.equals("disableAutoStart")) {
      setAppAutoStart(false, callbackContext);
      return true;
    }

    return false;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
      throws JSONException {
    for (int r : grantResults) {
      if (r == PackageManager.PERMISSION_DENIED) {
        ExecingCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
        return;
      }
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : REQUIRED_PERMISSIONS) {
      if (!cordova.hasPermission(permission)) {
        return false;
      }
    }
    return true;
  }

  private void setAppAutoStart(boolean enabled, CallbackContext callbackContext) {
    Context context = cordova.getActivity().getApplicationContext();
    SharedPreferences.Editor sharedPreferencesEditor = context
        .getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE).edit();

    sharedPreferencesEditor.clear();
    if (enabled) {
      String mainActivityClassName = cordova.getActivity().getLocalClassName();
      sharedPreferencesEditor.putString(SharedPreferencesKey, mainActivityClassName);
    } else {
      sharedPreferencesEditor.remove(SharedPreferencesKey);
    }
    sharedPreferencesEditor.commit();
  }

  private void setAndroidKioskMode(boolean enabled, CallbackContext callbackContext) {
    Context activityContext = cordova.getActivity();
    ComponentName deviceAdmin = new ComponentName(activityContext, KioskModeDeviceAdminReceiver.class);
    mDpm = (DevicePolicyManager) activityContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    if (!mDpm.isAdminActive(deviceAdmin)) {
      Toast.makeText(activityContext, "This app is not a device admin!", Toast.LENGTH_SHORT).show();
      Intent deviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
      deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
      deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Activate this device administrator");
      cordova.getActivity().startActivity(deviceAdminIntent);
    }
    String packgeName = activityContext.getPackageName();
    if (mDpm.isDeviceOwnerApp(packgeName)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          mDpm.setLockTaskPackages(deviceAdmin, new String[] { packgeName });
          try {
            if (enabled) {
              if (mDpm.isLockTaskPermitted(packgeName)) {
                cordova.getActivity().startLockTask();
                callbackContext.success("success");
              } else {
                Toast.makeText(activityContext, "Kiosk Mode not permitted", Toast.LENGTH_SHORT).show();
                callbackContext.error("Kiosk Mode not permitted");
              }
            } else {
              cordova.getActivity().stopLockTask();
              callbackContext.success("success");
            }
          } catch (Exception e) {
            Toast.makeText(activityContext, "exception occurred:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error("exception occurred:" + e.getMessage());
            // TODO: Log and handle appropriately
          }
        }
      });
    } else {
      Toast.makeText(activityContext, "This app is not the device owner!", Toast.LENGTH_SHORT).show();
      callbackContext.error("This app is not the device owner!");
    }
    cordova.getActivity().runOnUiThread(() -> {
      try {
        updateSystemUiVisibility(enabled);
      } catch (Exception e) {
        Toast.makeText(activityContext, "An exception occurred while updating the UI:" + e.getMessage(),
            Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void updateSystemUiVisibility(boolean enabledKioskMode) {
    int viewOptions;
    if (enabledKioskMode) {
      viewOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
          View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
          View.SYSTEM_UI_FLAG_FULLSCREEN |
          View.SYSTEM_UI_FLAG_IMMERSIVE;
    } else {
      viewOptions = View.SYSTEM_UI_FLAG_VISIBLE;
    }
    cordova.getActivity().getWindow().getDecorView().setSystemUiVisibility(viewOptions);
  }

  private String getLauncherPackageName() {
    Intent intent = new Intent(Intent.ACTION_MAIN);

    intent.addCategory(Intent.CATEGORY_HOME);

    return this.cordova
        .getActivity()
        .getPackageManager()
        .resolveActivity(intent, 0).activityInfo.packageName;
  }

}
