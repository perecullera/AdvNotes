package com.ioc.advnotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Notes extends Activity {
	TextView titol;
	int  id;
	DBInterface bd ;
	String titoltxt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    bd = new DBInterface(this);
	    Intent intent = getIntent();
	    id = intent.getIntExtra("id", 0);
	    titoltxt = intent.getStringExtra("titol");
	    setContentView(R.layout.notes);
	    // TODO Auto-generated method stub
	    titol = (TextView) findViewById(R.id.titoltext);
	    titol.setText(titoltxt);
	    
	    
	}
	
	public void onClickEsborra (View v){
		 bd.obre();
		 bd.esborraNota(titoltxt);
		 bd.tanca();
		 this.finish();
	}

}
