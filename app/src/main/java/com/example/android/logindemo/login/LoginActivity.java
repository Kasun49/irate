package com.example.android.logindemo.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.logindemo.Main.ImagesActivity;
import com.example.android.logindemo.R;
import com.example.android.logindemo.model.User;
import com.example.android.logindemo.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
//variables
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    protected FirebaseUser mFirebaseUser;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //xml layout
        setContentView(R.layout.activity_login);

        //assigning variables to objects
        Name = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Info = findViewById(R.id.tvInfo);
        Login = findViewById(R.id.btnLogin);
        userRegistration = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);

        //default showing in text view
        Info.setText("No of attempts remaining: 5");

        //firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //pop up progress box
        progressDialog = new ProgressDialog(this);

        //firebase finding uuser capabilty
        FirebaseUser uuser = firebaseAuth.getCurrentUser();

        //if uuser not null go to images activity
        if(uuser != null){
            finish();
            startActivity(new Intent(LoginActivity.this, ImagesActivity.class));
        }
    //validating uuser
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString(), Password.getText().toString());

               // Intent homeActivity = new Intent(LoginActivity.this,ImagesActivity.class);
                //startActivity(homeActivity);
               // finish();
            }
        });
    //button to regeister activity
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    //button to pass word reset
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
            }
        });
    }

    private void validate(String userName, String userPassword) {
    //simple dialog box
        progressDialog.setMessage("Welcome To iRate");
        progressDialog.show();
    //seeing if users
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    User user = new User();
                    String photoUrl = null;
                    if (user.getPhotoUrl() != null) {
                        user.setPhotoUrl(user.getPhotoUrl().toString());
                    }

                    user.setEmail(Name.getText().toString());
                    user.setUser(Name.getText().toString());
                    user.setUid(user.getUid());

                    FirebaseUtils.getUserRef(user.getEmail().replace(".", ","))
                            .setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    mFirebaseUser = firebaseAuth.getCurrentUser();
                                    finish();
                                }
                            });

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ImagesActivity.class));
                   // checkEmailVerification();
                }else{
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("No of attempts remaining: " + counter);
                    progressDialog.dismiss();
                    if(counter == 0){
                        Login.setEnabled(false);
                    }
                }
            }
        });


    }
/*
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        startActivity(new Intent(LoginActivity.this, ImagesActivity.class));

     if(emailflag){
           finish();


          startActivity(new Intent(LoginActivity.this, ImagesActivity.class));
      }else{
          Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
           firebaseAuth.signOut();
        }
    }
    */

}
