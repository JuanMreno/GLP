package com.aplications.glp;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioFragmentVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioFragmentVehiculo extends Fragment {

    private static final String TAG = "FrmlrioFrgmentVhclo";

    private static final String FECHA_PARAM                 = "param1";
    private static final String HORA_PARAM                  = "param2";
    private static final String NOMBRE_CLIENTE_PARAM        = "param3";
    private static final String IDENTIFICACION_PARAM        = "param4";
    private static final String DIRECCION_PARAM             = "param5";
    private static final String TELEFONO_PARAM              = "param6";
    private static final String RECARGA_N_PARAM             = "param7";
    private static final String CAP_CIL_REC_PARAM           = "param8";
    private static final String CAP_CIL_ENT_PARAM           = "param9";

    private String fechaParam;
    private String horaParam;
    private String nombreClienteParam;
    private String identificacionParam;
    private String direccionParam;
    private String telefonoParam;
    private String recargaNParam;
    private String capCilRecParam;
    private String capCilEntParam;

    private SessionProfile session;
    private SqliteManager sqlite;

    public static FormularioFragmentVehiculo newInstance(String... param) {
        FormularioFragmentVehiculo fragment = new FormularioFragmentVehiculo();
        Bundle args = new Bundle();
        args.putString(FECHA_PARAM,              param[0]);
        args.putString(HORA_PARAM,               param[1]);
        args.putString(NOMBRE_CLIENTE_PARAM,     param[2]);
        args.putString(IDENTIFICACION_PARAM,     param[3]);
        args.putString(DIRECCION_PARAM,          param[4]);
        args.putString(TELEFONO_PARAM,           param[5]);
        args.putString(RECARGA_N_PARAM,          param[6]);
        args.putString(CAP_CIL_REC_PARAM,        param[7]);
        args.putString(CAP_CIL_ENT_PARAM,        param[8]);
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
            fechaParam              = getArguments().getString(FECHA_PARAM);
            horaParam               = getArguments().getString(HORA_PARAM);
            nombreClienteParam      = getArguments().getString(NOMBRE_CLIENTE_PARAM);
            identificacionParam     = getArguments().getString(IDENTIFICACION_PARAM);
            direccionParam          = getArguments().getString(DIRECCION_PARAM);
            telefonoParam           = getArguments().getString(TELEFONO_PARAM);
            recargaNParam           = getArguments().getString(RECARGA_N_PARAM);
            capCilRecParam          = getArguments().getString(CAP_CIL_REC_PARAM);
            capCilEntParam          = getArguments().getString(CAP_CIL_ENT_PARAM);
        }

        setHasOptionsMenu(true);
        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        PrincipalActivity ma = (PrincipalActivity)getActivity();
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
        final TextView editRecargaN                 = (TextView)view.findViewById(R.id.editRecargaN);

        final Spinner spinnerCapCilRec              = (Spinner)view.findViewById(R.id.spinner_cap_cil_rec);
        final Spinner spinnerCapCilEnt              = (Spinner)view.findViewById(R.id.spinner_cap_cil_ent);
        
        Button btnGuardar           = (Button)view.findViewById(R.id.btnGuardar);
        Button btnGuardarAgregar    = (Button)view.findViewById(R.id.btnGuardarAgregar);

        vehiBase.setText(session.getTipoNombre());

        /////////////////   Datos Retomados     ////////////////////////////////////////////////////
        if(fechaParam != null){
            fecha.setText(fechaParam);
            hora.setText(horaParam);
            editNombreCliente.setText(nombreClienteParam);
            editIdentificacionCliente.setText(identificacionParam);
            editDireccionCliente.setText(direccionParam);
            editTelefono.setText(telefonoParam);
            //editRecargaN.setText(recargaNParam);

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
        }

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
                                editRecargaN.getText().toString().equals("") ||
                                !((PrincipalActivity) getActivity()).isCeImgSet() ||
                                !((PrincipalActivity) getActivity()).isCrImgSet()
                        ) {
                    ((PrincipalActivity) getActivity()).toastMensaje(getActivity().getResources().getString(R.string.txt_toast_campos_req));
                    return;
                }
                guardar(v.getId() == R.id.btnGuardarAgregar);
            }
        };

        btnGuardar.setOnClickListener(onClickListener);
        btnGuardarAgregar.setOnClickListener(onClickListener);

        fecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog pickerDialog = new DatePickerDialog(
                            getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    fecha.setText(
                                            String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year));
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

                    pickerDialog.setMessage("Fecha");
                    pickerDialog.show();
                }
            }
        });

        hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog pickerDialog = new TimePickerDialog(
                            getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    hora.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                                }
                            },
                            calendar.get(Calendar.HOUR),
                            calendar.get(Calendar.MINUTE),
                            true
                    );

                    pickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
                    pickerDialog.setMessage("Hora");
                    pickerDialog.show();
                }
            }
        });
        System.gc();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void guardar(Boolean retomarDatos){
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

        Log.w(TAG,"editRecargaN: " + editRecargaN.getText().toString());

        Cursor cursor = sqlite.selectQuery("SELECT last_insert_rowid()");

        if(cursor == null) {
            return;
        }

        if(cursor.moveToFirst()){
            String id = cursor.getString(0);

            File crFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_REC_TEMP_FILE_NAME
            );

            File ceFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_ENT_TEMP_FILE_NAME);

            File crFileRenamed = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CR_"+id+".jpg");
            File ceFileRenamed = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CE_"+id+".jpg");

            crFile.renameTo(crFileRenamed);
            ceFile.renameTo(ceFileRenamed);

            String crRuta = crFileRenamed.getAbsolutePath();
            String ceRuta = ceFileRenamed.getAbsolutePath();

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
                params[2] = editNombreCliente.getText().toString();
                params[3] = editIdentificacionCliente.getText().toString();
                params[4] = editDireccionCliente.getText().toString();
                params[5] = editTelefono.getText().toString();
                params[6] = editRecargaN.getText().toString();
                params[7] = spinnerCapCilRec.getSelectedItem().toString();
                params[8] = spinnerCapCilEnt.getSelectedItem().toString();

                FormularioFragmentVehiculo ffp = FormularioFragmentVehiculo.newInstance(params);
                fm.beginTransaction()
                        .replace(R.id.frame_container,ffp,PrincipalActivity.FORMULARIO_VEHICULO_FRAGMENT_TAG)
                        .commit();
            }
            else{
                fm.beginTransaction()
                        .replace(
                                R.id.frame_container,
                                new FormularioFragmentVehiculo(),
                                PrincipalActivity.FORMULARIO_VEHICULO_FRAGMENT_TAG
                        )
                        .commit();
            }
        }
        else{
            Log.w(TAG,"last_insert_rowid vacio");
        }
    }
}
