package com.example.contactapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_ID = "contactId";

    private TextView txtName, txtPhone;
    private ImageView imgProfile;
    private ImageButton btnCall;
    private Button btnEdit, btnDelete;
    private DBHelper dbHelper;
    private Contact contact;

    private final ActivityResultLauncher<Intent> editContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    int contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);
                    contact = dbHelper.getContactById(contactId);
                    displayContactDetails();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        imgProfile = findViewById(R.id.imgProfile);
        btnCall = findViewById(R.id.btnCall);
        btnEdit = findViewById(R.id.btnEdit); // Initialize btnEdit
        btnDelete = findViewById(R.id.btnDelete); // Initialize btnDelete

        dbHelper = new DBHelper(this);

        int contactId = getIntent().getIntExtra(EXTRA_CONTACT_ID, -1);
        contact = dbHelper.getContactById(contactId);

        if (contact != null) {
            displayContactDetails();
        } else {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCall.setOnClickListener(v -> makePhoneCall());
        btnEdit.setOnClickListener(v -> editContact());
        btnDelete.setOnClickListener(v -> deleteContact());
    }

    private void displayContactDetails() {
        txtName.setText(contact.getName());
        txtPhone.setText(contact.getPhone() != null ? contact.getPhone() : "No phone available");

        if (contact.getProfilePictureUri() != null && !contact.getProfilePictureUri().isEmpty()) {
            Uri profileUri = Uri.parse(contact.getProfilePictureUri());
            Glide.with(this)
                    .load(profileUri)
                    .placeholder(R.drawable.ic_default_contact)
                    .error(R.drawable.ic_default_contact)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_default_contact);
        }
    }

    private void makePhoneCall() {
        if (contact.getPhone() != null && !contact.getPhone().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contact.getPhone()));

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        } else {
            Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
        }
    }

    private void editContact() {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contact.getId());
        editContactLauncher.launch(intent);
    }

    private void deleteContact() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteContact(contact.getId());
                    Toast.makeText(ContactDetailActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}