package com.example.notifyme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notifyme.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_name, et_email, et_password, et_confirm_password;
    Button bt_signup;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    SweetAlertDialog sweetAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        et_name = (EditText)findViewById(R.id.name);
        et_email = (EditText)findViewById(R.id.email);
        et_password = (EditText)findViewById(R.id.password);
        et_confirm_password = (EditText)findViewById(R.id.confirm_password);
        bt_signup = (Button)findViewById(R.id.signup);

        //Initializing Objects
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void setClickListeners()
    {
        bt_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == bt_signup.getId())
        {
            if(et_name.getText().length() > 0)
            {
                if(et_email.getText().length() > 0)
                {
                    if(et_password.getText().length()>0)
                    {
                        if(et_password.getText().length() >= 8)
                        {
                            if(et_confirm_password.getText().length() > 0)
                            {
                                if(et_confirm_password.getText().length() >= 8)
                                {
                                    if(et_password.getText().toString().equals(et_confirm_password.getText().toString()))
                                    {
                                        sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                                        sweetAlertDialog.setCancelable(false);
                                        sweetAlertDialog.setTitle("Authenticating");
                                        sweetAlertDialog.setContentText("Please Wait");
                                        sweetAlertDialog.show();
                                        sweetAlertDialog.getButton(R.id.confirm_button).setVisibility(View.GONE);
                                        auth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                    final User user = new User(et_name.getText().toString(),
                                                            et_email.getText().toString());
                                                    databaseReference.child("Users").child(et_email.getText().toString().replace(".","(period)")).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("name", user.getName());
                                                            editor.putString("email", user.getEmail());
                                                            editor.commit();
                                                            sweetAlertDialog.dismiss();
                                                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent HomePage = new Intent(RegisterActivity.this, HomePageActivity.class);
                                                            startActivity(HomePage);
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                    sweetAlertDialog.setTitle("Error");
                                                    sweetAlertDialog.setContentText("Account already exists");
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitle("Password Mismatch");
                                        sweetAlertDialog.setContentText("Password and Confirm Password must be same");
                                        sweetAlertDialog.show();
                                    }
                                }
                                else
                                {
                                    sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setTitle("Invalid Confirm Password Length");
                                    sweetAlertDialog.setContentText("Confirm password must be 8 characters long");
                                    sweetAlertDialog.show();
                                }
                            }
                            else
                            {
                                sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setTitle("Required");
                                sweetAlertDialog.setContentText("Enter Confirm password");
                                sweetAlertDialog.show();
                            }
                        }
                        else
                        {
                            sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setTitle("Invalid Password Length");
                            sweetAlertDialog.setContentText("Password must be 8 Characters Long");
                            sweetAlertDialog.show();
                        }
                    }
                    else
                    {
                        sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitle("Required");
                        sweetAlertDialog.setContentText("Enter Password");
                        sweetAlertDialog.show();
                    }
                }
                else
                {
                    sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("Required");
                    sweetAlertDialog.setContentText("Enter Email");
                    sweetAlertDialog.show();
                }
            }
            else
            {
                sweetAlertDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("Required");
                sweetAlertDialog.setContentText("Enter Name");
                sweetAlertDialog.show();
            }
        }
    }
}
