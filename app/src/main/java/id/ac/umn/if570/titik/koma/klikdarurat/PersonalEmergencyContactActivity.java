package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class PersonalEmergencyContactActivity extends AppCompatActivity {
    private RecyclerView rvPersonalEmergencyContact;
    private ArrayList<PersonalEmergencyContact> listPersonalEmergencyContact = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_emergency_contact);

        rvPersonalEmergencyContact = findViewById(R.id.rv_personal_emergency_contact);
        rvPersonalEmergencyContact.setHasFixedSize(true);

        listPersonalEmergencyContact.addAll(PersonalEmergencyContactData.getListData());

        PersonalEmergencyContactAdapter adapter = new PersonalEmergencyContactAdapter(listPersonalEmergencyContact);
        rvPersonalEmergencyContact.setLayoutManager(new LinearLayoutManager(this));
        rvPersonalEmergencyContact.setAdapter(adapter);
    }
}