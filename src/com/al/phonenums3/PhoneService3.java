package com.al.phonenums3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneService3 extends Service {
	public final static String broadcastMessageCode = "com.al.phonenums3.PhoneService3.broadcast";
	public final static String startnigParamName = "StartOrStop";
	public final static String startingStart = "start";
	public final static String startingStop = "stop";
	
	class bcReceiver extends BroadcastReceiver {
	    private boolean incomingCall = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		       if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
		           String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		           if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
		               //Трубка не поднята, телефон звонит
		               String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		               incomingCall = true;
		               Log.v( "PhoneService3", "Show window: " + phoneNumber);

		           } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
		               //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
		               if (incomingCall) {
		                   Log.v( "PhoneService3", "Close window.");
		                   incomingCall = false;
		               }
		           } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
		               //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
		               //или в ситуации "отказался поднимать трубку и сбросил звонок".
		               if (incomingCall) {
		                  Log.v( "PhoneService3", "Close window.");
		                  incomingCall = false;
		               }
		           }
		       }
		}
	};
	
	private final bcReceiver receiver = new bcReceiver();
	
	private void sendServiceStatus( String serviceStatus ) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES); // don't rise stopped packages 
		intent.setAction(broadcastMessageCode);
		intent.putExtra(startnigParamName, serviceStatus);
		sendBroadcast(intent);
	}
	
	
    @Override
    public void onCreate() {
        super.onCreate();

        startService();
        Log.v("PhoneService3", "onCreate(..)");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("PhoneService3", "onBind(..)");
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
        Log.v("PhoneService3", "Service onDestroy(). " );
        stopService();
    }

    private void startService() {
    	Log.d("PhoneService3", "registering phone calls receiver within service");
    	
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.provider.Telephony.SMS_RECEIVED");
    	filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

    	registerReceiver(receiver, filter);
    	sendServiceStatus( startingStart );
    }
    
    private void stopService() {
    	Log.d("PhoneService3", "unregistering phone calls receiver within service");
    	sendServiceStatus( startingStop );
    	unregisterReceiver(receiver);
    }
}

