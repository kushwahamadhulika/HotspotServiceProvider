package com.ratnesh.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class AllServices extends AppCompatActivity {
    LinearLayout nav_withdraw,nav_profile,nav_home;
    Intent intent;
    FirebaseAuth mAuth;
    String uid;
    String TAG="ALLServices";
    DatabaseReference reff;
    SharedPreferences pref;
    ServiceUserModel model;
    SharedPreferences.Editor edit;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
try {

    //Notification tokenGenerator


    FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(AllServices.this, msg, Toast.LENGTH_SHORT).show();
                }
            });

    pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);

    mAuth = FirebaseAuth.getInstance();
    user=mAuth.getCurrentUser();
    Log.w(TAG,"User=>"+user);
    if (user == null) {
        Intent intent = new Intent(AllServices.this, PhoneNumberActivity.class);
        startActivity(intent);
    }else
        uid=user.getUid();

    reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(uid);
    model = new ServiceUserModel();
    fetchUserDetails(uid);
    //BottomNavBarCode
    nav_home = findViewById(R.id.nav_Home);
    nav_profile = findViewById(R.id.nav_Profile);
    nav_withdraw = findViewById(R.id.nav_Withdraw);
    nav_withdraw.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, Wallet.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    });

    nav_home.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, AllServices.class);
            startActivity(intent);
        }
    });
    nav_profile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, PartnerProfile.class);
            startActivity(intent);
        }
    });
}catch (Exception e){
    e.printStackTrace();
}
}

    private void fetchUserDetails(String uid) {
try {
    edit=pref.edit();

    reff.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
            model = dataSnapshot.getValue(ServiceUserModel.class);
            edit.putString("userName", model.getName());
            edit.putString("WalletBalance", model.getWalletBalance());
            edit.putString("add1", model.getAdd1());
            edit.putString("add2", model.getAdd2());
            edit.putString("add3", model.getAdd3());
            edit.putString("phn", model.getPhn());
            edit.putString("Profileimage", model.getProfileimage());
            edit.putString("Servicetype", model.getServicetype());

                Log.w(TAG,"NAME=>"+model.getName());
            edit.commit();
            edit.apply();}

            Log.w(TAG, "ModelUserName=>" + model.getName() + "DatasnapShot=>" + dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}catch (Exception e){
    e.printStackTrace();
}
    }
}