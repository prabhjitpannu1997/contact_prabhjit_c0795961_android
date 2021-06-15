package com.example.contact_prabhjit_c0795961_android;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    List<Contact> contactList;
    DatabaseHelper databaseHelper;
    TextView contactOutput;
    EditText search;
    ListView contactListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactOutput = findViewById(R.id.number_of_contacts);
        this.setTitle("list");
        contactList = new ArrayList<>();
        search = findViewById(R.id.search_list);
        databaseHelper = new DatabaseHelper(this);
        contactListView = findViewById(R.id.lv_contact);

        loadEmployees();
    }

    private void loadEmployees() {
        String sql = "SELECT * FROM employee";
        Cursor cursor = databaseHelper.getAllContacts();

        if (cursor.moveToFirst()) {
            do {
                // create an employee instance
                contactList.add(new Contact(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        contactOutput.setText(contactList.size()+" - CONTACTS");
        // create an adapter to display the employees
        ContactAdapter contactAdapter = new ContactAdapter(this,
                R.layout.list_layout_contact,
                contactList,
                databaseHelper);
        contactListView.setAdapter(contactAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}