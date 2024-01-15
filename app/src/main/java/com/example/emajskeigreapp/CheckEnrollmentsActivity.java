package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckEnrollmentsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RequestQueue requestQueueDelete;

    private LinearLayout dogodki;
    private String url = "https://emajskeigre.azurewebsites.net/api/v1/enrollment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_enrollments);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueueDelete = Volley.newRequestQueue(getApplicationContext());
        dogodki = (LinearLayout) findViewById(R.id.lyDogodki);
        dogodki.removeAllViews();
    }

    public  void prikaziVpise(View view){
        dogodki.removeAllViews();
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



    public void deleteDogodek(View view) {
        TextView clickedTextView = (TextView) view;

        CharSequence hint = clickedTextView.getHint();

        String deleteUrl = url + "/" + hint; // Append enrollmentID to the URL

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("LOG_VOLLEY", "Delete response: " + response);
                // Handle the response if needed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_VOLLEY", "Delete error: " + error.toString());
                // Handle the error if needed
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ApiKey", "SecretKey");
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(stringRequest);
        dogodki.removeView(view);
    }


    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String enrollmentID = object.getString("enrollmentID");
                    String eventID = object.getString("eventID");
                    String enrollmentDate = object.getString("enrollmentDate");

                    String rowData =(enrollmentID+" "+"Dogodek z IDjem: "+eventID+"\n Datum začetka: "+enrollmentDate);

                    TextView rowTextView = new TextView(CheckEnrollmentsActivity.this);
                    int generatedId = View.generateViewId();
                    rowTextView.setId(generatedId);
                    rowTextView.setHint(enrollmentID);
                    rowTextView.setText(rowData);
                    rowTextView.setTextSize(18);
                    rowTextView.setPadding(40,40,40,40);
                    rowTextView.setTextColor(getResources().getColor(R.color.white));

                    rowTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event
                            deleteDogodek((TextView) v);
                        }
                    });

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(10, 10, 10, 10); // Adjust the margin values as needed


                    // Apply layout parameters
                    rowTextView.setLayoutParams(layoutParams);

                    // Add the TextView to the layout
                    dogodki.addView(rowTextView);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            if (dogodki.getChildCount() == 0) {
                TextView rowTextView = new TextView(CheckEnrollmentsActivity.this);
                rowTextView.setText("Niste prijavljeni na noben dogodek.");
                rowTextView.setTextSize(18);
                rowTextView.setPadding(40,40,40,40);
                rowTextView.setTextColor(getResources().getColor(R.color.white));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(10, 10, 10, 10); // Adjust the margin values as needed

                rowTextView.setLayoutParams(layoutParams);
                dogodki.addView(rowTextView);
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