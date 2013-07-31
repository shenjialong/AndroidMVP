package com.example.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.os.Environment;

public class GlobalUtil {
	static String externPath;

	public static String getExternalAbsolutePath(Context context) {
		if (externPath == null) {
			externPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		return externPath;
	}
	
	public static String getId() { 
        try { 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            UUID uuid = UUID.randomUUID(); 
            String guidStr = uuid.toString(); 
            md.update(guidStr.getBytes(), 0, guidStr.length()); 
            return new BigInteger(1, md.digest()).toString(16); 
        } catch (NoSuchAlgorithmException e) { 
            return null; 
        } 
    }

}
