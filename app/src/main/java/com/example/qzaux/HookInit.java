package com.example.qzaux;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookInit implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (BuildConfig.APPLICATION_ID.equals(lpparam.packageName)) {
			XposedHelpers.findAndHookMethod(
				MainActivity.class.getName(),
				lpparam.classLoader,
				"isModuleActivated",
				XC_MethodReplacement.returnConstant(true));
		}

		if ("com.example.toolbox".equals(lpparam.packageName)) {
			XposedHelpers.findAndHookMethod(
				"com.example.toolbox.settings.SettingsActivity",
				lpparam.classLoader,
				"onResume",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) {
						Activity activity = (Activity) param.thisObject;
						ViewGroup content = activity.findViewById(android.R.id.content);

						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

						Button btn = new Button(activity);
						btn.setText("QzAux Hook 设置");
						btn.setPadding(48, 20, 48, 20);
						btn.setAllCaps(false);
						btn.setLayoutParams(lp);
						btn.setOnClickListener(v -> {
							Intent intent = new Intent();
							intent.setClassName("com.example.qzaux",
								"com.example.qzaux.hooksettings.QzAuxSettingsActivity");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							activity.startActivity(intent);
						});
						content.addView(btn, content.getChildCount());
					}
				});
		}

	}

}
