package org.techtown.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
{
    EditText etEmail;
    EditText etPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String stEmail;
    String stPassword;

    String userEmail;

    String TAG = "LoginActivity";


    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail =(EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        Button btnRegister = (Button)findViewById(R.id.btnRegister);


        mAuth = FirebaseAuth.getInstance();

        etEmail.setText("");
        etPassword.setText("");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    SharedPreferences sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };




        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();

                if(isValidEmail() && isValidPasswd()) {
                    registerUser(stEmail, stPassword);
                }
            }
        });

        Button btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();

                if(isValidEmail() && isValidPasswd())
                {
                    userLogin(stEmail,stPassword);
                }
            }
        });
    }


    public void registerUser(String email, String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(LoginActivity.this, "회원가입 성공" , Toast.LENGTH_SHORT).show();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(LoginActivity.this, "회원가입 실패" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



    public void userLogin(String email, String password)
    {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (task.isSuccessful())
                        {
                            // 로그인 성공

                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(LoginActivity.this,TabActivity.class);
                            startActivity(in);
                            finish();
                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (stEmail.isEmpty())
        {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(stEmail).matches())
        {
            // 이메일 형식 불일치
            return false;
        }
        else
            {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (stPassword.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(stPassword).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

}
