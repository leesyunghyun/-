package com.example.smarttrashcan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MotorSpeed extends Activity {

	Button btnApply;
	EditText etSpeed;
	Intent result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.motorspeed);
		btnApply = (Button)findViewById(R.id.btnapply);
		etSpeed = (EditText)findViewById(R.id.etspeed);
		
		btnApply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!etSpeed.getText().toString().equals(""))
				{
					if((Integer.parseInt(etSpeed.getText().toString()) > 0) && (Integer.parseInt(etSpeed.getText().toString()) < 256))
					{
						result = new Intent();
						result.putExtra("speed", etSpeed.getText().toString());
						setResult(0, result);
						finish();
					}
				}
			}
		});
		
	}

	
}
