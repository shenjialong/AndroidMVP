package com.example.util;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

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
	
	public static Bitmap preHandleImage(InputStream is){
		
		 BitmapFactory.Options opts = new Options();  
        opts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(is);  
        int bitmapheight = opts.outHeight;  
        int bitmapwidht = opts.outWidth;  
        Log.i("sjl", "bitmapheight"+bitmapheight);
        Log.i("sjl", "bitmapwidht"+bitmapwidht);
        
        if(bitmapheight<500){
//       	 do nothing 
//          �����ݿ��Դ���СͼƬ���˵�  �������Ŵ���
        }else{
       	 int scaleX =4;
            opts.inSampleSize=scaleX;  
        }
        opts.inJustDecodeBounds=false;//���ǵĽ���λͼ  
        bitmap= BitmapFactory.decodeStream(is);
        return bitmap;
	}
	/**
	 * ͼƬԤ���� ����OOM���� 
	 * @param imageView
	 * @param path
	 * @return
	 */
	public static Bitmap preHandleImage(ImageView imageView,String path){
		
		 BitmapFactory.Options opts = new Options();  
         // ���ﲢ���������ļ���һ��λͼ������ǰ�λͼ�ı߿���Ϣ����ȡ����  
         opts.inJustDecodeBounds = true;  
         Bitmap bitmap = BitmapFactory.decodeFile(path, opts);  
         // ��ȡ��ǰλͼ�ı߿���Ϣ  
         int bitmapheight = opts.outHeight;  
         int bitmapwidht = opts.outWidth;  
         Log.i("sjl", "bitmapheight"+bitmapheight);
         Log.i("sjl", "bitmapwidht"+bitmapwidht);
         
         if(bitmapheight<500){
//        	 do nothing 
//           �����ݿ��Դ���СͼƬ���˵�  �������Ŵ���
         }else{
        	 int scaleX =4;
             opts.inSampleSize=scaleX;  
         }
         
         // ��ȡ��ǰ����ı߿���Ϣ  
//         int windowWidht = imageView.getWidth();  
//         int windowHidht = imageView.getHeight();  
           
         
//         int scalaY =2;  
         //��ʱ��X��ı�������  
//         if(scaleX>scalaY&&scalaY>1){  
//             opts.inSampleSize=scaleX;  
//         }  
         //��ʱ��Y��ı�������  
//         if(scalaY>scaleX&&scaleX>1)  
//         {  
//             opts.inSampleSize=scalaY;  
//         }  
           
         opts.inJustDecodeBounds=false;//���ǵĽ���λͼ  
         //�����ű�������ʽ����ʽλͼ  
         bitmap=BitmapFactory.decodeFile(path,opts); 
         return bitmap;
		
	}
	
	
	 public static Bitmap small(Bitmap bitmap) {
		  Matrix matrix = new Matrix(); 
		  matrix.postScale(0.051f,0.051f); //���Ϳ�Ŵ���С�ı���
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
		 }

}
