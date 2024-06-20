package in.softment.mindmotivation.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import in.softment.mindmotivation.R;
import in.softment.mindmotivation.Util.MyAudioPlayer;
import in.softment.mindmotivation.Util.Services;


public class RelaxFragment extends Fragment {

    private Timer timer;

    private TextView breatheInOutText;
    private ImageView playBtn;
    private ImageView volumeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_relax, container, false);

       breatheInOutText = view.findViewById(R.id.breatheInText);
       playBtn = view.findViewById(R.id.playBtn);
       playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBreathe();
            }
        });
       volumeBtn = view.findViewById(R.id.volume);
       volumeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               volumeBtnTapped();
           }
       });

       view.findViewById(R.id.breatheImage).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               playBreathe();
           }
       });

       return view;
    }
    private void updateCounting() {
        // Assume MyAudioPlayer.getCurrentTimeForBreathe() returns an Integer
        int count = MyAudioPlayer.getInstance().getCurrentTimeForBreathe();



        if (count == 0 || count == 15 || count == 31 || count == 46) {


           getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    breatheInOutText.setText("Breathe\nIn");
                }
            });


        } else if (count == 9 || count == 24 || count == 40 || count == 56) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    breatheInOutText.setText("Breathe\nOut");
                }
            });


        } else if (count == 5 || count == 20 || count == 36 || count == 49) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    breatheInOutText.setText("Hold");
                }
            });

        }


    }

    public void pauseBreathe(){

        if (MyAudioPlayer.getInstance().isBreathePlaying()) {
            MyAudioPlayer.getInstance().stopBreathe();
            playBtn.setImageResource(R.drawable.playbutton);
            MyAudioPlayer.getInstance().changeBackgroundVolume(0.8f);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    private void volumeBtnTapped() {
        float volume = MyAudioPlayer.getInstance().getBreatheVolume();

        if (volume == 0) {
            volumeBtn.setImageResource(R.drawable.volume); // Replace with your drawable resource
            MyAudioPlayer.getInstance().changeBreatheVolume(0.9f); // Assuming changeBreatheVolume takes a float
        } else {
           volumeBtn.setImageResource(R.drawable.silent); // Replace with your drawable resource
            MyAudioPlayer.getInstance().changeBreatheVolume(0);
        }
    }
    public void playBreathe(){
        if (MyAudioPlayer.getInstance().isBreathePlaying()) {
            MyAudioPlayer.getInstance().stopBreathe();
            playBtn.setImageResource(R.drawable.playbutton);
            MyAudioPlayer.getInstance().changeBackgroundVolume(0.8f);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
        else {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateCounting();
                }
            }, 0, 1000); // 0 is the initial delay, 1000 is the period (1 second)

            MyAudioPlayer.getInstance().changeBreatheVolume(0.2f);
            MyAudioPlayer.getInstance().playBreathe(getContext());
            playBtn.setImageResource(R.drawable.pausebutton);

        }
    }
}
