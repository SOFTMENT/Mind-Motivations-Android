package in.softment.mindmotivation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import in.softment.mindmotivation.Fragment.DisclaimerFragment;
import in.softment.mindmotivation.Fragment.MusicInformationFragment;
import in.softment.mindmotivation.Model.AlbumModel;
import in.softment.mindmotivation.Model.FavouriteModel;
import in.softment.mindmotivation.Model.MusicModel;
import in.softment.mindmotivation.Model.UserModel;
import in.softment.mindmotivation.Util.MyAudioPlayer;
import in.softment.mindmotivation.Util.ProgressHud;
import in.softment.mindmotivation.Util.Services;

public class PlayMusicActivity extends AppCompatActivity {
    private RoundedImageView musicImage;
    private TextView musicName, artist, genre, startTime, endTime, catName;
    private AppCompatSeekBar seekBar;
    private ImageView playPause, next, previous, fav, upload;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private List<MusicModel> musicModels;
    private int position = 0;
    private Runnable updateRunnable;

    public PlayMusicActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        musicImage = findViewById(R.id.imageView);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        musicName = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        genre = findViewById(R.id.genre);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekbar);
        fav = findViewById(R.id.fav);




        catName = findViewById(R.id.catName);

        Intent intent = getIntent();
        musicModels = (ArrayList<MusicModel>) intent.getSerializableExtra("musicModels");
        position = intent.getIntExtra("position",0);
        checkIsFavorite(musicModels.get(position).getId(), UserModel.data.getUid(), new PlayListActivity.OnCheckFavoriteListener() {
            @Override
            public void onCheckFavoriteCompleted(boolean isFavorite) {
                if (isFavorite) {
                   fav.setImageResource(R.drawable.favouritefilled);
                }
                else {
                    fav.setImageResource(R.drawable.love);
                }
            }
        });
        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicInformationFragment disclaimerFragment = new MusicInformationFragment(musicModels.get(position).getAbout());
                disclaimerFragment.show(getSupportFragmentManager(),disclaimerFragment.getTag());
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFav(musicModels.get(0).getId());
            }
        });
        loadUI();
        if (position >= 0 && position < musicModels.size()) {
            MusicModel musicModel = musicModels.get(position);
            catName.setText(musicModel.getCatName());
            String musicURL = musicModel.getMusicUrl(); // Replace getMusicUrl with your actual method to get the URL
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(musicURL);

                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                play();
            } catch (IOException e) {

                throw new RuntimeException(e);
            }


        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBtnClicked();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtnClicked();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               previousBtnClicked();
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        play();
                    } else {
                        pause();
                    }
                }


            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress * 1000); // Since seekTo() takes milliseconds
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Implement if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Implement if needed
            }
        });


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateRunnable);
                MyAudioPlayer.getInstance().playBackground(PlayMusicActivity.this);
                finish();

            }
        });

    }

    private void loadUI() {
        MusicModel currentMusic = musicModels.get(position);


        musicName.setText(currentMusic.getTitle());
        artist.setText(currentMusic.getArtistName());
        genre.setText(currentMusic.getGenre());

        // Set the album art image
        // This assumes you have a method to load images from a URL. You might use a library like Glide or Picasso.
        loadImage(musicImage, currentMusic.getThumbnail());

        // Initialize the progress bar
        seekBar.setProgress(0);
        seekBar.setMax(currentMusic.getDuration());

        playPause = findViewById(R.id.play);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        fav = findViewById(R.id.fav);
        upload = findViewById(R.id.upload);

        // Convert total seconds to minutes and seconds for display
        endTime.setText(convertSecondsToMinAndSec(currentMusic.getDuration()));
        startTime.setText("00:00");
    }

    private void loadImage(RoundedImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Using Glide as an example
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder) // Placeholder image
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }
    }

    public void checkIsFavorite(String musicId, String userId, PlayListActivity.OnCheckFavoriteListener listener) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("Users").document(userId)
                .collection("Favorites").document(musicId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        listener.onCheckFavoriteCompleted(true);
                    } else {
                        listener.onCheckFavoriteCompleted(false);
                    }
                } else {
                    listener.onCheckFavoriteCompleted(false);
                }
            }
        });
    }
    public void setFav(String id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            ProgressHud.show(PlayMusicActivity.this,"");
            checkIsFavorite(id,user.getUid(), isFav -> {
                ProgressHud.dialog.dismiss();
                if (isFav) {

                    ProgressHud.show(PlayMusicActivity.this,"");

                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(user.getUid())
                            .collection("Favorites")
                            .whereEqualTo("musicId", id)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    FavouriteModel favModel = documentSnapshot.toObject(FavouriteModel.class);

                                    if (favModel != null) {
                                        updateAlbumTracks(favModel.getAlbumId(), -1);
                                    }
                                }

                                deleteFav(id, user.getUid(), success -> {
                                    ProgressHud.dialog.dismiss();
                                    if (success) {
                                        fav.setImageResource(R.drawable.love);
                                    }
                                });
                            });
                } else {
                    ProgressHud.show(PlayMusicActivity.this,"");
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(user.getUid())
                            .collection("Albums")
                            .orderBy("name")
                            .get()
                            .addOnCompleteListener(task -> {
                                ProgressHud.dialog.dismiss();
                                if (task.isSuccessful()) {
                                    List<AlbumModel> albumModels = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        albumModels.add(document.toObject(AlbumModel.class));
                                    }

                                    // Handle creating and showing alert dialog similar to UIAlertController in iOS
                                    // Assume `createSelectAlbumDialog` is a method that creates an AlertDialog for selecting an album
                                    createSelectAlbumDialog(albumModels, selectedAlbum -> {

                                        addFav(id, musicModels.get(0).getCatId(), selectedAlbum.getId(), user.getUid(), success -> {
                                            if (!success) {
                                                // adapter.notifyDataSetChanged();
                                            } else {
                                                updateAlbumTracks(selectedAlbum.getId(), 1);
                                            }
                                        });
                                    });

                                } else {
                                    Services.showCenterToast(PlayMusicActivity.this,"No album found");
                                }
                            });
                }
            });
        }
    }
    private void createSelectAlbumDialog(List<AlbumModel> albumModels, Consumer<AlbumModel> onAlbumSelected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Album");

        // Convert the album names into a String array for the adapter.
        String[] albumNames = new String[albumModels.size()];
        for (int i = 0; i < albumModels.size(); i++) {
            albumNames[i] = albumModels.get(i).getName();
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_selectable_list_item, albumNames);

        builder.setAdapter(adapter, (dialog, which) -> {
            // Here 'which' is the index of the selected item
            AlbumModel selectedAlbum = albumModels.get(which);
            onAlbumSelected.accept(selectedAlbum);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void updateAlbumTracks(String albumId, int count) {
        fav.setImageResource(R.drawable.favouritefilled);
        DocumentReference sfReference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Albums")
                .document(albumId);

        FirebaseFirestore.getInstance().runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot sfDocument = transaction.get(sfReference);
            Long oldTracks = sfDocument.getLong("tracks");
            if (oldTracks != null) {
                transaction.update(sfReference, "tracks", oldTracks + count);
            }
            return null;
        }).addOnCompleteListener(task -> {

        });
    }
    public void addFav(String musicId, String catId, String albumId, String uid, final PlayListActivity.OnCompletionListener listener) {
        FavouriteModel favModel = new FavouriteModel();
        favModel.setAlbumId(albumId);
        favModel.setMusicId(musicId);
        favModel.setUid(uid);
        favModel.setCatId(catId);
        favModel.setCreateDate(new Date());

        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Favorites")
                .document(musicId).set(favModel)
                .addOnSuccessListener(aVoid -> listener.onCompleted(true))
                .addOnFailureListener(e -> listener.onCompleted(false));
    }

    public void deleteFav(String musicId, String uid, final PlayListActivity.OnCompletionListener listener) {
        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Favorites")
                .document(musicId).delete()
                .addOnSuccessListener(aVoid -> listener.onCompleted(true))
                .addOnFailureListener(e -> listener.onCompleted(false));
    }
    private void play() {

        MyAudioPlayer.getInstance().stopBackground();

        mediaPlayer.start();


        // Updating play/pause button image
        playPause.setImageResource(R.drawable.pause3); // Replace with your pause button drawable

        // Update UI every second
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCounting();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateRunnable, 1000);
    }

    private void pause() {
        MyAudioPlayer.getInstance().playBackground(this);
        handler.removeCallbacks(updateRunnable);
        mediaPlayer.pause();
        // Updating play/pause button image
        playPause.setImageResource(R.drawable.playbutton3); // Replace with your play button drawable

    }
    private void updateCounting() {

            int duration = mediaPlayer.getCurrentPosition() / 1000; // Get current position in seconds
            startTime.setText(convertSecondsToMinAndSec(duration)); // Update start time TextView
            seekBar.setProgress(duration); // Update progress bar



            if (duration >= seekBar.getMax()) {
                if (position < musicModels.size() -1) {
                    nextBtnClicked(); // Method to play the next song
                }
                else {

                    handler.removeCallbacks(updateRunnable);
                    playPause.setImageResource(R.drawable.playbutton3);
                    MyAudioPlayer.getInstance().playBackground(PlayMusicActivity.this);
                    seekBar.setProgress(0);
                    startTime.setText("00:00");
                }
            }


    }

    private void nextBtnClicked() {
        if (position < musicModels.size() - 1) {
            position++;

            // Assuming loadUI method sets up the player with new track
            loadUI();


            if (!musicModels.get(position).getMusicUrl().isEmpty()) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(musicModels.get(position).getMusicUrl());
                    handler.removeCallbacks(updateRunnable);
                    play();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void previousBtnClicked() {
        if (position > 0) {
            position--;

            // Assuming loadUI method sets up the player with new track
            loadUI();
            play();
        }
    }


    private void shareBtnClicked() {
        String someText = "Check Out Mind Motivation App.";
        String url = "https://apps.apple.com/us/app/mind-motivation/1603551591";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, someText + " " + url);
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private String convertSecondsToMinAndSec(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
