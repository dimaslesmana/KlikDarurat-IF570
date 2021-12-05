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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPersonalEmergencyContactActivity extends AppCompatActivity {
    private TextInputLayout inputName;
    private TextInputLayout inputPhoneNumber;
    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnSave;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_emergency_contact);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_personal_emergency_contact_tv_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        initView();

        etName = inputName.getEditText();
        etPhoneNumber = inputPhoneNumber.getEditText();

        btnSave.setOnClickListener(v -> {
            saveContact();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initView() {
        inputName = findViewById(R.id.textInputLayout_add_personal_emergency_contact_name);
        inputPhoneNumber = findViewById(R.id.textInputLayout_add_personal_emergency_contact_phone_number);
        btnSave = findViewById(R.id.btn_add_personal_emergency_contact_save);
    }

    private void saveContact() {
        String contactName;
        String contactPhoneNumber;

        contactName = etName.getText().toString();
        contactPhoneNumber = etPhoneNumber.getText().toString();

        if (contactName.isEmpty()) {
            inputName.setError("Contact name is required!");
            inputName.requestFocus();
            return;
        }

        if (contactPhoneNumber.isEmpty()) {
            inputPhoneNumber.setError("Phone number is required!");
            inputPhoneNumber.requestFocus();
            return;
        }

        progressDialog.show();

        DocumentReference docRef = firestore.collection("users").document(currentUser.getUid());

        Map<String, Object> contact = new HashMap<>();
        contact.put("name", contactName);
        contact.put("phoneNumber", contactPhoneNumber);

        docRef.collection("contacts").add(contact)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Map<String, Object> contact = new HashMap<>();
                        contact.put("id", documentReference.getId());

                        documentReference.update(contact);

                        MainActivity.navController.navigate(R.id.nav_menu_personal_emergency_contact);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Toast.makeText(AddPersonalEmergencyContactActivity.this, "Failed to add contact! Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}