package id.ac.umn.if570.titik.koma.klikdarurat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {
    private TextInputLayout textInputFullName, textInputPhoneNumber, textInputEmail, textInputAddress;
    private EditText etFullName, etPhoneNumber, etEmail, etAddress;
    private String fullName, phoneNumber, email, address;
    private LinearLayout saveBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

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

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = fAuth.getCurrentUser();

        textInputFullName = findViewById(R.id.textInputLayout_edit_user_profile_full_name);
        textInputPhoneNumber = findViewById(R.id.textInputLayout_edit_user_profile_phone_number);
        textInputEmail = findViewById(R.id.textInputLayout_edit_user_profile_email);
        textInputAddress = findViewById(R.id.textInputLayout_edit_user_profile_address);
        saveBtn = findViewById(R.id.linearlayout_edit_user_profile_save);

        etFullName = textInputFullName.getEditText();
        etPhoneNumber = textInputPhoneNumber.getEditText();
        etEmail = textInputEmail.getEditText();
        etAddress = textInputAddress.getEditText();

        etEmail.setEnabled(false);

        etFullName.setText(fullName);
        etPhoneNumber.setText(phoneNumber);
        etEmail.setText(email);
        etAddress.setText(address);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = etFullName.getText().toString();
                phoneNumber =  etPhoneNumber.getText().toString();
                email = etEmail.getText().toString();
                address = etAddress.getText().toString();

                if(fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty()){
                    Toast.makeText(EditUserProfileActivity.this, "One or many field are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(currentUser.getUid());

                        Map<String,Object> edited = new HashMap<>();
                        edited.put("fullName", fullName);
                        edited.put("phoneNumber", phoneNumber);
                        edited.put("email", email);
                        edited.put("address", address);

                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditUserProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                MainActivity.navController.navigate(R.id.nav_menu_user_profile);
                                finish();
                            }
                        });

                        // Toast
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
