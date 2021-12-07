package id.ac.umn.if570.titik.koma.klikdarurat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseHelper.instance == null) {
            FirebaseHelper.instance = new FirebaseHelper();
        }

        if (FirebaseHelper.instance.isAuthenticated()) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_main);
            navController = Navigation.findNavController(this, R.id.fragment_nav_main);

            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}