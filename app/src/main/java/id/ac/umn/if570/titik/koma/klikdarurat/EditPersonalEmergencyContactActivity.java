package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class EditPersonalEmergencyContactActivity extends AppCompatActivity {
    private TextInputLayout inputName;
    private TextInputLayout inputPhoneNumber;
    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnSave;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private PersonalEmergencyContact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_emergency_contact);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.edit_personal_emergency_contact_tv_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedContact = (PersonalEmergencyContact) bundle.getSerializable("SELECTED_CONTACT");

        initView();

        etName = inputName.getEditText();
        etPhoneNumber = inputPhoneNumber.getEditText();

        etName.setText(selectedContact.getName());
        etPhoneNumber.setText(selectedContact.getPhoneNumber());

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
        inputName = findViewById(R.id.textInputLayout_edit_personal_emergency_contact_name);
        inputPhoneNumber = findViewById(R.id.textInputLayout_edit_personal_emergency_contact_phone_number);
        btnSave = findViewById(R.id.btn_edit_personal_emergency_contact_save);
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

        Map<String, Object> editedContact = new HashMap<>();
        editedContact.put("name", contactName);
        editedContact.put("phoneNumber", contactPhoneNumber);

        docRef.collection("contacts").document(selectedContact.getId()).update(editedContact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Intent intent = new Intent(EditPersonalEmergencyContactActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Toast.makeText(EditPersonalEmergencyContactActivity.this, "Failed to edit contact! Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}