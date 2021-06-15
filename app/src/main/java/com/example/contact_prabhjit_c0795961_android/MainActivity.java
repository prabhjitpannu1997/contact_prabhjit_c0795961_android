package com.example.contact_prabhjit_c0795961_android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // sqLite openHelper instance
    DatabaseHelper sqLiteDatabase;
    EditText first_name,last_name,phone,email,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        phone = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);

        // initializing the instance of sqLLite openHelper class
        sqLiteDatabase = new DatabaseHelper(this);

        findViewById(R.id.btn_add_employee).setOnClickListener(this::onClick);
        findViewById(R.id.btn_show_employee).setOnClickListener(this::onClick);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_employee:
                addEmployee();
                break;
            case R.id.btn_show_employee:
                startActivity(new Intent(this, ContactActivity.class));
                break;
        }
    }
    private void addEmployee() {
        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String homeAddress = address.getText().toString().trim();

        if (firstName.isEmpty()) {
            first_name.setError("name field cannot be empty");
            first_name.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            last_name.setError("salary cannot be empty");
            last_name.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            phone.setError("salary cannot be empty");
            phone.requestFocus();
            return;
        }
        if (emailAddress.isEmpty()) {
            email.setError("salary cannot be empty");
            email.requestFocus();
            return;
        }
        if (homeAddress.isEmpty()) {
            address.setError("salary cannot be empty");
            address.requestFocus();
            return;
        }

        // insert employee into database table with the help of database openHelper class
        if (sqLiteDatabase.addContact(firstName, lastName, emailAddress,phoneNumber,homeAddress))
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Contact NOT Added", Toast.LENGTH_SHORT).show();

        clearFields();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        clearFields();
    }

    private void clearFields() {
        first_name.setText("");
        last_name.setText("");
        phone.setText("");
        email.setText("");
        address.setText("");
        first_name.clearFocus();
        last_name.clearFocus();
        phone.clearFocus();
        email.clearFocus();
        address.clearFocus();
    }
}