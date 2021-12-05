package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvRegisterAccount;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initView();

        tvRegisterAccount.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);

        FirebaseHelper.instance.loginUser(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseAuth auth = FirebaseHelper.instance.getAuth();
                        FirebaseHelper.instance.setCurrentUser(auth.getCurrentUser());

                        if (FirebaseHelper.instance.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Harap verifikasi email Anda terlebih dahulu.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Gagal untuk masuk. Email atau kata sandi salah.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnLogin.getId()) {
            doLogin();
        } else if (viewId == tvRegisterAccount.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        } else if (viewId == tvForgotPassword.getId()) {
            Log.d("LoginActivity", "Forgot Password clicked");
        }
    }

    private void initView() {
        etEmail = ((TextInputLayout) findViewById(R.id.textInputLayout_login_email)).getEditText();
        etPassword = ((TextInputLayout) findViewById(R.id.textInputLayout_login_password)).getEditText();
        tvRegisterAccount = findViewById(R.id.tv_login_register_account);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
        btnLogin = findViewById(R.id.button_login_login);
        progressBar = findViewById(R.id.progressBar);
    }
}