package com.aplications.glp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aplications.glp.ListaRegistrosActivity;
import com.aplications.glp.R;
import com.aplications.glp.objetos.Registro;

import java.lang.ref.WeakReference;
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

        String[] paramsRec = new String[1];
        paramsRec[0] = registros.get(position).getBmpCilRecCod();
        new BitmapWorkerTask(imgRecibido).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,paramsRec);

        String[] paramsEnt = new String[1];
        paramsEnt[0] = registros.get(position).getBmpCilEnCod();
        new BitmapWorkerTask(imgEntregado).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,paramsEnt);

        txtFecha.setText(registro.getFecha() + " " + registro.getHora());
        return convertView;
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
        options.inSampleSize = calculateInSampleSize(options, options.outWidth/40, options.outWidth/40);
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
