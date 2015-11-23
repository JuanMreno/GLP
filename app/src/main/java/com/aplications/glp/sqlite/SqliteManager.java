package com.aplications.glp.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ´ñ´b on 15/11/2015.
 */
public class SqliteManager extends SQLiteOpenHelper {
    static final ArrayList<String> sqlCreate = new ArrayList<String>(){{
        add("CREATE TABLE " +
                "usuarios " +
            "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo_tag TEXT, " +
                "tipo_nombre TEXT, " +
                "nombre TEXT, " +
                "identificacion TEXT, " +
                "telefono TEXT" +
            ") ");
        add("CREATE TABLE " +
                "reportes " +
            "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ciudad TEXT, " +
                "fecha TEXT, " +
                "hora TEXT, " +
                "vehiculo TEXT, " +
                "nombre_cliente TEXT, " +
                "identificacion TEXT, " +
                "direccion TEXT, " +
                "telefono TEXT, " +
                "recarga_n TEXT, " +
                "cilindro_recibido TEXT, " +
                "cap_cil_rec TEXT, " +
                "cilindro_entregado TEXT, " +
                "cap_cil_ent TEXT, " +
                "tara_cil_ent TEXT, " +
                "peso_real TEXT, " +
                "error TEXT, " +
                "estado TEXT, " +
                "fecha_registro datetime default current_timestamp " +
            ")");
    }};

    static final ArrayList<String> sqlUpdate = new ArrayList<String>(){{
        add("DROP TABLE IF EXISTS usuarios; ");
        add("DROP TABLE IF EXISTS reportes ");
    }};

    private String TAG = "SqliteManager";

    public SqliteManager(Context contexto, String nombre,
                                SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        for(String q : sqlCreate)
            db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        Log.w(TAG, "onUpgrade");
        for(String q : sqlUpdate)
            db.execSQL(q);

        for(String q : sqlCreate)
            db.execSQL(q);

    }

    public Boolean query(String q){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(q);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Cursor selectQuery(String q){
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(q, null);
            return cursor;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
