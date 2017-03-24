package com.example.jit.checkmate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText email_field,pass_field,name_field,contactno_field;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_field = (EditText)findViewById(R.id.email_text);
        pass_field = (EditText)findViewById(R.id.passwordtext);
        name_field = (EditText)findViewById(R.id.name_field);
        contactno_field = (EditText)findViewById(R.id.contactno_field);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };

        mRootref = FirebaseDatabase.getInstance().getReference();
    }

    public void signup(View v){

        final String email=email_field.getText().toString();
        String password=pass_field.getText().toString();
        final String conactno = contactno_field.getText().toString();
        if(email.length()==0){email_field.setError("required !"); return;}
        if(password.length()<6){pass_field.setError("password must be of atleast length 6"); return;}

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "createUserWithEmail:onComplete: " + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "registration failed", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name_field.getText().toString())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String[] tokens = email.split("@");
                                        String id = tokens[0];
                                        DatabaseReference cref = FirebaseDatabase.getInstance().getReference().child("user");
                                        cref.child(id).setValue(conactno);
                                        Intent i = new Intent(SignUp.this,MainActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                    Toast.makeText(SignUp.this,"registration successful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
