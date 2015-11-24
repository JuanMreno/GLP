package com.aplications.glp;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioFragmentVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioFragmentVehiculo extends Fragment {

    private static final String TAG = "FormularioFragmentVehiculo";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private SessionProfile session;
    private SqliteManager sqlite;

    public static FormularioFragmentVehiculo newInstance(String param1, String param2) {
        FormularioFragmentVehiculo fragment = new FormularioFragmentVehiculo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FormularioFragmentVehiculo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        PrincipalActivity ma = (PrincipalActivity)getActivity();
        ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_vehiculo));
        ma.getSupportActionBar().show();
        ma.setBtmpCilRec(null);
        ma.setBtmpCilEnt(null);

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
        
        final Spinner spinnerCapCilRec              = (Spinner)view.findViewById(R.id.spinner_cap_cil_rec);
        final Spinner spinnerCapCilEnt              = (Spinner)view.findViewById(R.id.spinner_cap_cil_ent);
        
        Button btnGuardar           = (Button)view.findViewById(R.id.btnGuardar);
        Button btnGuardarAgregar    = (Button)view.findViewById(R.id.btnGuardarAgregar);

        vehiBase.setText(session.getTipoNombre());

        SqliteManager sqlite =
                new SqliteManager(
                        getActivity(),
                        getActivity().getResources().getString(R.string.db_name),
                        null,
                        getActivity().getResources().getInteger(R.integer.db_version)
                );
        Cursor cursor = sqlite.selectQuery("SELECT * FROM reportes  ORDER BY fecha_registro DESC LIMIT 1");

        if(cursor == null) return view;
        if(cursor.moveToFirst()){
            String compareValue = cursor.getString(1);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ciudades_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCiudades.setAdapter(adapter);
            if (!compareValue.equals(null)) {
                int spinnerPosition = adapter.getPosition(compareValue);
                spinnerCiudades.setSelection(spinnerPosition);
            }
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        fecha.getText().toString().equals("") ||
                        hora.getText().toString().equals("") ||
                        editNombreCliente.getText().toString().equals("") ||
                        editIdentificacionCliente.getText().toString().equals("") ||
                        editDireccionCliente.getText().toString().equals("") ||
                        editTelefono.getText().toString().equals("") ||
                        ((PrincipalActivity) getActivity()).getBtmpCilEnt() == null ||
                        ((PrincipalActivity) getActivity()).getBtmpCilRec() == null
                    ) {
                    ((PrincipalActivity) getActivity()).toastMensaje(getActivity().getResources().getString(R.string.txt_toast_campos_req));
                    return;
                }
                guardar();
            }
        });

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

        Spinner spinnerCapCilRec              = (Spinner)getView().findViewById(R.id.spinner_cap_cil_rec);
        Spinner spinnerCapCilEnt              = (Spinner)getView().findViewById(R.id.spinner_cap_cil_ent);

        Bitmap btmpCilRec = ((PrincipalActivity)getActivity()).getBtmpCilRec();
        Bitmap btmpCilEnt = ((PrincipalActivity)getActivity()).getBtmpCilEnt();

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
                "estado" +
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
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilRec.getSelectedItem().toString() + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilEnt.getSelectedItem().toString() + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'" +
            ")"
        );

        Cursor cursor = sqlite.selectQuery("SELECT last_insert_rowid()");

        if(cursor == null) {
            Log.w(TAG, "last_insert_rowid NULL");
            return;
        }

        if(cursor.moveToFirst()){
            String id = cursor.getString(0);
            String crRuta = saveImage(btmpCilRec,"CR_"+id);
            if(crRuta == null){
                Log.w(TAG,"btmpCilRec NO GUARDADO");
            }

            String ceRuta = saveImage(btmpCilEnt,"CE_"+id);
            if(ceRuta == null){
                Log.w(TAG,"btmpCilEnt NO GUARDADO");
            }

            sqlite.query(
                    "UPDATE  " +
                            "reportes " +
                            "SET " +
                            "cilindro_recibido = '" + crRuta != null ? crRuta : "" + "'," +
                            "cilindro_entregado = '" + ceRuta != null ? ceRuta : "'" +
                            " WHERE " +
                            "id = " + id
            );

            ((PrincipalActivity)getActivity()).toastMensaje(getActivity().getString(R.string.txt_mns_reg_exito));
        }
        else{
            Log.w(TAG,"last_insert_rowid vacio");
        }
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
                    ruta = file.getPath() + file.getName();
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
