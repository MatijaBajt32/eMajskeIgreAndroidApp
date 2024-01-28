package com.example.emajskeigreapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;


public class Registracija extends AppCompatActivity {


    private String StudentsDorm = "";
    private EditText Ime;
    private EditText Priimek;
    private EditText VpisnaStevilka;
    private EditText Geslo;
    private EditText PonovnoGeslo;
    private boolean Ime1;
    private boolean Priimek1;
    private boolean VpisnaStevilka1;
    private boolean Geslo1;
    private boolean PonovnoGeslo1;
    private boolean Datum1;
    private EditText Datum;
    private StudentsDormitory st;
    private String [] data;
    private final String url = "https://emajskeigre-is.azurewebsites.net/api/v1/dormitory";
    private final String url1 = "https://emajskeigre-is.azurewebsites.net/api/v1/student";
    private RequestQueue requestQueue;
    private HashMap<String,String[]> students;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);
        //init variables
        st = (StudentsDormitory) getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        data = st.getDormitory();
        Ime=((EditText) findViewById(R.id.nameRegistration));
        Priimek=((EditText) findViewById(R.id.lastNameRegistration));
        VpisnaStevilka=((EditText) findViewById(R.id.studentsId));
        Geslo=((EditText) findViewById(R.id.password));
        PonovnoGeslo=((EditText) findViewById(R.id.passwordRepeat));
        Datum = (EditText) findViewById(R.id.datumRojstva);
        students= st.getUsers();

        //Setting eventListeners to all of the fields in registration activity
        eventListeners();

        Spinner sp = (Spinner) findViewById(R.id.dormitorys);
        String [] dorms = new String[data.length];
        int i=0;
        //Extracting index of dorm
        for (String dorm:data) {
            String [] o = dorm.split(" ");
            dorms[i]=o[1]+" "+o[2];
            i++;
        }
        //Setting up Spinner with existing dorms
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dorms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        //On selected item get selected item and his index
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String [] x = data[position].split(" ");
                StudentsDorm = x[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }
    //all eventListeners
    private void eventListeners(){
        Ime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();
                //Če je ime krajše od 2 pomeni da to ni celotno ime zato izpiši error
                if(inputText.length()<2){
                    Ime.setError("Vnesite svoje ime" );
                    Ime1=false;
                }
                else Ime1=true;
            }
        });
        Priimek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();
                //Če je ime krajše od 2 pomeni da to ni celotno ime zato izpiši error
                if(inputText.length()<2){
                    Priimek.setError("Vnesite svoj priimek" );
                    Priimek1=false;
                }
                else Priimek1=true;
            }
        });
        VpisnaStevilka.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();

                String requiredPrefix = "[0-9]{8}";
                if (!inputText.matches(requiredPrefix)) {
                    VpisnaStevilka.setError("Vpisna številka mora biti tako : 63210018" );
                    VpisnaStevilka1=false;
                }else VpisnaStevilka1=true;
            }
        });
        Geslo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isValidPassword(editable.toString())) {
                    Geslo.setError("Geslo mora biti vsklajeno z pravili: dologo vsaj 7 znakov, vsebovati vsaj 1 veliko črko in 1 številko in en poseben znak");
                    Geslo1=false;
                }else{
                    Geslo1=true;
                }
            }

            private boolean isValidPassword(String password) {
                if (password.length() < 6) return false;

                boolean hasUppercase = !password.equals(password.toLowerCase());
                boolean hasNumber = password.matches(".*\\d.*");
                boolean hasSpecialChar = !password.matches("[.,!#$%&/()=?*]*");

                return hasUppercase && hasNumber && hasSpecialChar;
            }
        });
        PonovnoGeslo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(Geslo.getText().toString())) {
                    PonovnoGeslo.setError("Gesli se ne ujemata");
                    PonovnoGeslo1=false;
                }else{
                    PonovnoGeslo1=true;
                }
            }
        });

        Datum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isValidFormat("dd-MM-yyyy", Datum.getText().toString())) {
                    Datum1 = true;
                    // Proceed with your logic here
                } else {
                    // Date format is not valid or the date is invalid
                    Datum1 = false;
                    Datum.setError("Neveljaven datum. Prosim uporabi DD-MM-YYYY format");
                }
            }
        });
    }
    //Concluding registration form
    public void registracija(View view){

        students = st.getUsers();
        //Checking if all of the fields were correctly filled
        if(Ime1 && Priimek1 && VpisnaStevilka1 && Geslo1 && PonovnoGeslo1 && Datum1){
            //Checking if EnrollmentNumber already exists in database
            if(students.containsKey(VpisnaStevilka.getText().toString())){
                Toast.makeText(getApplicationContext(), "Vpisali ste napačno vpisno številko", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Uspešna registracija", Toast.LENGTH_SHORT).show();
                //Inserting student to database
                sendStudent();
                delay(true);
            }
        }else Toast.makeText(getApplicationContext(), "Neuspešna registracija", Toast.LENGTH_SHORT).show();
    }

    public void sendStudent(){
        try {
            //Creating JSON object
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("enrollmentNumber", VpisnaStevilka.getText().toString());
            jsonBody.put("firstName", Ime.getText().toString());
            jsonBody.put("lastName", Priimek.getText().toString());
            jsonBody.put("passwordAndroid", Geslo.getText().toString());
            String [] dtum = Datum.getText().toString().split("-");
            String endDatum = dtum[2]+"-"+dtum[1]+"-"+dtum[0]+"T00:00:00";
            jsonBody.put("birthDate", endDatum);
            jsonBody.put("dormitoryID",Integer.parseInt(StudentsDorm));

            final String mRequestBody = jsonBody.toString();

            Printer printer = new LogPrinter(Log.DEBUG, "MyTag");
            printer.println(mRequestBody);
            //strinRequest for POST Method
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
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
    public void delay(boolean next){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Checking if fields were filled correctly and closing registration other wise
                //alert user
                if(next) {
                    st.getStudents();
                    prikaziAuth();
                }else
                    Toast.makeText(getApplicationContext(), "Poglejte da ste vpisali podatke prav", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }
    //Showing Login form
    public void prikaziAuth() {
        Intent intent = new Intent(this, Autentication.class);
        startActivity(intent);
    }
    //Function to check if Date format is valid to our standard
    private boolean isValidFormat(String format, String value) {
        String datePattern = "^\\d{2}-\\d{2}-\\d{4}$";
        if (!value.matches(datePattern)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}