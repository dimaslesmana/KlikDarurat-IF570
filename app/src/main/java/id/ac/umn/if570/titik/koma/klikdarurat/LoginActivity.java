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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegisterAccount;
    private TextInputLayout editTextemail, editTextpassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("REGISTRATION_SUCCESS");
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
        }

        initView();

        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvRegisterAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnLogin.getId()) {
            Log.d("LoginActivity", "Login button clicked");
            String email = editTextemail.getEditText().getText().toString().trim();
            String password = editTextpassword.getEditText().getText().toString().trim();

            if(email.isEmpty()){
                editTextemail.setError("Email is required!");
                editTextemail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextemail.setError("Please enter a valid email!");
                editTextemail.requestFocus();
                return;
            }
            if(password.isEmpty()){
                editTextpassword.setError("Password is required!");
                editTextpassword.requestFocus();
                return;
            }
            if(password.length() < 6){
                editTextpassword.setError("Min password length is 6 characters!");
                editTextpassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        currentUser = mAuth.getCurrentUser();

                        if (currentUser.isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Failed to login Please check your credentials", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (viewId == tvForgotPassword.getId()) {
            Log.d("LoginActivity", "Forgot Password clicked");
        } else if (viewId == tvRegisterAccount.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

    }

    private void initView() {
        btnLogin = findViewById(R.id.button_login_login);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
        tvRegisterAccount = findViewById(R.id.tv_login_register_account);
        editTextemail = findViewById(R.id.textInputLayout_login_email);
        editTextpassword = findViewById(R.id.textInputLayout_login_password);

        progressBar = findViewById(R.id.progressBar);

        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
    }
}