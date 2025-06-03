package com.simuladomobile.simuladomobileJBS;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.simuladomobile.simuladomobileJBS.databinding.ActivityMainBinding;
import com.simuladomobile.simuladomobileJBS.model.Usuario;
import com.simuladomobile.simuladomobileJBS.repository.UsuarioRepository;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;
import com.simuladomobile.simuladomobileJBS.model.Usuario;
import com.simuladomobile.simuladomobileJBS.repository.RegistroCarroRepository;
import com.simuladomobile.simuladomobileJBS.repository.UsuarioRepository;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

//        RegistroCarroRepository registroCarroRepository = new RegistroCarroRepository();
//        registroCarroRepository.save(
//                new RegistroCarro(LocalDate.now(), LocalDate.of(2025, 12, 30), "ABC1234")
//        );
    }

}