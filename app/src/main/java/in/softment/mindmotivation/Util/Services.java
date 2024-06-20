package in.softment.mindmotivation.Util;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.revenuecat.purchases.CustomerInfo;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesError;
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.softment.mindmotivation.MainActivity;
import in.softment.mindmotivation.MembershipActivity;
import in.softment.mindmotivation.Model.UserModel;
import in.softment.mindmotivation.R;
import in.softment.mindmotivation.SignInActivity;
import in.softment.mindmotivation.SignUpActivity;
import in.softment.mindmotivation.Welcome2Activity;
import in.softment.mindmotivation.WelcomeActivity;

public class Services {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "moments ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
    public static void addUserDataOnServer(Context context, UserModel userModel){

        ProgressHud.show(context,"");
        FirebaseFirestore.getInstance().collection("Users").document(userModel.getUid()).set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressHud.dialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(context, Welcome2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    if (context instanceof SignInActivity) {
                        ((SignInActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    else {
                        ((SignUpActivity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
                else {
                    Services.showDialog(context,"ERROR",task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public static void fullScreen(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }
    public static String convertSecondsToMinAndSec(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
    public static void showCenterToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    public static Date convertStringToDate(String sDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(sDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }
    public static String convertDateToDayName(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "EEEE";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }
    public static  String convertDateToTimeString(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "dd-MMM-yyyy, hh:mm a";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static int getAge(Date dob) {
        long diff = new Date().getTime() - dob.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 365;
    }

    public static  String convertDateToString(Date date) {
        if (date == null) {
            date = new Date();
        }
        date.setTime(date.getTime());
        String pattern = "dd-MMM-yyyy";
        DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return  df.format(date);
    }

    public static void sentEmailVerificationLink(Context context){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ProgressHud.show(context,"");
            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ProgressHud.dialog.dismiss();

                    if (task.isSuccessful()) {
                        showDialog(context,"Vérifiez votre e-mail","Nous avons envoyé un lien de vérification sur votre adresse e-mail.");
                    }
                    else {
                        showDialog(context, "ERROR", task.getException().getLocalizedMessage());
                    }
                }
            });
        }
        else {
            ProgressHud.dialog.dismiss();
        }
    }

    public static void showDialog(Context context,String title,String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        Activity activity = (Activity) context;
        View view = activity.getLayoutInflater().inflate(R.layout.error_message_layout, null);
        TextView titleView = view.findViewById(R.id.title);
        TextView msg = view.findViewById(R.id.message);
        titleView.setText(title);

        msg.setText(message);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", message);
                manager.setPrimaryClip(clipData);
                Services.showDialog(context,"Copié","Le texte a été copié");

            }
        });
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


            }
        });

        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();

        }

    }

    public static void logout(Context context) {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }


//
//    public static void addFavorite(String nannyUid, CheckIsSuccessCallback checkIsSuccessCallback) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("id",nannyUid);
//        map.put("date", new Date());
//        FirebaseFirestore.getInstance().collection("Parents").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(nannyUid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    checkIsSuccessCallback.completion(true);
//                }
//                else {
//                    checkIsSuccessCallback.completion(false);
//                }
//            }
//        });
//    }
//
//
//    public static void getFavoriteStatus(Context context, String nannyUid, CheckFavoritesCallback checkFavoritesCallback){
//
//        FirebaseFirestore.getInstance().collection("Parents").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(nannyUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult() != null && task.getResult().exists()) {
//
//                        checkFavoritesCallback.completion(true);
//                    }
//                    else {
//                        checkFavoritesCallback.completion(false);
//                    }
//                }
//                else {
//                    checkFavoritesCallback.completion(false);
//                }
//            }
//        });
//
//    }

    public static void openPdf(Context context,Integer raw) throws IOException {
        // Open the PDF file from raw folder
        InputStream inputStream = context.getResources().openRawResource(raw);

        // Copy the file to the cache folder
        File file = new File(context.getCacheDir(), raw+".pdf");
        FileOutputStream output = new FileOutputStream(file);
        try {
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File cacheFile = new File(context.getCacheDir(), raw+".pdf");

        // Get the URI of the cache file from the FileProvider
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", cacheFile);
        if (uri != null) {
            // Create an intent to open the PDF in a third-party app
            Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
            pdfViewIntent.setData(uri);
            pdfViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(pdfViewIntent, "Choose PDF viewer"));
        }
    }

    public static void getUserData(Welcome2Activity welcome2Activity,String uid, boolean showProgress) {

        if (showProgress) {
            ProgressHud.show(welcome2Activity,"");
        }

        FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (showProgress) {
                    ProgressHud.dialog.dismiss();
                }

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            UserModel.data = documentSnapshot.toObject(UserModel.class);
                           
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Purchases.getSharedInstance().getCustomerInfo(new ReceiveCustomerInfoCallback() {
                                        @Override
                                        public void onReceived(@NonNull CustomerInfo customerInfo) {

                                            if (customerInfo.getEntitlements().get("Premium") != null && customerInfo.getEntitlements().get("Premium").isActive()) {
                                                Constants.expireDate = customerInfo.getEntitlements().get("Premium").getExpirationDate();
                                                String identifier = customerInfo.getEntitlements().get("Premium").getProductIdentifier();
                                                if (identifier.equals("in.softment.mindmotivations.monthly")) {
                                                    Constants.membershipType = "MONTHLY";
                                                }
                                                else if (identifier.equals("in.softment.mindmotivations.yearly")) {
                                                    Constants.membershipType = "YEARLY";
                                                }
                                                Intent intent  = new Intent(welcome2Activity, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                welcome2Activity.startActivity(intent);
                                                welcome2Activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            }
                                            else {

                                                    Intent intent  = new Intent(welcome2Activity, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    welcome2Activity.startActivity(intent);


                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull PurchasesError purchasesError) {

                                            Intent intent  = new Intent(welcome2Activity, MembershipActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            welcome2Activity.startActivity(intent);
                                        }
                                    });

                                }
                            },2000);
                        }
                        else {
                            Intent intent = new Intent(welcome2Activity, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            welcome2Activity.startActivity(intent);
                        }

                    }
                    else {
                        Services.showDialog(welcome2Activity, "ERROR", "Something went wrong");

                    }
                }
                else {
                    Services.showDialog(welcome2Activity, "ERROR", task.getException().getLocalizedMessage());

                }

            }
        });
    }
}
