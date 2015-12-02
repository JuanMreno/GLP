package com.aplications.glp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aplications.glp.adapters.ListaRegistrosAdapter;
import com.aplications.glp.objetos.Registro;
import com.aplications.glp.objetos.SessionProfile;
import com.aplications.glp.sqlite.SqliteManager;
import com.aplications.glp.utils.FileManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListaRegistrosActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static String FRAGMENT_TAG = "ListaRegistrosActivityFragmentTag";
    private SqliteManager sqlite;
    ArrayList<Registro> registros;
    private String TAG = "LstaRgstrosFrgment";

    public ListaRegistrosActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_registros, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sqlite =
            new SqliteManager(
                getActivity(),
                getResources().getString(R.string.db_name),
                null,
                getResources().getInteger(R.integer.db_version)
            );

        Cursor cursor = sqlite.selectQuery("SELECT * FROM reportes");

        Bitmap cilRec = null;
        Bitmap cilEnt = null;

        registros = new ArrayList<>();
        if(cursor != null){
            if(cursor.moveToFirst()){
                if(FileManager.isExternalStorageReadable()){
                    Log.w(TAG,cursor.getString(10));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;

                    cilRec = BitmapFactory.decodeFile(cursor.getString(10),options);
                    cilEnt = BitmapFactory.decodeFile(cursor.getString(12),options);
                }

                do{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    cilRec.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String cilRecEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                    cilEnt.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    b = baos.toByteArray();
                    String cilEntEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                    registros.add(
                        new Registro(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cilRecEncoded,
                            cursor.getString(11),
                            cilEntEncoded,
                            cursor.getString(13),
                            cursor.getString(14),
                            cursor.getString(15),
                            cursor.getString(16),
                            cursor.getString(17),
                            cursor.getString(18),
                            cursor.getString(19)
                        )
                    );
                }while(cursor.moveToNext());
            }
        }

        ListView listView = (ListView)getView().findViewById(R.id.listView);
        ListaRegistrosAdapter adapter = new ListaRegistrosAdapter(getActivity(),registros);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListaRegistrosActivity activity = (ListaRegistrosActivity) getActivity();
        activity.setResultNFinish(registros.get(position));
    }

}
