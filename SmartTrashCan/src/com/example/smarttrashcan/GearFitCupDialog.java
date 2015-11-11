package com.example.smarttrashcan;

import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.samsung.android.sdk.cup.ScupButton;
import com.samsung.android.sdk.cup.ScupButton.ClickListener;
import com.samsung.android.sdk.cup.ScupDevice;
import com.samsung.android.sdk.cup.ScupDialog;

public class GearFitCupDialog extends ScupDialog{

	String mStrDelimiter = "\n";
	OutputStream mOutputStream;
	int number=0;
	ScupButton buttonCover;
	ScupButton buttonDetect;
	
	boolean iscover = false;
	boolean isdetect = false;
	ScupButton[] sbutton = new ScupButton[9];
	boolean isbutton[] = new boolean[9];
	MainActivity mainactivity;
	
	public GearFitCupDialog(Context arg0, MainActivity activity, OutputStream MainStream, boolean bcover, boolean bdetect) {
		super(arg0);
		// TODO Auto-generated constructor stub
		iscover = bcover;
		isdetect = bdetect;
		mOutputStream = MainStream;
		mainactivity = activity;
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		setBackEnabled(true);
		this.setWidgetAlignment(ScupDialog.WIDGET_ALIGN_HORIZONTAL_CENTER);

		setBackgroundColor(Color.rgb(61, 183, 204));
		
		buttonCover = new ScupButton(this);
		buttonDetect = new ScupButton(this);
		
		buttonCover.setText("뚜껑");
		buttonDetect.setText("움직임");
		buttonCover.setWidth(ScupButton.WRAP_CONTENT);
		buttonDetect.setWidth(ScupButton.WRAP_CONTENT);
		buttonCover.setBackgroundColor(Color.rgb(0, 130, 153));
		buttonDetect.setBackgroundColor(Color.rgb(0, 130, 153));
		buttonCover.setTextSize(13);
		buttonDetect.setTextSize(13);
		
		if(iscover)
		{
			buttonCover.setTextColor(Color.rgb(188, 229,92));
		}
		else
		{
			buttonCover.setTextColor(Color.rgb(189, 189, 189));
		}
		
		if(isdetect)
		{
			buttonDetect.setTextColor(Color.rgb(188, 229,92));
		}
		else
		{
			buttonDetect.setTextColor(Color.rgb(189, 189, 189));
		}
		
		buttonCover.setMargin(2, 2, 2, 2);
		buttonDetect.setMargin(2, 2, 2, 2);
		buttonCover.show();
		buttonDetect.show();
		
		for(int i=0;i<9;i++)
		{
			sbutton[i] = new ScupButton(this);
			
			sbutton[i].setWidth(27);

			sbutton[i].setHeight(25);
			
			sbutton[i].setBackgroundColor(Color.rgb(0, 130, 153));
			
			sbutton[i].setTextSize(17);
			sbutton[i].setTextColor(Color.rgb(189, 189, 189));
			sbutton[i].setMargin(2,2,2,2);

			sbutton[i].show();
		}
		
		sbutton[4].setTextColor(Color.rgb(188, 229,92));
		isbutton[4] = true;
		
		sbutton[0].setText("↖");
		sbutton[1].setText("↑");
		sbutton[2].setText("↗");
		
		sbutton[3].setText("← ");
		sbutton[4].setText(" ◎ ");
		sbutton[5].setText(" →");
		
		sbutton[6].setText("↙");
		sbutton[7].setText("↓");
		sbutton[8].setText("↘");

		sbutton[0].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[0])
				{
					sbutton[0].setTextColor(Color.rgb(189, 189, 189));
					isbutton[0] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 0)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[0].setTextColor(Color.rgb(188, 229,92));
					isbutton[0] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 0)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
							
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 0)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}

					sendData("leftup");
				}
				update();
				
			}
		});
		
		sbutton[1].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[1])
				{
					sbutton[1].setTextColor(Color.rgb(189, 189, 189));
					isbutton[1] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 1)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[1].setTextColor(Color.rgb(188, 229,92));
					isbutton[1] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 1)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 1)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("up");
				}
				update();
			}
		});
		
		sbutton[2].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[2])
				{
					sbutton[2].setTextColor(Color.rgb(189, 189, 189));
					isbutton[2] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 2)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
							
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					
					sendData("stop");
				}
				else
				{
					sbutton[2].setTextColor(Color.rgb(188, 229,92));
					isbutton[2] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 2)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
							
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 2)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("rightup");
				}
				update();
			}
		});
		
		sbutton[3].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[3])
				{
					sbutton[3].setTextColor(Color.rgb(189, 189, 189));
					isbutton[3] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 3)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
							
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[3].setTextColor(Color.rgb(188, 229,92));
					isbutton[3] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 3)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
							
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 3)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("left");
				}
				
				update();
			}
		});
		
		sbutton[4].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[4])
				{
					for(int i = 0; i<9;i++)
					{
						if(i == 4)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					return;
				}
				else
				{
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 4)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}		
				update();
			}
		});
		
		sbutton[5].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[5])
				{
					sbutton[5].setTextColor(Color.rgb(189, 189, 189));
					isbutton[5] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 5)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[5].setTextColor(Color.rgb(188, 229,92));
					isbutton[5] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 5)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 5)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("right");
				}
				update();
			}
		});
		
		sbutton[6].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[6])
				{
					sbutton[6].setTextColor(Color.rgb(189, 189, 189));
					isbutton[6] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 6)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[6].setTextColor(Color.rgb(188, 229,92));
					isbutton[6] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 6)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 6)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("leftdown");
				}

				update();
			}
		});
		
		sbutton[7].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[7])
				{
					sbutton[7].setTextColor(Color.rgb(189, 189, 189));
					isbutton[7] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 7)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[7].setTextColor(Color.rgb(188, 229,92));
					isbutton[7] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 7)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 7)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("down");
				}			
				
				update();
			}
		});
		
		sbutton[8].setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isbutton[8])
				{
					sbutton[8].setTextColor(Color.rgb(189, 189, 189));
					isbutton[8] = false;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 8)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					sbutton[4].setTextColor(Color.rgb(188, 229,92));
					isbutton[4] = true;
					
					for(int i=0;i<9;i++)
					{
						if(i == 4)
						{
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("stop");
				}
				else
				{
					sbutton[8].setTextColor(Color.rgb(188, 229,92));
					isbutton[8] = true;
					
					for(int i = 0; i<9;i++)
					{
						if(i == 8)
						{
							continue;
						}
						else
						{
							if(isbutton[i])
							{
								sbutton[i].setTextColor(Color.rgb(189, 189, 189));
								isbutton[i] = false;
							}
						}
					}
					
					for(int i=0;i<9;i++)
					{
						if(i == 8)
						{	
							mainactivity.setMainControlState(i, true);
						}
						else
						{
							mainactivity.setMainControlState(i, false);
						}
					}
					
					sendData("rightdown");
				}
				update();
			}
		});
		
		buttonCover.setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(iscover)
				{
					buttonCover.setTextColor(Color.rgb(189, 189, 189));
					iscover = false;
					sendData("coverclose");
				}
				else
				{
					buttonCover.setTextColor(Color.rgb(188, 229,92));
					iscover = true;
					sendData("coveropen");
				}
				mainactivity.setMainCoverState(iscover);
				update();
			}
		});
	
		buttonDetect.setClickListener(new ClickListener() {
			
			@Override
			public void onClick(ScupButton arg0) {
				// TODO Auto-generated method stub
				if(isdetect)
				{
					buttonDetect.setTextColor(Color.rgb(189, 189, 189));
					isdetect = false;
					sendData("detmovoff");
				}
				else
				{
					buttonDetect.setTextColor(Color.rgb(188, 229,92));
					isdetect = true;
					sendData("detmovon");
				}
				mainactivity.setMainDetectState(isdetect);
				update();
			}
		});
		
		setBackPressedListener(new BackPressedListener() {
			
			@Override
			public void onBackPressed(ScupDialog arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	void setDetectState(boolean detect)
	{
		//여긴 변화된 상태값을 바로 적용해야 하므로 값을 반대로 적용해야됨 다시한번더 생각해봐~!!
		isdetect = detect;
		if(isdetect)
		{
			buttonDetect.setTextColor(Color.rgb(188, 229,92));
		}
		else
		{
			buttonDetect.setTextColor(Color.rgb(189, 189, 189));
		}
		update();
	}
	
	void setCoverState(boolean cover)
	{
		//여긴 변화된 상태값을 바로 적용해야 하므로 값을 반대로 적용해야됨 다시한번더 생각해봐~!!
		iscover = cover;
		if(iscover)
		{
			buttonCover.setTextColor(Color.rgb(188, 229,92));
		}
		else
		{
			buttonCover.setTextColor(Color.rgb(189, 189, 189));
		}
		update();
	}
	
	void setMainControlState(int number,boolean state)
	{
		for(int i=0;i<9;i++)
		{
			if(i == number)
			{
				if(state)
				{
					sbutton[i].setTextColor(Color.rgb(188, 229, 92));
				}
				else
				{
					sbutton[i].setTextColor(Color.rgb(189, 189, 189));
				}
			}
			else
			{
				sbutton[i].setTextColor(Color.rgb(189, 189, 189));
			}
		}	
	}
	
	
	void sendData(String msg) {
		msg += mStrDelimiter; // 문자열 종료표시 (\n)
		try {
			// getBytes() : String을 byte로 변환
			// OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에
			// 있는 데이터를 한번에 기록해 준다.
			mOutputStream.write(msg.getBytes()); // 문자열 전송.
		} catch (Exception e) { // 문자열 전송 도중 오류가 발생한 경우
			GearFitCupDialog.this.showToast("데이터 전송중 오류 발생", GearFitCupDialog.TOAST_DURATION_SHORT);
			finish(); // App 종료
		}
	}
}
