package com.aplications.glp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aplications.glp.ListaRegistrosActivity;
import com.aplications.glp.R;
import com.aplications.glp.objetos.Registro;

import java.util.ArrayList;

/**
 * Created by ´ñ´b on 25/11/2015.
 */
public class ListaRegistrosAdapter extends BaseAdapter{

    private final Context context;
    ArrayList<Registro> registros;

    public ListaRegistrosAdapter(Context context, ArrayList<Registro> registros) {
        this.context = context;
        this.registros = registros;
    }

    @Override
    public int getCount() {
        return registros.size();
    }

    @Override
    public Object getItem(int position) {
        return registros.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(registros.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_list_registro, null);
        }

        Registro registro = registros.get(position);

        ImageView imgRecibido   = (ImageView)convertView.findViewById(R.id.imgRecibido);
        ImageView imgEntregado  = (ImageView)convertView.findViewById(R.id.imgEntregado);
        TextView txtFecha       = (TextView)convertView.findViewById(R.id.txtFecha);

        byte[] decodedByte = Base64.decode(registro.getBmpCilRecCod(), 0);
        imgRecibido.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));

        decodedByte = Base64.decode(registro.getBmpCilEnCod(), 0);
        imgEntregado.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));

        txtFecha.setText(registro.getFecha() + " " + registro.getHora());

        return convertView;
    }
}
