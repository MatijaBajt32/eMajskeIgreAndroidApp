package com.example.emajskeigreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class Autentication extends AppCompatActivity {

    private EditText vpis ;
    private EditText geslo;
    private StudentsDormitory users;
    private HashMap<String,String[]> uporabniki;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentication);
        vpis=(EditText) findViewById(R.id.enrollmentNum);
        geslo=(EditText) findViewById(R.id.password);
        users = (StudentsDormitory) getApplication();

    }
    public void prijava(View view){
        users.getStudents();
        uporabniki = users.getUsers();
        String key =vpis.getText().toString();
        if(uporabniki.containsKey(key)){
            String[] x= uporabniki.get(key);
            if(x[1].equals(geslo.getText().toString())){
                String[] user= new String[6];
                user[0]=key;
                System.arraycopy(x, 0, user, 1, user.length - 1);
                users.setLiveUser(user);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Napačno geslo", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(getApplicationContext(), "Uporabnik ne obstaja", Toast.LENGTH_SHORT).show();

        }

    }

    public void prikaziRegistracijo(View view) {
        Intent intent = new Intent(this, Registracija.class);
        startActivity(intent);
    }
}