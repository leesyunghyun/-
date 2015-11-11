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
	// 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
	BluetoothAdapter mBluetoothAdapter;
	/**
	 * BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다. 연결하고자 하는 다른
	 * 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스. 현재 기기가 아닌 다른 블루투스 기기와의 연결 및
	 * 정보를 알아낼 때 사용.
	 */
	BluetoothDevice mRemoteDevie;
	// 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
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
		// BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
		BluetoothDevice selectedDevice = null;
		// getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
		// Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
		for (BluetoothDevice deivce : mDevices) {
			// getName() : 단말기의 Bluetooth Adapter 이름을 반환
			if (name.equals(deivce.getName())) {
				selectedDevice = deivce;
				break;
			}
		}
		return selectedDevice;
	}

	// 문자열 전송하는 함수(쓰레드 사용 x)
	void sendData(String msg) {
		msg += mStrDelimiter; // 문자열 종료표시 (\n)
		try {
			// getBytes() : String을 byte로 변환
			// OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에
			// 있는 데이터를 한번에 기록해 준다.
			mOutputStream.write(msg.getBytes()); // 문자열 전송.
			Log.d("전송값",msg);
		} catch (Exception e) { // 문자열 전송 도중 오류가 발생한 경우
			Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			
			finish(); // App 종료
		}
	}

	// connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
	// 실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
	void connectToSelectedDevice(String selectedDeviceName) {
		// BluetoothDevice 원격 블루투스 기기를 나타냄.
		mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
		// java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
		UUID uuid = java.util.UUID
				.fromString("00001101-0000-1000-8000-00805f9b34fb");

		try {
			// 소켓 생성, RFCOMM 채널을 통한 연결.
			// createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와
			// 통신할 수 있는 소켓을 생성함.
			// 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를
			// 리턴함.
			mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
			mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

			// 데이터 송수신을 위한 스트림 얻기.
			// BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
			// 1. 데이터를 보내기 위한 OutputStrem
			// 2. 데이터를 받기 위한 InputStream
			mOutputStream = mSocket.getOutputStream();
			mInputStream = mSocket.getInputStream();

			ControlInit();
			
			// 데이터 수신 준비.
			beginListenForData();

		} catch (Exception e) { // 블루투스 연결 중 오류 발생
			Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // App 종료
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
	// 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
	void beginListenForData() {
		final Handler handler = new Handler();

		readBufferPosition = 0; // 버퍼 내 수신 문자 저장 위치.
		readBuffer = new byte[1024]; // 수신 버퍼.

		// 문자열 수신 쓰레드.
		mWorkerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
				// interrupt() 메소드는 하던 일을 멈추는 메소드이다.
				// isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
				while (!Thread.currentThread().isInterrupted()) {
					try {
						// InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은
						// 수 있는 문자열 개수를 반환함.
						int byteAvailable = mInputStream.available(); // 수신 데이터
																		// 확인
						if (byteAvailable > 0) { // 데이터가 수신된 경우.
							byte[] packetBytes = new byte[byteAvailable];
							// read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1
							// 리턴.
							mInputStream.read(packetBytes);
							for (int i = 0; i < byteAvailable; i++) {
								byte b = packetBytes[i];
								if (b == mCharDelimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									// System.arraycopy(복사할 배열, 복사시작점, 복사된 배열,
									// 붙이기 시작점, 복사할 개수)
									// readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로
									// 복사.
									System.arraycopy(readBuffer, 0,
											encodedBytes, 0,
											encodedBytes.length);

									final String data = new String(
											encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									handler.post(new Runnable() {
										// 수신된 문자열 데이터에 대한 처리.
										@Override
										public void run() {
											// mStrDelimiter = '\n';
											/*
											 * mEditReceive.setText(mEditReceive
											 * .getText().toString() + data +
											 * mStrDelimiter);
											 */
											
											//여기다가 상태변화 정리
										}
									});
								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}
						}

					} catch (Exception e) { // 데이터 수신 중 오류 발생.
						Toast.makeText(getApplicationContext(),
								"데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG)
								.show();
						if(gearDialog != null)
						{
							gearDialog.finish();
						}
						finish(); // App 종료.
					}
				}
			}

		});
	}

	// 블루투스 지원하며 활성 상태인 경우.
	void selectDevice() {
		// 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
		// getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
		mDevices = mBluetoothAdapter.getBondedDevices();
		mPariedDeviceCount = mDevices.size();

		if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
			Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // App 종료.
		}
		// 페어링된 장치가 있는 경우.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("블루투스 장치 선택");

		// 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
		List<String> listItems = new ArrayList<String>();
		for (BluetoothDevice device : mDevices) {
			// device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
			listItems.add(device.getName());
		}
		listItems.add("취소"); // 취소 항목 추가.

		// CharSequence : 변경 가능한 문자열.
		// toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
		final CharSequence[] items = listItems
				.toArray(new CharSequence[listItems.size()]);
		// toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
		listItems.toArray(new CharSequence[listItems.size()]);

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if (item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른
													// 경우.
					Toast.makeText(getApplicationContext(),
							"연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
					if(gearDialog != null)
					{
						gearDialog.finish();
					}
					finish();
				} else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
					connectToSelectedDevice(items[item].toString());
				}
			}

		});

		builder.setCancelable(false); // 뒤로 가기 버튼 사용 금지.
		AlertDialog alert = builder.create();
		alert.show();
	}

	void checkBluetooth() {
		/**
		 * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다. 이경우 Toast를 사용해
		 * 에러메시지를 표시하고 앱을 종료한다.
		 */
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) { // 블루투스 미지원
			Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.",
					Toast.LENGTH_LONG).show();
			if(gearDialog != null)
			{
				gearDialog.finish();
			}
			finish(); // 앱종료
		} else { // 블루투스 지원
			/**
			 * isEnable() : 블루투스 모듈이 활성화 되었는지 확인. true : 지원 , false : 미지원
			 */
			if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
				Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.",
						Toast.LENGTH_LONG).show();
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로
				// 사용(0이상)
				/**
				 * startActivityForResult 함수 호출후 다이얼로그가 나타남 "예" 를 선택하면 시스템의 블루투스
				 * 장치를 활성화 시키고 "아니오" 를 선택하면 비활성화 상태를 유지 한다. 선택 결과는
				 * onActivityResult 콜백 함수에서 확인할 수 있다.
				 */
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else
				// 블루투스 지원하며 활성 상태인 경우.
				selectDevice();
		}
	}

	// onDestroy() : 어플이 종료될때 호출 되는 함수.
	// 블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
	@Override
	protected void onDestroy() {
		try {
			mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
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

	// onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
	// RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
	// RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우 RESULT_CANCELED

	/**
	 * 사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의
	 * 허가/거부를 확인할수 있다. 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드.
	 * REQUEST_ENABLE_BT 값 두번째 resultCode : 종료된 액티비티가 setReuslt로 지정한 결과 코드.
	 * RESULT_OK, RESULT_CANCELED 값중 하나가 들어감. 세번째 data : 종료된 액티비티가 인테트를 첨부했을 경우,
	 * 그 인텐트가 들어있고 첨부하지 않으면 null
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여
		// 사용함.
		switch (requestCode) {
		case 0:
			if(data != null)
			{
				sendData("motorspeed/" + data.getStringExtra("speed"));
				tvSpeed.setText(data.getStringExtra("speed"));
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) { // 블루투스 활성화 상태
				selectDevice();
			} else if (resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
				Toast.makeText(getApplicationContext(),
						"블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
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

	// 버튼 이벤트

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
						btnTopLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
					}
					else
					{
						btnTopLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnTopLeft.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnTopLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(0);
					}
					else
					{
						btnTopLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnTopLeft.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnTopCenter:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnTopCenter.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnTopCenter.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnTopCenter.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnTopCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(1);
					}
					else
					{	
						btnTopCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnTopCenter.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnTopRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnTopRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnTopRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnTopRight.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnTopRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(2);
					}
					else
					{	
						btnTopRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnTopRight.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnCenterLeft:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnCenterLeft.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(3);
					}
					else
					{	
						btnCenterLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnCenterLeft.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
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
						btnCenterStop.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
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
						btnCenterStop.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(4);
					}	
				}
				return false;
			case R.id.btnCenterRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnCenterRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnCenterRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnCenterRight.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnCenterRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(5);
					}
					else
					{	
						btnCenterRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnCenterRight.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomLeft:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnBottomLeft.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(6);
					}
					else
					{	
						btnBottomLeft.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnBottomLeft.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomCenter:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnBottomCenter.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(7);
					}
					else
					{	
						btnBottomCenter.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnBottomCenter.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBottomRight:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if(bloop == true)
					{
						btnBottomRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색

					}
					else
					{
						btnBottomRight.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
						btnBottomRight.setTextColor(Color.rgb(188, 229, 92));//변환색
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
						btnBottomRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						changeBTNTXTcolor(8);
					}
					else
					{	
						btnBottomRight.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
						btnBottomRight.setTextColor(Color.rgb(189, 189, 189)); //원래글씨색
						btnCenterStop.setTextColor(changeTXTcolor);
						handle.sendEmptyMessage(R.id.btnCenterStop);
					}
				}
				break;
			case R.id.btnBluetoothConnect:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnBluetoothConnect.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
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
					btnBluetoothConnect.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				return false;
			case R.id.btnMotorSpeed:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnMotorSpeed.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnMotorSpeed.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				
				return false;
			case R.id.btndetect:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btndetect.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if(bWarming == false)
					{
						btndetect.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
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
					btndetect.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				return false;
			case R.id.btnCover:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnCover.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
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
					btnCover.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				return false;
			case R.id.btnloop:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnLoop.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
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
					btnLoop.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				return false;
			case R.id.btnReset:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnreset.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnreset.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
				}
				return false;
			case R.id.btnGear:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btnGearfit.setBackgroundColor(Color.rgb(0, 87, 102));//변환색
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					btnGearfit.setBackgroundColor(Color.rgb(0, 130, 153)); //원래색
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
					tvwarm.setText(nbWarm + " 초 ");
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
