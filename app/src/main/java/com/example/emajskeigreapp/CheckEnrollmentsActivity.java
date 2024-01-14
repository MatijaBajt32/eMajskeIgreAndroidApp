package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckEnrollmentsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView dogodki;
    private String url = "https://emajskeigre.azurewebsites.net/api/v1/enrollment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_enrollments);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        dogodki = (TextView) findViewById(R.id.dogodkiE);
    }

    public  void prikaziVpise(View view){
        dogodki.setText("Seznam vpisanih dogodkov");
        if (view != null){
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
        }
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();
            int x = 1;
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String student = object.getString("studentID");
                    if(student.matches("1")){
                        String eventId = object.getString("eventID");
                        String enrollmentDate = object.getString("enrollmentDate");
                        String enrollmentId = x+"";
                        x++;

                        data.add(enrollmentId+". "+enrollmentDate + " " + eventId );
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            dogodki.setText("");

            if(data.isEmpty()){
                dogodki.setText("Niste prijavljeni na noben dogodek.");
            }else{
                for (String row: data){
                    String currentText = dogodki.getText().toString();
                    dogodki.setText(currentText + "\n\n" + row);
                }
            }


        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };
}