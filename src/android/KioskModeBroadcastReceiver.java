package com.huayu.cordova.plugin.android.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class KioskModeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// String actionName= intent.getAction();
		// Toast.makeText(context, "接收到广播:"+actionName, Toast.LENGTH_SHORT).show();
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			AppAutoStarter autoStarter = new AppAutoStarter();
			autoStarter.run(context);
		}
	}

	public class AppAutoStarter {
		public void run(Context context) {
			SharedPreferences sharedPreferences = context
					.getSharedPreferences(AndroidKioskPlugin.SharedPreferencesName, Context.MODE_PRIVATE);
			String packageName = context.getPackageName();
			String activityClassName = sharedPreferences.getString(AndroidKioskPlugin.SharedPreferencesKey, "");
			if (!activityClassName.equals("")) {
				String finalClassName = String.format("%s.%s", packageName, activityClassName);
				Intent launchIntent = new Intent();
				launchIntent.setClassName(context, finalClassName);
				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				launchIntent.putExtra("com.huayu.cordova.plugin.android.kiosk auto start", true);
				ContextCompat.startActivity(context, launchIntent, null);
			}
		}

	}
}