package com.handstandtech.timetap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileCacheUtil {

	public static void putBitmapInFileCache(String imgSrc, Bitmap bitmap) {
		imgSrc = Util.utf8Encode(imgSrc);
		if (bitmap != null) {
			try {
				File file = getFileInCache(imgSrc);
				FileUtil.assureFileCreated(file);
				FileOutputStream outputStream = new FileOutputStream(file);
				boolean successful = bitmap.compress(
						Bitmap.CompressFormat.JPEG, 100, outputStream);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		} else {
			Log.e(Constants.TAG, "Bitmap was null. - " + imgSrc);
		}

	}

	private static File getFileInCache(String imgSrc) {
		File cacheDir = FileUtil.getCacheDirectory();
		return new File(cacheDir, imgSrc);
	}

	public static Bitmap getBitmapFromFileCache(String imgSrc) {
		imgSrc = Util.utf8Encode(imgSrc);
		Bitmap bitmap = null;
		try {
			File file = getFileInCache(imgSrc);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return bitmap;
	}
}
