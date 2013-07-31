package com.example.cameratest;
  
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.example.util.GlobalUtil;
import com.example.util.ListenerUtil;
import com.umeng.analytics.MobclickAgent;
public class MainActivity extends Activity implements OnClickListener{  
    private ImageView preview;  
    private Button saveBtn;  
    private Button recordBtn;  
    private Button playBtn;  
    private Button stopBtn;  
    private Button clearBtn; 
    private Spinner catogerySP;
//  private OnClickListener imgViewListener;  
    private Bitmap myBitmap;  
    private byte[] mContent;  
    String returnString;  
    DataBaseHelper dataBaseHelper;
    private static final int REQUEST_CAMERA = 1;  
    private static final int REQUEST_CALENDAR = 2;  
//  ��������ļ���·��
//  �����ڱ�ʾ  ��ǰ��¼���ж���
    int yyItemIndex;
    int picItemIndex;
//  ��ʱ���� ���ֶε� UUID���ɹ���
    String card_id;
    
    SQLiteDatabase db;
    File file;
    MediaRecorder mMediaRecorder;
    NavigationBar nb;
    EditText cardnameET;
    LinearLayout ll;
    boolean imageflag=false;
//    ����������   cardtype 1  catogerytype 0
    String cardType;
    @ Override  
    public void onCreate (Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        dataBaseHelper=DataBaseHelper.getDataBaseHelper(MainActivity.this);
        Intent intent=getIntent();
        
        cardType=intent.getStringExtra("type");
        Log.i("sjl", "cardType is "+cardType);
        setContentView(R.layout.activity_camera2);  
        preview=(ImageView)findViewById(R.id.uploadIV);
        preview.setOnClickListener(this);
        
//        �������˷������ݵĻ���  ����ͬ������
        MobclickAgent.updateOnlineConfig(this);
        
        initUI();
        initSettings();
    }
    public void initSettings(){
    	SharedPreferences sp=getSharedPreferences("xiaoyudi", 0);
    	yyItemIndex=sp.getInt("yyItemIndex", 0);
    	picItemIndex=sp.getInt("picItemIndex", 0);
//    	card_id=sp.getInt("card_id", 0);
    }
    
