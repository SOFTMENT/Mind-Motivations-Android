package in.softment.mindmotivation.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.softment.mindmotivation.Adapter.CategoryAdapter;
import in.softment.mindmotivation.Model.CategoryModel;
import in.softment.mindmotivation.NotificationActivity;
import in.softment.mindmotivation.PlayListActivity;
import in.softment.mindmotivation.R;
import in.softment.mindmotivation.Util.Services;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;

    private TextView noCategoriesAvailable;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        view.findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });


                recyclerView = view.findViewById(R.id.recyclerview);

                noCategoriesAvailable = view.findViewById(R.id.noCategoryAvailable);

                // Assuming you have a layout file with a RecyclerView (activity_music.xml)
                adapter = new CategoryAdapter(categoryModels,getContext(), this::onCategoryClicked);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

                getAllCategories();

                return view;
    }
    private void onCategoryClicked(CategoryModel categoryModel) {
      Intent intent = new Intent(getContext(), PlayListActivity.class);
      intent.putExtra("catModel",categoryModel);
      startActivity(intent);
    }
    private void getAllCategories() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Categories").orderBy("createDate", Query.Direction.DESCENDING)
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {

                    if (e != null) {
                        Services.showDialog(getContext(),"ERROR",e.getLocalizedMessage());
                        return;
                    }

                    categoryModels.clear();
                    for (QueryDocumentSnapshot document : snapshots) {
                        CategoryModel category = document.toObject(CategoryModel.class);
                        categoryModels.add(category);
                    }

                    adapter.notifyDataSetChanged();
                    noCategoriesAvailable.setVisibility(categoryModels.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}
