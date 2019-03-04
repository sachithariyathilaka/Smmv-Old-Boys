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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Register extends AppCompatActivity{

    private Button register;
    private TextView login;
    private EditText email,password,confirm;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        register=(Button) findViewById(R.id.register);
        login=(TextView) findViewById(R.id.login);
        email=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        confirm=(EditText) findViewById(R.id.confirm);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent=new Intent(Register.this, Login.class);
                startActivity(loginIntent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerAccount();
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currunt_user=firebaseAuth.getCurrentUser();
        if(currunt_user!=null)
        {
            sendUserToMain();
        }
    }

    private void sendEmailVerificationMessage()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Register.this,"Registration Succusfull!! Please Check your inbox to verify your account.....",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }



                    else
                    {
                        Toast.makeText(Register.this,"Error Occured!!",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }

                }
            });
        }
    }

    private void sendUserToMain()
    {
        Intent homeIntent=new Intent(Register.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void registerAccount() {

        String Email=email.getText().toString();
        String Password=password.getText().toString();
        String repasword=confirm.getText().toString();

        if(TextUtils.isEmpty(Email))
        {
            Toast.makeText(this,"Please Enter Your Email", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(repasword))
        {
            Toast.makeText(this,"Please Re-Type Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(!Password.equals(repasword))
        {
            Toast.makeText(this,"Your Password is Doesn't Match. Please Check Again!!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            progressDialog.setMessage("Registering User...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this,"Registered Succusfully!!!",Toast.LENGTH_SHORT).show();
                                sendEmailVerificationMessage();
                                senduserToSetup();

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this,"Oops Try Again!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }

    private void senduserToSetup()
    {
        Intent setupIntent=new Intent(Register.this, setup.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
