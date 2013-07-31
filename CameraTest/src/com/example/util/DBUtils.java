package com.example.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
	SQLiteDatabase db;
	Context context;
	public DBUtils(Context context){
		this.context=context; 
	}
	boolean firstTime;
	public SQLiteDatabase initDB(){
		//�򿪻򴴽�test.db���ݿ�  
        db=context.openOrCreateDatabase("xiaoyudi.db", Context.MODE_PRIVATE, null);  
        
        if(firstTime){
        	db.execSQL("DROP TABLE IF EXISTS card");  
            db.execSQL("CREATE TABLE card (_id INTEGER PRIMARY KEY AUTOINCREMENT, cardindex SMALLINT,cardname VARCHAR, picindex INT,yyindex INT" +
            		"type VARCHAR)");
        }
        return db; 
	}
	
	
}
