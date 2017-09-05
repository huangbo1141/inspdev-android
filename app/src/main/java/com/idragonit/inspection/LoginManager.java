package com.idragonit.inspection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.idragonit.inspection.models.UserInfo;

/**
 * Created by CJH on 2015-06-27.
 */

public class LoginManager {

    static final String TAG = "Inspection.LoginManager";

    static final String PREFERENCE_ID = TAG + "_id";
    static final String PREFERENCE_EMAIL = TAG + "_email";
    static final String PREFERENCE_FIRSTNAME = TAG + "_firstname";
    static final String PREFERENCE_LASTNAME = TAG + "_lastname";

    public static void setUser(Context context, UserInfo user){
        try{
            SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCE_ID, user.id);
            editor.putString(PREFERENCE_EMAIL, user.email);
            editor.putString(PREFERENCE_FIRSTNAME, user.firstname);
            editor.putString(PREFERENCE_LASTNAME, user.lastname);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void logout(Context context){
        try{
            SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFERENCE_EMAIL, "");
            editor.putString(PREFERENCE_FIRSTNAME, "");
            editor.putString(PREFERENCE_LASTNAME, "");
            editor.putString(PREFERENCE_ID, "");
            editor.commit();

            AppData.USER.init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static UserInfo getUser(Context context){
        try{
            UserInfo user = new UserInfo();

            SharedPreferences pref = context.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);
            user.email = pref.getString(PREFERENCE_EMAIL, "");
            user.firstname = pref.getString(PREFERENCE_FIRSTNAME, "");
            user.lastname = pref.getString(PREFERENCE_LASTNAME, "");
            user.id = pref.getString(PREFERENCE_ID, "");

            if (user.id.length()==0)
                return null;

            return user;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
