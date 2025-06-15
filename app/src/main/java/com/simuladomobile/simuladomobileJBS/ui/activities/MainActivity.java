package com.simuladomobile.simuladomobileJBS.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simuladomobile.simuladomobileJBS.R;
import com.simuladomobile.simuladomobileJBS.databinding.ActivityMainBinding;
import com.simuladomobile.simuladomobileJBS.ui.fragments.PasswordDialogFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_registrar_carro, R.id.navigation_administrador)
                .build();

        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(item -> handleNavigation(item));
    }

    private boolean handleNavigation(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_registrar_carro) {
            navController.navigate(R.id.navigation_registrar_carro);
            return true;

        } else if (itemId == R.id.navigation_administrador) {
            PasswordDialogFragment dialog = PasswordDialogFragment.newInstance(() -> {
                navController.navigate(R.id.navigation_administrador);
            });
            dialog.show(getSupportFragmentManager(), "PasswordDialog");
            return false;
        }

        return false;
    }
}
