package com.aplications.glp;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;
import com.aplications.glp.sqlite.SqliteManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioFragmentVehiculo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioFragmentVehiculo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SessionProfile session;

    // TODO: Rename and change types and number of parameters
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

        SharedPreferencesManager sp = new SharedPreferencesManager(getActivity(),SharedPreferencesManager.SESION_PROFILE);

        if(sp == null) return;

        session = sp.getSesionData();
        PrincipalActivity ma = (PrincipalActivity)getActivity();
        ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_vehiculo));
        ma.getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario_vehiculo, container, false);

        TextView vehiBase = (TextView)view.findViewById(R.id.editVehiBase);
        Spinner spinnerCiudades = (Spinner)view.findViewById(R.id.spinner_ciudades);

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
        return view;
    }


}
