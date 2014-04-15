package com.ioc.advnotes;

import java.io.File;
import java.io.IOException;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Nova_nota extends Activity implements LocationListener {
	MapFragment mapf;
	GoogleMap gmap;
	
	/** constant per la camera**/
	private static final int APP_CAMERA = 0;
	private Uri identificadorImatge;
	
	/**constants audio**/
	private String mNomFitxer = null;
	private MediaRecorder mRecorder = null; 
	private MediaPlayer mPlayer = null;
	private boolean mGravant = false; 
	
	//constants animacio
	ImageView imatgeMIC;
	Animation animacioMIC;
	
	
	/**views**/
	View map;
	View veu;
	View foto;
	View descrip;
	EditText titol;
	EditText desctext;
	View [] vistes = new View [4];
	
	/**constants localització**/
	double latitud;
	double longitud;
	
	//constants bd
	private DBInterface db;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.nova_nota);
	    // instanciem els views
	    titol = (EditText)findViewById(R.id.titol);
	    descrip = findViewById(R.id.descripciolayout);
	    vistes[0]=descrip;
	    map = findViewById(R.id.map);
	    vistes[1]= map;
	    foto = findViewById(R.id.foto);
	    vistes[2]= foto;
	    veu = findViewById(R.id.veu);
	    vistes[3]= veu;
	    desctext = (EditText) findViewById(R.id.descripcio);
	    
	    imatgeMIC = (ImageView)findViewById(R.id.imageView2);
	    
	    //instanciem bd
	    db = new DBInterface(this);
	    
	    //localització
	    LocationManager gestorLoc = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    gestorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
	    
	    
	}
	public void onButtonClicked (View v) {
        switch(v.getId()){
            case R.id.nota_text:
                 //DO something
            	descrip.setVisibility(View.VISIBLE);
            	map.setVisibility(View.GONE);
            	foto.setVisibility(View.GONE);
            	veu.setVisibility(View.GONE);
            break;
            case R.id.nota_foto:
                 //DO something
            	foto.setVisibility(View.VISIBLE);
            	descrip.setVisibility(View.GONE);
            	map.setVisibility(View.GONE);
            	veu.setVisibility(View.GONE);
            	
            break;
            case R.id.nota_loca:
                 //DO something
            	map.setVisibility(View.VISIBLE);
            	descrip.setVisibility(View.VISIBLE);
            	foto.setVisibility(View.GONE);
            	veu.setVisibility(View.GONE);
            	
            	
            	ViewGroup.LayoutParams params = descrip.getLayoutParams();
            	params.height = 250;
            	descrip.requestLayout();
            	
            	mapf = (MapFragment) getFragmentManager().findFragmentById(R.id.mapaFragment);
            	// Si volem accedir al view haurem de fet un mapa.getView();
            	gmap = mapf.getMap();
            	// Posem el mapa en mode hybrid
            	gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            	gmap.setMyLocationEnabled(true);
            	LocationManager gestorLoc = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        	    gestorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            	// Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();

                // Getting the name of the best provider
                String provider = gestorLoc.getBestProvider(criteria, true);

                // Getting Current Location
                Location location = gestorLoc.getLastKnownLocation(provider);

                if(location!=null){
                        onLocationChanged(location);
                }

                gestorLoc.requestLocationUpdates(provider, 20000, 0, this);

            break;
            case R.id.nota_veu:
                //DO something
            	veu.setVisibility(View.VISIBLE);
            	foto.setVisibility(View.GONE);
            	descrip.setVisibility(View.GONE);
            	map.setVisibility(View.GONE);
            	
            	
            break;
        }
        

  }
	public void onClickBotoGravar(View v){
		mNomFitxer  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) +  "/gravacio.3gp";
     	if(mGravant)
     	{
             aturaGravacio();
             imatgeMIC.clearAnimation();
     	}
     	else
     	{
             comencaGravacio();    
             animacioMIC = AnimationUtils.loadAnimation(this, R.anim.grava);
         	imatgeMIC.startAnimation(animacioMIC);
     	}
     	// Canvia a l'altre estat
     	mGravant = !mGravant;
	}
	private void comencaGravacio() {
		// Crea el MediaRecorder i especifica la font d'audio, el format
		// de sortida i el fitxer, i el codificador d'audio
		mRecorder = new MediaRecorder();
	    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    mRecorder.setOutputFile(mNomFitxer);
	    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    mRecorder.setOnInfoListener(new OnInfoListener() {
	    	@Override
	    	public void onInfo(MediaRecorder mr, int what, int extra) {                     
	    		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
	    			mRecorder.stop();
	    			imatgeMIC.clearAnimation();

	    		}          
	    	}
	    });
	    mRecorder.setMaxDuration(20000);

	    // En enllestir la gravació pot haver problemes, per tant cal 
	    // preveure excepcions
	    try {
	        mRecorder.prepare();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Si s'ha pogut disposar tot correctament, es comença a gravar	
	    mRecorder.start();
		
	}
	private void aturaGravacio() {
		mRecorder.stop();
	    mRecorder.release();
	    mRecorder = null;
		
	}
	public void fesFoto(View view) {
		// Es crea l'intent per a l'aplicació de fotos
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// Es crea un nou fitxer al'emmagatzematge extern i se li passa a l'intent
		File foto = new File(Environment.getExternalStorageDirectory(), "Foto.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
		// Es guarda l'identificador de la imatge per recuperar-la quan estigui feta
		identificadorImatge = Uri.fromFile(foto);
		// S'engega l'activitat
		startActivityForResult(intent, APP_CAMERA);
		}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
		super.onActivityResult(requestCode, resultCode, data); switch (requestCode)
		{
		case APP_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				// El ContentResolver na aals continguts
				// (la imatge emmagatzemada en aquest cas)
				ContentResolver contRes = getContentResolver();
				// Cal dir−li que el contingut del fitxer ha canviat contRes.notifyChange(identificadorImatge, null);
				// Accedeix a l’ImageView i hi carrega la foto que ha fet la
				// càmera
				ImageView imageView = (ImageView) findViewById(R.id.imageView1); Bitmap bitmap;
				// Com que la càrrega de la imatge pot fallar, cal tractar // les possibles excepcions
				try {
					bitmap = android.provider.MediaStore.Images.Media.getBitmap(contRes,identificadorImatge); imageView.setImageBitmap(bitmap);
					// Mostra la localització de la foto 
					Toast.makeText(this, identificadorImatge.toString(), Toast.LENGTH_LONG).show(); 
				} catch (Exception e) {
					Toast.makeText(this, "No es pot carregar la imatge" + identificadorImatge.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	public void onClickAfegeix(View v){
		db.obre();
		if (titol.getText().length() == 0 || titol.getText().toString().equals("Títol")){
			Toast.makeText(this, "Falta info del títol", Toast.LENGTH_SHORT).show();
		}else{
			for (int i = 0; i<vistes.length; i++){
				View vi = vistes[i];
				int j = vistes.length;
				
				if (vi.getVisibility() == View.VISIBLE){
					//si la descripcio és visible només pot ser el mapa o la nota de text
					if (vi == descrip){
						if (map.getVisibility() == View.VISIBLE){
							
							db.insertNotaMapa(titol.getText().toString(),longitud,latitud,"mapa");
							this.finish();
							}else {
							if (desctext.getText().length() == 0 || desctext.getText().toString().equals("Títol")){
								Toast.makeText(this, "Falta omplir la descripció", Toast.LENGTH_SHORT).show();
							} else {
								db.insertNotaText(titol.getText().toString(),desctext.getText().toString(), "text");
								desctext.getText().toString();
								this.finish();
							}
							
						}
					}else if (vi==foto){
						if (identificadorImatge != null){
							long id = db.insertNotaFoto(titol.getText().toString(),identificadorImatge.toString(),"foto");
							
						}else {
							Toast.makeText(this, "No has fet cap foto", Toast.LENGTH_SHORT).show();
						}
					}else if (vi == veu){
						
						if (mNomFitxer != null){
							db.insertNotaVeu(titol.getText().toString(),mNomFitxer.toString(),"veu");
							this.finish();
						}else {
							Toast.makeText(this, "No has fet cap gravació", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			
		}
	}
	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		 latitud =(int) (loc.getLatitude () * 1E6);
		 longitud =(int) (loc.getLongitude() * 1E6);
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
