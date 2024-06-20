package in.softment.mindmotivation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import in.softment.mindmotivation.Util.MyAudioPlayer;
import in.softment.mindmotivation.Util.Services;

public class Welcome2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);
        Services.fullScreen(this);

        MyAudioPlayer.getInstance().playBackground(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
             Services.getUserData(this, FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
    }
}
