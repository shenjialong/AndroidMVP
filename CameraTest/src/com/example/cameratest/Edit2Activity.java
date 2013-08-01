package com.example.cameratest;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.umeng.analytics.MobclickAgent;

public class Edit2Activity extends Activity {

	NavigationBar nb;
	ListView lv; 
	ListView catLv; 
	Cursor datasource4card;
	Cursor datasource4cato;
//	Ԥ���滻�� λ�õ� �������   ����ҳ���е����λ��position  ��parent
	int replacePosition;
	String parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		Intent  intent =getIntent();
		replacePosition=intent.getIntExtra("position", 0);
		Log.i("sjl", "�滻����  �õ���replacePosition:"+replacePosition);
		parent=intent.getStringExtra("parent");
		Log.i("sjl", "�滻����  �õ���parent id is:"+parent);
		init();
	}
	@Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
	public void init(){
		setupViews();
		initNavigationBar();
	}
	
	public void initNavigationBar(){
		nb.setTvTitle("�滻");
		nb.setBtnRightVisble(false);
		nb.setBtnLeftBacground(R.drawable.ic_back);
		nb.setBtnLeftClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	DataBaseHelper myDbHelper ;
	public void setupViews(){
		nb=(NavigationBar)findViewById(R.id.nb_edit);
		lv=(ListView)findViewById(R.id.listView);
		catLv=(ListView)findViewById(R.id.listView2);
		myDbHelper = DataBaseHelper.getDataBaseHelper(Edit2Activity.this);
		datasource4card=myDbHelper.getDataSource(Constants.TYPE_CARD);
		SimpleCursorAdapter adapter4card=new SimpleCursorAdapter(Edit2Activity.this, R.layout.listitem, datasource4card, new String[]{"name"},new int[]{R.id.listitem});
		lv.setAdapter(adapter4card);
		
		datasource4cato=myDbHelper.getDataSource(Constants.TYPE_CATEGORY);
		SimpleCursorAdapter adapter4cato=new SimpleCursorAdapter(Edit2Activity.this, R.layout.listitem, datasource4cato, new String[]{"name"},new int[]{R.id.listitem});
		catLv.setAdapter(adapter4cato);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int location, long id) {
				replayBack(datasource4card,location,3);
				}
		});
		
		catLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int location, long id) {
				replayBack(datasource4cato,location,3);
				}	
		});
	}
	
	public void replayBack(Cursor datasource,int location,int resultCode){
		Cursor c=datasource;
		if(c.moveToPosition(location)){
			String _id = c.getString(c.getColumnIndex("_id"));  
			String cardname = c.getString(c.getColumnIndex("name"));  
			String type = c.getString(c.getColumnIndex("type"));  
			String image= c.getString(c.getColumnIndex("image"));  
			String audio= c.getString(c.getColumnIndex("audio"));
			Intent data=new Intent();
//			��Ҫ���⽫image��idת��Ϊ·����Ϣ
			data.putExtra("image", image);
			data.putExtra("type", type);
			data.putExtra("name", cardname);
			data.putExtra("audio", audio);
			data.putExtra("_id", _id);
			data.putExtra("position", replacePosition);
			myDbHelper.insertIntoCard_tree(_id, parent, replacePosition);
//			����ǰһ��ҳ�洫�ݹ�����   position parent ������¼
			setResult(resultCode, data);
			Log.i("sjl", "���ڸ������ݿ� ����2*8ҳ��");
			finish();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}

}