    public void initNavigation(){
    	nb=(NavigationBar)findViewById(R.id.navigationBar1);
    	if(cardType==Constants.TYPE_CARD){
    		nb.setTvTitle("�¿�Ƭ");
    	}else{
    		nb.setTvTitle("��Ŀ¼");
    	}
    	
    	nb.setBtnLeftBacground(R.drawable.ic_back);
    	nb.setBtnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	nb.setBtnRightVisble(false);
    	nb.setBtnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Toast.makeText(MainActivity.this, "���ܿ�����..", Toast.LENGTH_LONG).show();
			}
		});
    }

    public void initSpinner(){
    	Cursor datasource=dataBaseHelper.getCardTypes();
    	SimpleCursorAdapter adapter=new SimpleCursorAdapter(MainActivity.this, R.layout.listitem, datasource, new String[]{"name"},new int[]{R.id.listitem});
    	catogerySP.setAdapter(adapter);
    	catogerySP.setOnItemSelectedListener(new ListenerUtil.SpinnerSelectedListener(datasource)); 
    }
    
    public void initUI(){
    	
    	initNavigation();
    	
    	saveBtn=(Button)findViewById(R.id.saveBtn);
    	recordBtn=(Button)findViewById(R.id.recordBtn);
    	playBtn=(Button)findViewById(R.id.playBtn);
    	clearBtn=(Button)findViewById(R.id.clearBtn);
    	stopBtn=(Button)findViewById(R.id.stopBtn);
    	cardnameET=(EditText)findViewById(R.id.cardnameET);
    	catogerySP=(Spinner)findViewById(R.id.spinner1);
    	ll=(LinearLayout)findViewById(R.id.catogeryPart);
    	Log.i("sjl", "cardType :"+cardType);
    	if(Constants.TYPE_CARD.equals(cardType)){
//    		Log.i("sjl", "��ʼ�������� ");
//    		��ʱ������ �ṩ�û�ѡ�� ��Ƭ ����Ŀ¼�Ĺ���
    		ll.setVisibility(View.GONE);
//    		initSpinner();
    	}else{
    		ll.setVisibility(View.GONE);
    	}
    	
    	playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playBtn.setEnabled(false);
				 MediaPlayer mp=MediaPlayer.create(MainActivity.this, Uri.fromFile(file));
				 if(mp!=null){
					 mp.start();
					 mp.setOnCompletionListener(new OnCompletionListener() {
                         @Override
                         public void onCompletion(MediaPlayer mp) {
                                 mp.release();
                                 playBtn.setEnabled(true);
                         }
					 });
				 }
			}
		});
    	stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
				playBtn.setEnabled(true);
				clearBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}
		});
    	clearBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				file.delete();
				playBtn.setEnabled(false);
				stopBtn.setEnabled(false);
				recordBtn.setEnabled(true);
				clearBtn.setEnabled(false);
				Toast.makeText(MainActivity.this, "��ɾ��", Toast.LENGTH_LONG).show();
			}
		});
    	saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(imageflag){
					String name=cardnameET.getText().toString();
//					UUID ���� 
					String image=GlobalUtil.getId();
					String audio=GlobalUtil.getId();
					card_id=GlobalUtil.getId();
					Log.i("sjl", "���ɵ�image audio��UUID�ֱ��ǣ�"+image+"-"+audio);
					Log.i("sjl", "���ɵ�card_id  UUID�ǣ�"+card_id);
					String image_filename=picItemIndex+".jpg";
					String audio_filename=yyItemIndex+".mp3";
					dataBaseHelper.addCards(card_id, cardType, name, image,audio, image_filename,audio_filename);
//					begin �������˲���ռ� �½�Ŀ¼���߿�Ƭ������
					HashMap<String, String> info= new HashMap<String, String>();
					info.put("name", name);
					info.put("type", cardType);
					MobclickAgent.onEvent(MainActivity.this, "newevent", info);
					Log.i("sjl", "�����Ѿ���������");
//					end 
					Log.i("sjl", "���������ݿⱣ���¼�¼  ����ֵ��");
					
					Log.i("sjl", "name:"+name);
					Log.i("sjl", "image:"+image);
					Log.i("sjl", "audio:"+audio);
					Log.i("sjl", "id:"+card_id);
					Log.i("sjl", "image_filename:"+image_filename);
					Log.i("sjl", "audio_filename:"+audio_filename);
					
					
			        SharedPreferences sp=getSharedPreferences("xiaoyudi", 0);
			        sp.edit().putInt("yyItemIndex", ++yyItemIndex).putInt("picItemIndex", ++picItemIndex).commit();
					Toast.makeText(MainActivity.this, "���� �ɹ�", Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(MainActivity.this, "���ϴ�ͼƬ", Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
    	recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					 file = new File(Constants.dir_path_yy+ yyItemIndex + ".mp3"); 
					 Toast.makeText(getApplicationContext(), "����¼����¼���ļ���"+file.getAbsolutePath(), Toast.LENGTH_LONG) 
					 .show(); 
					 /* ����¼���ļ�����һ���������ļ���ǰ׺���ڶ��������Ǻ�׺��������������SD·�� */
					 try {
					 /* ʵ����MediaRecorder���� */
					 mMediaRecorder = new MediaRecorder();
					 /* ������˷� */
					 mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					 /* ��������ļ��ĸ�ʽ */
					 mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					 /* ������Ƶ�ļ��ı��� */
					 mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					 /* ��������ļ���·�� */
					 file.createNewFile();
					 mMediaRecorder.setOutputFile(file.getAbsolutePath());
					 /* ׼�� */
					 mMediaRecorder.prepare();
					 /* ��ʼ */
					 mMediaRecorder.start();
					 recordBtn.setEnabled(false);
					 stopBtn.setEnabled(true);
					 } catch (Exception e) {
							e.printStackTrace();
					 }
					Toast.makeText(MainActivity.this, "¼����..", Toast.LENGTH_LONG).show();
			}
		});
    }
    
  
    @ Override  
    protected void onActivityResult ( int requestCode , int resultCode , Intent data )  
    {  
        super.onActivityResult(requestCode, resultCode, data);  
  
        ContentResolver resolver = getContentResolver();  
        /** 
         * ��Ϊ���ַ�ʽ���õ���startActivityForResult������ 
         * �������ִ����󶼻�ִ��onActivityResult������ ����Ϊ�����𵽵�ѡ�����Ǹ���ʽ��ȡͼƬҪ�����жϣ� 
         * �����requestCode��startActivityForResult����ڶ���������Ӧ 
         */  
        if (requestCode == 0)  
        {  
            try  
            {  
            	if(data!=null){
            		Uri originalUri = data.getData();  
                    if(originalUri!=null){
                    	Log.i("sjl", "uri is not null");
                    preview.setImageURI(originalUri);
//                    // ��ͼƬ���ݽ������ֽ�����  
                    mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));  
//                    // ���ֽ�����ת��ΪImageView�ɵ��õ�Bitmap����  
                    myBitmap = getPicFromBytes(mContent, null);  
                    File f = new File(Constants.dir_path_pic+imageName);  
                    Log.i("sjl", "1");
                    f.createNewFile();  
                    FileOutputStream fOut = null;  
                    try {  
                            fOut = new FileOutputStream(f);
                            Log.i("sjl", "2");
                    } catch (FileNotFoundException e) {  
                            e.printStackTrace();  
                    }  
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); 
                    myBitmap.recycle();
                    fOut.close();
                    fOut=null;
                    f=null;
                    
                    
                    }
                    imageflag=true;
            	}
                
            } catch ( Exception e )  
            {  
                System.out.println(e.getMessage());  
            }  
  
        } else if (requestCode == REQUEST_CAMERA){  
            try  
            {  
//            	������Ƭ����
                super.onActivityResult(requestCode, resultCode, data); 
                if(resultCode == RESULT_OK){  
                	Log.i("sjl", "data is RESULT_OK");
                	preview.setImageURI(Uri.fromFile(new File(Constants.dir_path_pic+imageName)));
                	imageflag=true;
                	return; 
              }  
//                Bundle extras = data.getExtras();
//                myBitmap = (Bitmap) extras.get("data");  
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
//                mContent = baos.toByteArray();  
            } catch ( Exception e )  
            {  
                e.printStackTrace();  
            }  
            // �ѵõ���ͼƬ���ڿؼ�����ʾ  
//            preview.setImageBitmap(myBitmap);
//            preview.setImageBitmap(ImageUtil.toRoundCorner(myBitmap, 10));//���������Ƭת��Բ����ʾ��Ԥ���ؼ���  
        }else if(requestCode==REQUEST_CALENDAR){  
            if(resultCode == RESULT_OK){  
//                happenDate.setCalendar(data.getIntExtra("year", 1900), data.getIntExtra("month", 0), data.getIntExtra("day", 1));  
            }  
        }  
    }  
  
    public static Bitmap getPicFromBytes ( byte[] bytes , BitmapFactory.Options opts )  
    {  
        if (bytes != null)  
            if (opts != null)  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);  
            else  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
        return null;  
    }  
  
    public static byte[] readStream ( InputStream inStream ) throws Exception  
    {  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        while ((len = inStream.read(buffer)) != -1)  
        {  
            outStream.write(buffer, 0, len);  
        }  
        byte[] data = outStream.toByteArray();  
        outStream.close();  
        inStream.close();  
        return data;  
    }  
  
    String imageName;
    
    @Override  
    public void onClick(View v) {  
        int id = v.getId();  
        switch(id){  
            case R.id.uploadIV:{  
                final CharSequence[] items =  
                {"���", "����"};  
                AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("ѡ��ͼƬ").setItems(items,  
                        new DialogInterface.OnClickListener()  
                        {  
                            public void onClick ( DialogInterface dialog , int item )  
                            {  
                            	imageName=picItemIndex+".jpg";
                                if (item == 1){  
//                                  Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");  
//                                  startActivityForResult(getImageByCamera, REQUEST_CAMERA); 
//                                	����  ��ָ��·���ķ�ʽ ����ͼƬ sjl 2013 07 23 
                                    File sdcardTempFile = new File(Constants.dir_path_pic, imageName);  
                                    Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);    
                                    Uri u=Uri.fromFile(sdcardTempFile);   
                                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);   
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);   
                                    intent.putExtra("return-data", true);  
                                    startActivityForResult(intent, REQUEST_CAMERA); 
                                    sdcardTempFile=null;
                                    u=null;
                                    
                                } else{  
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);  
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);  
                                    getImage.setType("image/jpeg");  
                                    startActivityForResult(getImage, 0);  
                                }  
                            }  
                        }).create();  
                dlg.show();  
            }  
            break;  
            default:  
            break;  
        }  
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
    
      
}  