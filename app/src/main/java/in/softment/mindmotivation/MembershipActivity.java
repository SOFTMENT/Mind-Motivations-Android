package in.softment.mindmotivation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.revenuecat.purchases.CustomerInfo;
import com.revenuecat.purchases.Offering;
import com.revenuecat.purchases.Offerings;
import com.revenuecat.purchases.Package;
import com.revenuecat.purchases.PurchaseParams;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesError;
import com.revenuecat.purchases.interfaces.PurchaseCallback;
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback;
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback;
import com.revenuecat.purchases.models.StoreTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import in.softment.mindmotivation.Util.Constants;
import in.softment.mindmotivation.Util.ProgressHud;
import in.softment.mindmotivation.Util.Services;


public class MembershipActivity extends AppCompatActivity {

    public String membershipType= "";
    RadioButton monthlyRadioBtn, yearlyRadioBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_membership_view_controller);

        monthlyRadioBtn = findViewById(R.id.radioBtn1);
        yearlyRadioBtn = findViewById(R.id.radioBtn2);

        monthlyRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthlyRadioBtn.setChecked(true);
                yearlyRadioBtn.setChecked(false);
                membershipType = "BLACK";
            }
        });

        yearlyRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthlyRadioBtn.setChecked(false);
                yearlyRadioBtn.setChecked(true);
                membershipType = "PLATINUM";
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services.logout(MembershipActivity.this);
            }
        });

        findViewById(R.id.subscribeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (membershipType.isEmpty()) {
                    Services.showCenterToast(MembershipActivity.this,"Select membership");
                }
                else {

                    ProgressHud.show(MembershipActivity.this,"");
                    Purchases.getSharedInstance().getOfferings(new ReceiveOfferingsCallback() {
                        @Override
                        public void onReceived(@NonNull Offerings offerings) {



                            Offering offering = offerings.getCurrent();

                            Package mPackage = null;

                            if (membershipType.equals("BLACK")) {
                                mPackage = offering.getAvailablePackages().get(0);
                            }
                            else if (membershipType.equals("PLATINUM")) {
                                mPackage = offering.getAvailablePackages().get(1);
                            }



                            Purchases.getSharedInstance().purchase(
                                    new PurchaseParams.Builder(MembershipActivity.this, mPackage).build(),
                                    new PurchaseCallback() {
                                        @Override
                                        public void onCompleted(@NonNull StoreTransaction storeTransaction, @NonNull CustomerInfo customerInfo) {
                                            ProgressHud.dialog.dismiss();
                                            if (customerInfo.getEntitlements().get("Premium") != null && customerInfo.getEntitlements().get("Premium").isActive()) {
                                                Services.showCenterToast(MembershipActivity.this,"Subscription Purchased Successfully");
                                                Constants.expireDate = customerInfo.getEntitlements().get("Premium").getExpirationDate();
                                                Constants.membershipType = membershipType;
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(MembershipActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);

                                                    }
                                                },1200);
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull PurchasesError purchasesError, boolean b) {
                                            ProgressHud.dialog.dismiss();
                                            Services.showDialog(MembershipActivity.this,"ERROR",purchasesError.getMessage());
                                        }
                                    }
                            );



                        }

                        @Override
                        public void onError(@NonNull PurchasesError purchasesError) {
                            ProgressHud.dialog.dismiss();
                            Services.showDialog(MembershipActivity.this,"ERROR",purchasesError.getMessage());
                        }
                    });


                }
                }

        });

        findViewById(R.id.restore).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProgressHud.show(MembershipActivity.this,"Restoring...");
                Purchases.getSharedInstance().restorePurchases(new ReceiveCustomerInfoCallback() {
                    @Override
                    public void onReceived(@NonNull CustomerInfo customerInfo) {
                        ProgressHud.dialog.dismiss();
                        if (customerInfo.getEntitlements().get("Premium") != null && customerInfo.getEntitlements().get("Premium").isActive()) {
                            Constants.expireDate = customerInfo.getEntitlements().get("Premium").getExpirationDate();
                            String identifier = customerInfo.getEntitlements().get("Premium").getProductIdentifier();
                            if (identifier.equals("in.softment.mindmotivations.monthly")) {
                                Constants.membershipType = "MONTHLY";
                            }
                            else if (identifier.equals("in.softment.mindmotivations.yearly")) {
                                Constants.membershipType = "YEARLY";
                            }
                            Intent intent  = new Intent(MembershipActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            Services.showCenterToast(MembershipActivity.this,"No active subscription found.");
                        }
                    }

                    @Override
                    public void onError(@NonNull PurchasesError purchasesError) {
                        ProgressHud.dialog.dismiss();
                        Services.showDialog(MembershipActivity.this,"ERROR",purchasesError.getMessage());
                    }


                });
            }
        });

        findViewById(R.id.privacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Services.openPdf(MembershipActivity.this,R.raw.privacypolicy);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        findViewById(R.id.termsOfUse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Services.openPdf(MembershipActivity.this,R.raw.termsofuse);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }


}
