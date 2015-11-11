package com.example.smarttrashcan;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.cup.Scup;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnTouchListener {

	GearFitCupDialog gearDialog = null;
	
	static final int REQUEST_ENABLE_BT = 10;
	int mPariedDeviceCount = 0;
	boolean bloop = true;
	
	boolean bcover = false;
	boolean bdetect = false;
	
	boolean bWarming = false;
	int nbWarm = 60;
	Set<BluetoothDevice> mDevices;
	// ���� ������� ����� ����ϱ� ���� ������Ʈ.
	BluetoothAdapter mBluetoothAdapter;
	/**
	 * BluetoothDevice �� ����� ��ġ������ �˾Ƴ� �� �ִ� �ڼ��� �޼ҵ� �� ���°��� �˾Ƴ� �� �ִ�. �����ϰ��� �ϴ� �ٸ�
	 * ������� ����� �̸�, �ּ�, ���� ���� ���� ������ ��ȸ�� �� �ִ� Ŭ����. ���� ��Ⱑ �ƴ� �ٸ� ������� ������ ���� ��
	 * ������ �˾Ƴ� �� ���.
	 */
	BluetoothDevice mRemoteDevie;
	// ����Ʈ���� �� �� ����̽��� ��� ä�ο� ���� �ϴ� BluetoothSocket
	BluetoothSocket mSocket = null;
	OutputStream mOutputStream = null;
	InputStream mInputStream = null;
	String mStrDelimiter = "\n";
	char mCharDelimiter = '\n';

	Thread mWorkerThread = null;
	byte[] readBuffer;
	int readBufferPosition;

	Button btnBluetoothConnect;
	Button btnMotorSpeed;

	Button btnTopLeft;
	Button btnTopCenter;
	Button btnTopRight;

	Button btnCenterLeft;
	Button btnCenterStop;
	Button btnCenterRight;

	Button btnBottomLeft;
	Button btnBottomCenter;
	Button btnBottomRight;

	Button btnCover;
	Button btnLoop;
	
	Button btndetect;
	
	Button btnreset;
	Button btnGearfit;
	
	TextView tvConn,tvSpeed,tvDct,tvCover,tvLoop;
	
	final int originTXTcolor = Color.rgb(189, 189, 189);
	final int originBGcolor = Color.rgb(0, 130, 153);
	final int changeTXTcolor = Color.rgb(188, 229, 92);
	final int changeBGcolor = Color.rgb(0, 87, 102);
	
	LayoutInflater vi;
	LinearLayout abc; 

	TextView tvwarm; 
	AlertDialog alert;
	AlertDialog.Builder dia1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Scup scup = new Scup();
		try {
			scup.initialize(getApplicationContext());
		} catch (SsdkUnsupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		abc = (LinearLayout) vi.inflate(
				R.layout.mwarming, null);
		tvwarm = (TextView) abc
				.findViewById(R.id.tvwarmtime);
		dia1 = new AlertDialog.Builder(
				MainActivity.this);
		dia1.setView(abc);
		dia1.setCancelable(false);
		alert = dia1.create();
		
		btnBluetoothConnect = (Button) findViewById(R.id.btnBluetoothConnect);
		btnMotorSpeed = (Button) findViewById(R.id.btnMotorSpeed);

		btnTopLeft = (Button) findViewById(R.id.btnTopLeft);
		btnTopCenter = (Button) findViewById(R.id.btnTopCenter);
		btnTopRight = (Button) findViewById(R.id.btnTopRight);

		btnCenterLeft = (Button) findViewById(R.id.btnCenterLeft);
		btnCenterStop = (Button) findViewById(R.id.btnCenterStop);
		btnCenterRight = (Button) findViewById(R.id.btnCenterRight);

		btnBottomLeft = (Button) findViewById(R.id.btnBottomLeft);
		btnBottomCenter = (Button) findViewById(R.id.btnBottomCenter);
		btnBottomRight = (Button) findViewById(R.id.btnBottomRight);

		btnCover = (Button) findViewById(R.id.btnCover);
		btnLoop = (Button) findViewById(R.id.btnloop);
		btndetect = (Button) findViewById(R.id.btndetect);
		btnGearfit = (Button) findViewById(R.id.btnGear);
		btnreset = (Button) findViewById(R.id.btnReset);
		
		tvConn = (TextView) findViewById(R.id.textView2);
		tvSpeed = (TextView) findViewById(R.id.textView4);
		tvDct = (TextView) findViewById(R.id.textView6);
		tvCover = (TextView) findViewById(R.id.textView8);
		tvLoop = (TextView) findViewById(R.id.textView10);

		btnBluetoothConnect.setOnClickListener(this);
		btnMotorSpeed.setOnClickListener(this);
		btnTopLeft.setOnClickListener(this);
		btnTopCenter.setOnClickListener(this);
		btnTopRight.setOnClickListener(this);
		btnCenterLeft.setOnClickListener(this);
		btnCenterStop.setOnClickListener(this);
		btnCenterRight.setOnClickListener(this);
		btnBottomLeft.setOnClickListener(this);
		btnBottomCenter.setOnClickListener(this);
		btnBottomRight.setOnClickListener(this);
		btnCover.setOnClickListener(this);
		btnLoop.setOnClickListener(this);
		btndetect.setOnClickListener(this);
		btnreset.setOnClickListener(this);
		btnGearfit.setOnClickListener(this);
		
		btnTopLeft.setOnTouchListener(this);
		btnTopCenter.setOnTouchListener(this);
		btnTopRight.setOnTouchListener(this);
		btnCenterLeft.setOnTouchListener(this);
		btnCenterStop.setOnTouchListener(this);
		btnCenterRight.setOnTouchListener(this);
		btnBottomLeft.setOnTouchListener(this);
		btnBottomCenter.setOnTouchListener(this);
		btnBottomRight.setOnTouchListener(this);
		btnBluetoothConnect.setOnTouchListener(this);
		btnMotorSpeed.setOnTouchListener(this);
		btndetect.setOnTouchListener(this);
		btnCover.setOnTouchListener(this);
		btnLoop.setOnTouchListener(this);
		btnGearfit.setOnTouchListener(this);
		btnreset.setOnTouchListener(this);
		
		//ControlInit();
		checkBluetooth();
	}

	BluetoothDevice getDeviceFromBondedList(String name) {
		// BluetoothDevice : �� �� ��� ����� ����.
		BluetoothDevice selectedDevice = null;
		// getBondedDevices �Լ��� ��ȯ�ϴ� �� �� ��� ����� Set �����̸�,
		// Set ���Ŀ����� n ��° ���Ҹ� ������ ����� �����Ƿ� �־��� �̸��� ���ؼ� ã�´�.
		for (BluetoothDevice deivce : mDevices) {
			// getName() : �ܸ����� Bluetooth Adapter �̸��� ��ȯ
			if (name.equals(deivce.getName())) {
				selectedDevice = deivce;
				break;
			}
		}
		return selectedDevice;
	}

	// ���ڿ� �����ϴ� �Լ�(������ ��� x)
	void sendData(String msg) {
		msg += mStrDelimiter; // ���ڿ� ����ǥ�� (\n)
		try {
			// getBytes() : String�� byte�� ��ȯ
			// OutputStream.write : �����͸� ������ write(byte[]) �޼ҵ带 �����. byte[] �ȿ�
			// �ִ� �����͸� �ѹ��� ����� �ش�.
			mOutputStream.write(msg.getBytes()); // ���ڿ� ����.
			Log.d("���۰�",msg);
		} catch (Exception e) { // ���ڿ� ���� ���� ������ �߻��� ���
			Toast.makeText(getApplicationContext(), "������ ������ ������ �߻�",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			
			finish(); // App ����
		}
	}

	// connectToSelectedDevice() : ���� ��ġ�� �����ϴ� ������ ��Ÿ��.
	// ���� ������ �ۼ����� ���ؼ��� �������κ��� ����� ��Ʈ���� ��� ����� ��Ʈ���� �̿��Ͽ� �̷�� ����.
	void connectToSelectedDevice(String selectedDeviceName) {
		// BluetoothDevice ���� ������� ��⸦ ��Ÿ��.
		mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
		// java.util.UUID.fromString : �ڹٿ��� �ߺ����� �ʴ� Unique Ű ����.
		UUID uuid = java.util.UUID
				.fromString("00001101-0000-1000-8000-00805f9b34fb");

		try {
			// ���� ����, RFCOMM ä���� ���� ����.
			// createRfcommSocketToServiceRecord(uuid) : �� �Լ��� ����Ͽ� ���� ������� ��ġ��
			// ����� �� �ִ� ������ ������.
			// �� �޼ҵ尡 �����ϸ� ����Ʈ���� �� �� ����̽��� ��� ä�ο� �����ϴ� BluetoothSocket ������Ʈ��
			// ������.
			mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
			mSocket.connect(); // ������ ���� �Ǹ� connect() �Լ��� ȣ�������ν� �α���� ������ �Ϸ�ȴ�.

			// ������ �ۼ����� ���� ��Ʈ�� ���.
			// BluetoothSocket ������Ʈ�� �ΰ��� Stream�� �����Ѵ�.
			// 1. �����͸� ������ ���� OutputStrem
			// 2. �����͸� �ޱ� ���� InputStream
			mOutputStream = mSocket.getOutputStream();
			mInputStream = mSocket.getInputStream();

			ControlInit();
			
			// ������ ���� �غ�.
			beginListenForData();

		} catch (Exception e) { // ������� ���� �� ���� �߻�
			Toast.makeText(getApplicationContext(), "������� ���� �� ������ �߻��߽��ϴ�.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // App ����
		}
	}

	void ControlInit()
	{
		if(bWarming == false)
		{
			handle.sendEmptyMessageDelayed(1000, 1000);
			alert.show();
		}
	}
	// ������ ����(������ ��� ���ŵ� �޽����� ��� �˻���)
	void beginListenForData() {
		final Handler handler = new Handler();

		readBufferPosition = 0; // ���� �� ���� ���� ���� ��ġ.
		readBuffer = new byte[1024]; // ���� ����.

		// ���ڿ� ���� ������.
		mWorkerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// interrupt() �޼ҵ带 �̿� �����带 �����Ű�� �����̴�.
				// interrupt() �޼ҵ�� �ϴ� ���� ���ߴ� �޼ҵ��̴�.
				// isInterrupted() �޼ҵ带 ����Ͽ� ���߾��� ��� �ݺ����� ������ �����尡 �����ϰ� �ȴ�.
				while (!Thread.currentThread().isInterrupted()) {
					try {
						// InputStream.available() : �ٸ� �����忡�� blocking �ϱ� ������ ����
						// �� �ִ� ���ڿ� ������ ��ȯ��.
						int byteAvailable = mInputStream.available(); // ���� ������
																		// Ȯ��
						if (byteAvailable > 0) { // �����Ͱ� ���ŵ� ���.
							byte[] packetBytes = new byte[byteAvailable];
							// read(buf[]) : �Է½�Ʈ������ buf[] ũ�⸸ŭ �о ���� ���� ��쿡 -1
							// ����.
							mInputStream.read(packetBytes);
							for (int i = 0; i < byteAvailable; i++) {
								byte b = packetBytes[i];
								if (b == mCharDelimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									// System.arraycopy(������ �迭, ���������, ����� �迭,
									// ���̱� ������, ������ ����)
									// readBuffer �迭�� ó�� ���� ������ encodedBytes �迭��
									// ����.
									System.arraycopy(readBuffer, 0,
											encodedBytes, 0,
											encodedBytes.length);

									final String data = new String(
											encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									handler.post(new Runnable() {
										// ���ŵ� ���ڿ� �����Ϳ� ���� ó��.
										@Override
										public void run() {
											// mStrDelimiter = '\n';
											/*
											 * mEditReceive.setText(mEditReceive
											 * .getText().toString() + data +
											 * mStrDelimiter);
											 */
											
											//����ٰ� ���º�ȭ ����
										}
									});
								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}
						}

					} catch (Exception e) { // ������ ���� �� ���� �߻�.
						Toast.makeText(getApplicationContext(),
								"������ ���� �� ������ �߻� �߽��ϴ�.", Toast.LENGTH_LONG)
								.show();
						if(gearDialog != null)
						{
							gearDialog.finish();
						}
						finish(); // App ����.
					}
				}
			}

		});
	}

	// ������� �����ϸ� Ȱ�� ������ ���.
	void selectDevice() {
		// ������� ����̽��� �����ؼ� ����ϱ� ���� ���� �� �Ǿ�߸� �Ѵ�
		// getBondedDevices() : ���� ��ġ ��� ������ �Լ�.
		mDevices = mBluetoothAdapter.getBondedDevices();
		mPariedDeviceCount = mDevices.size();

		if (mPariedDeviceCount == 0) { // ���� ��ġ�� ���� ���.
			Toast.makeText(getApplicationContext(), "���� ��ġ�� �����ϴ�.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // App ����.
		}
		// ���� ��ġ�� �ִ� ���.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("������� ��ġ ����");

		// �� ����̽��� �̸���(���� �ٸ�) �ּҸ� ������. �� �� ����̽����� ǥ���Ѵ�.
		List<String> listItems = new ArrayList<String>();
		for (BluetoothDevice device : mDevices) {
			// device.getName() : �ܸ����� Bluetooth Adapter �̸��� ��ȯ.
			listItems.add(device.getName());
		}
		listItems.add("���"); // ��� �׸� �߰�.

		// CharSequence : ���� ������ ���ڿ�.
		// toArray : List���·� �Ѿ�°� �迭�� �ٲ㼭 ó���ϱ� ���� toArray() �Լ�.
		final CharSequence[] items = listItems
				.toArray(new CharSequence[listItems.size()]);
		// toArray �Լ��� �̿��ؼ� size��ŭ �迭�� ���� �Ǿ���.
		listItems.toArray(new CharSequence[listItems.size()]);

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if (item == mPariedDeviceCount) { // ������ ��ġ�� �������� �ʰ� '���' �� ����
													// ���.
					Toast.makeText(getApplicationContext(),
							"������ ��ġ�� �������� �ʾҽ��ϴ�.", Toast.LENGTH_LONG).show();
					if(gearDialog != null)
					{
						gearDialog.finish();
					}
					finish();
				} else { // ������ ��ġ�� ������ ���, ������ ��ġ�� ������ �õ���.
					connectToSelectedDevice(items[item].toString());
				}
			}

		});

		builder.setCancelable(false); // �ڷ� ���� ��ư ��� ����.
		AlertDialog alert = builder.create();
		alert.show();
	}

	void checkBluetooth() {
		/**
		 * getDefaultAdapter() : ���� ���� ������� ����� ������ null �� �����Ѵ�. �̰�� Toast�� �����
		 * �����޽����� ǥ���ϰ� ���� �����Ѵ�.
		 */
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) { // ������� ������
			Toast.makeText(getApplicationContext(), "��Ⱑ ��������� �������� �ʽ��ϴ�.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // ������
		} else { // ������� ����
			/**
			 * isEnable() : ������� ����� Ȱ��ȭ �Ǿ����� Ȯ��. true : ���� , false : ������
			 */
			if (!mBluetoothAdapter.isEnabled()) { // ������� �����ϸ� ��Ȱ�� ������ ���.
				Toast.makeText(getApplicationContext(), "���� ��������� ��Ȱ�� �����Դϴ�.",
						Toast.LENGTH_LONG).show();
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// REQUEST_ENABLE_BT : ������� Ȱ�� ������ ���� ����� App ���� �˷��� �� �ĺ��ڷ�
				// ���(0�̻�)
				/**
				 * startActivityForResult �Լ� ȣ���� ���̾�αװ� ��Ÿ�� "��" �� �����ϸ� �ý����� �������
				 * ��ġ�� Ȱ��ȭ ��Ű�� "�ƴϿ�" �� �����ϸ� ��Ȱ��ȭ ���¸� ���� �Ѵ�. ���� �����
				 * onActivityResult �ݹ� �Լ����� Ȯ���� �� �ִ�.
				 */
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else
				// ������� �����ϸ� Ȱ�� ������ ���.
				selectDevice();
		}
	}

	// onDestroy() : ������ ����ɶ� ȣ�� �Ǵ� �Լ�.
	// ������� ������ �ʿ����� �ʴ� ��� ����� ��Ʈ�� ������ �ݾ���.
	@Override
	protected void onDestroy() {
		try {
			mWorkerThread.interrupt(); // ������ ���� ������ ����
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			mInputStream.close();
			mSocket.close();
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	// onActivityResult : ������� ���ð�� Ȯ�� (�ƴϿ�, ��)
	// RESULT_OK: ��������� Ȱ��ȭ ���·� ����� ���. "��"
	// RESULT_CANCELED : ������ ������� "�ƴϿ�" �������� ��Ȱ�� ���·� ���� �ִ� ��� RESULT_CANCELED

	/**
	 * ����ڰ� request�� �㰡(�Ǵ� �ź�)�ϸ� �ȵ���̵� ���� onActivityResult �޼ҵ��� ȣ���ؼ� request��
	 * �㰡/�źθ� Ȯ���Ҽ� �ִ�. ù��° requestCode : startActivityForResult ���� ����ߴ� ��û �ڵ�.
	 * REQUEST_ENABLE_BT �� �ι�° resultCode : ����� ��Ƽ��Ƽ�� setReuslt�� ������ ��� �ڵ�.
	 * RESULT_OK, RESULT_CANCELED ���� �ϳ��� ��. ����° data : ����� ��Ƽ��Ƽ�� ����Ʈ�� ÷������ ���,
	 * �� ����Ʈ�� ����ְ� ÷������ ������ null
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// startActivityForResult �� ������ ����� �� �̷� ������ switch ���� ����Ͽ� � ��û���� �����Ͽ�
		// �����.
		switch (requestCode) {
		case 0:
			if(data != null)
			{
				sendData("motorspeed/" + data.getStringExtra("speed"));
				tvSpeed.setText(data.getStringExtra("speed"));
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) { // ������� Ȱ��ȭ ����
				selectDevice();
			} else if (resultCode == RESULT_CANCELED) { // ������� ��Ȱ��ȭ ���� (����)
				Toast.makeText(getApplicationContext(),
						"��������� ����� �� ���� ���α׷��� �����մϴ�", Toast.LENGTH_LONG).show();
				if(gearDialog != null)
				{
					gearDialog.finish();
				}
				
				finish();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ��ư �̺�Ʈ

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int btnid = v.getId();

		switch (btnid) {
		case R.id.btnBluetoothConnect:
			selectDevice();
			break;
		case R.id.btnMotorSpeed:
			Intent intent = new Intent(MainActivity.this, MotorSpeed.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.btnTopLeft:
			if (bloop) {
				sendData("leftup");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(0, true);	
				}
			}
			break;
		case R.id.btnTopCenter:
			if (bloop) {
				sendData("up");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(1, true);	
				}
			}
			break;
		case R.id.btnTopRight:
			if (bloop) {
				sendData("rightup");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(2, true);	
				}
			}
			break;
		case R.id.btnCenterLeft:
			if (bloop) {
				sendData("left");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(3, true);	
				}
			}
			break;
		case R.id.btnCenterStop:
			if (bloop) {
				sendData("stop");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(4, true);	
				}
			}
			break;
		case R.id.btnCenterRight:
			if (bloop) {
				sendData("right");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(5, true);	
				}
			}
			break;
		case R.id.btnBottomLeft:
			if (bloop) {
				sendData("leftdown");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(6, true);	
				}
			}
			break;
		case R.id.btnBottomCenter:
			if(bloop)
			{
				sendData("down");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(7, true);	
				}
			}
			break;
		case R.id.btnBottomRight:
			if (bloop) {
				sendData("rightdown");
				if(gearDialog != null)
				{
					gearDialog.setMainControlState(8, true);	
				}
			}
			break;
		case R.id.btnCover:
			if(bcover == false)
			{
				bcover = true;
				sendData("coveropen");
				tvCover.setText("Open");
				tvCover.setTextColor(Color.rgb(188,229,92));
			}
			else if(bcover == true)
			{
				bcover = false;
				sendData("coverclose");
				tvCover.setText("Close");
				tvCover.setTextColor(Color.rgb(189,189,189));
			}
			
			if(gearDialog != null)
			{
				gearDialog.setCoverState(bcover);
			}
			break;
		case R.id.btnloop:
			sendData("stop");
			if (bloop) {
				bloop = !bloop;
				btnCenterStop.setTextColor(changeTXTcolor);
				tvLoop.setText("OFF");
				tvLoop.setTextColor(Color.rgb(189,189,189));
			} else {
				bloop = !bloop;
				btnCenterStop.setTextColor(changeTXTcolor);
				changeBTNTXTcolor(4);
				tvLoop.setText("ON");
				tvLoop.setTextColor(Color.rgb(188,229,92));
			}
			break;
		case R.id.btndetect:
			if(bdetect == false)
			{
				bdetect = true;
				sendData("detmovon");
				tvDct.setText("ON");
				tvDct.setTextColor(Color.rgb(188,229,92));
			}
			else if(bdetect == true)
			{
				bdetect = false;
				sendData("detmovoff");
				tvDct.setText("OFF");
				tvDct.setTextColor(Color.rgb(189,189,189));
			}
			
			if(gearDialog != null)
			{
				gearDialog.setDetectState(bdetect);	
			}
			
			break;
		case R.id.btnReset:
			sendData("reset");
			ControlInit();
			if(gearDialog != null)
			{
				gearDialog = new GearFitCupDialog(getApplicationContext(),MainActivity.this, mOutputStream, bcover,bdetect); 
			}
			break;
		case R.id.btnGear:
			if(gearDialog == null)
			{
				gearDialog = new GearFitCupDialog(getApplicationContext(),MainActivity.this, mOutputStream, bcover,bdetect);
			}
			else
			{
				gearDialog.finish();
				gearDialog = null;
				gearDialog = new GearFitCupDialog(getApplicationContext(),MainActivity.this, mOutputStream, bcover,bdetect);
			}
			break;
		}
	}
	
	void setMainDetectState(boolean detect)
	{
		bdetect = detect;
		if(bdetect == false)
		{
			tvDct.setText("OFF");
			tvDct.setTextColor(Color.rgb(189,189,189));
			btndetect.setTextColor(originTXTcolor);
		}
		else if(bdetect == true)
		{
			tvDct.setText("ON");
			tvDct.setTextColor(Color.rgb(188,229,92));
			btndetect.setTextColor(changeTXTcolor);
		}
	}
	
	void setMainCoverState(boolean cover)
	{
		bcover = cover;
		if(bcover == false)
		{
			tvCover.setText("Close");
			tvCover.setTextColor(Color.rgb(189,189,189));
			btnCover.setTextColor(originTXTcolor);
		}
		else if(bcover == true)
		{
			tvCover.setText("Open");
			tvCover.setTextColor(Color.rgb(188,229,92));
			btnCover.setTextColor(changeTXTcolor);
		}
	}
	
	void setMainControlState(int number,boolean state)
	{
		switch(number)
		{
		case 0:
			if(state)
			{
				btnTopLeft.setTextColor(changeTXTcolor);
			}
			else
			{
				btnTopLeft.setTextColor(originTXTcolor);
			}
			break;
		case 1:
			if(state)
			{
				btnTopCenter.setTextColor(changeTXTcolor);
			}
			else
			{
				btnTopCenter.setTextColor(originTXTcolor);
			}
			break;
		case 2:
			if(state)
			{
				btnTopRight.setTextColor(changeTXTcolor);
			}
			else
			{
				btnTopRight.setTextColor(originTXTcolor);
			}
			break;
		case 3:
			if(state)
			{
				btnCenterLeft.setTextColor(changeTXTcolor);
			}
			else
			{
				btnCenterLeft.setTextColor(originTXTcolor);
			}
			break;
		case 4:
			if(state)
			{
				btnCenterStop.setTextColor(changeTXTcolor);
			}
			else
			{
				btnCenterStop.setTextColor(originTXTcolor);
			}
			break;
		case 5:
			if(state)
			{
				btnCenterRight.setTextColor(changeTXTcolor);
			}
			else
			{
				btnCenterRight.setTextColor(originTXTcolor);
			}
			break;
		case 6:
			if(state)
			{
				btnBottomLeft.setTextColor(changeTXTcolor);
			}
			else
			{
				btnBottomLeft.setTextColor(originTXTcolor);
			}
			break;
		case 7:
			if(state)
			{
				btnBottomCenter.setTextColor(changeTXTcolor);
			}
			else
			{
				btnBottomCenter.setTextColor(originTXTcolor);
			}
			break;
		case 8:
			if(state)
			{
				btnBottomRight.setTextColor(changeTXTcolor);
			}
			else
			{
				btnBottomRight.setTextColor(originTXTcolor);
			}
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
			int btnid = v.getId();

			switch (btnid) {
			case R.id.btnTopLeft:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnTopLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
					}
					else
					{
						btnTopLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnTopLeft.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnTopLeft);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnTopLeft.getCurrentTextColor() == originTXTcolor)
						{
							btnTopLeft.setTextColor(changeTXTcolor);
						}
						else if(btnTopLeft.getCurrentTextColor() == changeTXTcolor)
						{
							btnTopLeft.setTextColor(originTXTcolor);
						}
						btnTopLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(0);
					}
					else
					{
						btnTopLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnTopLeft.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnTopCenter:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnTopCenter.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnTopCenter.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnTopCenter.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnTopCenter);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {		
					if(bloop == true)
					{
						if(btnTopCenter.getCurrentTextColor() == originTXTcolor)
						{
							btnTopCenter.setTextColor(changeTXTcolor);
						}
						else if(btnTopCenter.getCurrentTextColor() == changeTXTcolor)
						{
							btnTopCenter.setTextColor(originTXTcolor);
						}
						btnTopCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(1);
					}
					else
					{	
						btnTopCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnTopCenter.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnTopRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnTopRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnTopRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnTopRight.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnTopRight);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnTopRight.getCurrentTextColor() == originTXTcolor)
						{
							btnTopRight.setTextColor(changeTXTcolor);
						}
						else if(btnTopRight.getCurrentTextColor() == changeTXTcolor)
						{
							btnTopRight.setTextColor(originTXTcolor);
						}
						btnTopRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(2);
					}
					else
					{	
						btnTopRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnTopRight.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnCenterLeft:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnCenterLeft.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterLeft);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnCenterLeft.getCurrentTextColor() == originTXTcolor)
						{
							btnCenterLeft.setTextColor(changeTXTcolor);
						}
						else if(btnCenterLeft.getCurrentTextColor() == changeTXTcolor)
						{
							btnCenterLeft.setTextColor(originTXTcolor);
						}
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(3);
					}
					else
					{	
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnCenterLeft.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnCenterStop:
				if(bloop == false)
				{
					return false;
				}
				else
				{
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						btnCenterStop.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
					}
	
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if(btnCenterStop.getCurrentTextColor() == originTXTcolor)
						{
							btnCenterStop.setTextColor(changeTXTcolor);
						}
						else if(btnCenterStop.getCurrentTextColor() == changeTXTcolor)
						{
							btnCenterStop.setTextColor(originTXTcolor);
						}
						btnCenterStop.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(4);
					}	
				}
				return false;
			case R.id.btnCenterRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnCenterRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnCenterRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnCenterRight.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterRight);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnCenterRight.getCurrentTextColor() == originTXTcolor)
						{
							btnCenterRight.setTextColor(changeTXTcolor);
						}
						else if(btnCenterRight.getCurrentTextColor() == changeTXTcolor)
						{
							btnCenterRight.setTextColor(originTXTcolor);
						}
						btnCenterRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(5);
					}
					else
					{	
						btnCenterRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnCenterRight.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomLeft:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnBottomLeft.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnBottomLeft);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnBottomLeft.getCurrentTextColor() == originTXTcolor)
						{
							btnBottomLeft.setTextColor(changeTXTcolor);
						}
						else if(btnBottomLeft.getCurrentTextColor() == changeTXTcolor)
						{
							btnBottomLeft.setTextColor(originTXTcolor);
						}
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(6);
					}
					else
					{	
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnBottomLeft.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomCenter:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnBottomCenter.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnBottomCenter);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnBottomCenter.getCurrentTextColor() == originTXTcolor)
						{
							btnBottomCenter.setTextColor(changeTXTcolor);
						}
						else if(btnBottomCenter.getCurrentTextColor() == changeTXTcolor)
						{
							btnBottomCenter.setTextColor(originTXTcolor);
						}
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(7);
					}
					else
					{	
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnBottomCenter.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��

					}
					else
					{
						btnBottomRight.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
						btnBottomRight.setTextColor(Color.rgb(188, 229, 92));//��ȯ��
						btnCenterStop.setTextColor(originTXTcolor);
						handle.sendEmptyMessage(R.id.btnBottomRight);
					}
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bloop == true)
					{
						if(btnBottomRight.getCurrentTextColor() == originTXTcolor)
						{
							btnBottomRight.setTextColor(changeTXTcolor);
						}
						else if(btnBottomRight.getCurrentTextColor() == changeTXTcolor)
						{
							btnBottomRight.setTextColor(originTXTcolor);
						}
						btnBottomRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						changeBTNTXTcolor(8);
					}
					else
					{	
						btnBottomRight.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						btnBottomRight.setTextColor(Color.rgb(189, 189, 189)); //�����۾���
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBluetoothConnect:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnBluetoothConnect.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(btnBluetoothConnect.getCurrentTextColor() == originTXTcolor)
					{
						btnBluetoothConnect.setTextColor(changeTXTcolor);
					}
					else if(btnBluetoothConnect.getCurrentTextColor() == changeTXTcolor)
					{
						btnBluetoothConnect.setTextColor(originTXTcolor);
					}
					btnBluetoothConnect.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			case R.id.btnMotorSpeed:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnMotorSpeed.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnMotorSpeed.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				
				return false;
			case R.id.btndetect:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btndetect.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bWarming == false)
					{
						btndetect.setBackgroundColor(Color.rgb(0, 130, 153)); //������
						return false;
					}
					
					if(btndetect.getCurrentTextColor() == originTXTcolor)
					{
						btndetect.setTextColor(changeTXTcolor);
					}
					else if(btndetect.getCurrentTextColor() == changeTXTcolor)
					{
						btndetect.setTextColor(originTXTcolor);
					}
					btndetect.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			case R.id.btnCover:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnCover.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(btnCover.getCurrentTextColor() == originTXTcolor)
					{
						btnCover.setTextColor(changeTXTcolor);
					}
					else if(btnCover.getCurrentTextColor() == changeTXTcolor)
					{
						btnCover.setTextColor(originTXTcolor);
					}
					btnCover.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			case R.id.btnloop:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnLoop.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(btnLoop.getCurrentTextColor() == originTXTcolor)
					{
						btnLoop.setTextColor(changeTXTcolor);
					}
					else if(btnLoop.getCurrentTextColor() == changeTXTcolor)
					{
						btnLoop.setTextColor(originTXTcolor);
					}
					btnLoop.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			case R.id.btnReset:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnreset.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnreset.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			case R.id.btnGear:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnGearfit.setBackgroundColor(Color.rgb(0, 87, 102));//��ȯ��
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnGearfit.setBackgroundColor(Color.rgb(0, 130, 153)); //������
				}
				return false;
			}
			
			if(bloop)
			{
				return false;
			}
			else
			{
				return true;
			}
	}

	void changeBTNTXTcolor(int i)
	{
		switch(i)
		{
		case 0:
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 1:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 2:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 3:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 4:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 5:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 6:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 7:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomRight.setTextColor(originTXTcolor);
			break;
		case 8:
			btnTopLeft.setTextColor(originTXTcolor);
			btnTopCenter.setTextColor(originTXTcolor);
			btnTopRight.setTextColor(originTXTcolor);
			btnCenterLeft.setTextColor(originTXTcolor);
			btnCenterStop.setTextColor(originTXTcolor);
			btnCenterRight.setTextColor(originTXTcolor);
			btnBottomLeft.setTextColor(originTXTcolor);
			btnBottomCenter.setTextColor(originTXTcolor);
			break;
		}
	}
	Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case R.id.btnTopLeft:
				sendData("leftup");
				break;
			case R.id.btnTopCenter:
				sendData("up");
				break;
			case R.id.btnTopRight:
				sendData("rightup");
				break;
			case R.id.btnCenterLeft:
				sendData("left");
				break;
			case R.id.btnCenterStop:
				sendData("stop");
				break;
			case R.id.btnCenterRight:
				sendData("right");
				break;
			case R.id.btnBottomLeft:
				sendData("leftdown");
				break;
			case R.id.btnBottomCenter:
				sendData("down");
				break;
			case R.id.btnBottomRight:
				sendData("rightdown");
				break;
			case 1000:
				nbWarm--;
				if(nbWarm > 0)
				{
					tvwarm.setText(nbWarm + " �� ");
					handle.sendEmptyMessageDelayed(1000, 1000);
				}
				else
				{
					bWarming = true;
					
					tvConn.setText("Connect");
					tvConn.setTextColor(Color.rgb(188,229,92));
					tvSpeed.setText("230");
					bdetect = false;
					tvDct.setText("OFF");
					tvDct.setTextColor(Color.rgb(189,189,189));
					bcover = false;
					tvCover.setText("Close");
					tvCover.setTextColor(Color.rgb(189, 189, 189));
					bloop = true;
					tvLoop.setText("ON");
					tvLoop.setTextColor(Color.rgb(188, 229, 92));
					
					btnBluetoothConnect.setTextColor(Color.rgb(188, 229, 92));
					btnMotorSpeed.setTextColor(Color.rgb(188, 229, 92));
					btnLoop.setTextColor(Color.rgb(188, 229, 92));
					btnCenterStop.setTextColor(Color.rgb(188, 229, 92));
					alert.dismiss();
				}
				break;
			}
		}
	};
}
