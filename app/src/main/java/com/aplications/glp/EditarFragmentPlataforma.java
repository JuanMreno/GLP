package com.aplications.glp;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.lang.ref.WeakReference;
import java.util.Calendar;

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
        View view = inflater.inflate(R.layout.fragment_edit_form_plataforma, container, false);

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

            ////////    IMAGEN CILINDRO ENTREGADO   ////////////////////////////////////////////////
            ImageView imgEntregado = (ImageView) view.findViewById(R.id.imgEntregado);

            String[] paramsEnt = new String[1];
            paramsEnt[0] = registro.getBmpCilEnCod();
            new BitmapWorkerTask(imgEntregado).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsEnt);

            ////////    IMAGEN CILINDRO RECIBIDO    ////////////////////////////////////////////////
            ImageView imgRecibido = (ImageView) view.findViewById(R.id.imgRecibido);

            String[] paramsRec = new String[1];
            paramsRec[0] = registro.getBmpCilRecCod();
            new BitmapWorkerTask(imgRecibido).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsRec);

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
                if (hasFocus) {
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

                    pickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    pickerDialog.setMessage("Hora");
                    pickerDialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal, menu);
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

        if(ma.isCrImgEditada()){
            File crFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_REC_TEMP_FILE_NAME
            );

            File crFileToRename = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CR_"+registro.getId()+".jpg");

            if(crFileToRename.exists())
                crFileToRename.delete();

            crFile.renameTo(crFileToRename);
        }

        if(ma.isCeImgEditada()){
            File ceFile = new File(
                    FileManager.getAlbumStorageDir(getString(R.string.app_name)),
                    PrincipalActivity.CIL_ENT_TEMP_FILE_NAME);

            File ceFileToRename = new File(FileManager.getAlbumStorageDir(getString(R.string.app_name)),"CE_"+registro.getId()+".jpg");

            if(ceFileToRename.exists())
                ceFileToRename.delete();

            ceFile.renameTo(ceFileToRename);
        }

        ma.toastMensaje("Cambios guardados con éxito");
        ma.iniMain();
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            String path = params[0];

            Log.w("BitmapWorkerTask","img path: " + path);
            return decodeSampledBitmapFromFile(path);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, options.outWidth/30, options.outWidth/30);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

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
