package in.softment.mindmotivation;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
        import android.widget.TextView;
        import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import in.softment.mindmotivation.Adapter.PlaylistAdapter;
import in.softment.mindmotivation.Model.AlbumModel;
import in.softment.mindmotivation.Model.CategoryModel;
import in.softment.mindmotivation.Model.FavouriteModel;
import in.softment.mindmotivation.Model.MusicModel;
import in.softment.mindmotivation.Util.ProgressHud;
import in.softment.mindmotivation.Util.Services;

public class PlayListActivity extends AppCompatActivity {

    private ImageView backView;
    private TextView categoryName;
    private TextView totalTracks;
    private TextView noMusicsAvailable;
    private RecyclerView recyclerView;
    // Assuming CategoryModel, MusicModel, and AlbumModel are properly defined Java classes
    private CategoryModel categoryModel;
    private ArrayList<MusicModel> musicModels = new ArrayList<>();
    private List<AlbumModel> albumModels = new ArrayList<>();
    // Assuming PlaylistAdapter is a properly defined RecyclerView.Adapter subclass
    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // This line will allow your layout to be drawn under the status bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        setContentView(R.layout.activity_play_list); // Set to your layout file

        backView = findViewById(R.id.back);
        categoryName = findViewById(R.id.title);
        totalTracks = findViewById(R.id.tracks);
        noMusicsAvailable = findViewById(R.id.noCategoryAvailable);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistAdapter(this, musicModels, new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(PlayListActivity.this, PlayMusicActivity.class);

                intent.putExtra("musicModels", musicModels);
                intent.putExtra("position", position);
                startActivity(intent);
            }

            @Override
            public void onFavClicked(MusicModel musicModel) {
                    setFav(musicModel.getId());
            }
        });
        recyclerView.setAdapter(adapter);

        // Set up RecyclerView, Adapter, LayoutManager...

        backView.setOnClickListener(view -> finish());

        categoryModel = (CategoryModel) getIntent().getSerializableExtra("catModel");
        categoryName.setText(categoryModel.getTitle());
        totalTracks.setText(String.format("%d Tracks", categoryModel.getTracks()));

        getAllMusics();
    }

    private void getAllMusics() {
        ProgressHud.show(this,"");
        FirebaseFirestore.getInstance()
                .collection("Categories")
                .document(categoryModel.getId())
                .collection("Musics")
                .orderBy("title")
                .addSnapshotListener((snapshot, error) -> {
                   ProgressHud.dialog.dismiss();

                    if (error != null) {
                        // Handle error
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        musicModels.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            MusicModel musicModel = document.toObject(MusicModel.class);
                            musicModels.add(musicModel);
                        }
                    }

                    noMusicsAvailable.setVisibility(musicModels.isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.notifyDataSetChanged(); // Notify adapter about data changes
                });
    }

    public void setFav(String id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            ProgressHud.show(PlayListActivity.this,"");
            checkIsFavorite(id,user.getUid(), isFav -> {
                ProgressHud.dialog.dismiss();
                if (isFav) {

                    ProgressHud.show(PlayListActivity.this,"");

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
                                       adapter.notifyDataSetChanged();
                                    }
                                });
                            });
                } else {
                    ProgressHud.show(PlayListActivity.this,"");
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

                                        addFav(id, categoryModel.getId(), selectedAlbum.getId(), user.getUid(), success -> {
                                            if (!success) {
                                               // adapter.notifyDataSetChanged();
                                            } else {
                                                updateAlbumTracks(selectedAlbum.getId(), 1);
                                            }
                                        });
                                    });

                                } else {
                                    Services.showCenterToast(PlayListActivity.this,"No album found");
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
        adapter.notifyDataSetChanged();
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


    public void checkIsFavorite(String musicId, String userId, OnCheckFavoriteListener listener) {
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

    public void addFav(String musicId, String catId, String albumId, String uid, final OnCompletionListener listener) {
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

    public void deleteFav(String musicId, String uid, final OnCompletionListener listener) {
        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Favorites")
                .document(musicId).delete()
                .addOnSuccessListener(aVoid -> listener.onCompleted(true))
                .addOnFailureListener(e -> listener.onCompleted(false));
    }

    public interface OnCompletionListener {
        void onCompleted(boolean isSuccess);
    }
    public interface OnCheckFavoriteListener {
        void onCheckFavoriteCompleted(boolean isFavorite);
    }
}

