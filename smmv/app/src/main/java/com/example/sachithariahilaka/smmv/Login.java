package com.example.sachithariahilaka.smmv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextView register,Forget;
    private Button login;
    private EditText user,pswd;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Boolean emailchecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        register=(TextView) findViewById(R.id.register_view);
        login=(Button) findViewById(R.id.login);
        user=(EditText) findViewById(R.id.username);
        pswd=(EditText) findViewById(R.id.password);
        Forget=findViewById(R.id.forget);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegister();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        Forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendUserToReset();

            }
        });



    }

    private void sendUserToReset()
    {
        Intent resetIntent=new Intent(Login.this, reset.class);
        startActivity(resetIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currunt_user=firebaseAuth.getCurrentUser();
        if(currunt_user!=null)
        {
            verifyEmail();
        }


    }

    private void verifyEmail()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        emailchecker=user.isEmailVerified();

        if(emailchecker)
        {
            Toast.makeText(Login.this,"You Are Logged In!!", Toast.LENGTH_SHORT).show();
            sendUserToMain();
        }

        else
        {
            Toast.makeText(this,"Please Verify Your Account...",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }




    private void userLogin()
    {
        String Email=user.getText().toString();
        String password=pswd.getText().toString();

        if(TextUtils.isEmpty(Email))
        {
            Toast.makeText(this,"Please Enter You Email", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }

        else
        {
            progressDialog.setMessage("Logging To Account...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            firebaseAuth.signInWithEmailAndPassword(Email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                verifyEmail();

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this,"Login Error!! Please Check Your Email And Password ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToMain()
    {
        Intent homeIntent=new Intent(Login.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void sendUserToRegister() {

        Intent registerIntent=new Intent(Login.this, Register.class);
        startActivity(registerIntent);
    }
}
