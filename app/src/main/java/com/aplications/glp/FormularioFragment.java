package com.aplications.glp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.shared_preferences.SharedPreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormularioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormularioFragment newInstance(String param1, String param2) {
        FormularioFragment fragment = new FormularioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FormularioFragment() {
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

        SessionProfile session = sp.getSesionData();
        PrincipalActivity ma = (PrincipalActivity)getActivity();

        if(session.getTipoUsuario().equals(SessionProfile.TIPO_PLATAFORMA))
            ma.getSupportActionBar()
                .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_plataforma));
        else
            ma.getSupportActionBar()
                    .setTitle(ma.getResources().getString(R.string.app_name) + " - " + ma.getResources().getString(R.string.txt_vehiculo));

        ma.getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_formulario_vehiculo, container, false);
    }


}
