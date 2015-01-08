package com.al.phonenums3;

import android.app.Activity;
import android.app.ActivityManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


// http://stackoverflow.com/questions/9092134/broadcast-receiver-within-a-service

public class MainActivity extends Activity implements View.OnClickListener {

    Button buttonStart, buttonStop;
    
    class ServiceStartedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

	        if (PhoneService3.broadcastMessageCode.equals(intent.getAction())) {
	        	String startingStatus = 
	        			intent.getExtras().getString(PhoneService3.startnigParamName);
	        	
	        	if (startingStatus == null)
	        		startingStatus = "__null__";
	        	
	        	
	            Log.v( "PhoneService3", 
	            		PhoneService3.broadcastMessageCode + " message code received : "
	            		+ startingStatus
	            		);
	            
	            MainActivity.this.updateButtonStatus();
	        }
			
		}
    };
    
    private ServiceStartedReceiver ssReceiver = new ServiceStartedReceiver(); 

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    private void updateButtonStatus() {
    	boolean isServiceRunning = isMyServiceRunning(PhoneService3.class);
    	
    	Log.v( "PhoneService3", "updateButtonStatus: Service is : " + (isServiceRunning ? "RUNNING" : "STOPPED") );
    	
    	buttonStart.setEnabled(!isServiceRunning);
    	buttonStop.setEnabled(isServiceRunning);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.button_start_service);
        buttonStop = (Button) findViewById(R.id.button_stop_service);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(PhoneService3.broadcastMessageCode);
        registerReceiver(ssReceiver, filter);
        
        updateButtonStatus();
    }
    
    @Override
    protected void onDestroy() {
    	unregisterReceiver(ssReceiver);
    	
    	super.onDestroy();
    }
    
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_service:
                Log.v("PhoneService3", "onClick: Starting service.");
                startService(new Intent(this, PhoneService3.class));
                break;
            case R.id.button_stop_service:
                Log.v("PhoneService3", "onClick: Stopping service.");
                stopService(new Intent(this, PhoneService3.class));
                break;
        }
    }
        
}

