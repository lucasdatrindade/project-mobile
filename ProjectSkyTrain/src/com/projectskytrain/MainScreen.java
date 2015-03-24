package com.projectskytrain;

import com.example.projectskytrain.R;
import com.projectskytrain.constants.Station;
import com.projectskytrain.database.VanSkytrainDB;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreen extends Activity {
	String[] stnNames = new String[56];

	
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getStation(stnNames);
		super.onCreate(savedInstanceState);
		createDataBase();
		setContentView(R.layout.main_screen);
		
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,stnNames);
		 final AutoCompleteTextView fromStation = (AutoCompleteTextView)findViewById(R.id.txtFrom);
		 final AutoCompleteTextView toStation = (AutoCompleteTextView)findViewById(R.id.txtTo);
		// final TextView test = (TextView)findViewById(R.id.textView1);
		 Button search = (Button)findViewById(R.id.btnSearch);
		 
		 search.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//test.setText(toStation.getEditableText().toString());
			}
		}) ;
		 
		 fromStation.setAdapter(adapter);
         toStation.setAdapter(adapter);
         
         
        

	}
	
	public void getStation( String [] arr){
		
		int count=0;
		for (Station var : Station.values()) {
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
	


}
