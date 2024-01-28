package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private TextView thisUser ;
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
        thisUser=(TextView)findViewById(R.id.trenutniUporabnik);
        thisUser.setText(student[3]+" "+student[4]);
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
            int index = 1;
            int barva = 0;
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
                        String rowData =(index+". "+"Dogodek : "+name+"\n Datum začetka: "+datu+"\n Ob "+ura);
                        index++;
                        TextView rowTextView = new TextView(CheckEnrollmentsActivity.this);
                        int generatedId = View.generateViewId();
                        rowTextView.setId(generatedId);
                        rowTextView.setHint(enrollmentID);
                        rowTextView.setText(rowData);
                        rowTextView.setTextSize(18);
                        rowTextView.setPadding(20,20,20,20);

                        rowTextView.setTextColor(getResources().getColor(R.color.black));
                        switch (barva){
                            case 0:
                                rowTextView.setBackgroundResource(R.drawable.rounded_blue);
                                barva++;
                                break;
                            case 1:
                                rowTextView.setBackgroundResource(R.drawable.rounded_green);
                                barva++;
                                break;
                            case 2:
                                rowTextView.setBackgroundResource(R.drawable.rounded_orange);
                                barva=0;
                                break;
                        }

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
                    }
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
}