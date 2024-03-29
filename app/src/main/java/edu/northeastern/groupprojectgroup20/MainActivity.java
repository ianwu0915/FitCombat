package edu.northeastern.groupprojectgroup20;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//import com.unity3d.player.UnityPlayerActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;


import edu.northeastern.groupprojectgroup20.data.model.UserDetails;
import edu.northeastern.groupprojectgroup20.databinding.ActivityMainBinding;
import edu.northeastern.groupprojectgroup20.healthconnect.HealthConnect;
import edu.northeastern.groupprojectgroup20.healthconnect.HealthConnectActivity;
import edu.northeastern.groupprojectgroup20.healthconnect.HealthConnectUtil;
import edu.northeastern.groupprojectgroup20.healthconnect.HealthDataService;
import edu.northeastern.groupprojectgroup20.ui.login.Login;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Button health_connect;
    private Button unityLauncher;

    private HealthConnect healthConnect;
    private ActivityResultContract<Set<String>, Set<String>> permissionRequestContract;
    private ActivityResultLauncher<Set<String>> permissionRequestLauncher;

    private ImageView genderImageView;
    TextView display_user_email;

    TextView display_full_name;
    ImageView profile_image;
    TextView sign_out;
    FirebaseAuth auth;
    FirebaseUser user;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);



        // get user account info
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // set navibar
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
         display_full_name = headerView.findViewById(R.id.user_name_display);
         display_user_email = headerView.findViewById(R.id.user_email_display);
        profile_image = headerView.findViewById(R.id.profile_image_header);

        sign_out = headerView.findViewById(R.id.sign_out);
         display_full_name.setText(user.getDisplayName());
         display_user_email.setText(user.getEmail());

        Uri uri = user.getPhotoUrl();
        if (uri != null) {
            Picasso.get().load(uri).into(profile_image);
        }


         sign_out.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FirebaseAuth.getInstance().signOut();
                 Intent intent = new Intent(getApplicationContext(), Login.class);
                 startActivity(intent);
             }
         });
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

////        user_email.setText(user.getEmail());
//        Log.d(TAG, "user email:           +++++++ " + user.getEmail());

//        // create a firebase realtime database
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        // create a db table for store or update if need
//        DatabaseReference myRef = db.getReference("message");
//
//        myRef.setValue("Hello, World!");

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Test health connect activity
        health_connect = findViewById(R.id.health_connect_btn);
        health_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthConnectActivity.class);
                startActivity(intent);
            }
        });

        //Initialize HealthConnect and check for permission to access health data
        healthConnect = new HealthConnect(this);

        // Initialize the ActivityResultLauncher and ActivityResultContract
        permissionRequestContract = healthConnect.requestPermissionsActivityContract();
        permissionRequestLauncher = registerForActivityResult(permissionRequestContract, grantedPermissions -> {
            if (grantedPermissions.containsAll(healthConnect.getRequiredPermissions())) {
                // Permissions granted, start HealthDataService
                Intent serviceIntent = new Intent(this, HealthDataService.class);
                startService(serviceIntent);
            } else {
                // Handle permission denial
                Log.d("MainActivity", "Required permissions not granted.");
            }
        });

        HealthConnectUtil.INSTANCE.checkPermissions(this, allPermissionsGranted -> {
            runOnUiThread(() -> {
                if (allPermissionsGranted) {
                    // Permissions granted, start HealthDataService
                    Intent serviceIntent = new Intent(MainActivity.this, HealthDataService.class);
                    startService(serviceIntent);
                } else {
                    // Request permissions
                    Set<String> permissionsToRequest = healthConnect.getRequiredPermissions();
                    permissionRequestLauncher.launch(permissionsToRequest);
                }
            });
            return null;
        });


//        Intent serviceIntent = new Intent(this, HealthDataService.class);
//        startService(serviceIntent);

        unityLauncher = findViewById(R.id.unityLauncher);
//        unityLauncher.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, UnityPlayerActivity.class);
//            startActivity(intent);
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    public boolean havePermission() {
//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return false;
//        }
//    }
}