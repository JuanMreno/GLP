package com.aplications.glp.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ´ñ´b on 15/11/2015.
 */
public class SqliteManager extends SQLiteOpenHelper {
    static final String sqlCreate =
            "CREATE TABLE " +
                "usuarios " +
            "(" +
                "id INTEGER PRIMARY KEY, " +
                "tipo_tag TEXT, " +
                "tipo_nombre TEXT, " +
                "nombre TEXT, " +
                "identificacion TEXT, " +
                "telefono TEXT" +
            ")";

    static final String sqlUpdate = "DROP TABLE IF EXISTS usuarios";

    public SqliteManager(Context contexto, String nombre,
                                SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL(sqlUpdate);
        db.execSQL(sqlCreate);
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
