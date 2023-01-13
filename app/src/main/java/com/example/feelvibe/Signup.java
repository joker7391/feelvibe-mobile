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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    EditText txtEmailReg, txtPasswordReg, txtConfirmPassword;
    Button btnRegister, btnAlreadyHaveAnAccount;
    String emailPattern = "[a-zA-z0-9,_-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        txtEmailReg = findViewById(R.id.txtEmailReg);
        txtPasswordReg = findViewById(R.id.txtPasswordReg);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnAlreadyHaveAnAccount = findViewById(R.id.btnAlreadyHaveAnAccount);
        progressDialog = new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performAuth();
            }
        });

        btnAlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openLogin();
            }
        });

    }
    private void performAuth(){
        String email = txtEmailReg.getText().toString();
        String password = txtPasswordReg.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if(!email.matches(emailPattern))
        {
             txtEmailReg.setError("Enter correct email");
        }
        else if(password.isEmpty() || password.length()<8)
        {
             txtPasswordReg.setError("Enter Correct password");
        }
        else if(!password.equals(confirmPassword))
        {
              txtConfirmPassword.setError("Password does not match");
        }
        else
        {
              progressDialog.setMessage("Please wait");
              progressDialog.setTitle("Registration");
              progressDialog.setCanceledOnTouchOutside(false);
              progressDialog.show();

              mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful())
                     {
                         progressDialog.dismiss();
                         //sendUserToNextActivity();

                        //add data to firestore
                         add_Data();

                         Intent intent = new Intent(Signup.this, UserProfile.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(intent);
                         Toast.makeText(Signup.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                     }else
                     {
                         progressDialog.dismiss();
                         Toast.makeText(Signup.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                     }
                  }
              });
        }
    }

    private void add_Data() {
        String email = txtEmailReg.getText().toString();
        String password = txtPasswordReg.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("confirm password", confirmPassword);

        db.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Signup.this, "users Registered", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sendUserToNextActivity(){
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}