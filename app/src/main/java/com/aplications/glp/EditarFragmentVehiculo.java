package com.aplications.glp;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.aplications.glp.objetos.Registro;
import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarFragmentVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarFragmentVehiculo extends Fragment {

    private static final String TAG = "FrmlrioFrgmentVhclo";
    private static final String REGISTRO_PARAM                 = "param1";

    private Registro registro;

    private SessionProfile session;
    private SqliteManager sqlite;
    private PrincipalActivity ma;

    public static EditarFragmentVehiculo newInstance(Registro registro) {
        EditarFragmentVehiculo fragment = new EditarFragmentVehiculo();
        Bundle args = new Bundle();
        args.putString(REGISTRO_PARAM, new Gson().toJson(registro));
        fragment.setArguments(args);
        return fragment;
    }

    public EditarFragmentVehiculo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            registro = new Gson().fromJson(getArguments().getString(REGISTRO_PARAM), Registro.class);
        }

        setHasOptionsMenu(true);
        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        ma = (PrincipalActivity)getActivity();
        ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_vehiculo));
        ma.getSupportActionBar().show();
        ma.setCeImgEditada(false);
        ma.setCrImgEditada(false);

        sqlite =
                new SqliteManager(
                        getActivity(),
                        getActivity().getResources().getString(R.string.db_name),
                        null,
                        getActivity().getResources().getInteger(R.integer.db_version)
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario_vehiculo, container, false);

        final Spinner spinnerCiudades               = (Spinner)view.findViewById(R.id.spinner_ciudades);
        final TextView fecha                        = (TextView)view.findViewById(R.id.editFecha);
        final TextView hora                         = (TextView)view.findViewById(R.id.editHora);
        final TextView vehiBase                     = (TextView)view.findViewById(R.id.editVehiBase);
        final TextView editNombreCliente            = (TextView)view.findViewById(R.id.editNombreCliente);
        final TextView editIdentificacionCliente    = (TextView)view.findViewById(R.id.editIdentificacionCliente);
        final TextView editDireccionCliente         = (TextView)view.findViewById(R.id.editDireccionCliente);
        final TextView editTelefono                 = (TextView)view.findViewById(R.id.editTelefono);
        final TextView editValor                    = (TextView)view.findViewById(R.id.editValor);
        final TextView editRecargaN                    = (TextView)view.findViewById(R.id.editRecargaN);

        final Spinner spinnerCapCilRec              = (Spinner)view.findViewById(R.id.spinner_cap_cil_rec);
        final Spinner spinnerCapCilEnt              = (Spinner)view.findViewById(R.id.spinner_cap_cil_ent);
        
        Button btnGuardar           = (Button)view.findViewById(R.id.btnGuardar);
        Button btnGuardarAgregar    = (Button)view.findViewById(R.id.btnGuardarAgregar);

        vehiBase.setText(session.getTipoNombre());

        /////////////////   Datos Retomados     ////////////////////////////////////////////////////
        if(registro.getFecha() != null){
            fecha.setText(registro.getFecha());
            hora.setText(registro.getHora());
            editNombreCliente.setText(registro.getNombreCliente());
            editIdentificacionCliente.setText(registro.getIdentificacion());
            editDireccionCliente.setText(registro.getDireccion());
            editTelefono.setText(registro.getTelefono());

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cap_cilindros_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCapCilRec.setAdapter(adapter);
            if (!registro.getCapCilRec().equals(null)) {
                int spinnerPosition = adapter.getPosition(registro.getCapCilRec());
                spinnerCapCilRec.setSelection(spinnerPosition);
            }

            spinnerCapCilEnt.setAdapter(adapter);
            if (!registro.getCapCilEnt().equals(null)) {
                int spinnerPosition = adapter.getPosition(registro.getCapCilEnt());
                spinnerCapCilEnt.setSelection(spinnerPosition);
            }
        }

        String compareValue =registro.getCiudad();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ciudades_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudades.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            spinnerCiudades.setSelection(spinnerPosition);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        fecha.getText().toString().equals("") ||
                                hora.getText().toString().equals("") ||
                                editNombreCliente.getText().toString().equals("") ||
                                editIdentificacionCliente.getText().toString().equals("") ||
                                editDireccionCliente.getText().toString().equals("") ||
                                editTelefono.getText().toString().equals("") ||
                                editValor.getText().toString().equals("") ||
                                editRecargaN.getText().toString().equals("")
                        ) {
                    ((PrincipalActivity) getActivity()).toastMensaje(getActivity().getResources().getString(R.string.txt_toast_campos_req));
                    return;
                }
                guardar();
            }
        };

        btnGuardar.setOnClickListener(onClickListener);
        btnGuardarAgregar.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void guardar(){
        Spinner spinnerCiudades               = (Spinner)getView().findViewById(R.id.spinner_ciudades);
        TextView fecha                        = (TextView)getView().findViewById(R.id.editFecha);
        TextView hora                         = (TextView)getView().findViewById(R.id.editHora);
        TextView vehiBase                     = (TextView)getView().findViewById(R.id.editVehiBase);
        TextView editNombreCliente            = (TextView)getView().findViewById(R.id.editNombreCliente);
        TextView editIdentificacionCliente    = (TextView)getView().findViewById(R.id.editIdentificacionCliente);
        TextView editDireccionCliente         = (TextView)getView().findViewById(R.id.editDireccionCliente);
        TextView editTelefono                 = (TextView)getView().findViewById(R.id.editTelefono);
        TextView editValor                    = (TextView)getView().findViewById(R.id.editValor);
        TextView editRecargaN                 = (TextView)getView().findViewById(R.id.editRecargaN);

        Spinner spinnerCapCilRec              = (Spinner)getView().findViewById(R.id.spinner_cap_cil_rec);
        Spinner spinnerCapCilEnt              = (Spinner)getView().findViewById(R.id.spinner_cap_cil_ent);

        sqlite.query(
            "INSERT INTO " +
                "reportes " +
            "(" +
                "ciudad," +
                "fecha," +
                "hora," +
                "vehiculo," +
                "nombre_cliente," +
                "identificacion," +
                "direccion," +
                "telefono," +
                "recarga_n," +
                "cilindro_recibido," +
                "cap_cil_rec," +
                "cilindro_entregado," +
                "cap_cil_ent," +
                "tara_cil_ent," +
                "peso_real," +
                "error," +
                "estado," +
                "valor" +
            ") " +
            "VALUES" +
            "(" +
                "'" + spinnerCiudades.getSelectedItem().toString() + "'," +
                "'" + fecha.getText().toString() + "'," +
                "'" + hora.getText().toString() + "'," +
                "'" + vehiBase.getText().toString() + "'," +
                "'" + editNombreCliente.getText().toString() + "'," +
                "'" + editIdentificacionCliente.getText().toString() + "'," +
                "'" + editDireccionCliente.getText().toString() + "'," +
                "'" + editTelefono.getText().toString() + "'," +
                "'" + editRecargaN.getText().toString() + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilRec.getSelectedItem().toString() + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilEnt.getSelectedItem().toString() + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + editValor.getText().toString() + "'" +
            ")"
        );

        if(!ma.isCrImgEditada()){
            File crFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_REC_TEMP_FILE_NAME
            );

            File crFileToRename = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CR_"+registro.getId()+".jpg");
            crFile.renameTo(crFileToRename);
        }

        if(!ma.isCeImgEditada()){
            File ceFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_ENT_TEMP_FILE_NAME);

            File ceFileToRename = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CE_"+registro.getId()+".jpg");
            ceFile.renameTo(ceFileToRename);
        }

        ma.toastMensaje("Cambios guardados con éxito");
        ma.iniMain();
    }

    private String saveImage(Bitmap bitmap, String name){
        String ruta = null;
        try{
            if(FileManager.isExternalStorageWritable()){
                File file = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),name+".jpg");

                if(file.exists()) file.delete();

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    ruta = file.getPath();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                Log.w(TAG, "isExternalStorageWritable FALSE");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ruta;
    }

}
