package id.ac.umn.if570.titik.koma.klikdarurat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {
    private EditText etFullName;
    private EditText etPhoneNumber;
    private EditText etEmail;
    private EditText etAddress;
    private Button btnSave;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private CircularProgressIndicator circularProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.edit_user_profile_tv_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent data = getIntent();
        fullName = data.getStringExtra("fullName");
        phoneNumber = data.getStringExtra("phoneNumber");
        email = data.getStringExtra("email");
        address = data.getStringExtra("address");

        initView();

        etEmail.setEnabled(false);

        etFullName.setText(fullName);
        etPhoneNumber.setText(phoneNumber);
        etEmail.setText(email);
        etAddress.setText(address);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = etFullName.getText().toString();
                phoneNumber =  etPhoneNumber.getText().toString();
                email = etEmail.getText().toString();
                address = etAddress.getText().toString();

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
                    Toast.makeText(EditUserProfileActivity.this, "Email wajib diisi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                circularProgressIndicator.setVisibility(View.VISIBLE);

                FirebaseUser currentUser = FirebaseHelper.instance.getCurrentUser();

                currentUser.updateEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String,Object> editedUser = new HashMap<>();
                                editedUser.put("fullName", fullName);
                                editedUser.put("phoneNumber", phoneNumber);
                                editedUser.put("email", email);
                                editedUser.put("address", address);

                                FirebaseHelper.instance.updateUserDocument(currentUser.getUid(), editedUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                circularProgressIndicator.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(EditUserProfileActivity.this, "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                                                MainActivity.navController.navigate(R.id.nav_menu_user_profile);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditUserProfileActivity.this, "Gagal edit akun.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                circularProgressIndicator.setVisibility(View.GONE);
                                Toast.makeText(EditUserProfileActivity.this, "Gagal edit akun.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initView() {
        btnSave = findViewById(R.id.btn_edit_user_profile_save);
        etFullName = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_user_profile_full_name)).getEditText();
        etPhoneNumber = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_user_profile_phone_number)).getEditText();
        etEmail = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_user_profile_email)).getEditText();
        etAddress = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_user_profile_address)).getEditText();
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
    }
}
