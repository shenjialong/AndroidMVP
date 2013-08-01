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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.umeng.analytics.MobclickAgent;

public class EditCardActivity extends Activity  implements OnClickListener{

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_card, menu);
		return true;
	}
	  
    private ImageView preview;  
    private Button saveBtn;  
    private Button recordBtn;  
    private Button playBtn;  
    private Button stopBtn;  
    private Button clearBtn;  
    private TextView catogaryText;  
    private Bitmap myBitmap;  
    private byte[] mContent; 
    private Spinner catogerySP;
    private boolean imageFlag=false;
    public String audio;
    public String image;
    public String audio_filename;
    public String image_filename;
    public String name;
    public String _id;
    public String type;
    File audioFile;
    File imageFile;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db=null;
    MediaRecorder mMediaRecorder;
    NavigationBar nb;
    EditText cardnameET;
    String returnString;  
    
    
    private static final int REQUEST_CAMERA = 1;  
    private static final int REQUEST_CALENDAR = 2;  
    
    
    @Override  
    public void onCreate (Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_camera2); 
        initUI();
        init();
//      �������˷������ݵĻ���  ����ͬ������
        MobclickAgent.updateOnlineConfig(this);
        catogaryText.setVisibility(View.VISIBLE);
        catogerySP.setVisibility(View.GONE);
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
    	dataBaseHelper = DataBaseHelper.getDataBaseHelper(EditCardActivity.this);
    	Intent intent=getIntent();
    	audio=intent.getStringExtra("audio");
    	image=intent.getStringExtra("image");
    	image_filename=dataBaseHelper.queryFilename(image);
    	if(audio!=null){
    		audio_filename=dataBaseHelper.queryFilename(audio);
    	}
    	name=intent.getStringExtra("name");
    	type=intent.getStringExtra("type");
    	_id=intent.getStringExtra("_id");
    	imageFile = new File(Constants.dir_path_pic, image_filename);
    	preview=(ImageView)findViewById(R.id.uploadIV);
    	if(imageFile!=null){
    		Uri uri=Uri.fromFile(imageFile);
        	preview.setImageURI(uri);
        	uri=null;
        	imageFile=null;
        	imageFlag=true;
    	}else{
    		Log.i("sjl", "��ʼ�������� ��⵽ͼƬΪNULL");
    	}
    	
    	cardnameET.setText(name);
    	catogaryText.setText("catogery");
    	if(audio_filename!=null){
    		audioFile = new File(Constants.dir_path_yy, audio_filename);
    	}
    	
    	if(audioFile!=null&&audioFile.exists()){
        	playBtn.setEnabled(true);
    	}else{
        	playBtn.setEnabled(false);
    	}
    	recordBtn.setEnabled(false);
    	clearBtn.setEnabled(false);
		cardnameET.setEnabled(false);
		preview.setOnClickListener(this);
		preview.setEnabled(false);
    	
    }
