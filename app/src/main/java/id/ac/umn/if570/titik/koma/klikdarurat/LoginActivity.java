package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegisterAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        } else if (viewId == tvForgotPassword.getId()) {
            Log.d("LoginActivity", "Forgot Password clicked");
        } else if (viewId == tvRegisterAccount.getId()) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void initView() {
        btnLogin = findViewById(R.id.button_login_login);
        tvForgotPassword = findViewById(R.id.tv_login_forgot_password);
        tvRegisterAccount = findViewById(R.id.tv_login_register_account);
    }
}