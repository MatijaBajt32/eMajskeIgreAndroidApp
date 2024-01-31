package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Profil extends AppCompatActivity {

    private String [] student;
    private StudentsDormitory st;
    private EditText ime;
    private EditText priimek;
    private EditText vpisnaSt;
    private EditText datumR;
    private EditText password;
    private Button btn;

    private String imeS;
    private String priimekS;
    private String vpisnaS;
    private String datumS;
    private String passwordS;

    private String url= "https://emajskeigre-is.azurewebsites.net/api/v1/student";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        st = (StudentsDormitory) getApplication();
        student = st.getLiveUser();
        url = url+"/"+student[1];
        ime = (EditText) findViewById(R.id.nameP);
        priimek = (EditText) findViewById(R.id.lastNameP);
        vpisnaSt = (EditText) findViewById(R.id.studentsIdP);
        datumR = (EditText) findViewById(R.id.datumRojstvaP);
        password = (EditText) findViewById(R.id.passwordP);
        btn = (Button) findViewById(R.id.saveBtn);
        ime.setText(student[3]);
        imeS = student[3];
        priimek.setText(student[4]);
        priimekS = student[4];
        vpisnaSt.setText(student[0]);
        vpisnaS = student[0];
        datumR.setText(student[5].split("T")[0].split("-")[2]+"-"+student[5].split("T")[0].split("-")[1]+"-"+student[5].split("T")[0].split("-")[0]);
        datumS = student[5].split("T")[0].split("-")[2]+"-"+student[5].split("T")[0].split("-")[1]+"-"+student[5].split("T")[0].split("-")[0];
        password.setText(student[2]);
        passwordS = student[2];

        addListeners();

    }

    private void addListeners(){
        ime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!ime.getText().toString().equals(imeS)) {
                    btn.setText("Shrani");
                }else btn.setText("Nazaj");

            }
        });
        priimek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!priimek.getText().toString().equals(priimekS)) {
                    btn.setText("Shrani");
                }else btn.setText("Nazaj");

            }
        });
        datumR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!datumR.getText().toString().equals(datumS)) {
                    btn.setText("Shrani");
                }else btn.setText("Nazaj");

            }
        });
        vpisnaSt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!vpisnaSt.getText().toString().equals(vpisnaS)) {
                    btn.setText("Shrani");
                }else btn.setText("Nazaj");

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!password.getText().toString().equals(passwordS)) {
                    btn.setText("Shrani");
                }else btn.setText("Nazaj");

            }
        });
    }
    public void clickedBtn(View view){
        if(btn.getText().toString().equals("Shrani")){
            updateStudent();
        }else{
            finish();
        }
    }

    private void updateStudent(){
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("enrollmentNumber", vpisnaSt.getText().toString());
            jsonBody.put("firstName", ime.getText().toString());
            jsonBody.put("lastName", priimek.getText().toString());
            jsonBody.put("passwordAndroid", password.getText().toString());
            jsonBody.put("birthDate", datumR.getText().toString());

            final String mRequestBody = jsonBody.toString();

            Printer printer = new LogPrinter(Log.DEBUG, "MyTag");
            printer.println(mRequestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
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
                        printer.println(responseString);
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
}