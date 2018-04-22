package trial.example.com.smart_parking_system.startpage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import trial.example.com.smart_parking_system.MainActivity;
import trial.example.com.smart_parking_system.R;

public class signin extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private TextInputEditText etEmail,etPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth=FirebaseAuth.getInstance();
        etEmail=findViewById(R.id.etEmail1);
        etPass=findViewById(R.id.etPass1);

        findViewById(R.id.btSignIn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=etEmail.getText().toString();
                String pass=etPass.getText().toString();

                if(email.length()<9 || pass.length()<6)
                    Toast.makeText(getApplicationContext(),"Please enter valid username and password",Toast.LENGTH_LONG).show();
                else{
                    fnSignIn(email,pass);
                }


            }
        });

    }



    void fnSignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(signin.this, "Sucessfully signed in.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signin.this, MainActivity.class));
                            finish();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(signin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



}
