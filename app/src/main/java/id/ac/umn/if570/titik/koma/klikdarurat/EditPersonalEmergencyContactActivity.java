package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class EditPersonalEmergencyContactActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_SELECTED_CONTACT = "SELECTED_CONTACT";
    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnSave;
    private CircularProgressIndicator circularProgressIndicator;
    private PersonalEmergencyContact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_emergency_contact);

        if (!FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.edit_personal_emergency_contact_tv_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedContact = (PersonalEmergencyContact) bundle.getSerializable(EXTRA_SELECTED_CONTACT);

        etName.setText(selectedContact.getName());
        etPhoneNumber.setText(selectedContact.getPhoneNumber());

        btnSave.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == btnSave.getId()) {
            updateContact();
        }
    }

    private void updateContact() {
        String contactName;
        String contactPhoneNumber;

        contactName = etName.getText().toString();
        contactPhoneNumber = etPhoneNumber.getText().toString();

        if (contactName.isEmpty()) {
            etName.setError("Nama wajib diisi.");
            etName.requestFocus();
            return;
        }

        if (contactPhoneNumber.isEmpty()) {
            etPhoneNumber.setError("Nomor telepon wajib diisi.");
            etPhoneNumber.requestFocus();
            return;
        }

        circularProgressIndicator.setVisibility(View.VISIBLE);

        Map<String, Object> updatedContact = new HashMap<>();
        updatedContact.put("_nameSearch", contactName.toLowerCase());
        updatedContact.put("_nameOrder", contactName.toUpperCase());
        updatedContact.put("name", contactName);
        updatedContact.put("phoneNumber", contactPhoneNumber);

        FirebaseHelper.instance.updateContactDocument(FirebaseHelper.instance.getCurrentUser().getUid(), selectedContact.getId(), updatedContact)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        circularProgressIndicator.setVisibility(View.GONE);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditPersonalEmergencyContactActivity.this, "Kontak berhasil diperbarui.", Toast.LENGTH_SHORT).show();

                        MainActivity.navController.navigate(R.id.nav_menu_personal_emergency_contact);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditPersonalEmergencyContactActivity.this, "Gagal memperbarui kontak.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        etName = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_personal_emergency_contact_name)).getEditText();
        etPhoneNumber = ((TextInputLayout) findViewById(R.id.textInputLayout_edit_personal_emergency_contact_phone_number)).getEditText();
        btnSave = findViewById(R.id.btn_edit_personal_emergency_contact_save);
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
    }
}