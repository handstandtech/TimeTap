package com.handstandtech.timetap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "TimeTap";

	public static void writeBytesToFile(File file, byte[] bytes) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(outputStream);
			bos.write(bytes);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getTimeTapStorageDirectoryPath() {
		File externalStorageDirectory = Environment
				.getExternalStorageDirectory();
		String baseStorageDirectoryPath = externalStorageDirectory
				.getAbsolutePath();
		Log.i(TAG, "Basic Storage Directory Path: " + baseStorageDirectoryPath);
		String stayOrganizedStorageDirectoryPath = baseStorageDirectoryPath
				+ "/stayorganized";
		Log.i(TAG, "Time Tap Storage Directory Path: "
				+ stayOrganizedStorageDirectoryPath);
		return stayOrganizedStorageDirectoryPath;
	}

	public static File getPhotosDirectory() {
		String filePath = getTimeTapStorageDirectoryPath() + "/photos/";
		Log.i(TAG, "Photos Directory: " + filePath);
		File photosDir = new File(filePath);
		return photosDir;
	}

	public static File getCacheDirectory() {
		String filePath = getTimeTapStorageDirectoryPath() + "/cache/";
		Log.i(TAG, "Cache Directory: " + filePath);
		File cacheDir = new File(filePath);
		assureDirectoryCreated(cacheDir);
		return cacheDir;
	}

	// public static String getImageFileNameAndEnsureCreated() {
	// File file = createImageFile();
	//
	// return file.getAbsolutePath();
	// }

	public static File createImageFile() {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy_MM_dd_HH_mm_ss_SSS_Z", Locale.US);
		String filename = String.format("%s.jpg", sdf.format(new Date()));
		Log.i(TAG, "Created Image Filename: " + filename);

		// do something when the button is clicked
		File dir = getPhotosDirectory();
		assureDirectoryCreated(dir);
		File file = new File(dir, filename);
		Log.i(TAG, "Directory Path: " + dir.getAbsolutePath());
		Log.d(TAG, "Image Path: " + file.getAbsolutePath());

		// Directory Exists, lets create the file
		Log.i(TAG, "Directory has been created, Creating file...");
		assureFileCreated(file);
		return file;
	}

	public static void assureFileCreated(File file) {
		try {
			boolean fileCreated = file.createNewFile();
			Log.i(TAG, "File Created: " + file.getAbsolutePath());
		} catch (IOException e) {
			Log.e(TAG, "Error Creating File.", e);
		}
	}

	private static void assureDirectoryCreated(File dir) {
		try {
			boolean exists = dir.exists();
			if (!exists) {
				Log.i(TAG, "Directory doesn't exist");
				boolean success = dir.mkdirs();
				if (success) {
					Log.e(TAG, "Directory: created");
				} else {
					Log.e(TAG, "Directory Creation failed");
				}
			} else {
				Log.i(TAG, "Directory already exists.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void debugPrintStorageLocations(Activity activity) {
		Log.i(TAG, "External Storage Directory: "
				+ Environment.getExternalStorageDirectory().getAbsolutePath());
		Log.i(TAG, "Cache Directory: "
				+ activity.getCacheDir().getAbsolutePath());
	}
}
