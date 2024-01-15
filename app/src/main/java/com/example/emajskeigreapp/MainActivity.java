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

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private LinearLayout osebe;
    private String url = "https://emajskeigre.azurewebsites.net/api/v1/event";

    public static final String EXTRA_MESSAGE = "com.example.emajskeigreapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        osebe = (LinearLayout) findViewById(R.id.linearDogodki);
        osebe.removeAllViews();
    }

    public  void prikaziDogodke(View view){
        osebe.removeAllViews();
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

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String eventID = object.getString("eventID");
                    String title = object.getString("eventTitle");
                    String description = object.getString("eventDescription");
                    String eventDate = object.getString("eventDate");

                    String rowData =(eventID+". "+title + " " + eventDate);

                    TextView rowTextView = new TextView(MainActivity.this);
                    int generatedId = View.generateViewId();
                    rowTextView.setId(generatedId);
                    rowTextView.setHint(description);
                    rowTextView.setText(rowData);
                    rowTextView.setTextSize(18);
                    rowTextView.setPadding(40,40,40,40);
                    rowTextView.setTextColor(getResources().getColor(R.color.white));

                    rowTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event
                            ogledDogodkaActivity((TextView) v);
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
                    osebe.addView(rowTextView);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

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


    public void checkEnrollmentActivity(View view) {
        Intent intent = new Intent(this, CheckEnrollmentsActivity.class);
        startActivity(intent);
    }

    public void ogledDogodkaActivity(View view) {
        TextView clickedTextView = (TextView) view;

        // Get the text from the clicked TextView
        String clickedText = clickedTextView.getText().toString();

        CharSequence hint = clickedTextView.getHint();

        // Create an intent to start the new activity
        Intent intent = new Intent(this, OgledDogodka.class);

        // Put the text as an extra in the intent
        intent.putExtra("EXTRA_TEXT_FROM_CLICKED_VIEW", clickedText);
        intent.putExtra("EXTRA_HINT_FROM_CLICKED_VIEW", hint != null ? hint.toString() : "");

        // Start the new activity
        startActivity(intent);
    }
}