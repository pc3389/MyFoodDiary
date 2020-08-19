package android.example.myfoodrecords;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    //public static RealmConfiguration placeConfig;
    public static RealmConfiguration foodConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        foodConfig =
                new RealmConfiguration.Builder()
                        .name("myfoodrealm13.realm")
                        .build();

        Realm.setDefaultConfiguration(foodConfig);
    }
}
