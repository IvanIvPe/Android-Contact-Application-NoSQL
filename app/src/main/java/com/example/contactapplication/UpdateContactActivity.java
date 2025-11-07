package com.example.contactapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateContactActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";

    private EditText editTextName;
    private EditText editTextPhone;

    private Contact contact;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DBHelper(this);

        int contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);
        contact = dbHelper.getContactById(contactId);

        if (contact != null) {
            editTextName.setText(contact.getName());
            editTextPhone.setText(contact.getPhone());

            btnUpdate.setOnClickListener(v -> updateContact());
        } else {
            Toast.makeText(this, "Invalid contact data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateContact() {
        String updatedName = editTextName.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();

        if (!updatedName.isEmpty() && !updatedPhone.isEmpty()) {
            contact.setName(updatedName);
            contact.setPhone(updatedPhone);

            dbHelper.updateContact(contact);

            Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Name and phone cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
