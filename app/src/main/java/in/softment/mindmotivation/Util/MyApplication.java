package in.softment.mindmotivation.Util;

import android.app.Application;

import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Purchases.configure(new PurchasesConfiguration.Builder(this, "goog_UrpMvJFDIDoIgihTkBzzCHeotbh").build());
    }
}
