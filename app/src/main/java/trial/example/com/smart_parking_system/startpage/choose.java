package trial.example.com.smart_parking_system.startpage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import trial.example.com.smart_parking_system.R;

public class choose extends AppCompatActivity implements View.OnClickListener {


    private Button btSignin,btSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);


        findViewById(R.id.btSignIn).setOnClickListener(this);
        findViewById(R.id.btSignUp).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btSignIn:
                startActivity(new Intent(this,signin.class));
                break;


            case R.id.btSignUp:
                startActivity(new Intent(this,signup.class));
                break;

        }
    }
}
