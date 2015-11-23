package com.aplications.glp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PrincipalActivity extends AppCompatActivity {

    private static String TAG = "PrincipalActivity";
    public static final String REGISTRO_INTENT_CODE = "RegistroIntentCode";
    public static final  String ENTREGADO_TAG = "ENTREGADO";
    public static final  String RECIBIDO_TAG = "RECIBIDO";
    public static final  String REPORTES_FILE_NAME = "reporte.xml";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String MAIN_ACTIVITY_FRAGMENT_TAG = "PrincipalActivityFragmentTag";
    public static final String FORMULARIO_VEHICULO_FRAGMENT_TAG = "FormularioVehiculoFragmentTag";
    public static final String FORMULARIO_PLATAFORMA_FRAGMENT_TAG = "FormularioPlataformaFragmentTag";

    private SqliteManager sqlite;
    private String tipoImgSelec = "";

    private Bitmap btmpCilRec = null;
    private Bitmap btmpCilEnt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        sqlite =
                new SqliteManager(
                        PrincipalActivity.this,
                        getResources().getString(R.string.db_name),
                        null,
                        getResources().getInteger(R.integer.db_version)
                );

        SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);
        FragmentManager fm = getSupportFragmentManager();

        if(sp.getSesionData() != null){
            if(sp.getSesionData().getTipoUsuario().equals(SessionProfile.TIPO_VEHICULO))
                fm.beginTransaction()
                        .replace(R.id.frame_container, new FormularioFragmentVehiculo(), FORMULARIO_VEHICULO_FRAGMENT_TAG)
                        .commit();
            else
                fm.beginTransaction()
                        .replace(R.id.frame_container,new FormularioFragmentPlataforma(), FORMULARIO_PLATAFORMA_FRAGMENT_TAG)
                        .commit();
        }
        else{
            fm.beginTransaction()
                    .replace(R.id.frame_container, new PrincipalActivityFragment(), MAIN_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_cerrar_sesion:
                SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);
                sp.removeSesionProfile();

                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.frame_container, new PrincipalActivityFragment(), MAIN_ACTIVITY_FRAGMENT_TAG)
                        .commit();

                if(FileManager.isExternalStorageWritable() && FileManager.isExternalStorageReadable()){
                    File file = FileManager.getAlbumStorageDir(getString(R.string.app_name));

                    if(file.exists()) file.delete();
                }
                else{
                    Log.w(TAG,"action_cerrar_sesion, isExternalStorageWritable isExternalStorageReadable NULL");
                }

                sqlite.query("DELETE FROM reportes");
                break;
            case R.id.action_generar_reporte:
                writeData();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView imageView;

            if(tipoImgSelec.equals(RECIBIDO_TAG)){
                imageView = (ImageView) findViewById(R.id.imgRecibido);
                btmpCilRec = imageBitmap;
            }
            else{
                imageView = (ImageView) findViewById(R.id.imgEntregado);
                btmpCilEnt = imageBitmap;
            }

            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void eventoRegistro(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialog_registro_content,null);

        final Spinner spinner         = (Spinner)  view.findViewById(R.id.spinner_dialog);
        final EditText editVehiPlat   = (EditText) view.findViewById(R.id.editVehiBase);
        final EditText editNombre     = (EditText) view.findViewById(R.id.editNombre);
        final EditText editIdent      = (EditText) view.findViewById(R.id.editIdentificacion);
        final EditText editTel        = (EditText) view.findViewById(R.id.editTelefono);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.txt_dialog_title_registro))
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.txt_dialog_aceptar),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(editVehiPlat.equals("")){
                                    editVehiPlat.setError(getResources().getString(R.string.txt_input_error_empty));
                                    return;
                                }
                                if(editNombre.equals("")){
                                    editNombre.setError(getResources().getString(R.string.txt_input_error_empty));
                                    return;
                                }
                                if(editIdent.equals("")){
                                    editIdent.setError(getResources().getString(R.string.txt_input_error_empty));
                                    return;
                                }
                                if(editTel.equals("")){
                                    editTel.setError(getResources().getString(R.string.txt_input_error_empty));
                                    return;
                                }

                                String tipoUsuario = "";
                                if(spinner.getSelectedItemPosition() == 0)
                                    tipoUsuario = SessionProfile.TIPO_VEHICULO;
                                else
                                    tipoUsuario = SessionProfile.TIPO_PLATAFORMA;

                                sqlite.query("DELETE FROM usuarios WHERE tipo_nombre = '" + editVehiPlat.getText().toString() + "'");

                                sqlite.query(
                                        "INSERT INTO " +
                                                "usuarios " +
                                                "(" +
                                                "tipo_tag," +
                                                "tipo_nombre," +
                                                "nombre," +
                                                "identificacion," +
                                                "telefono" +
                                                ") " +
                                                "VALUES" +
                                                "(" +
                                                "'" + tipoUsuario + "'," +
                                                "'" + editVehiPlat.getText().toString() + "'," +
                                                "'" + editNombre.getText().toString() + "'," +
                                                "'" + editIdent.getText().toString() + "'," +
                                                "'" + editTel.getText().toString() + "'" +
                                                ")"
                                );

                                SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);

                                sp.setSesionProfile(
                                        new SessionProfile(
                                                tipoUsuario,
                                                editVehiPlat.getText().toString(),
                                                editNombre.getText().toString(),
                                                editIdent.getText().toString(),
                                                editTel.getText().toString()
                                        )
                                );

                                FragmentManager fm = getSupportFragmentManager();

                                if(tipoUsuario.equals(SessionProfile.TIPO_VEHICULO))
                                    fm.beginTransaction()
                                            .replace(R.id.frame_container, new FormularioFragmentVehiculo(), FORMULARIO_VEHICULO_FRAGMENT_TAG)
                                            .commit();
                                else
                                    fm.beginTransaction()
                                            .replace(R.id.frame_container,new FormularioFragmentPlataforma(), FORMULARIO_PLATAFORMA_FRAGMENT_TAG)
                                            .commit();

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.txt_dialog_cancelar), null)
                .create()
                .show();
    }

    public void eventoIngresar(View v){
        final EditText editVehiPlat   = (EditText) findViewById(R.id.editVehiBase);

        if(editVehiPlat.equals("")){
            editVehiPlat.setError(getResources().getString(R.string.txt_input_error_empty));
            return;
        }

        Cursor cursor = sqlite.selectQuery("SELECT * FROM usuarios WHERE tipo_nombre = '" + editVehiPlat.getText().toString() + "'");

        if(cursor != null){
            if(cursor.moveToFirst()){
                SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);

                sp.setSesionProfile(
                        new SessionProfile(
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5)
                        )
                );

                FragmentManager fm = getSupportFragmentManager();

                if(cursor.getString(1).equals(SessionProfile.TIPO_VEHICULO))
                    fm.beginTransaction()
                            .replace(R.id.frame_container, new FormularioFragmentVehiculo(), FORMULARIO_VEHICULO_FRAGMENT_TAG)
                            .commit();
                else
                    fm.beginTransaction()
                            .replace(R.id.frame_container,new FormularioFragmentPlataforma(), FORMULARIO_PLATAFORMA_FRAGMENT_TAG)
                            .commit();
            }
            else{
                Toast.makeText(this, getString(R.string.txt_tlt_suario_no_registrado), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,getString(R.string.txt_tlt_suario_no_registrado),Toast.LENGTH_LONG).show();
        }
    }

    public void eventoRegistrarFoto(View v){
        tipoImgSelec = v.getTag().toString();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void dialogMessage(String title, String mns){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(mns)
                .setPositiveButton(getResources().getString(R.string.txt_dialog_aceptar),null)
                .create()
                .show();
    }

    public void toastMensaje(String m){
        Toast.makeText(this, m, Toast.LENGTH_LONG).show();
    }

    public Bitmap getBtmpCilRec() {
        return btmpCilRec;
    }

    public Bitmap getBtmpCilEnt() {
        return btmpCilEnt;
    }

    public void writeData(){
        try
        {
            if(!FileManager.isExternalStorageReadable()){
                Log.w(TAG,"isExternalStorageReadable FALSE");
                return;
            }

            File root = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
            if (!root.exists())
                root.mkdirs();

            File gpxfile = new File(root, REPORTES_FILE_NAME);

            if(gpxfile.exists()) gpxfile.delete();

            FileWriter writer = new FileWriter(gpxfile);
            //BufferedWriter out = new BufferedWriter(writer);

            OutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(gpxfile));

                out.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><entries>".getBytes("UTF-8"));
                
                Cursor cursor = sqlite.selectQuery("SELECT * FROM reportes");

                if(cursor != null){
                    if(cursor.moveToFirst()){
                        do{
                            out.write("<entry>".getBytes("UTF-8"));

                            out.write(("<CIUDAD>" + cursor.getString(1) +"</CIUDAD>").getBytes("UTF-8"));
                            out.write(("<FECHA>" + cursor.getString(2) +"</FECHA>").getBytes("UTF-8"));
                            out.write(("<HORA>" + cursor.getString(3) +"</HORA>").getBytes("UTF-8"));
                            out.write(("<VEHICULO>" + cursor.getString(4) +"</VEHICULO>").getBytes("UTF-8"));
                            out.write(("<NOMBRE_CLIENTE>" + cursor.getString(5) + "</NOMBRE_CLIENTE>").getBytes("UTF-8"));
                            out.write(("<IDENTIFICACION>" + cursor.getString(6) + "</IDENTIFICACION>").getBytes("UTF-8"));
                            out.write(("<DIRECCION>" + cursor.getString(7) +"</DIRECCION>").getBytes("UTF-8"));
                            out.write(("<TELEFONO>" + cursor.getString(8) +"</TELEFONO>").getBytes("UTF-8"));
                            out.write(("<CILINDRO_RECIBIDO>" + cursor.getString(10) + "</CILINDRO_RECIBIDO>").getBytes("UTF-8"));
                            out.write(("<CAP_CIL_REC>" + cursor.getString(11) +"</CAP_CIL_REC>").getBytes("UTF-8"));
                            out.write(("<CILINDRO_ENTREGADO>" + cursor.getString(12) + "</CILINDRO_ENTREGADO>").getBytes("UTF-8"));
                            out.write(("<CAP_CIL_ENT>" + cursor.getString(13) +"</CAP_CIL_ENT>").getBytes("UTF-8"));
                            out.write(("<TARA_CIL_ENT>" + cursor.getString(14) +"</TARA_CIL_ENT>").getBytes("UTF-8"));
                            out.write(("<PESO_REAL>" + cursor.getString(15) +"</PESO_REAL>").getBytes("UTF-8"));
                            out.write(("<ERROR>" + cursor.getString(16) +"</ERROR>").getBytes("UTF-8"));
                            out.write(("<ESTADO>" + cursor.getString(17) + "</ESTADO>").getBytes("UTF-8"));

                            out.write(("</entry>").getBytes("UTF-8"));

                            out.flush();
                        }while(cursor.moveToNext());
                    }
                }

                out.write(("</entries>").getBytes("UTF-8"));
                out.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                if (out != null) {
                    out.close();
                }
            }

            /*

            //writer.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><entries>");
            //writer.flush();

            Cursor cursor = sqlite.selectQuery("SELECT * FROM reportes");

            if(cursor != null){
                if(cursor.moveToFirst()){
                    do{
                        writer.append("<entry>");

                        writer.append("<CIUDAD>"+               cursor.getString(1) +"</CIUDAD>");
                        writer.append("<FECHA>"+                cursor.getString(2) +"</FECHA>");
                        writer.append("<HORA>"+                 cursor.getString(3) +"</HORA>");
                        writer.append("<VEHICULO>"+             cursor.getString(4) +"</VEHICULO>");
                        writer.append("<NOMBRE_CLIENTE>"+       cursor.getString(5) +"</NOMBRE_CLIENTE>");
                        writer.append("<IDENTIFICACION>"+       cursor.getString(6) +"</IDENTIFICACION>");
                        writer.append("<DIRECCION>"+            cursor.getString(7) +"</DIRECCION>");
                        writer.append("<TELEFONO>"+             cursor.getString(8) +"</TELEFONO>");
                        writer.append("<CILINDRO_RECIBIDO>"+    cursor.getString(10) +"</CILINDRO_RECIBIDO>");
                        writer.append("<CAP_CIL_REC>"+          cursor.getString(11) +"</CAP_CIL_REC>");
                        writer.append("<CILINDRO_ENTREGADO>"+   cursor.getString(12) +"</CILINDRO_ENTREGADO>");
                        writer.append("<CAP_CIL_ENT>"+          cursor.getString(13) +"</CAP_CIL_ENT>");
                        writer.append("<TARA_CIL_ENT>"+         cursor.getString(14) +"</TARA_CIL_ENT>");
                        writer.append("<PESO_REAL>"+            cursor.getString(15) +"</PESO_REAL>");
                        writer.append("<ERROR>"+                cursor.getString(16) +"</ERROR>");
                        writer.append("<ESTADO>"+               cursor.getString(17) +"</ESTADO>");

                        writer.append("</entry>");

                        writer.flush();
                    }while(cursor.moveToNext());
                }
            }

            writer.append("</entries>");
            writer.flush();

            writer.close();
            */
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            Log.w(TAG,gpxfile.getPath());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
