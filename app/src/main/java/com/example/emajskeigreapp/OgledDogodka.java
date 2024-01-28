package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class OgledDogodka extends AppCompatActivity {

    private String id;
    private TextView status;
    private RequestQueue requestQueue;
    private String formattedDate;
    private String [] student;
    private StudentsDormitory st;
    private final String url = "https://emajskeigre-is.azurewebsites.net/api/v1/enrollment";
    private TextView upr;
    private String naslov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogled_dogodka);
        st = (StudentsDormitory) getApplication();
        student = st.getLiveUser();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        status = (TextView) findViewById(R.id.status1);

        upr = (TextView) findViewById(R.id.trenutniUporabnik);
        upr.setText(student[3]+" "+student[4]);

        // Retrieve the data using the key
        Intent intent = getIntent();
        String textFromClickedView = intent.getStringExtra("EXTRA_TEXT_FROM_CLICKED_VIEW");
        String hintFromClickedView = intent.getStringExtra("EXTRA_HINT_FROM_CLICKED_VIEW");

        String [] podatki = textFromClickedView.split(" ");
        naslov = "";
        String datum = "";
        for (int i = 1; i < podatki.length; i++) {
            if(podatki[i].equals("Datum:")){
                datum = podatki[i+1];
                break;
            }else if(!podatki[i].equals("Datum:")){
                naslov=naslov+" "+podatki[i];
            }
        }


        id =podatki[0].substring(0,1);

        int index = hintFromClickedView.toLowerCase().indexOf("Nagrada:".toLowerCase());
        String opis = hintFromClickedView.substring(0,index);
        hintFromClickedView =hintFromClickedView.substring(index+"Nagrada: ".length());
        String nagrada = hintFromClickedView;

        TextView ime= (TextView) findViewById(R.id.teImeDogodka);
        ime.setText(naslov);
        TextView opisD= (TextView) findViewById(R.id.teOpisDogodka);
        opisD.setText("Opis: "+opis);
        TextView datumD= (TextView) findViewById(R.id.teDatumDogodka);
        datumD.setText("Datum dogodka: "+datum);
        TextView nagradaD= (TextView) findViewById(R.id.teNagrada);
        nagradaD.setText("Nagrada: "+nagrada);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        formattedDate = dateFormat.format(today);

    }
    public static boolean containsOnlyCharacters(String str) {
        return str.matches("[a-zA-ZščžŠČŽ]+");
    }
    public void VnosDogodka(View view) {
        Toast.makeText(getApplicationContext(), "Prijavljeni ste na dogodek: "+naslov, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("enrollmentDate", formattedDate);
            jsonBody.put("eventID", id);
            jsonBody.put("studentID", student[1]);

            final String mRequestBody = jsonBody.toString();



            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return mRequestBody == null ? null : mRequestBody.getBytes(StandardCharsets.UTF_8);
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    params.put("Content-Type", "application/json");
                    return params;
                }

            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void MainMenu(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void nazajNaP(View view){
        finish();
    }
}