package com.handstandtech.timetap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import android.graphics.Bitmap;

public class MemoryCache {
	private static HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	/**
	 * @param id
	 *            Should be UTF-8 Encoded
	 */
	public static Bitmap get(String id) {
		id = Util.utf8Encode(id);
		if (!cache.containsKey(id))
			return null;
		SoftReference<Bitmap> ref = cache.get(id);
		return ref.get();
	}

	/**
	 * @param id
	 *            Should be UTF-8 Encoded
	 */
	public static void put(String id, Bitmap bitmap) {
		id = Util.utf8Encode(id);
		cache.put(id, new SoftReference<Bitmap>(bitmap));
	}

	public static void clear() {
		cache.clear();
	}
}