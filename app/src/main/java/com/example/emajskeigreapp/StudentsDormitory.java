package com.example.emajskeigreapp;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
//Global class sharing Students, dorms and liveUser alongside with get,set methods
public class StudentsDormitory extends Application {
    private final HashMap<String, String> domovi = new HashMap<>();
    private final HashMap<String,String[]> users = new HashMap<>();
    private String[] liveUser;
    private RequestQueue requestQueue;
    private RequestQueue requestQueueStudent;
    private final String url = "https://emajskeigre-is.azurewebsites.net/api/v1/dormitory";
    private final String urlStud = "https://emajskeigre-is.azurewebsites.net/api/v1/student";

    public void setUsers(String id, String[] data){
        users.put(id,data);
    }

    public String [] getDormitory() {
        String [] result = new String[domovi.size()];
        int i = 0;
        for (String item: domovi.keySet()) {
            result[i]= item +" "+domovi.get(item);
            i++;
        }
        return result;
    }
    public HashMap<String,String[]> getUsers() {
        HashMap<String,String[]> result = new HashMap<>();
        for (String item: users.keySet()) {
            result.put(item,users.get(item));
        }
        return result;
    }
    public void setLiveUser(String[] user){
        liveUser= user;
    }
    public String[] getLiveUser(){
        return liveUser;
    }
    public void logOut(){
        liveUser=new String[6];
    }

    @Override
    public void onCreate() {
        super.onCreate();
        liveUser=new String[6];

        requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener)
            {
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    return params;
                }
            };
            requestQueue.add(request);
        getStudents();

    }

    public void getStudents(){
        requestQueueStudent = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest requestStudent = new JsonArrayRequest(urlStud, jsonArrayListenerStudent, errorListener)
        {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "SecretKey");
                return params;
            }
        };
        requestQueueStudent.add(requestStudent);
    }
    private final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String dormitoryID = object.getString("dormitoryID");
                    String dormitoryTitle = object.getString("dormitoryTitle");
                    domovi.put(dormitoryID,dormitoryTitle);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
        }

    };
    private final Response.Listener<JSONArray> jsonArrayListenerStudent = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String enrollNum = object.getString("enrollmentNumber");
                    if(enrollNum!=null) {
                        String studentID = object.getString("studentID");
                        String firstName = object.getString("firstName");
                        String lastName = object.getString("lastName");
                        String birthDate = object.getString("birthDate");
                        String dormitoryID = object.getString("dormitoryID");
                        String passwordAndroid = object.getString("passwordAndroid");
                        String[] u = new String[]{studentID, passwordAndroid, firstName, lastName, birthDate, dormitoryID};
                        setUsers(enrollNum, u);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
        }

    };

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };
}
