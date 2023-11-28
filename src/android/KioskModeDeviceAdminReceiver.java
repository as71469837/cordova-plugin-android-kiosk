package com.huayu.cordova.plugin.android.kiosk;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class KioskModeDeviceAdminReceiver extends DeviceAdminReceiver {

	@Override
	public void onEnabled(Context context, Intent intent) {
		Toast.makeText(context, "Device Admin enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return "Warning: Device Admin is going to be disabled.";
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		Toast.makeText(context, "Device Admin disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
		Toast.makeText(context, "Enter Kiosk Mode", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLockTaskModeExiting(Context context, Intent intent) {
		Toast.makeText(context, "Exit Kiosk Mode", Toast.LENGTH_SHORT).show();
	}
}