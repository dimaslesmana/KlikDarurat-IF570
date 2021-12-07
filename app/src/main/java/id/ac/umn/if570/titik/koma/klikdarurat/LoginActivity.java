package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private Button btnRegisterAccount;
    private CircularProgressIndicator circularProgressIndicator;

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

        tvForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegisterAccount.setOnClickListener(this);
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

        circularProgressIndicator.setVisibility(View.VISIBLE);

        FirebaseHelper.instance.loginUser(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        circularProgressIndicator.setVisibility(View.GONE);
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
        } else if (viewId == btnRegisterAccount.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        } else if (viewId == tvForgotPassword.getId()) {
            MaterialAlertDialogBuilder passwordResetDialog = new MaterialAlertDialogBuilder(v.getContext());

            TextInputLayout textInputLayoutForgotPassword = new TextInputLayout(v.getContext());
            textInputLayoutForgotPassword.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            textInputLayoutForgotPassword.setBoxBackgroundColor(ContextCompat.getColor(v.getContext(), android.R.color.white));
            textInputLayoutForgotPassword.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            textInputLayoutForgotPassword.setHintTextAppearance(R.style.Base_Widget_MaterialComponents_TextInputLayout);

            TextInputEditText inputResetEmail = new TextInputEditText(textInputLayoutForgotPassword.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(36, 36, 36, 36);

            textInputLayoutForgotPassword.addView(inputResetEmail, layoutParams);

            inputResetEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            inputResetEmail.setHint(R.string.hint_email);
            inputResetEmail.setMaxLines(1);

            passwordResetDialog.setTitle(R.string.label_reset_password);
            passwordResetDialog.setView(textInputLayoutForgotPassword);

            passwordResetDialog
                    .setPositiveButton(R.string.dialog_send, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            circularProgressIndicator.setVisibility(View.VISIBLE);

                            String email = inputResetEmail.getText().toString();
                            FirebaseHelper.instance.getAuth().sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            circularProgressIndicator.setVisibility(View.GONE);
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(LoginActivity.this, "Silakan cek email anda untuk atur ulang kata sandi.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "Permintaan atur ulang kata sandi gagal.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            passwordResetDialog.create().show();
        }
    }

    private void initView() {
        etEmail = ((TextInputLayout) findViewById(R.id.textInputLayout_login_email)).getEditText();
        etPassword = ((TextInputLayout) findViewById(R.id.textInputLayout_login_password)).getEditText();
        btnRegisterAccount = findViewById(R.id.btn_login_register_account);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
        btnLogin = findViewById(R.id.button_login_login);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
    }
}