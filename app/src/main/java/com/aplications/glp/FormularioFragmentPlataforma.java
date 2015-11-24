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

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioFragmentPlataforma#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioFragmentPlataforma extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FECHA_PARAM                 = "param1";
    private static final String HORA_PARAM                  = "param2";
    private static final String NOMBRE_CLIENTE_PARAM        = "param3";
    private static final String CAP_CIL_REC_PARAM           = "param4";
    private static final String CAP_CIL_ENT_PARAM           = "param5";
    private static final String TARA_PARAM                  = "param6";
    private static final String PESO_REAL_PARAM            = "param7";
    private static final String ERROR_PARAM                 = "param8";
    private static final String ESTADO_REC_PARAM            = "param9";

    private String fechaParam;
    private String horaParam;
    private String nombreClienteParam;
    private String capCilRecParam;
    private String capCilEntParam;
    private String taraParam;
    private String pesoRealParam;
    private String errorParam;
    private String estadoRecParam;

    private SessionProfile session;
    private SqliteManager sqlite;
    private String TAG = "FormularioFragmentPlataforma";

    // TODO: Rename and change types and number of parameters
    public static FormularioFragmentPlataforma newInstance(String... param) {
        FormularioFragmentPlataforma fragment = new FormularioFragmentPlataforma();
        Bundle args = new Bundle();
        args.putString(FECHA_PARAM,             param[0]);
        args.putString(HORA_PARAM,              param[1]);
        args.putString(NOMBRE_CLIENTE_PARAM,    param[2]);
        args.putString(CAP_CIL_REC_PARAM,       param[3]);
        args.putString(CAP_CIL_ENT_PARAM,       param[4]);
        args.putString(TARA_PARAM,              param[5]);
        args.putString(PESO_REAL_PARAM,         param[6]);
        args.putString(ERROR_PARAM,             param[7]);
        args.putString(ESTADO_REC_PARAM,        param[8]);
        fragment.setArguments(args);
        return fragment;
    }

    public FormularioFragmentPlataforma() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fechaParam              = getArguments().getString(FECHA_PARAM);
            horaParam               = getArguments().getString(HORA_PARAM);
            nombreClienteParam      = getArguments().getString(NOMBRE_CLIENTE_PARAM);
            capCilRecParam          = getArguments().getString(CAP_CIL_REC_PARAM);
            capCilEntParam          = getArguments().getString(CAP_CIL_ENT_PARAM);
            taraParam               = getArguments().getString(TARA_PARAM);
            pesoRealParam           = getArguments().getString(PESO_REAL_PARAM);
            errorParam              = getArguments().getString(ERROR_PARAM);
            estadoRecParam          = getArguments().getString(ESTADO_REC_PARAM);
        }

        setHasOptionsMenu(true);
        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        PrincipalActivity ma = (PrincipalActivity)getActivity();
        ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_plataforma));
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
        View view = inflater.inflate(R.layout.fragment_formulario_plataforma, container, false);

        final Spinner spinnerCiudades     = (Spinner)view.findViewById(R.id.spinner_ciudades);
        final TextView fecha              = (TextView)view.findViewById(R.id.editFecha);
        final TextView hora               = (TextView)view.findViewById(R.id.editHora);
        final TextView vehiBase           = (TextView)view.findViewById(R.id.editVehiBase);
        final TextView nombreCliente      = (TextView)view.findViewById(R.id.editNombreCliente);
        final Spinner spinnerCapCilRec    = (Spinner)view.findViewById(R.id.spinner_cap_cil_rec);
        final Spinner spinnerCapCilEnt    = (Spinner)view.findViewById(R.id.spinner_cap_cil_ent);

        final TextView tara               = (TextView)view.findViewById(R.id.editTara);
        final TextView pesoReal           = (TextView)view.findViewById(R.id.editPesoReal);
        final TextView error              = (TextView)view.findViewById(R.id.editError);
        final Spinner spinnerEstadoCilRec = (Spinner)view.findViewById(R.id.spinner_estado_cil_rec);

        Button btnGuardar           = (Button)view.findViewById(R.id.btnGuardar);
        Button btnGuardarAgregar    = (Button)view.findViewById(R.id.btnGuardarAgregar);

        vehiBase.setText(session.getTipoNombre());

        /////////////////   Datos Retomados     ////////////////////////////////////////////////////
        if(fechaParam != null){
            fecha.setText(fechaParam);
            hora.setText(horaParam);
            nombreCliente.setText(nombreClienteParam);
            tara.setText(taraParam);
            pesoReal.setText(pesoRealParam);
            error.setText(errorParam);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cap_cilindros_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCapCilRec.setAdapter(adapter);
            if (!capCilRecParam.equals(null)) {
                int spinnerPosition = adapter.getPosition(capCilRecParam);
                spinnerCapCilRec.setSelection(spinnerPosition);
            }

            spinnerCapCilEnt.setAdapter(adapter);
            if (!capCilEntParam.equals(null)) {
                int spinnerPosition = adapter.getPosition(capCilEntParam);
                spinnerCapCilEnt.setSelection(spinnerPosition);
            }

            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.estado_cilindros_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstadoCilRec.setAdapter(adapter);
            if (!estadoRecParam.equals(null)) {
                int spinnerPosition = adapter.getPosition(estadoRecParam);
                spinnerEstadoCilRec.setSelection(spinnerPosition);
            }
        }

        Cursor cursor = sqlite.selectQuery("SELECT * FROM reportes  ORDER BY fecha_registro DESC LIMIT 1");

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

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG,"onClickListener");
                if (
                        fecha.getText().toString().equals("") ||
                        hora.getText().toString().equals("") ||
                        nombreCliente.getText().toString().equals("") ||
                        tara.getText().toString().equals("") ||
                        pesoReal.getText().toString().equals("") ||
                        error.getText().toString().equals("") ||
                        ((PrincipalActivity) getActivity()).getBtmpCilEnt() == null ||
                        ((PrincipalActivity) getActivity()).getBtmpCilRec() == null
                ) {
                    ((PrincipalActivity) getActivity()).toastMensaje(getActivity().getResources().getString(R.string.txt_toast_campos_req));
                    return;
                }
                guardar(v.getId() == R.id.btnGuardarAgregar);
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

    private void guardar(Boolean retomarDatos){
        Spinner spinnerCiudades     = (Spinner)getView().findViewById(R.id.spinner_ciudades);
        TextView fecha              = (TextView)getView().findViewById(R.id.editFecha);
        TextView hora               = (TextView)getView().findViewById(R.id.editHora);
        TextView vehiBase           = (TextView)getView().findViewById(R.id.editVehiBase);
        TextView nombreCliente      = (TextView)getView().findViewById(R.id.editNombreCliente);
        Spinner spinnerCapCilRec    = (Spinner)getView().findViewById(R.id.spinner_cap_cil_rec);
        Spinner spinnerCapCilEnt    = (Spinner)getView().findViewById(R.id.spinner_cap_cil_ent);

        TextView tara               = (TextView)getView().findViewById(R.id.editTara);
        TextView pesoReal           = (TextView)getView().findViewById(R.id.editPesoReal);
        TextView error              = (TextView)getView().findViewById(R.id.editError);
        Spinner spinnerEstadoCilRec = (Spinner)getView().findViewById(R.id.spinner_estado_cil_rec);

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
                "'" + nombreCliente.getText().toString() + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilRec.getSelectedItem().toString() + "'," +
                "'" + "" + "'," +
                "'" + spinnerCapCilEnt.getSelectedItem().toString() + "'," +
                "'" + tara.getText().toString() + "'," +
                "'" + pesoReal.getText().toString() + "'," +
                "'" + error.getText().toString() + "'," +
                "'" + spinnerEstadoCilRec.getSelectedItem().toString() + "'" +
            ")"
        );

        Cursor cursor = sqlite.selectQuery("SELECT last_insert_rowid()");

        if(cursor == null) {
            Log.w(TAG,"last_insert_rowid NULL");
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

            String crCampo  = crRuta != null ? crRuta : "";
            String ceCampo  = ceRuta != null ? ceRuta : "";
            sqlite.query(
                    "UPDATE  " +
                            "reportes " +
                            "SET " +
                            "cilindro_recibido = '" + crCampo + "'," +
                            "cilindro_entregado = '" + ceCampo + "'" +
                            " WHERE " +
                            "id = " + id
            );

            ((PrincipalActivity)getActivity()).toastMensaje(getActivity().getString(R.string.txt_mns_reg_exito));

            FragmentManager fm = getActivity().getSupportFragmentManager();
            if(retomarDatos){
                String[] params = new String[9];
                params[0] = fecha.getText().toString();
                params[1] = hora.getText().toString();
                params[2] = nombreCliente.getText().toString();
                params[3] = spinnerCapCilRec.getSelectedItem().toString();
                params[4] = spinnerCapCilEnt.getSelectedItem().toString();
                params[5] = tara.getText().toString();
                params[6] = pesoReal.getText().toString();
                params[7] = error.getText().toString();
                params[8] = spinnerEstadoCilRec.getSelectedItem().toString();

                FormularioFragmentPlataforma ffp = FormularioFragmentPlataforma.newInstance(params);
                fm.beginTransaction()
                    .replace(R.id.frame_container,ffp,PrincipalActivity.FORMULARIO_PLATAFORMA_FRAGMENT_TAG)
                    .commit();
            }
            else{
                fm.beginTransaction()
                    .replace(
                            R.id.frame_container,
                            new FormularioFragmentPlataforma(),
                            PrincipalActivity.FORMULARIO_PLATAFORMA_FRAGMENT_TAG
                    )
                        .commit();
            }
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
