package com.example.notifyme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notifyme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_email, et_password;
    TextView tv_create_account;
    Button bt_login;
    FirebaseAuth auth;
    SweetAlertDialog sweetAlertDialog;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        initializeViews();
        setClickListeners();
    }

    public void changeStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    public void initializeViews()
    {
        et_email = (EditText)findViewById(R.id.email);
        et_password = (EditText)findViewById(R.id.password);
        bt_login = (Button)findViewById(R.id.login);
        tv_create_account = (TextView)findViewById(R.id.create_an_account);

        // Initializing Objects
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void setClickListeners()
    {
        bt_login.setOnClickListener(this);
        tv_create_account.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == bt_login.getId())
        {
            if(et_email.getText().length() > 0)
            {
                if(et_password.getText().length()>0)
                {
                    if(et_password.getText().length() >= 8)
                    {
                        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setCancelable(false);
                        sweetAlertDialog.setTitle("Authenticating");
                        sweetAlertDialog.setContentText("Please Wait");
                        sweetAlertDialog.show();
                        sweetAlertDialog.getButton(R.id.confirm_button).setVisibility(View.GONE);
                        auth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    String email = et_email.getText().toString();
                                    email = email.replace(".", "(period)");
                                    databaseReference.child("Users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("name", dataSnapshot.child("name").getValue().toString());
                                            editor.putString("email", dataSnapshot.child("email").getValue().toString());
                                            editor.commit();
                                            sweetAlertDialog.dismiss();
                                            Intent homepage = new Intent(LoginActivity.this, HomePageActivity.class);
                                            startActivity(homepage);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else
                                {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitle("Error");
                                    sweetAlertDialog.setContentText("Account not found");
                                }
                            }
                        });
                    }
                    else
                    {
                        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitle("Invalid Password Length");
                        sweetAlertDialog.setContentText("Password must be 8 Characters Long");
                        sweetAlertDialog.show();
                    }
                }
                else
                {
                    sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("Required");
                    sweetAlertDialog.setContentText("Enter Password");
                    sweetAlertDialog.show();
                }
            }
            else
            {
                sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("Required");
                sweetAlertDialog.setContentText("Enter Email");
                sweetAlertDialog.show();
            }
        }
        else if(view.getId() == tv_create_account.getId())
        {
            Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(register_intent);
            // Open Signup Activity
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null)
        {
            Intent homePageIntent = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(homePageIntent);
            finish();
        }
    }
}
