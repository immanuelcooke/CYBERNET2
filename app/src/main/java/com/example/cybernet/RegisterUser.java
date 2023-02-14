package com.example.cybernet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser, login;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword, editTextConfirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();


        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPassword);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
            case R.id.login:
                startActivity(new Intent(this, dashboard.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email= editTextEmail.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();
        String fullName= editTextFullName.getText().toString().trim();
        String age= editTextAge.getText().toString().trim();
        String confirmPassword= editTextConfirmPassword.getText().toString().trim();
        String profilePicture = "";

        if(fullName.isEmpty()){
            editTextFullName.setError("You must enter your name!");
            editTextFullName.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextAge.setError("You must enter your age!");
            editTextAge.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("You must enter your email!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("You must enter a password!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPassword.setError("The minimum length for a password should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPassword)){
            editTextPassword.setError("The passwords do not match!");
            editTextPassword.requestFocus();
            editTextConfirmPassword.setError("The passwords do not match!");
            editTextConfirmPassword.requestFocus();
            return;
        }





        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()){
                            //User user = new User(fullName, email, age);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new User(fullName, email, age, profilePicture)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                // Then user should be redirected
                                                startActivity(new Intent(RegisterUser.this, dashboard.class));
                                            } else {
                                                Toast.makeText(RegisterUser.this, "Failed to register user! Please try again.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterUser.this, "Failed to register user! Please try again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });





    }
}