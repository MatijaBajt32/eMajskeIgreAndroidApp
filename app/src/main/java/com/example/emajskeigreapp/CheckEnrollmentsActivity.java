package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class CheckEnrollmentsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RequestQueue requestQueueDelete;
    private String [] student;
    private StudentsDormitory st;
    private Button btn;
    private String[] eventName;
    private LinearLayout dogodki;
    private final String url = "https://emajskeigre-is.azurewebsites.net/api/v1/enrollment";

    private Intent intentAuth = new Intent(this, Autentication.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_enrollments);
        //init variables

        Intent intent = getIntent();

        eventName = intent.getStringArrayExtra("EventName");

        btn = findViewById(R.id.buttonOdjava);
        st = (StudentsDormitory) getApplication();
        student = st.getLiveUser();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueueDelete = Volley.newRequestQueue(getApplicationContext());
        dogodki = (LinearLayout) findViewById(R.id.lyDogodki);
        dogodki.removeAllViews();
        //show enrollments of the live user
        prikaziVpise(btn);
    }

    //Getting enrollments from database and showing only live user's
    public  void prikaziVpise(View view){
        dogodki.removeAllViews();
        TextView naslov = new TextView(CheckEnrollmentsActivity.this);
        naslov.setText("Moji dogodki");
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
        dogodki.addView(naslov);
        lookDogodek(view);
    }

    public void lookDogodek(View view){
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


    //When Enrollment is clicked delete it from database
    public void deleteDogodek(View view) {
        TextView clickedTextView = (TextView) view;

        CharSequence hint = clickedTextView.getHint();

        //Appending enrollmentID to url
        String deleteUrl = url + "/" + hint;

        //stringRequest for DELETE Method for enrollment
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


    private final Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            String[] ids =new String[eventName.length];
            String[] names = new String[eventName.length];
            for (int i = 0; i < eventName.length; i++) {
                String event="";
                ids[i] = eventName[i].split(" ")[0];
                for (int j = 1; j < eventName[i].split(" ").length; j++) {
                    event=event+" "+ eventName[i].split(" ")[j];
                }
                names[i]= event.trim();

            }
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String studentID = object.getString("studentID");
                    if(studentID.equals(student[1])){
                        String enrollmentID = object.getString("enrollmentID");
                        String eventID = object.getString("eventID");
                        String enrollmentDate = object.getString("enrollmentDate");

                        String datu = enrollmentDate.split("T")[0].split("-")[2]+"-"+enrollmentDate.split("T")[0].split("-")[1]+"-"+enrollmentDate.split("T")[0].split("-")[0];
                        String ura = enrollmentDate.split("T")[1].split(":")[0]+":"+enrollmentDate.split("T")[1].split(":")[1];

                        String name = "";
                        for (int j = 0; j < ids.length; j++) {
                            if(eventID.equals(ids[j])){
                                name = names[j];
                                break;
                            }
                        }
                        String rowData =(name+"\n "+datu+" ob "+ura);

                        TextView rowTextView = new TextView(CheckEnrollmentsActivity.this);
                        int generatedId = View.generateViewId();
                        rowTextView.setId(generatedId);
                        rowTextView.setHint(enrollmentID);
                        rowTextView.setText(rowData);
                        rowTextView.setTextSize(18);
                        rowTextView.setPadding(40,40,40,40);

                        rowTextView.setTextColor(getResources().getColor(R.color.black));
                        rowTextView.setBackgroundResource(R.drawable.textview_shadow);

                        rowTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle the click event
                                deleteDogodek((TextView) v);
                            }
                        });

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(10, 10, 10, 10); // Adjust the margin values as needed


                        // Apply layout parameters
                        rowTextView.setLayoutParams(layoutParams);

                        // Add the TextView to the layout
                        dogodki.addView(rowTextView);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            if (dogodki.getChildCount() == 1) {
                TextView rowTextView = new TextView(CheckEnrollmentsActivity.this);
                rowTextView.setText("Niste prijavljeni na noben dogodek.");
                rowTextView.setTextSize(18);
                rowTextView.setPadding(40,40,40,40);
                rowTextView.setTextColor(getResources().getColor(R.color.black));

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

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };
    public void MainMenu(View view){
        finish();
    }
    public void checkDetails(View view){
        Intent intent = new Intent(this, Profil.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void odjava(View view){

        st.logOut();
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
                startActivity(intentAuth);
            }
        }, 2000);
    }
}