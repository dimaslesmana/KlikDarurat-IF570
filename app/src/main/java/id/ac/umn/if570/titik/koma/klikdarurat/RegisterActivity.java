package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etFullName;
    private EditText etPhoneNumber;
    private EditText etAddress;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;
    private Button btnLoginAccount;
    private CircularProgressIndicator circularProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initView();

        btnRegister.setOnClickListener(this);
        btnLoginAccount.setOnClickListener(this);
    }

    private void createContactsCollection(String userId) {
        Map<String, Object> contact = new HashMap<>();
        contact.put("name", "EMPTY_RESERVED");
        contact.put("_nameOrder", "EMPTY_RESERVED");

        FirebaseHelper.instance.createContactsCollection(userId, contact);
    }

    private void doRegister() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Nama lengkap wajib diisi.");
            etFullName.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Nomor telepon wajib diisi.");
            etPhoneNumber.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Alamat wajib diisi.");
            etAddress.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email wajib diisi.");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email tidak valid.");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Kata sandi wajib diisi.");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Kata sandi minimal 6 karakter.");
            etPassword.requestFocus();
            return;
        }

        circularProgressIndicator.setVisibility(View.VISIBLE);

        FirebaseHelper.instance.registerUser(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseAuth auth = FirebaseHelper.instance.getAuth();
                        FirebaseHelper.instance.setCurrentUser(auth.getCurrentUser());
                        String userId = auth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("phoneNumber", phoneNumber);
                        user.put("address", address);
                        user.put("email", email);

                        FirebaseHelper.instance.addUserDocument(userId, user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        circularProgressIndicator.setVisibility(View.GONE);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        createContactsCollection(userId);
                                        FirebaseHelper.instance.sendVerificationEmail();
                                        FirebaseHelper.instance.logoutUser();

                                        Toast.makeText(RegisterActivity.this, "Berhasil membuat akun, silakan verifikasi email Anda.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Gagal membuat akun.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        circularProgressIndicator.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Gagal membuat akun.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnRegister.getId()) {
            doRegister();
        } else if (viewId == btnLoginAccount.getId()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initView() {
        etFullName = ((TextInputLayout) findViewById(R.id.textInputLayout_register_full_name)).getEditText();
        etPhoneNumber = ((TextInputLayout) findViewById(R.id.textInputLayout_register_phone_number)).getEditText();
        etAddress = ((TextInputLayout) findViewById(R.id.textInputLayout_register_address)).getEditText();
        etEmail = ((TextInputLayout) findViewById(R.id.textInputLayout_register_email)).getEditText();
        etPassword = ((TextInputLayout) findViewById(R.id.textInputLayout_register_password)).getEditText();
        btnLoginAccount = findViewById(R.id.btn_register_login_account);
        btnRegister = findViewById(R.id.button_register_register);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
    }
}