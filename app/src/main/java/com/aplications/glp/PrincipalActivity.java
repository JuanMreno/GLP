package com.aplications.glp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aplications.glp.objetos.Registro;
import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PrincipalActivity extends AppCompatActivity implements
        DialogInterface.OnClickListener{

    private static String TAG = "PrincipalActivity";
    public static final String REGISTRO_INTENT_CODE = "RegistroIntentCode";
    public static final  String ENTREGADO_TAG = "ENTREGADO";
    public static final  String RECIBIDO_TAG = "RECIBIDO";
    public static final  String REPORTES_FILE_NAME = "reporte.xml";

    public static final  String CIL_REC_TEMP_FILE_NAME = "cil_rec_temp.jpg";
    public static final  String CIL_ENT_TEMP_FILE_NAME = "cil_ent_temp.jpg";
    private static final int    REQUEST_IMAGE_CAPTURE = 1;
    public static final int     LISTA_REG_ACTIVITY_CODE = 2;
    private int                 REQUEST_IMAGE_EDIT_CAPTURE = 3;

    public static final String MAIN_ACTIVITY_FRAGMENT_TAG = "PrincipalActivityFragmentTag";
    public static final String FORMULARIO_VEHICULO_FRAGMENT_TAG = "FormularioVehiculoFragmentTag";
    public static final String FORMULARIO_PLATAFORMA_FRAGMENT_TAG = "FormularioPlataformaFragmentTag";
    public static final String EDITAR_VEHICULO_FRAGMENT_TAG = "EditarVehiculoFragmentTag";
    public static final String EDITAR_PLATAFORMA_FRAGMENT_TAG = "EditarPlataformaFragmentTag";

    private SqliteManager sqlite;
    private String tipoImgSelec = "";

    private SessionProfile session;

    private boolean crImgSet = false;
    private boolean ceImgSet = false;

    private boolean crImgEditada;
    private boolean ceImgEditada;

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
        session = sp.getSesionData();
        iniMain();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_cerrar_sesion:
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                View v = inflater.inflate(R.layout.dialog_content_cerrar_sesion,null);

                final EditText editText = (EditText)v.findViewById(R.id.editPass);
                new AlertDialog.Builder(this)
                    .setTitle("Validar cierre sesión")
                    .setView(v)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(editText.getText().toString().equals("")){
                                Toast.makeText(PrincipalActivity.this,"Campo requerido",Toast.LENGTH_LONG).show();
                                return;
                            }

                            String val = editText.getText().toString();

                            if(val.equals(getString(R.string.pass_cerrar_sesion))){
                                SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);
                                sp.removeSesionProfile();

                                FragmentManager fm = getSupportFragmentManager();
                                fm.beginTransaction()
                                        .replace(R.id.frame_container, new PrincipalActivityFragment(), MAIN_ACTIVITY_FRAGMENT_TAG)
                                        .commit();

                                if(FileManager.isExternalStorageWritable() && FileManager.isExternalStorageReadable()){
                                    File file = FileManager.getAlbumStorageDir(getString(R.string.app_name));

                                    if (file.isDirectory())
                                    {
                                        String[] children = file.list();
                                        for (int i = 0; i < children.length; i++)
                                            new File(file, children[i]).delete();
                                    }
                                }
                                else{
                                    Log.w(TAG,"action_cerrar_sesion, isExternalStorageWritable isExternalStorageReadable NULL");
                                }

                                sqlite.query("DELETE FROM reportes");
                            }
                            else{
                                Toast.makeText(PrincipalActivity.this,"Contraseña inválida.",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar",null)
                    .create()
                    .show();
                break;
            case R.id.action_generar_reporte:
                writeData();
                break;
            case R.id.action_lista_registros:
                Intent intent = new Intent(this,ListaRegistrosActivity.class);
                startActivityForResult(intent, LISTA_REG_ACTIVITY_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                //Bundle extras = data.getExtras();
                //Bitmap imageBitmap = (Bitmap) extras.get("data");

                ImageView imageView;

                SharedPreferencesManager sp = new SharedPreferencesManager(PrincipalActivity.this,SharedPreferencesManager.SESION_PROFILE);
                session = sp.getSesionData();

                if(tipoImgSelec.equals(RECIBIDO_TAG)){
                    setCrImgEditada(true);
                    imageView = (ImageView) findViewById(R.id.imgRecibido);

                    File tempFile = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),CIL_REC_TEMP_FILE_NAME);
                    crImgSet = true;
                    imageView.setImageBitmap(decodeSampledBitmapFromFile(tempFile.getAbsolutePath()));
                }
                else{
                    setCeImgEditada(true);
                    imageView = (ImageView) findViewById(R.id.imgEntregado);

                    File tempFile = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),CIL_ENT_TEMP_FILE_NAME);
                    ceImgSet = true;
                    imageView.setImageBitmap(decodeSampledBitmapFromFile(tempFile.getAbsolutePath()));
                }
            }

            if (requestCode == LISTA_REG_ACTIVITY_CODE && resultCode == RESULT_OK) {
                final String extra = data.getExtras().getString(ListaRegistrosActivity.REGISTRO_EXTRA);

                if(!extra.equals("")){
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            FragmentManager fm = getSupportFragmentManager();

                            Registro registro = new Gson().fromJson(extra,Registro.class);
                            if(session.getTipoUsuario().equals(SessionProfile.TIPO_VEHICULO)){
                                fm.beginTransaction()
                                        .replace(R.id.frame_container, EditarFragmentVehiculo.newInstance(registro), EDITAR_VEHICULO_FRAGMENT_TAG)
                                        .commit();
                            }
                            else{
                                fm.beginTransaction()
                                        .replace(R.id.frame_container, EditarFragmentPlataforma.newInstance(registro), EDITAR_PLATAFORMA_FRAGMENT_TAG)
                                        .commit();
                            }
                        }
                    });
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        EditarFragmentPlataforma fp = (EditarFragmentPlataforma) fm.findFragmentByTag(EDITAR_PLATAFORMA_FRAGMENT_TAG);
        if(fp != null){
            if(fp.isVisible()){
                iniMain();
                return;
            }
        }

        EditarFragmentVehiculo fv = (EditarFragmentVehiculo) fm.findFragmentByTag(EDITAR_VEHICULO_FRAGMENT_TAG);
        if(fv != null){
            if(fv.isVisible()){
                iniMain();
                return;
            }
        }

        finish();
        super.onBackPressed();
    }

    public void iniMain(){
        FragmentManager fm = getSupportFragmentManager();
        if(session != null){
            if(session.getTipoUsuario().equals(SessionProfile.TIPO_VEHICULO))
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

    public void eventoIngresar(View v) {
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

        File fileToWrite = null;
        /*
        FragmentManager fm = getSupportFragmentManager();
        if(session.getTipoUsuario().equals(SessionProfile.TIPO_VEHICULO)){
            EditarFragmentVehiculo fv = (EditarFragmentVehiculo)fm.findFragmentByTag(EDITAR_VEHICULO_FRAGMENT_TAG);

            if(fv != null){
                if(fv.isVisible()){
                    if(tipoImgSelec.equals(RECIBIDO_TAG))
                        fileToWrite = new File(fv.getImgCilRecName());
                    else
                        fileToWrite = new File(fv.getImgCilEntName());
                }
            }
        }
        else{
            EditarFragmentPlataforma fp = (EditarFragmentPlataforma)fm.findFragmentByTag(EDITAR_PLATAFORMA_FRAGMENT_TAG);

            if(fp != null){
                if(fp.isVisible()){
                    if(tipoImgSelec.equals(RECIBIDO_TAG))
                        fileToWrite = new File(fp.getImgCilRecName());
                    else
                        fileToWrite = new File(fp.getImgCilEntName());
                }
            }
        }
        */

        if(fileToWrite == null){
            if(tipoImgSelec.equals(RECIBIDO_TAG))
                fileToWrite = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),CIL_REC_TEMP_FILE_NAME);
            else
                fileToWrite = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)), CIL_ENT_TEMP_FILE_NAME);
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileToWrite));
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

    public void writeData(){
        try
        {
            if(!FileManager.isExternalStorageReadable()){
                Log.w(TAG,"isExternalStorageReadable FALSE");
                return;
            }

            File gpxfile = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)), REPORTES_FILE_NAME);

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

                            out.write(("<N>" + cursor.getString(0) +"</N>").getBytes("UTF-8"));
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
                            out.write(("<VALOR>" + cursor.getString(18) + "</VALOR>").getBytes("UTF-8"));

                            out.write(("</entry>").getBytes("UTF-8"));

                            out.flush();
                        }while(cursor.moveToNext());
                        
                        String finalRow = "<entry>" +
                                "      <N></N>" +
                                "      <CIUDAD></CIUDAD>" +
                                "      <FECHA></FECHA>" +
                                "      <HORA></HORA>" +
                                "      <VEHICULO></VEHICULO>" +
                                "      <NOMBRE_CLIENTE></NOMBRE_CLIENTE>" +
                                "      <IDENTIFICACION></IDENTIFICACION>" +
                                "      <DIRECCION></DIRECCION>" +
                                "      <TELEFONO></TELEFONO>" +
                                "      <CILINDRO_RECIBIDO></CILINDRO_RECIBIDO>" +
                                "      <CAP_CIL_REC></CAP_CIL_REC>" +
                                "      <CILINDRO_ENTREGADO></CILINDRO_ENTREGADO>" +
                                "      <CAP_CIL_ENT></CAP_CIL_ENT>" +
                                "      <TARA_CIL_ENT></TARA_CIL_ENT>" +
                                "      <PESO_REAL></PESO_REAL>" +
                                "      <ERROR></ERROR>" +
                                "      <ESTADO></ESTADO>" +
                                "      <VALOR></VALOR>" +
                                "   </entry>";

                        out.write((finalRow).getBytes("UTF-8"));
                        out.flush();
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

            Toast.makeText(this, "Reporte generado con éxito", Toast.LENGTH_SHORT).show();
            Log.w(TAG,gpxfile.getPath());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    public boolean isCrImgSet() {
        return crImgSet;
    }

    public void setCrImgSet(boolean crImgSet) {
        this.crImgSet = crImgSet;
    }

    public boolean isCeImgSet() {
        return ceImgSet;
    }

    public void setCeImgSet(boolean ceImgSet) {
        this.ceImgSet = ceImgSet;
    }

    public boolean isCrImgEditada() {
        return crImgEditada;
    }

    public void setCrImgEditada(boolean crImgEditada) {
        this.crImgEditada = crImgEditada;
    }

    public boolean isCeImgEditada() {
        return ceImgEditada;
    }

    public void setCeImgEditada(boolean ceImgEditada) {
        this.ceImgEditada = ceImgEditada;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, options.outWidth/10, options.outWidth/10);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
