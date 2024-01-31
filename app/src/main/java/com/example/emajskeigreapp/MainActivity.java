package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView live;
    private Button btn;
    private StudentsDormitory uporabniki;
    private String[] events;
    private final String url = "https://emajskeigre-is.azurewebsites.net/api/v1/event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        osebe = (LinearLayout) findViewById(R.id.linearDogodki);
        osebe.removeAllViews();
        //setting live user text
        uporabniki = (StudentsDormitory) getApplication();
        String [] uporab = uporabniki.getLiveUser();

        //display events
        btn = (Button) findViewById(R.id.button3);
        prikaziDogodke(btn);
    }

    //Logout function
    public void odjava(View view){

        uporabniki.logOut();
        Toast.makeText(getApplicationContext(), "Odjavljanje...", Toast.LENGTH_SHORT).show();
        //after delay return to login view
        delay();
    }
    public void delay(){

        Handler handler = new Handler(Looper.getMainLooper());
        //After 2000ms logout
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                }
        }, 2000);
    }


    public  void prikaziDogodke(View view){
        //clear linearLayout
        osebe.removeAllViews();
        TextView naslov = new TextView(MainActivity.this);
        naslov.setText("Dogodki");
        naslov.setTextSize(36);
        naslov.setElevation(20f);
        naslov.setTypeface(naslov.getTypeface(), Typeface.BOLD);
        naslov.setTextColor(getResources().getColor(R.color.black));
        naslov.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 10, 10, 40); // Adjust the margin values as needed


        //Apply layout parameters
        naslov.setLayoutParams(layoutParams);

        osebe.addView(naslov);
        if (view != null){
            //create request for GET method from API
            JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener)
            {
                //Using ApiKey to authorize Api endpoint
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


    private final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            events = new String[response.length()];

            for (int i = 0; i < response.length(); i++){
                try {
                    //From JSON object taking data for future use
                    JSONObject object =response.getJSONObject(i);
                    String eventID = object.getString("eventID");
                    String title = object.getString("eventTitle");
                    String description = object.getString("eventDescription");
                    String eventDate = object.getString("eventDate");
                    //Creating better format of date
                    String datu = eventDate.split("T")[0].split("-")[2]+"-"+eventDate.split("T")[0].split("-")[1]+"-"+eventDate.split("T")[0].split("-")[0];

                    String rowData =(title + "\n Datum: " + datu.replace("\n",""));
                    //Setting events variable to post title of event ahead to next View
                    events[i]=eventID+" "+title;

                    //Attributes of new TextView
                    TextView rowTextView = new TextView(MainActivity.this);
                    int generatedId = View.generateViewId();
                    rowTextView.setId(generatedId);
                    rowTextView.setHint(description+"EventID:"+eventID);
                    rowTextView.setText(rowData);
                    rowTextView.setTextSize(18);
                    rowTextView.setPadding(40,40,40,40);
                    rowTextView.setElevation(20f);
                    rowTextView.setTextColor(getResources().getColor(R.color.black));
                    rowTextView.setBackgroundResource(R.drawable.textview_shadow);

                    rowTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event
                            ogledDogodkaActivity((TextView) v);
                        }
                    });
                    //Sett layoutParameters fornew TextView
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(10, 10, 10, 10); // Adjust the margin values as needed


                    //Apply layout parameters
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

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    //When Ogled prijavljenih dogodkov button is clicked
    public void checkEnrollmentActivity(View view) {
        Intent intent = new Intent(this, CheckEnrollmentsActivity.class);
        //Add events to starting activity
        intent.putExtra("EventName",events);
        startActivity(intent);
        //Animation sliding activity
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //When event TextView is clicked
    public void ogledDogodkaActivity(View view) {
        TextView clickedTextView = (TextView) view;

        //Get the text from the clicked TextView
        String clickedText = clickedTextView.getText().toString();
        //Get the hint from the clicked TextView
        CharSequence hint = clickedTextView.getHint();

        Intent intent = new Intent(this, OgledDogodka.class);

        //Put hint and text to intent
        intent.putExtra("EXTRA_TEXT_FROM_CLICKED_VIEW", clickedText);
        intent.putExtra("EXTRA_HINT_FROM_CLICKED_VIEW", hint != null ? hint.toString() : "");

        //Start the new activity
        startActivity(intent);
    }
    //On back button pressed close current Activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    //On users name and lastName clicked
    public void checkDetails(View view){
        Intent intent = new Intent(this, Profil.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}