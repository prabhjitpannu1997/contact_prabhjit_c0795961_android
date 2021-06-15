package com.example.contact_prabhjit_c0795961_android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter implements Filterable {

    private static final String TAG = "EmployeeAdapter";

    Context context;
    int layoutRes;
    DatabaseHelper sqLiteDatabase;
    List<Contact> contactList;
    List<Contact> totalContacts;

    public ContactAdapter(@NonNull Context context, int resource, List<Contact> contactList, DatabaseHelper sqLiteDatabase) {
        super(context, resource, contactList);
        this.contactList = contactList;
        this.totalContacts = contactList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.context = context;
        this.layoutRes = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = convertView;
        if (v == null) v = inflater.inflate(layoutRes, null);
        TextView firstName = v.findViewById(R.id.first_name_list);
        TextView lastName = v.findViewById(R.id.last_name_list);
        TextView phoneNumber = v.findViewById(R.id.phone_number_list);

        final Contact contact = contactList.get(position);
        firstName.setText(contact.getFirstName());
        lastName.setText(contact.getLastName());
        phoneNumber.setText(contact.getPhoneNumber());

        v.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee(contact);
            }

            private void updateEmployee(final Contact contact) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.dialog_update_contact, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText etFirstName = view.findViewById(R.id.first_name_edit);
                final EditText etLastName = view.findViewById(R.id.last_name_edit);
                final EditText etPhone = view.findViewById(R.id.phone_number_edit);
                final EditText etEmail = view.findViewById(R.id.emai_edit);
                final EditText etaddress = view.findViewById(R.id.address_edit);

                etFirstName.setText(contact.getFirstName());
                etLastName.setText(contact.getLastName());
                etPhone.setText(contact.getPhoneNumber());
                etEmail.setText(contact.getEmailAddress());
                etaddress.setText(contact.getHomeAddress());

                view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String firstName = etFirstName.getText().toString().trim();
                        String lastName = etLastName.getText().toString().trim();
                        String phone = etPhone.getText().toString().trim();
                        String email = etEmail.getText().toString().trim();
                        String addrss = etaddress.getText().toString().trim();

                        if (firstName.isEmpty()) {
                            etFirstName.setError("name field cannot be empty");
                            etFirstName.requestFocus();
                            return;
                        }
                        if (lastName.isEmpty()) {
                            etLastName.setError("name cannot be empty");
                            etLastName.requestFocus();
                            return;
                        }if (phone.isEmpty()) {
                            etPhone.setError("phone cannot be empty");
                            etPhone.requestFocus();
                            return;
                        }
                        if (email.isEmpty()) {
                            etEmail.setError("email cannot be empty");
                            etEmail.requestFocus();
                            return;
                        }if (addrss.isEmpty()) {
                            etaddress.setError("address cannot be empty");
                            etaddress.requestFocus();
                            return;
                        }

                        if (sqLiteDatabase.updateContact(contact.getId(), firstName, lastName, phone,email,addrss))
                            loadEmployees();
                        loadEmployees();
                        alertDialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(contact);
            }

            private void deleteEmployee(final Contact contact) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sqLiteDatabase.deleteContact(contact.getId()))
                            loadEmployees();
                        loadEmployees();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "The employee (" + contact.getFirstName() + ") is not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        Log.d(TAG, "getView: " + getCount());
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+contact.getPhoneNumber()));
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Message", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) context,
                                            new String[]{Manifest.permission.SEND_SMS}, 123);
                                    return;
                                }
                                Uri uri = Uri.parse("smsto:"+contact.getPhoneNumber());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.putExtra("sms_body", "Hello");
                                context.startActivity(intent);
                            }
                        })
                        .setNeutralButton("Email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"+contact.getEmailAddress()));
                                intent.putExtra(Intent.EXTRA_EMAIL, contact.getEmailAddress());
                                context.startActivity(intent);
                            }
                        }).show();
                return false;
            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    private void loadEmployees() {
        Cursor cursor = sqLiteDatabase.getAllContacts();
        contactList.clear();
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

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint==null || constraint.length()==0){
                    filterResults.count = totalContacts.size();
                    filterResults.values = totalContacts;
                }else{
                    List<Contact> resultList = new ArrayList<>();
                    String search = constraint.toString().toLowerCase();
                    for (Contact contact : totalContacts){
                        if(contact.getFirstName().contains(search) || contact.getLastName().contains(search) ){
                            resultList.add(contact);
                        }
                        filterResults.count = resultList.size();
                        filterResults.values = resultList;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactList = (List<Contact>)results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
