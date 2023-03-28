package vungnv.com.foodappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.fragments.ProductAwaitingApprovalFragment;
import vungnv.com.foodappadmin.fragments.InformationFragment;
import vungnv.com.foodappadmin.fragments.SettingsFragment;
import vungnv.com.foodappadmin.fragments.UserAwaitingApprovalFragment;
import vungnv.com.foodappadmin.model.CategoryModel;
import vungnv.com.foodappadmin.utils.NetworkChangeListener;
import vungnv.com.foodappadmin.utils.createNotificationChannel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constant {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgBack;
    private View mHeaderView;
    private TextView tvHome, tvSettings;

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    createNotificationChannel notification = new createNotificationChannel();

    private ArrayList<CategoryModel> aListCate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        notification.createNotificationChannel(MainActivity.this);
        //vungnv.com.foodappadmin.utils.createNotification.mCreateNotification(MainActivity.this, "Tiêu đề", "Nội dung");

        toolbar.setTitle(ORDER);
        toolbar.setTitleTextColor(Color.WHITE);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        createNotificationChannel(MainActivity.this);
        //createNotification(MainActivity.this, NOTIFICATION, MESSAGE);

        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();

            }
        });

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(SettingsFragment.newInstance());
                drawerLayout.closeDrawer(navigationView);
            }
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, 0, 0);
        toggle.syncState();

        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
        navigationView.setNavigationItemSelectedListener(this);
        //replaceFragment(OrderFragment.newInstance());
        Fragment initialFragment = new InformationFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, initialFragment)
                .addToBackStack(null)
                .commit();

    }

    //products_awaiting_approval
    private void init() {
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.naviView);
        mHeaderView = navigationView.getHeaderView(0);
        imgBack = mHeaderView.findViewById(R.id.imgBack);
        tvSettings = mHeaderView.findViewById(R.id.tvSettings);
        tvHome = mHeaderView.findViewById(R.id.tvHome);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int index = item.getItemId();
        switch (index) {
            case R.id.users_awaiting_approval:
                replaceFragment(UserAwaitingApprovalFragment.newInstance());
                break;
            case R.id.products_awaiting_approval:
                replaceFragment(ProductAwaitingApprovalFragment.newInstance());
                break;
            case R.id.info:
                replaceFragment(InformationFragment.newInstance());
                break;

        }
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    public void replaceFragment(Fragment fra) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fra);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        // NotificationChannel for Android 8.0 and higher
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = null;
        notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void createNotification(Context context, String title, String message) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.food_app_admin_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }
    private void listProductAwaitingApproval() {
        aListCate = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_products_awaiting_approval");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel data = dataSnapshot.getValue(CategoryModel.class);
                    aListCate.add(data);
                }
                if (aListCate.size() == 0) {
                    Toast.makeText(MainActivity.this, ERROR_FETCHING_DATE + CATEGORY_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "list size: " + aListCate.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }
    private void listCate() {
        aListCate = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel data = dataSnapshot.getValue(CategoryModel.class);
                    aListCate.add(data);
                }
                if (aListCate.size() == 0) {
                    Toast.makeText(MainActivity.this, ERROR_FETCHING_DATE + CATEGORY_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "list size: " + aListCate.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}