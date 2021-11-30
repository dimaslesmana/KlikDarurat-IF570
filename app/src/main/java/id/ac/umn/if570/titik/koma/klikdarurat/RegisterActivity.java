package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRegister;
    private TextView tvLoginAccount;
    private TextInputLayout editTextfull_name, editTextphone_number, editTextaddress, editTextemail, editTextpassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        initView();

        btnRegister.setOnClickListener(this);
        tvLoginAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnRegister.getId()) {
            Log.d("RegisterActivity", "Register button clicked");

            String email= editTextemail.getEditText().getText().toString().trim();
            String password= editTextpassword.getEditText().getText().toString().trim();
            String full_name= editTextfull_name.getEditText().getText().toString().trim();
            String phone_number= editTextphone_number.getEditText().getText().toString().trim();
            String address= editTextaddress.getEditText().getText().toString().trim();

            if (full_name.isEmpty()){
                editTextfull_name.setError("Full name is required!");
                editTextfull_name.requestFocus();
                return;
            }

            if (phone_number.isEmpty()){
                editTextphone_number.setError("Phone Number is required!");
                editTextphone_number.requestFocus();
                return;
            }

            if (address.isEmpty()){
                editTextaddress.setError("Addres is required!");
                editTextaddress.requestFocus();
                return;
            }

            if (email.isEmpty()){
                editTextemail.setError("Email is required!");
                editTextemail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextemail.setError("Please provide valid email");
                editTextemail.requestFocus();
                return;
            }
            if (password.isEmpty()){
                editTextpassword.setError("Password is required!");
                editTextpassword.requestFocus();
                return;
            }
            if (password.length() <6){
                editTextpassword.setError("Min Password 6 Karakter!");
                editTextpassword.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = mAuth.getCurrentUser();
                                String userId = currentUser.getUid();
                                DocumentReference documentReference = firestore.collection("users").document(userId);

                                Map<String, Object> user = new HashMap<>();
                                user.put("fullName", full_name);
                                user.put("email", email);
                                user.put("phoneNumber", phone_number);
                                user.put("address", address);

                                documentReference.set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressBar.setVisibility(View.GONE);
                                                currentUser.sendEmailVerification();
                                                mAuth.signOut();

                                                // redirect to login layout!
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                intent.putExtra("REGISTRATION_SUCCESS", "Registration success. Check Your email to verify your account!");
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                //createContactsCollection(userId);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else if (viewId == tvLoginAccount.getId()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void initView() {
        btnRegister = findViewById(R.id.button_register_register);
        tvLoginAccount = findViewById(R.id.tv_register_login_account);

        editTextfull_name = findViewById(R.id.textInputLayout_register_full_name);
        editTextemail = findViewById(R.id.textInputLayout_register_email);
        editTextphone_number = findViewById(R.id.textInputLayout_register_phone_number);
        editTextaddress = findViewById(R.id.textInputLayout_register_address);
        editTextpassword = findViewById(R.id.textInputLayout_register_password);
        progressBar = findViewById(R.id.progressBar);
    }

    private void createContactsCollection(String userId) {
        DocumentReference documentReference = firestore.collection("contacts").document(userId);

        Map<String, Object> contact = new HashMap<>();
//        Map<String, Object> map = new HashMap<>();
//        map.put("phoneNumber", "123");
//
//        contact.put("keluarga", new HashMap<>());

        documentReference.set(contact);
    }
}