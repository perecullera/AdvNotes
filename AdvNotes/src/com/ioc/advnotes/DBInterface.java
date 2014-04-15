package com.ioc.advnotes;



import java.util.Date;

import com.google.android.gms.internal.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DBInterface {
	
	public static final String CLAU_ID = "_id";
	public static final String CLAU_TITOL = "titol";
	public static final String CLAU_DESCRIPCIO = "descripcio";
	public static final String CLAU_INFO = "info";
	public static final String CLAU_LAT = "lat";
	public static final String CLAU_LON = "long";
	public static final String CLAU_TIPUS = "tipus";

	public static final String TAG = "DBInterface";

	public static final String BD_NOM = "BDDNotes";
	public static final String BD_TAULA = "Notes";
	public static final int VERSIO = 3;

	public static final String BD_CREATE = "create table " + BD_TAULA + "( "
			+ CLAU_ID + " integer primary key autoincrement, " + CLAU_TIPUS + " text not null,"
			+CLAU_TITOL + " text not null, " + CLAU_DESCRIPCIO + " text , " + CLAU_INFO
			+ " text ," +  CLAU_LAT + " float ," + CLAU_LON
			+ " float " + ");";

	private final Context context;
	private AjudaBD ajuda;
	private SQLiteDatabase bd;
	
	
	public DBInterface(Context con) {
		this.context = con;
		ajuda = new AjudaBD(context);
	}

	// Obre la BD
	public DBInterface obre() throws SQLException {
		bd = ajuda.getWritableDatabase();
		return this;
	}

	// Tanca la BD
	public void tanca() {
		ajuda.close();
	}
	
	public long insertNotaText (String titol, String descripcio, String tipus) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CLAU_TITOL, titol);
		initialValues.put(CLAU_DESCRIPCIO, descripcio);
		initialValues.put(CLAU_TIPUS, tipus);
		
		return bd.insert(BD_TAULA, null, initialValues);

	}
	
	public long insertNotaMapa (String titol, Double longitud, Double latitud, String tipus){
		ContentValues initialValues = new ContentValues();
		initialValues.put(CLAU_TITOL, titol);
		initialValues.put(CLAU_LON, longitud);
		initialValues.put(CLAU_LAT, latitud);
		initialValues.put(CLAU_TIPUS, tipus);
		return bd.insert(BD_TAULA, null, initialValues);
	}
	
	public long insertNotaVeu (String titol, String info, String tipus){
		ContentValues initialValues = new ContentValues();
		initialValues.put(CLAU_TITOL, titol);
		initialValues.put(CLAU_INFO, info);
		initialValues.put(CLAU_TIPUS, tipus);
		return bd.insert(BD_TAULA, null, initialValues);
	}
	public long insertNotaFoto(String titol, String info, String tipus){
		ContentValues initialValues = new ContentValues();
		initialValues.put(CLAU_TITOL, titol);
		initialValues.put(CLAU_INFO, info);
		initialValues.put(CLAU_TIPUS, tipus);
		return bd.insert(BD_TAULA, null, initialValues);
	}
	public void insereix (double lon){
		ContentValues initialValues = new ContentValues();
		initialValues.put(CLAU_LON, lon);
		bd.insert(BD_TAULA, null, initialValues);
	}
	// Retorna les notes
			public Cursor obtenirNotes(String titol) throws SQLException {
				Cursor mCursor = bd.query(true, BD_TAULA,
						new String[] { CLAU_ID, CLAU_TITOL, CLAU_DESCRIPCIO }, CLAU_TITOL + " = "
								+ titol, null, null, null, null, null);

				if (mCursor != null)
					mCursor.moveToFirst();

				return mCursor;

			}
	// Retorna totes les notes
	public Cursor obtenirTotesLesNotes() {
		return bd.query(BD_TAULA, new String[] { CLAU_ID, CLAU_TITOL, CLAU_TIPUS,
				 }, null,
						null, null, null, null);
	}
	
	// Esborra una nota
		public boolean esborraNota(String IDFila) {
			return bd.delete(BD_TAULA, CLAU_TITOL + " = " + IDFila, null) > 0;
		}
	
	private static class AjudaBD extends SQLiteOpenHelper {
		AjudaBD(Context con) {
			super(con, BD_NOM, null, VERSIO);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(BD_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int VersioAntiga,
				int VersioNova) {
			Log.w(TAG, "Actualitzant Base de dades de la versi—" + VersioAntiga
					+ " a " + VersioNova + ". Destruirˆ  totes les dades");
			db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);

			onCreate(db);
		}
	}
}




