package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AddPersonalEmergencyContactActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnSave;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_emergency_contact);

        if (!FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_personal_emergency_contact_tv_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnSave.getId()) {
            addContact();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void addContact() {
        String contactName;
        String contactPhoneNumber;

        contactName = etName.getText().toString();
        contactPhoneNumber = etPhoneNumber.getText().toString();

        if (contactName.isEmpty()) {
            etName.setError("Nama wajib diisi.");
            etName.requestFocus();
            return;
        }

        if (contactPhoneNumber.isEmpty()) {
            etPhoneNumber.setError("Nomor telepon wajib diisi.");
            etPhoneNumber.requestFocus();
            return;
        }

        progressDialog.show();

        Map<String, Object> newContact = new HashMap<>();
        newContact.put("name", contactName);
        newContact.put("phoneNumber", contactPhoneNumber);

        FirebaseHelper.instance.addContactDocument(FirebaseHelper.instance.getCurrentUser().getUid(), newContact)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Add id field to added contact
                        Map<String, Object> addedContact = new HashMap<>();
                        addedContact.put("id", documentReference.getId());

                        documentReference.update(addedContact)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AddPersonalEmergencyContactActivity.this, "Kontak berhasil ditambahkan.", Toast.LENGTH_SHORT).show();

                                        MainActivity.navController.navigate(R.id.nav_menu_personal_emergency_contact);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddPersonalEmergencyContactActivity.this, "Gagal menambahkan kontak.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPersonalEmergencyContactActivity.this, "Gagal menambahkan kontak.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        etName = ((TextInputLayout) findViewById(R.id.textInputLayout_add_personal_emergency_contact_name)).getEditText();
        etPhoneNumber = ((TextInputLayout) findViewById(R.id.textInputLayout_add_personal_emergency_contact_phone_number)).getEditText();
        btnSave = findViewById(R.id.btn_add_personal_emergency_contact_save);
    }
}