package com.aplications.glp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;

public class MainActivity extends AppCompatActivity{

    public static final String REGISTRO_INTENT_CODE = "RegistroIntentCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                            SqliteManager sqlite =
                                new SqliteManager(
                                    MainActivity.this,
                                    getResources().getString(R.string.db_name),
                                    null,
                                    getResources().getInteger(R.integer.db_version)
                                );

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
                                    "'"+tipoUsuario+"'," +
                                    "'"+editVehiPlat.getText().toString()+"'," +
                                    "'"+editNombre.getText().toString()+"'," +
                                    "'"+editIdent.getText().toString()+"'," +
                                    "'"+editTel.getText().toString()+"'" +
                                ")"
                            );

                            SharedPreferencesManager sp = new SharedPreferencesManager(MainActivity.this,SharedPreferencesManager.SESION_PROFILE);

                            sp.setSesionProfile(
                                new SessionProfile(
                                        tipoUsuario,
                                        editVehiPlat.getText().toString(),
                                        editNombre.getText().toString(),
                                        editIdent.getText().toString(),
                                        editTel.getText().toString()
                                )
                            );

                        }
                    })
            .setNegativeButton(getResources().getString(R.string.txt_dialog_cancelar), null)
            .create()
            .show();
    }
}
