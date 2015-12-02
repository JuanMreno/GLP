package com.aplications.glp.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.aplications.glp.objetos.SessionProfile;
import com.google.gson.Gson;

/**
 * Created by juan.moreno.indesap on 11/03/2015.
 */
public class SharedPreferencesManager {
    static public String SESION_PROFILE = "SESION_PROFILE";

    SharedPreferences sharedPreferences;
    Context context;

    String type;

    String SEND_POSITION_MODE = "SendPositionBusy";

    public SharedPreferencesManager(Context context,String type)
    {
        sharedPreferences = context.getSharedPreferences(type,Context.MODE_PRIVATE);
        this.type = type;
    }

    public void setBoolenState(boolean estado)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putBoolean(this.type, estado);
        editor.commit();
    }

    public boolean getBoolenState()
    {
        if(sharedPreferences.contains(this.type))
            return sharedPreferences.getBoolean(this.type,true);
        else
            return true;
    }

    public void setSesionProfile(SessionProfile sesion)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putString(SESION_PROFILE, new Gson().toJson(sesion));
        editor.commit();
    }

    public SessionProfile getSesionData()
    {
        if(sharedPreferences.contains(SESION_PROFILE))
            return new Gson().fromJson(sharedPreferences.getString(SESION_PROFILE,""),SessionProfile.class);
        else
            return null;
    }

    public void removeSesionProfile()
    {
        if(sharedPreferences.contains(SESION_PROFILE))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Log.w("Sesion","OFF");
        }

    }

    public boolean isSesionProfileSet()
    {
        if(sharedPreferences.contains(SESION_PROFILE))
            return true;
        else
            return false;
    }

    public void setString(String s){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString(this.type, s);
        editor.apply();
    }

    public String getString(){
        if(sharedPreferences.contains(this.type))
            return sharedPreferences.getString(this.type,"null");
        else
            return "null";
    }

}
