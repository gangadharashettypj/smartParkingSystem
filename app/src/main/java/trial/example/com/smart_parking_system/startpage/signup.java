package trial.example.com.smart_parking_system.startpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import trial.example.com.smart_parking_system.MainActivity;
import trial.example.com.smart_parking_system.R;

public class signup extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private Button btSignUp;
    private ProgressDialog pd;
    private TextInputEditText etName,etId,etUser,etPass,etAddress,etPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference();

        pd=new ProgressDialog(this);
        pd.setTitle("Regestering...");
        pd.setMessage("Please wait...");


        etName=findViewById(R.id.etName);
        etUser=findViewById(R.id.etUserName);
        etPass=findViewById(R.id.etPassword);
        etId=findViewById(R.id.etId);
        etAddress=findViewById(R.id.etAddress);
        etPhone=findViewById(R.id.etPhone);

        findViewById(R.id.btSignUp1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=etUser.getText().toString();
                String pass=etPass.getText().toString();

                if(email.length()<9 || pass.length()<6)
                    Toast.makeText(getApplicationContext(),"Please enter valid username and password",Toast.LENGTH_LONG).show();
                else{
                    pd.show();
                    fnSignUp(email.replace(" ",""),pass);
                }
            }
        });

    }

    void fnSignUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signup", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(signup.this, "Sucessfully created.",
                                    Toast.LENGTH_SHORT).show();

                            HashMap<String,String> usermap=new HashMap<>();
                            usermap.put("name",etName.getText().toString());
                            usermap.put("address",etAddress.getText().toString());
                            usermap.put("id",etId.getText().toString());
                            usermap.put("phone",etPhone.getText().toString());

                            mData.child("user").child(mAuth.getUid()).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       startActivity(new Intent(signup.this, MainActivity.class));
                                       finish();
                                       finish();
                                   }
                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signup", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
