package com.aplications.glp;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aplications.glp.objetos.Registro;
import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarFragmentPlataforma#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarFragmentPlataforma extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REGISTRO_PARAM                 = "param1";

    private Registro registro;

    private SessionProfile session;
    private SqliteManager sqlite;
    private String TAG = "FormularioFragmentPlataforma";
    private PrincipalActivity ma;

    // TODO: Rename and change types and number of parameters
    public static EditarFragmentPlataforma newInstance(Registro registro) {
        EditarFragmentPlataforma fragment = new EditarFragmentPlataforma();
        Bundle args = new Bundle();
        args.putString(REGISTRO_PARAM, new Gson().toJson(registro));
        fragment.setArguments(args);
        return fragment;
    }

    public EditarFragmentPlataforma() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            registro = new Gson().fromJson(getArguments().getString(REGISTRO_PARAM),Registro.class);
        }

        setHasOptionsMenu(true);
        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        ma = (PrincipalActivity)getActivity();
        ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_plataforma));
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
        btnGuardarAgregar.setVisibility(Button.GONE);

        vehiBase.setText(session.getTipoNombre());

        /////////////////   Datos Retomados     ////////////////////////////////////////////////////
        if(registro.getFecha() != null){
            fecha.setText(registro.getFecha());
            hora.setText(registro.getHora());
            nombreCliente.setText(registro.getNombreCliente());
            tara.setText(registro.getTara());
            pesoReal.setText(registro.getPesoReal());
            error.setText(registro.getError());

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

            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.estado_cilindros_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstadoCilRec.setAdapter(adapter);
            if (!registro.getEstado().equals(null)) {
                int spinnerPosition = adapter.getPosition(registro.getEstado());
                spinnerEstadoCilRec.setSelection(spinnerPosition);
            }

            byte[] decodedByte = Base64.decode(registro.getBmpCilEnCod(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

            ImageView imageView = (ImageView) view.findViewById(R.id.imgEntregado);
            imageView.setImageBitmap(bitmap);


            decodedByte = Base64.decode(registro.getBmpCilRecCod(), 0);
            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

            imageView = (ImageView) view.findViewById(R.id.imgRecibido);
            imageView.setImageBitmap(bitmap);

            //ma.setBtmpCilEnt(registro.getBmpCilEnCod());
            //ma.setBtmpCilRec(registro.getBmpCilRecCod());
        }

        String compareValue = registro.getCiudad();
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
                Log.w(TAG,"onClickListener");
                if (
                    fecha.getText().toString().equals("") ||
                    hora.getText().toString().equals("") ||
                    nombreCliente.getText().toString().equals("") ||
                    tara.getText().toString().equals("") ||
                    pesoReal.getText().toString().equals("") ||
                    error.getText().toString().equals("")
                ) {
                    ((PrincipalActivity) getActivity()).toastMensaje(getActivity().getResources().getString(R.string.txt_toast_campos_req));
                    return;
                }
                guardar();
            }
        };

        btnGuardar.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void guardar(){
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

        sqlite.query(
                "UPDATE " +
                        "reportes " +
                        "SET " +
                        "ciudad = '" + spinnerCiudades.getSelectedItem().toString() + "'," +
                        "fecha = '" + fecha.getText().toString() + "'," +
                        "hora = '" + hora.getText().toString() + "'," +
                        "vehiculo = '" + vehiBase.getText().toString() + "'," +
                        "nombre_cliente = '" + nombreCliente.getText().toString() + "'," +
                        "identificacion = '" + "" + "'," +
                        "direccion = '" + "" + "'," +
                        "telefono = '" + "" + "'," +
                        "recarga_n = '" + "" + "'," +
                        "cap_cil_rec = '" + spinnerCapCilRec.getSelectedItem().toString() + "'," +
                        "cap_cil_ent = '" + spinnerCapCilEnt.getSelectedItem().toString() + "'," +
                        "tara_cil_ent = '" + tara.getText().toString() + "'," +
                        "peso_real = '" + pesoReal.getText().toString() + "'," +
                        "error = '" + error.getText().toString() + "'," +
                        "estado = '" + spinnerEstadoCilRec.getSelectedItem().toString() + "' " +
                        " WHERE " +
                        " id = " + registro.getId()
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
