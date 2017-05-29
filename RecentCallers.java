package com.plugins.recentCallers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarath DR  on 20/06/2014.
 */
public class RecentCallers extends CordovaPlugin
{


    CallbackContext callbackContext;
    private Cursor c;
    private Context context;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        this.context = cordova.getActivity().getApplicationContext();
        this.callbackContext = callbackContext;

        if( action.equals("getRecentCallers")){
            Integer limit = args.getInt(0);
            this.getRecentCallers(limit);
            return true;
        }

        return false;
    }

    public void  getRecentCallers(Integer limit )
    {
        ContentResolver resolver = this.context.getContentResolver();
        c =  resolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        JSONArray resultSet 	= new JSONArray();
        int i = 1;

        int numberIndex = c.getColumnIndex( CallLog.Calls.NUMBER );
        int nameIndex   = c.getColumnIndex( CallLog.Calls.CACHED_NAME );
        int type        = c.getColumnIndex( CallLog.Calls.TYPE );

        List<String> names = new ArrayList<String>();

        while ( c.moveToNext() && i <= limit  ) {
            if( c.getInt( type ) == CallLog.Calls.INCOMING_TYPE ){

                String name     = c.getString( nameIndex );
                String number   = c.getString( numberIndex );

                if(name!=null && ( names.isEmpty() ||  !names.contains( number ) )  && number.length() > 7 ){
                    try {
                        JSONObject contactObject = new JSONObject();
                        contactObject.put("name", name);
                        contactObject.put("phone", number);
                        resultSet.put(contactObject);
                        names.add(number);
                        i++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        c.close();
        callbackContext.success(resultSet);
    }
}
