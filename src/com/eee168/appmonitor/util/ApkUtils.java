package com.eee168.appmonitor.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApkUtils {
	private static final String TAG = "ApkUtils";

	public static List<PackageInfo> getAllPackages(Context context) {
		List<PackageInfo> packageList = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();
		packageList = pm.getInstalledPackages(0);
		return packageList;
	}

	public static List<String> getApkFiles() {
		String path = "/";
		List<String> apkFiles = FileTools.getFilesFromPath(path);
		return apkFiles;
	}

	public static List<String> getApkPath(Context context) {
		List<String> apkPathList = new ArrayList<String>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pkginfolist = pm.getInstalledPackages(0);
		for (int i = 0; i < pkginfolist.size(); i++) {
			String sourceDir = pkginfolist.get(i).applicationInfo.sourceDir;
			apkPathList.add(sourceDir);
		}
		return apkPathList;
	}

	public static File getInstallApkFile(Context context, String packageName)
			throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		String sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;
		File file = null;
		if (sourceDir != null) {
			file = new File(sourceDir);
		}
		return file;
	}

	public static Map<String, PackageInfo> getApkPathMap(Context context) {
		Map<String, PackageInfo> apkMap = new HashMap<String, PackageInfo>();
		List<PackageInfo> packageList = getAllPackages(context);
		if (packageList != null && packageList.size() > 0) {
			for (PackageInfo pkg : packageList) {
				apkMap.put(pkg.packageName, pkg);
			}
		}
		return apkMap;
	}
	
	public static Map<String, PackageInfo> getSystemApkPathMap(Context context) {
		Map<String, PackageInfo> apkMap = new HashMap<String, PackageInfo>();
		List<PackageInfo> packageList = getAllPackages(context);
		if (packageList != null && packageList.size() > 0) {
			for (PackageInfo pkg : packageList) {
				if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					apkMap.put(pkg.packageName, pkg);
				}
			}
		}
		return apkMap;
	}

	public static Map<String, PackageInfo> getNonSystemApkPathMap(
			Context context) {
		Map<String, PackageInfo> apkMap = new HashMap<String, PackageInfo>();
		List<PackageInfo> packageList = getAllPackages(context);
		if (packageList != null && packageList.size() > 0) {
			for (PackageInfo pkg : packageList) {
				if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					apkMap.put(pkg.packageName, pkg);
				}
			}
		}
		return apkMap;
	}
}
