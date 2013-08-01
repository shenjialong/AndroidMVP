package com.example.cameratest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.po.Card;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.example.util.GlobalUtil;

public class ChildActivity extends Activity {

	NavigationBar nb;
	ImageView iv1;
	ImageView iv2;
	ImageView iv3;
	ImageView iv4;
	ImageView iv5;
	ImageView iv6;
	ImageView iv7;
	ImageView iv8;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	TextView tv4;
	TextView tv5;
	TextView tv6;
	TextView tv7;
	TextView tv8;
	String parent;
//	��ʼ��  �������ڵ������Ϊ  ��Ŀ¼�ڵ� 
	List<ImageView> ivList;
	List<TextView> tvList;
	DataBaseHelper myDbHelper;
	Map <Integer,Card> cardMap;
	int [] displayFlag=new int []{0,0,0,0,0,0,0,0};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		initUI();
		initData();
	}
	
	public void setOnClickListener(){
		
		for(int i=0;i<ivList.size();i++){
			ivList.get(i).setOnClickListener(new ImageViewListener(i));
		}
	}
	
	public class ImageViewListener implements  OnClickListener{
		int position ;
		public ImageViewListener(int position){
			this.position=position;
		}
		@Override
		public void onClick(View v) {
			if(!isLauchPage&&position==0){
//				����ҳ�� ��һ��ITEM Ϊ���ذ�ť
				finish();
			}else{
//				�ж��Ƿ�ΪĿ¼
					Card cardItem=cardMap.get(position);
//					�˴������� һ�����   ���磺 cardMap��size�� 3  ���ǵ����λ����2 ����ϵ�һ���ж�  ���� ʵ���ϵ����λ���ǿյ�   
					if(cardItem!=null){
						if(Constants.TYPE_CATEGORY.equals(cardItem.getType())){
							Log.i("sjl", "���ڽ�����һ������");
//							��Ŀ¼�ڵ�  ����������һ��Ŀ¼
							Intent intent=new Intent();
							intent.putExtra("isLauchPage", false);
							intent.putExtra("name", cardItem.getName());
							intent.putExtra("parent", cardItem.getId());
							intent.setClass(ChildActivity.this, ChildActivity.class);
							startActivity(intent);
						}else{
							 File audioFile = new File(Constants.dir_path_yy+ cardItem.getAudio_filename());
							 
							 if(audioFile!=null&&audioFile.exists()){
								 MediaPlayer mp=MediaPlayer.create(ChildActivity.this, Uri.fromFile(audioFile));
								 if(mp!=null){
									 mp.start();
									 mp.setOnCompletionListener(new OnCompletionListener() {
				                         @Override
				                         public void onCompletion(MediaPlayer mp) {
				                                 mp.release();
				                         }
									 });
								 }else{
									 Toast.makeText(ChildActivity.this, "δ�ҵ������ļ�", Toast.LENGTH_SHORT).show();
								 }
							 }else{
								 Toast.makeText(ChildActivity.this, "δ�ҵ������ļ�", Toast.LENGTH_SHORT).show();
							 }
							 audioFile=null;
						}
					}else{
						Log.i("sjl", "���ڷ���..���������ITEM ");
					}
			}
		}
	}
	public void initNavigationBar(){
		nb.setTvTitle("С���");
		nb.setBtnRightVisble(false);
		nb.setBtnLeftVisble(false);
	}
	public void initNavigationBar2(String catogeryName){
		nb.setTvTitle(catogeryName);
		nb.setBtnLeftBacground(R.drawable.ic_back);
		nb.setBtnRightVisble(false);
		nb.setBtnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	public void initData(){
			myDbHelper =  DataBaseHelper.getDataBaseHelper(ChildActivity.this);
			cardMap=myDbHelper.getChildsByParent(parent);
			Log.i("sjl", "parent id is :"+parent);
			Iterator it=cardMap.keySet().iterator(); 
		 	if(cardMap!=null){
	 			Log.i("sjl", "cardlist.size is "+cardMap.size());
	 			while(it.hasNext()){
	 				Integer key=(Integer)it.next();
	 				Card cardItem=cardMap.get(key);
		 			int position=cardItem.getPosition();
//		 			�˴����ý������ݵ� ͼƬλ�� flag ����Ϊ1 ���� ֵΪ0�Ĳ�����ʾ
		 			Log.i("sjl", "visible i :"+position);
		 			displayFlag[position]=1;
//		 			File picFile = new File(Constants.dir_path_pic, cardItem.getImage_filename());
//			    	Uri uri=Uri.fromFile(picFile);
//					ivList.get(position).setImageURI(uri);
					
					Bitmap mybitmap=GlobalUtil.preHandleImage(null,Constants.dir_path_pic+cardItem.getImage_filename());
					ivList.get(position).setImageBitmap(mybitmap);
					
					
					if(position!=0||(position==0&&isLauchPage)){
						if(cardItem.getType().equals(Constants.TYPE_CATEGORY)){
							ivList.get(position).setBackgroundResource(R.drawable.ic_category);
						}else{
							ivList.get(position).setBackgroundResource(R.drawable.ic_card);
						}
						tvList.get(position).setText(cardItem.getName());
					}
					
//					picFile=null;
//					uri=null;
					
	 			}
			}  
		 	for(int i=1;i<displayFlag.length;i++){
		 		if(displayFlag[i]==0){
		 			Log.i("sjl", "invisible i :"+i);
		 			ivList.get(i).setVisibility(View.INVISIBLE);
		 			tvList.get(i).setVisibility(View.INVISIBLE);
		 		}
		 	}
		 	
		 	if(!isLauchPage){
		 		ivList.get(0).setImageResource(R.drawable.ic_return);	
		 		tvList.get(0).setText("����");
		 		}
	}
	boolean isLauchPage;
	public void initUI(){
		ivList=new ArrayList<ImageView>();
		tvList=new ArrayList<TextView>();
		nb=(NavigationBar)findViewById(R.id.navigationBar_edit);
		iv1=(ImageView)findViewById(R.id.imageView1);
		iv2=(ImageView)findViewById(R.id.imageView2);
		iv3=(ImageView)findViewById(R.id.imageView3);
		iv4=(ImageView)findViewById(R.id.imageView4);
		iv5=(ImageView)findViewById(R.id.imageView5);
		iv6=(ImageView)findViewById(R.id.imageView6);
		iv7=(ImageView)findViewById(R.id.imageView7);
		iv8=(ImageView)findViewById(R.id.imageView8);
		ivList.add(iv1);
		ivList.add(iv2);
		ivList.add(iv3);
		ivList.add(iv4);
		ivList.add(iv5);
		ivList.add(iv6);
		ivList.add(iv7);
		ivList.add(iv8);
		Intent intent=getIntent();
		isLauchPage=intent.getBooleanExtra("isLauchPage", true);
		if(isLauchPage){
			Log.i("sjl", "�䵱��ҳ�Ľ�ɫ");
			initNavigationBar();
			parent="af35431e-cdea-4d66-b32f-57bf683a25ce";
		}else{
			Log.i("sjl", "�䵱��ҳ��Ľ�ɫ");
			parent=intent.getStringExtra("parent");
			initNavigationBar2(intent.getStringExtra("catogeryName"));
		}
		setOnClickListener();
		initTextView();
	}
	public void initTextView(){
		tv1=(TextView)findViewById(R.id.textView1);
		tv2=(TextView)findViewById(R.id.textView2);
		tv3=(TextView)findViewById(R.id.textView3);
		tv4=(TextView)findViewById(R.id.textView4);
		tv5=(TextView)findViewById(R.id.textView5);
		tv6=(TextView)findViewById(R.id.textView6);
		tv7=(TextView)findViewById(R.id.textView7);
		tv8=(TextView)findViewById(R.id.textView8);
		tvList.add(tv1);
		tvList.add(tv2);
		tvList.add(tv3);
		tvList.add(tv4);
		tvList.add(tv5);
		tvList.add(tv6);
		tvList.add(tv7);
		tvList.add(tv8);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_first, menu);
		return true;
	}

}
