package com.example.contactapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditContactActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 123;

    private EditText editTextName;
    private EditText editTextPhone;
    private Button btnUpdate;
    private ImageView imgProfile;

    private DBHelper dbHelper;
    private Contact contact;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (imageUri != null) {
                        imgProfile.setImageURI(imageUri);
                        dbHelper.updateContactPicture(contact.getId(), imageUri.toString());
                    }
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        loadProfileImage(selectedImageUri);
                        dbHelper.updateContactPicture(contact.getId(), selectedImageUri.toString());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgProfile = findViewById(R.id.imgProfile);

        dbHelper = new DBHelper(this);

        int contactId = getIntent().getIntExtra("contactId", -1);
        contact = dbHelper.getContactById(contactId);

        if (contact != null) {
            editTextName.setText(contact.getName());
            editTextPhone.setText(contact.getPhone());

            btnUpdate.setOnClickListener(v -> updateContact());

            imgProfile.setOnClickListener(this::onProfileImageClick);

            if (contact.getProfilePictureUri() != null) {
                loadProfileImage(Uri.parse(contact.getProfilePictureUri()));
            } else {
                imgProfile.setImageResource(R.drawable.ic_default_contact);
            }
        } else {
            Toast.makeText(this, "Invalid contact data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateContact() {
        String updatedName = editTextName.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();

        if (updatedName.isEmpty() || updatedPhone.isEmpty()) {
            Toast.makeText(this, "Name and phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.getRealm().executeTransaction(realm -> {
            contact.setName(updatedName);
            contact.setPhone(updatedPhone);
        });

        Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    public void onProfileImageClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");

        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                launchCamera();
            } else if (which == 1) {
                openGallery();
            }
        });

        builder.show();
    }

    private void launchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(
                            this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            photoFile
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    takePictureLauncher.launch(takePictureIntent);
                } else {
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e("EditContactActivity", "Error creating image file", e);
            return null;
        }
    }

    private void loadProfileImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.ic_default_contact)
                .error(R.drawable.ic_default_contact)
                .into(imgProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
