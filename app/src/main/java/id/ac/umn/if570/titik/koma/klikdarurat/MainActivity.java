package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new EmergencyServiceFragment());

        BottomNavigationView navView = findViewById(R.id.bottom_nav_main);
        navView.setOnItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_nav_main, fragment).commit();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_menu_emergency_service:
                selectedFragment = new EmergencyServiceFragment();
                break;
            case R.id.nav_menu_personal_emergency_contact:
                selectedFragment = new PersonalEmergencyContactFragment();
                break;
            case R.id.nav_menu_emergency_service_location:
                selectedFragment = new EmergencyServiceLocationFragment();
                break;
            case R.id.nav_menu_user_profile:
                selectedFragment = new UserProfileFragment();
                break;
        }

        return loadFragment(selectedFragment);
    }
}