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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRegister;
    private TextView banner, tvLoginAccount;
    private FirebaseAuth mAuth;
    private EditText editTextfull_name, editTextphone_number, editTextaddress, editTextemail, editTextpassword;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        btnRegister.setOnClickListener(this);
        tvLoginAccount.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        banner.setOnClickListener(this);

        editTextfull_name = (EditText) findViewById(R.id.full_name);
        editTextemail = (EditText) findViewById(R.id.email);
        editTextphone_number = (EditText) findViewById(R.id.phone_number);
        editTextaddress = (EditText) findViewById(R.id.address);
        editTextpassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnRegister.getId()) {
            Log.d("RegisterActivity", "Register button clicked");
        } else if (viewId == tvLoginAccount.getId()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        String email= editTextemail.getText().toString().trim();
        String password= editTextpassword.getText().toString().trim();
        String full_name= editTextfull_name.getText().toString().trim();
        String phone_number= editTextphone_number.getText().toString().trim();
        String address= editTextaddress.getText().toString().trim();

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
                            User user = new User(full_name, phone_number, address, email);

                            String path;
                            FirebaseDatabase.getInstance().getReference( "Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //redirect to login layout!
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }

                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initView() {
        btnRegister = findViewById(R.id.button_register_register);
        tvLoginAccount = findViewById(R.id.tv_register_login_account);

    }
}