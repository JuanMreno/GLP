package com.aplications.glp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.aplications.glp.objetos.Registro;
import com.google.gson.Gson;

public class ListaRegistrosActivity extends AppCompatActivity {

    public static final String REGISTRO_EXTRA = "RegistroExtra";
    private String TAG = "LstaRgstrosActvty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_registros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lista de Registros");
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
            .replace(R.id.frame_container,new ListaRegistrosActivityFragment(),ListaRegistrosActivityFragment.FRAGMENT_TAG)
            .commit();
    }

    public void setResultNFinish(Registro registro){
        Intent intent = new Intent();

        Log.w(TAG,"setResultNFinish");
        if (registro != null)
            intent.putExtra(REGISTRO_EXTRA,new Gson().toJson(registro));
        else
            intent.putExtra(REGISTRO_EXTRA,"null");
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}
