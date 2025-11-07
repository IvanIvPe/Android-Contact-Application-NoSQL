package com.example.contactapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContacts;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private DBHelper dbHelper;

    private final ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshContactList();
                    Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> editContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshContactList();
                    Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        contactList = dbHelper.getAllContacts();

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        contactAdapter = new ContactAdapter(this, contactList);
        contactAdapter.setOnItemClickListener(contact -> openContactDetailActivity(contact));
        recyclerViewContacts.setAdapter(contactAdapter);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> openAddContactActivity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContactList();
    }

    private void openAddContactActivity() {
        Intent intent = new Intent(this, AddContactActivity.class);
        addContactLauncher.launch(intent);
    }

    private void openContactDetailActivity(Contact contact) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT_ID, contact.getId());
        editContactLauncher.launch(intent);
    }

    private void refreshContactList() {
        contactList = dbHelper.getAllContacts();
        contactAdapter.setContactList(contactList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