//    -----------------------------------------------------------
    @Override  
    public void onClick(View v) {  
        int id = v.getId();  
        switch(id){  
            case R.id.uploadIV:{  
                final CharSequence[] items =  
                {"���", "����"};  
                AlertDialog dlg = new AlertDialog.Builder(EditCardActivity.this).setTitle("ѡ��ͼƬ").setItems(items,  
                        new DialogInterface.OnClickListener()  
                        {  
                            public void onClick ( DialogInterface dialog , int item )  
                            {  
                                if (item == 1){  
                                    File sdcardTempFile = new File(Constants.dir_path_pic, image_filename);  
                                    Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);    
                                    Uri u=Uri.fromFile(sdcardTempFile);   
                                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);   
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);   
                                    intent.putExtra("return-data", true);  
                                    startActivityForResult(intent, REQUEST_CAMERA);  
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
    
    public void initNavigation(){
    	nb=(NavigationBar)findViewById(R.id.navigationBar1);
    	nb.setBtnLeftBacground(R.drawable.ic_back);
    	nb.setTvTitle("�༭��Ƭ");
    	nb.setBtnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	nb.setBtnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				catogaryText.setVisibility(View.GONE);
				saveBtn.setVisibility(View.VISIBLE);
				cardnameET.setEnabled(true);
				clearBtn.setEnabled(true);
				v.setEnabled(false);
				catogerySP.setVisibility(View.VISIBLE);
				preview.setEnabled(true);
			}
		});
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
    	catogaryText=(TextView)findViewById(R.id.textView2);
    	saveBtn.setVisibility(View.INVISIBLE);
		setupOnClickListener();
    }
    public boolean isRecording=false;
    
    public  void setupOnClickListener(){
    	playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(audioFile.exists()){
					playBtn.setEnabled(false);
					 MediaPlayer mp=MediaPlayer.create(EditCardActivity.this, Uri.fromFile(audioFile));
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
				}else{
					Toast.makeText(EditCardActivity.this, "û�������ļ�",Toast.LENGTH_SHORT).show();
				}
			}
		});
    	
    	stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isRecording=false;
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
				if(audioFile.exists()){
					audioFile.delete();
					audioFile=null;
				}
				playBtn.setEnabled(false);
				stopBtn.setEnabled(false);
				recordBtn.setEnabled(true);
				clearBtn.setEnabled(false);
				Toast.makeText(EditCardActivity.this, "��ɾ��", Toast.LENGTH_LONG).show();
			}
		});
    	saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(imageFlag){
					if(isRecording){
						mMediaRecorder.stop();
						mMediaRecorder.release();
						mMediaRecorder = null;
					}
					String name=cardnameET.getText().toString();
					dataBaseHelper.updateCardInfos(image_filename, audio_filename, name, _id, image,audio);
//					begin �������˲���ռ� �½�Ŀ¼���߿�Ƭ������
					HashMap<String, String> info= new HashMap<String, String>();
					info.put("name", name);
					info.put("type", type);
					MobclickAgent.onEvent(EditCardActivity.this, "addEvent", info);
					Log.i("sjl", "����ɹ�  �������Ѿ���������");
//					end 
				    finish();
				}else{
					Toast.makeText(EditCardActivity.this, "���ϴ�ͼƬ", Toast.LENGTH_SHORT).show();
				}
//			        ά��Ӧ�õ�������Ϣ
			}
		});
    	recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isRecording=true;
				audioFile = new File(Constants.dir_path_yy+ audio_filename); 
					 Toast.makeText(getApplicationContext(), "��Ի�Ͳ����", Toast.LENGTH_LONG) 
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
					 audioFile.createNewFile();
					 mMediaRecorder.setOutputFile(audioFile.getAbsolutePath());
					 /* ׼�� */
					 mMediaRecorder.prepare();
					 /* ��ʼ */
					 mMediaRecorder.start();
					 recordBtn.setEnabled(false);
					 stopBtn.setEnabled(true);
					 } catch (Exception e) {
							e.printStackTrace();
					 }
			}
		});
    }
    
    
  
    @ Override  
    protected void onActivityResult ( int requestCode , int resultCode , Intent data )  
    {  
        super.onActivityResult(requestCode, resultCode, data);  
  
        ContentResolver resolver = getContentResolver();  
        if (requestCode == 0)  
        {  
            try  
            {  
            	if(data!=null){
            		Uri originalUri = data.getData();  
                	if(originalUri!=null){
                	   preview.setImageURI(originalUri);
//                       // ��ͼƬ���ݽ������ֽ�����  
                       mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));  
//                       // ���ֽ�����ת��ΪImageView�ɵ��õ�Bitmap����  
                       myBitmap = getPicFromBytes(mContent, null);  
                       File f = new File(Constants.dir_path_pic+image_filename);  
                       f.createNewFile();  
                       FileOutputStream fOut = null;  
                       try {  
                               fOut = new FileOutputStream(f);  
                       } catch (FileNotFoundException e) {  
                               e.printStackTrace();  
                       }  
                       myBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut); 
                       myBitmap.recycle();
                       originalUri=null;
                    		   f=null;
                       imageFlag=true;
                	}
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
                	String path=Constants.dir_path_pic+image_filename;
                	Uri u=Uri.fromFile(new File(path));
                	Log.i("sjl", "����ˢ��");
                	preview.setImageURI(null);
                	preview.setImageURI(u);
                	imageFlag=true;
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
	

}
