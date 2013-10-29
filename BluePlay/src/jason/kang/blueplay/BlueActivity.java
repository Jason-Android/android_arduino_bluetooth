package jason.kang.blueplay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BlueActivity extends Activity {
	private final static int REQUEST_CONNECT_DEVICE = 1;    //�궨���ѯ�豸���
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP����UUID��
	
	private InputStream is;    //������������������������
	//private TextView text0;    //��ʾ������
    private EditText edit0;    //��������������
    private TextView dis;       //����������ʾ���
    private ScrollView sv;      //��ҳ���
    private String smsg = "";    //��ʾ�����ݻ���
    private String fmsg = "";    //���������ݻ���
    private String linmsg = "";    //��ʱ�����ݻ���
    private Button buttonWendu;
    private Button buttonLed;
    private Button buttonShidu;
    private int WenduValue=0;
    private int ShiduValue=0;
    private int LedValue=0;
    public String filename=""; //��������洢���ļ���
    BluetoothDevice _device = null;     //�����豸
    BluetoothSocket _socket = null;      //����ͨ��socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //��ȡ�����������������������豸
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.main);   //���û���Ϊ������ main.xml
	        
	        //text0 = (TextView)findViewById(R.id.Text0);  //�õ���ʾ�����
	        edit0 = (EditText)findViewById(R.id.Edit0);   //�õ��������
	        sv = (ScrollView)findViewById(R.id.ScrollView01);  //�õ���ҳ���
	        dis = (TextView) findViewById(R.id.in);      //�õ�������ʾ���
	        buttonWendu=(Button)findViewById(R.id.Button_wendu);
	        buttonLed=(Button)findViewById(R.id.Button_led);
	        buttonShidu=(Button)findViewById(R.id.Button_shidu);
	       //����򿪱��������豸���ɹ�����ʾ��Ϣ����������
	        if (_bluetooth == null){
	        	Toast.makeText(this, "�޷����ֻ���������ȷ���ֻ��Ƿ����������ܣ�", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
	        
	        // �����豸���Ա�����  
	       new Thread(){
	    	   public void run(){
	    		   if(_bluetooth.isEnabled()==false){
	        		_bluetooth.enable();
	    		   }
	    	   }   	   
	       }.start();      
	}
	//���Ͱ�����Ӧ
    public void onSendButtonClicked(View v){
    	SendMessage(edit0.getText().toString());
    }
    //��������
    public void SendMessage(String message)
    {
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //�������������
    		byte[] bos = message.getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //�ֻ��л���Ϊ0a,�����Ϊ0d 0a���ٷ���
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}
    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }
    //���ջ�������ӦstartActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //���ӽ������DeviceListActivity���÷���
    		// ��Ӧ���ؽ��
            if (resultCode == Activity.RESULT_OK) {   //���ӳɹ�����DeviceListActivity���÷���
                // MAC��ַ����DeviceListActivity���÷���
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // �õ������豸���      
                _device = _bluetooth.getRemoteDevice(address);
 
                // �÷���ŵõ�socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                }
                //����socket
            	Button btn = (Button) findViewById(R.id.Button03);
                try{
                	_socket.connect();
                	Toast.makeText(this, "����"+_device.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();
                	btn.setText("�Ͽ�");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //�򿪽����߳�
                try{
            		is = _socket.getInputStream();   //�õ���������������
            		}catch(IOException e){
            			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		if(bThread==false){
            			ReadThread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}
            }
    		break;
    	default:break;
    	}
    }
    
    //���������߳�
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//String message="";
    		//�����߳�
    		while(true){
    			try{
    				
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				//String message="";
    				while(true){
    					num = is.read(buffer);         //��������
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					Log.i("s0", s0);
    					fmsg+=s0;    //�����յ�����
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					//message+=s;
    					
    					
    						
    					smsg+=s;   //д����ջ���
    					Log.i("s",s);
    					if(is.available()==0)
						{
						  
						   break;  //��ʱ��û�����ݲ�����������ʾ
						}
    				}
    				
    				//������ʾ��Ϣ��������ʾˢ��
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    
    	}
    };
    
    //��Ϣ�������
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		Log.i("smsg", smsg);
    		///dis.setText(smsg.substring(smsg.lastIndexOf("%")+1));   //��ʾ���� 
    		//if(smsg.contains("@"))
    		//{
	    		dis.setText(smsg);
	    		sv.scrollTo(0,dis.getMeasuredHeight()); //�����������һҳ
	    		//buttonWendu.setText(smsg.substring(smsg.lastIndexOf("%")+1,smsg.lastIndexOf("@")));
	    		//linmsg="";
	    	//	smsg="";
    		//}
    	}
    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blue, menu);
		return true;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
    	if(_socket!=null)  //�ر�����socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
	}
	 //���Ӱ�����Ӧ����
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //����������񲻿�������ʾ
    		Toast.makeText(this, " ��������...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //��δ�����豸���DeviceListActivity�����豸����
    	Button btn = (Button) findViewById(R.id.Button03);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //��ת��������
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //���÷��غ궨��
    	}
    	else{
    		 //�ر�����socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("����");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    
    //���水����Ӧ����
    public void onSaveButtonClicked(View v){
    	Save();
    }
    
    //���������Ӧ����
    public void onClearButtonClicked(View v){
    	smsg="";
    	fmsg="";
    	dis.setText(smsg);
    	return;
    }
    
    //�˳�������Ӧ����
    public void onQuitButtonClicked(View v){
    	finish();
    }
    //�¶Ȱ�����Ӧ����
    public void onWenduButtonClicked(View v){
    	SendMessage("a");
    	if(WenduValue==0)
    	{
    		buttonWendu.setText("�¶ȹر�");
    		WenduValue=1;
    	}else if(WenduValue==1){
    		buttonWendu.setText("�¶���ʾ");
    		WenduValue=0;
		}
    	Log.i("wendu",buttonWendu.getText().toString().trim());
    }
    //ʪ�Ȱ�����Ӧ����
    public void onShiduButtonClicked(View v){
    	SendMessage("b");
    	if(ShiduValue==0)
    	{
    		buttonShidu.setText("ʪ�ȹر�");
    		ShiduValue=1;
    	}else if(ShiduValue==1){
    		buttonShidu.setText("ʪ����ʾ");
    		ShiduValue=0;
		}
    }
    //����������Ӧ����
    public void onFengmingButtonClicked(View v){
    	SendMessage("c");
    	if(LedValue==0)
    	{
    		buttonLed.setText("�ص�");
    		LedValue=1;
    	}else if(LedValue==1){
    		buttonLed.setText("����");
    		LedValue=0;
		}
    }
    //���湦��ʵ��
	private void Save() {
		//��ʾ�Ի��������ļ���
		LayoutInflater factory = LayoutInflater.from(BlueActivity.this);  //ͼ��ģ�����������
		final View DialogView =  factory.inflate(R.layout.sname, null);  //��sname.xmlģ��������ͼģ��
		new AlertDialog.Builder(BlueActivity.this)
								.setTitle("�ļ���")
								.setView(DialogView)   //������ͼģ��
								.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() //ȷ��������Ӧ����
								{
									public void onClick(DialogInterface dialog, int whichButton){
										EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //�õ��ļ����������
										filename = text1.getText().toString();  //�õ��ļ���
										
										try{
											if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //���SD����׼����
												
												filename =filename+".txt";   //���ļ���ĩβ����.txt										
												File sdCardDir = Environment.getExternalStorageDirectory();  //�õ�SD����Ŀ¼
												File BuildDir = new File(sdCardDir, "/data");   //��dataĿ¼���粻����������
												if(BuildDir.exists()==false)BuildDir.mkdirs();
												File saveFile =new File(BuildDir, filename);  //�½��ļ���������Ѵ������½��ĵ�
												FileOutputStream stream = new FileOutputStream(saveFile);  //���ļ�������
												stream.write(fmsg.getBytes());
												stream.close();
												Toast.makeText(BlueActivity.this, "�洢�ɹ���", Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(BlueActivity.this, "û�д洢����", Toast.LENGTH_LONG).show();
											}
										
										}catch(IOException e){
											return;
										}
										
										
										
									}
								})
								.setNegativeButton("ȡ��",   //ȡ��������Ӧ����,ֱ���˳��Ի������κδ��� 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) { 
									}
								}).show();  //��ʾ�Ի���
	} 
}
