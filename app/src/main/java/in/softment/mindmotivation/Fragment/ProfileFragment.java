package in.softment.mindmotivation.Fragment;

import static in.softment.mindmotivation.Util.ProgressHud.show;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import in.softment.mindmotivation.MainActivity;
import in.softment.mindmotivation.MembershipActivity;
import in.softment.mindmotivation.Model.UserModel;
import in.softment.mindmotivation.NotificationActivity;
import in.softment.mindmotivation.R;
import in.softment.mindmotivation.SignInActivity;
import in.softment.mindmotivation.Util.ProgressHud;
import in.softment.mindmotivation.Util.Services;


public class ProfileFragment extends Fragment {


    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

        view.findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        //LOGOUT
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Services.logout(getContext());
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        view.findViewById(R.id.privacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Services.openPdf(getContext(),R.raw.privacypolicy);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        view.findViewById(R.id.termsOfUse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Services.openPdf(getContext(),R.raw.termsofuse);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        view.findViewById(R.id.faqs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Services.openPdf(getContext(),R.raw.faqs);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        view.findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "mindmotivationsapp@gmail.com" });
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        view.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                aboutUsFragment.show(mainActivity.getSupportFragmentManager(),aboutUsFragment.getTag());
            }
        });

        view.findViewById(R.id.disclaimer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisclaimerFragment disclaimerFragment = new DisclaimerFragment();
                disclaimerFragment.show(mainActivity.getSupportFragmentManager(),disclaimerFragment.getTag());
            }
        });

        view.findViewById(R.id.deleteAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setCancelable(false);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressHud.show(getContext(),"Deleting ...");
                        FirebaseFirestore.getInstance().collection("Users").document(UserModel.data.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ProgressHud.dialog.dismiss();
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(getContext(), SignInActivity.class));

                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                                            builder.setTitle("LOGIN AGAIN");
                                            builder.setMessage("Please login and try again delete account.");
                                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startActivity(new Intent(getContext(), SignInActivity.class));

                                                }
                                            });

                                            builder.show();

                                        }
                                    }
                                });
                            }
                        });
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}
