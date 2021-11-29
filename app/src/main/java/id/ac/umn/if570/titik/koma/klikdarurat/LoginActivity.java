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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegisterAccount;

    private EditText editTextemail, editTextpassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvRegisterAccount.setOnClickListener(this);

        editTextemail = (EditText) findViewById(R.id.email);
        editTextpassword = (EditText)  findViewById(R.id.password);

         progressBar = (ProgressBar) findViewById(R.id.progressBar);
         mAuth = FirebaseAuth.getInstance();

         tvForgotPassword = (TextView) findViewById(R.id.tv_login_forgot_password);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnLogin.getId()) {
            Log.d("LoginActivity", "Login button clicked");
        } else if (viewId == tvForgotPassword.getId()) {
            Log.d("LoginActivity", "Forgot Password clicked");
        } else if (viewId == tvRegisterAccount.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        String email = editTextemail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();

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
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        // redirect to user profile
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    }else {
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check Your email to verify your account", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Failed to login Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void initView() {
        btnLogin = findViewById(R.id.button_login_login);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
        tvRegisterAccount = findViewById(R.id.tv_login_register_account);


    }
}