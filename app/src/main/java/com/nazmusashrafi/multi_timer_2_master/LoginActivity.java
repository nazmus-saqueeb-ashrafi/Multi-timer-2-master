package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPwd;
    private Button loginBtn;
    private TextView loginQn;
    private TextView loginForgotPassword;
    private TextView loginSkip;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private FirebaseUser mCurrentUserAnym;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.editTextTextEmailAddress);
        loginPwd = findViewById(R.id.editTextTextPassword);
        loginBtn = (Button) findViewById(R.id.buttonLogin);
        loginQn = findViewById(R.id.textViewCreateNewAccount); //create new account
        loginForgotPassword = findViewById(R.id.textViewForgotPassword); //forgot password
        loginSkip = findViewById(R.id.textViewSkip);
        progressBar = findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserAnym = mAuth.getCurrentUser();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();

            }
        });

        loginForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });


        loginSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this,LoggedInTotalDashboardActivity.class);
//                startActivity(intent);


                //get intent from loggedinsettings fragment with mUser, then set mUser to mCurrentUserAnym


                //6:18
                if(mCurrentUserAnym == null){  //|| !mCurrentUserAnym.isEmailVerified()
                    progressBar.setVisibility(View.VISIBLE);


                    mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                System.out.println("logged in anym with id " );

                                Intent intent = new Intent(LoginActivity.this,LoggedInTotalDashboardActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(LoginActivity.this,"Error skipping login page",Toast.LENGTH_LONG).show();
                            }


                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }else{

                    Intent intent = new Intent(LoginActivity.this,LoggedInTotalDashboardActivity.class);
                    startActivity(intent);

                }


            }
        });


        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.GONE);

    }

    private void userLogin(){

        final String email = loginEmail.getText().toString().trim();
        final String password = loginPwd.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(password)){
            loginPwd.setError("Password is required");
            loginPwd.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) ){
            loginEmail.setError("Email is required");
            loginPwd.setError("Password is required");
            loginEmail.requestFocus();

        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please provide valid email");
            loginEmail.requestFocus();
            return;

        }

        if(password.length()<6){
            loginPwd.setError("Password should be atleast 6 characters long");
            loginPwd.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //verify email
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    if(user.isEmailVerified()){
                        //redirect to user profile
                        startActivity(new Intent(LoginActivity.this, LoggedInTotalDashboardActivity.class));

                    }else{
                        user.sendEmailVerification();

                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(LoginActivity.this,"Check your email to verify your account",Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.INVISIBLE);

                    }


                }else{
                    Toast.makeText(LoginActivity.this,"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }



}