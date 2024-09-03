package ru.apps.e1em.yandexadstemplate;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mobile.ads.common.MobileAds;

import ru.apps.e1em.yandexadstemplate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new YandexAdsStickyBanner(binding, this).init();
        // YandexAdsAppOpen yandexAdsAppOpen = new YandexAdsAppOpen(this);

        MobileAds.setAgeRestrictedUser(false);
        MobileAds.initialize(this, () -> {
            Log.i("Yandex Ads",">>> Yandex Ads initialized");
            // yandexAdsAppOpen.init();
        });
    }
}