package com.example.wheeldatetimepicker;


import java.security.Principal;
import java.util.Date;

import kankan.wheel.widget.ui.DateTimePickerView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	private DateTimePickerView dtp_start;
	private View view_parent;
	private Date startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dtp_start = (DateTimePickerView) findViewById(R.id.dtp_start);
		view_parent=findViewById(R.id.rl_parent);
		dtp_start.setParentView(view_parent);
		startTime=new Date(dtp_start.currentYear,dtp_start.currentMonth,dtp_start.currentDay,dtp_start.currentHour,dtp_start.currentMinute);
		
		
		
	}

	

}
