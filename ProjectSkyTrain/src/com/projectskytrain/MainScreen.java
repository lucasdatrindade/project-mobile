package com.projectskytrain;

import java.util.ArrayList;

import com.example.projectskytrain.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.projectskytrain.auxiliry.Station;
import com.projectskytrain.auxiliry.StationArrayAdapter;
import com.projectskytrain.constants.StationEnum;
import com.projectskytrain.database.VanSkytrainDB;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreen extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	
	 private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	 private String[] stnNames = new String[56];
	 private TextView timetxt;
	 private TextView pricetxt;
	 private TextView fromStn;
	 private TextView toStn;
	 private TextView lableFromStn;
	 private TextView lableToStn;
	 private CheckBox cbClosesteStn;
	 private GoogleApiClient mGoogleApiClient;
	 private double latitude;
	 private double longitude;
	 

	
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		getStation(stnNames);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		if (checkPlayServices()) {             
	        // Building the GoogleApi client
	        	buildGoogleApiClient();        
	     }       
	
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,stnNames);
		final AutoCompleteTextView fromStation = (AutoCompleteTextView)findViewById(R.id.txtFrom);
		final AutoCompleteTextView toStation = (AutoCompleteTextView)findViewById(R.id.txtTo);
		timetxt = (TextView)findViewById(R.id.txtTime);
		pricetxt = (TextView)findViewById(R.id.txtPrice);
		fromStn = (TextView)findViewById(R.id.txtFromStn);
		toStn = (TextView)findViewById(R.id.txtToStn);
		lableFromStn = (TextView)findViewById(R.id.lableFrom);
		lableToStn = (TextView)findViewById(R.id.lableTo);
		cbClosesteStn = (CheckBox)findViewById(R.id.cbClosest);
		
		fromStation.setAdapter(adapter);
        toStation.setAdapter(adapter);
		Button search = (Button)findViewById(R.id.btnSearch);
		Button seeMap= (Button)findViewById(R.id.btnMap);
		
		
		 search.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				final String fromStn=fromStation.getEditableText().toString();
				final String toStn=toStation.getEditableText().toString();
				hideKeyboard(v);
				calculate(fromStn, toStn);
				
			}
		}) ;
		
		seeMap.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainScreen.this, SkytrainLine.class));
			}
		});
		 
		 fromStation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					hideKeyboard(v);
				}
			}
		});
		 
		 toStation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(!hasFocus){
						hideKeyboard(v);
					}
				}
			});
		 
		 fromStation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fromStation.showDropDown();
			}
		});
		 
		 toStation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					toStation.showDropDown();
				}
			});
		 
		 cbClosesteStn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					fromStation.setText("");
					fromStation.setEnabled(false);
				}
				else
					fromStation.setEnabled(true);
			}
		});
	}
	
	private void calculate(String st1, String st2){
		int stCode1, stCode2;
		Station calcStn= new Station();
		
		if(cbClosesteStn.isChecked()){
			if(mGoogleApiClient!=null){
				stCode1 = getClosestStation();
				if(stCode1==-1){
					return;
				}
			}else{
				Toast.makeText(this, "Google Location Services is Not Available", Toast.LENGTH_SHORT).show();
				return;
			}
			st1 = Station.getStationInfo(stCode1).getName();
		}else{
			stCode1 = Station.getStationId(st1);	
		}
		lableFromStn.setText("From: ");
		lableToStn.setText(" To: ");
		fromStn.setText(st1);
		toStn.setText(st2);
		stCode2 = Station.getStationId(st2);
		if(stCode1!=-1 && stCode2!=-1){
			timetxt.setText(calcStn.getTimeAndPath(stCode1, stCode2)+" minutes");
			pricetxt.setText("$"+calcStn.getPrice());
			setListView(calcStn.getPath());
		}else{
			Toast.makeText(this, "No valid station was given", Toast.LENGTH_SHORT).show();
		}
		
			
	}

	private void getStation( String [] arr){
		
		int count=0;
		for (StationEnum var : StationEnum.values()) {
			arr[count++]=var.getName();
		}
	}
	
	private void createDataBase(){
		
		new Thread(new Runnable() {
	        public void run() {
	        	VanSkytrainDB vanSkyDB = new VanSkytrainDB(getApplicationContext());
	    		SQLiteDatabase db = vanSkyDB.getReadableDatabase();
	                }
	            }).start();
	}
	private void setListView(ArrayList<StationEnum> route){
		final ListView listStation = (ListView)findViewById(R.id.listview);
		
		final StationArrayAdapter adapter= new StationArrayAdapter(this, route);
		listStation.setAdapter(adapter);
		
	}
	
	private int getClosestStation(){
		int code = -1;
		double closeste = -1;
		if(setLocation()){
			for (StationEnum stn : StationEnum.values()) {
				double x = stn.getLatitude() - latitude;
				double y = stn.getLongitude() -  longitude;
				double a = Math.pow(x, 2.0);
				double b = Math.pow(y, 2.0);
				double dist = Math.pow((a+b), 0.5);
				
				if(closeste == -1){
					code = stn.getCode();
					closeste = dist;
				}else{
					if(dist < closeste){
						code = stn.getCode();
						closeste = dist;
					}
				}
				
			}
		}else
			Toast.makeText(this, "Failed to get device location", Toast.LENGTH_SHORT).show();
		return code; 
	}
	
	private boolean checkPlayServices() {        
    	int resultCode = GooglePlayServicesUtil                
    			.isGooglePlayServicesAvailable(this);        
    	if (resultCode != ConnectionResult.SUCCESS) {            
    		if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {                
    			GooglePlayServicesUtil.getErrorDialog(resultCode, this,
    					PLAY_SERVICES_RESOLUTION_REQUEST).show();            
    			} else {                
    				Toast.makeText(getApplicationContext(),
    						"This device is not supported.", Toast.LENGTH_LONG)
    						.show();                
    				finish();            
    				}            
    		return false;        
    		}        
    	return true;    
    }
	
	private boolean setLocation(){
		Location mLastLocation = LocationServices.FusedLocationApi                
    			.getLastLocation(mGoogleApiClient);         
    	if (mLastLocation != null) {           
    		latitude = mLastLocation.getLatitude();            
    		longitude = mLastLocation.getLongitude();  
    		return true;
    		        
		} else {             
			latitude = 0;            
    		longitude = 0; 
    		return false;
		}
	}
	
	protected synchronized void buildGoogleApiClient() {        
    	mGoogleApiClient = new GoogleApiClient.Builder(this)                
    	.addConnectionCallbacks(this)                
    	.addOnConnectionFailedListener(this)                
    	.addApi(LocationServices.API).build();    
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this,"Connection failed:"+ arg0.getErrorCode(), Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		setLocation();
			
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();        
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkPlayServices();
	}
	
	public void hideKeyboard(View v){
		InputMethodManager inputManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(v.getWindowToken(),0);
	}
	
	
	

}
