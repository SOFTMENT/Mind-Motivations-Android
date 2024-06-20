package in.softment.mindmotivation.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

import in.softment.mindmotivation.Adapter.AlbumAdapter;
import in.softment.mindmotivation.Model.AlbumModel;
import in.softment.mindmotivation.Model.UserModel;
import in.softment.mindmotivation.R;
import in.softment.mindmotivation.Util.ProgressHud;
import in.softment.mindmotivation.Util.Services;

public class FavouritesFragment extends Fragment {

    public LinearLayout noAlbumAvailable;
    private View addAlbumView;
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private ArrayList<AlbumModel> albumModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        noAlbumAvailable = view.findViewById(R.id.no_albums_available);
        addAlbumView = view.findViewById(R.id.addAlbum);
        recyclerView = view.findViewById(R.id.recyclerview);
        albumAdapter = new AlbumAdapter(getContext(),albumModels);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(albumAdapter);
        addAlbumView.setOnClickListener(v -> createAlbumClicked());
        getAllAlbums();

        return view;
    }

    private void getAllAlbums() {


        FirebaseFirestore.getInstance().collection("Users")
                .document(UserModel.data.getUid())
                .collection("Albums")
                .orderBy("name")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        // Handle the error. E.g., show a toast message
                        return;
                    }

                    albumModels.clear();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            AlbumModel album = document.toObject(AlbumModel.class);
                            albumModels.add(album);
                        }


                    }

                    if (albumModels.size() > 0) {
                        noAlbumAvailable.setVisibility(View.GONE);
                    }
                    else {
                        noAlbumAvailable.setVisibility(View.VISIBLE);
                    }
                    albumAdapter.notifyDataSetChanged();
                });
    }

    private void createAlbumClicked() {
        // 1. Create the AlertDialog.Builder

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Album");
        builder.setMessage("Enter album name");

        // 2. Add an EditText field to the dialog
        final EditText input = new EditText(getContext());
        input.setText("");
        builder.setView(input);

        // 3. Add the "Create" button
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sAlbumName = input.getText().toString().trim();
                if (!sAlbumName.isEmpty()) {
                    createNewAlbum(sAlbumName);
                }
            }
        });

        // 4. Add the "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // 5. Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void createNewAlbum(String name) {
        // Show progress HUD
        // Example: showProgressDialog("Loading...");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String docId = firestore.collection("Users").document(userId).collection("Albums").document().getId();

        AlbumModel albumModel = new AlbumModel();
        albumModel.setId(docId);
        albumModel.setName(name);
        albumModel.setTracks(0);

        ProgressHud.show(getContext(),"");
        firestore.collection("Users").document(userId).collection("Albums").document(docId)
                .set(albumModel)
                .addOnSuccessListener(aVoid -> {
                    ProgressHud.dialog.dismiss();
                    Services.showCenterToast(getContext(),"Album Added");
                })
                .addOnFailureListener(e -> {
                    ProgressHud.dialog.dismiss();
                    Services.showDialog(getContext(),"ERROR",e.getMessage());
                });
    }



}
