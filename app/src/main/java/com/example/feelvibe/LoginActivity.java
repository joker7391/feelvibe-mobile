package com.example.feelvibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnSignup, btnForgotPassword, btnSubmit;
    EditText txtEmail, txtPassword;
    String emailPattern = "[a-zA-z0-9,_-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnSignup = findViewById(R.id.btnSignup);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        progressDialog = new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {performLogin();}
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openSignup();}
        });
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openForgotPassword();}
        });

    }
    public void openSignup(){
        Intent intent1 = new Intent(this, Signup.class);
        startActivity(intent1);
    }
    public void openForgotPassword(){
        Intent intent2 = new Intent(this, ForgotPassword.class);
        startActivity(intent2);
    }
    private void performLogin(){
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(!email.matches(emailPattern))
        {
            txtEmail.setError("Enter correct email");
        }
        else if(password.isEmpty() || password.length()<8)
        {
            txtPassword.setError("Enter Correct password");
        }
        else
        {
            progressDialog.setMessage("Please wait while logging in");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful())
                   {
                       progressDialog.dismiss();
                       sendUserToNextActivity();
                       Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                   }
                   else
                   {
                       progressDialog.dismiss();
                       Toast.makeText(LoginActivity.this, "Wrong email and password",Toast.LENGTH_SHORT).show();
                   }
                }
            });
        }
    }

    private void sendUserToNextActivity(){
        Intent intent = new Intent(this, Drawer_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}