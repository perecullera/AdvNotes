package com.ioc.advnotes;



import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.bd;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	
	private List<String> notesString;
	private DBInterface db;
	private ListView feed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// L'array que contindrˆ les notes
		notesString = new ArrayList<String>();
		
		//obrim bd i obtenim notes
		db = new DBInterface(this);
		db.obre();
		Cursor dbNotes = db.obtenirTotesLesNotes();
		notesString.clear();
		if (dbNotes.moveToFirst()) {
			do {
				// Mostrem el nom seguit del tweet
				notesString.add(dbNotes.getString(1)+"-"+dbNotes.getString(2));

			} while (dbNotes.moveToNext());
		}
		// Tanquem la BD
		db.tanca();
		feed = (ListView) findViewById(R.id.llistanotes);
		// Preparem l'adapter de la llista
		feed.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.element_llista, notesString));
		// Mostrem el feed
		feed.setOnItemClickListener(MainActivity.this);
		feed.setVisibility(View.VISIBLE);
		
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    db.obre();
		Cursor dbNotes = db.obtenirTotesLesNotes();
		notesString.clear();
		if (dbNotes.moveToFirst()) {
			do {
				// Mostrem el nom seguit del tweet
				notesString.add(dbNotes.getString(1)+"-"+dbNotes.getString(2));

			} while (dbNotes.moveToNext());
		}
		// Tanquem la BD
		db.tanca();
		feed = (ListView) findViewById(R.id.llistanotes);
		// Preparem l'adapter de la llista
		feed.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.element_llista, notesString));
		// Mostrem el feed
		feed.setOnItemClickListener(MainActivity.this);
		feed.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.escriu:
	        	startActivity(new Intent("com.ioc.advnotes.Nova_nota"));
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		db.obre();
		TextView tw = (TextView)view;
		String txt =tw.getText().toString();
		Cursor cu = db.obtenirNotes(txt);
		int ide = cu.getInt(0);
		String titol = cu.getString(1);
		Intent mIntent = new Intent("com.ioc.advnotes.Notes");
		mIntent.putExtra("id", ide);
		mIntent.putExtra("titol", titol);
		db.tanca();
		/*Bundle mBundle = new Bundle();
		mBundle.extras.putString(key, value);
		mIntent.putExtras(mBundle);*/
		
		startActivity( mIntent);
		
	}

}
