package com.handstandtech.timetap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SerializationUtil {
	public static byte[] getBytes(Object serializableObj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out;
			out = new ObjectOutputStream(bos);

			out.writeObject(serializableObj);
			byte[] yourBytes = bos.toByteArray();

			out.close();
			bos.close();
			return yourBytes;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	public static Object getObjectFromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = new ObjectInputStream(bis);

			Object o = in.readObject();

			bis.close();
			in.close();
			return o;
		} catch (StreamCorruptedException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		return null;
	}

}
