package in.softment.mindmotivation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.softment.mindmotivation.Adapter.NotificationAdapter;
import in.softment.mindmotivation.Model.NotificationModel;
import in.softment.mindmotivation.Util.ProgressHud;


public class NotificationActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationModel> notificationModels;
    private TextView no_notifications_available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        notificationModels = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this,notificationModels);
        recyclerView.setAdapter(notificationAdapter);
        no_notifications_available = findViewById(R.id.no_notifications_available);

        //BACK
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getNotifications();
    }

    private void getNotifications() {
        ProgressHud.show(this,"");
        FirebaseFirestore.getInstance().collection("Notifications").orderBy("notificationTime",
                Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    notificationModels.clear();
                    if (task.getResult() != null && !task.getResult().isEmpty()) {

                        for(DocumentSnapshot documentsnap : task.getResult().getDocuments()) {

                            NotificationModel notificationModel = documentsnap.toObject(NotificationModel.class);
                            notificationModels.add(notificationModel);

                        }
                    }
                    if (notificationModels.size() > 0) {
                        no_notifications_available.setVisibility(View.GONE);
                    }
                    else {
                        no_notifications_available.setVisibility(View.VISIBLE);
                    }
                    notificationAdapter.notifyDataSetChanged();
                }

            }
        });
    }
}
