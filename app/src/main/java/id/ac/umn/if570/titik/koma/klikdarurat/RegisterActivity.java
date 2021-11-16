package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRegister;
    private TextView tvLoginAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        btnRegister.setOnClickListener(this);
        tvLoginAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnRegister.getId()) {
            Log.d("RegisterActivity", "Register button clicked");
        } else if (viewId == tvLoginAccount.getId()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void initView() {
        btnRegister = findViewById(R.id.button_register_register);
        tvLoginAccount = findViewById(R.id.tv_register_login_account);
    }
}