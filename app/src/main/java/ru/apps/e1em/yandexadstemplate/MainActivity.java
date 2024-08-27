package ru.apps.e1em.yandexadstemplate;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import ru.apps.e1em.yandexadstemplate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    // Yandex Ads.
    @Nullable
    private BannerAdView mBannerAd = null;
    private AppOpenAd mAppOpenAd;
    private final String AD_UNIT_ID  = "demo-banner-yandex";
    private boolean isAdShowOnColdStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        //region Yandex Ads
        //region Yandex Ads Sticky Banner
        mBinding.adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBinding.adContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mBannerAd = loadBannerAd(getAdSize());
                    }
                }
        );
        //endregion

        MobileAds.setAgeRestrictedUser(false);
        MobileAds.initialize(this, () -> {
            Log.i("Yandex Ads",">>> Yandex Ads initialized");
            //region Yandex Ads AppOpenAd
            loadAppOpenAd();
            final DefaultProcessLifecycleObserver processLifecycleObserver = new DefaultProcessLifecycleObserver() {
                @Override
                public void onProcessCameForeground() { showAppOpenAd(); }
            };
            ProcessLifecycleOwner.get().getLifecycle().addObserver(processLifecycleObserver);
            //endregion
        });
        //endregion
    }

    //region Yandex Ads Sticky Banner
    @NonNull
    private BannerAdSize getAdSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = mBinding.adContainerView.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);

        return BannerAdSize.stickySize(this, adWidth);
    }

    @NonNull
    private BannerAdView loadBannerAd(@NonNull final BannerAdSize adSize) {
        final BannerAdView bannerAd = mBinding.adContainerView;
        bannerAd.setAdSize(adSize);
        bannerAd.setAdUnitId(AD_UNIT_ID );
        bannerAd.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (isDestroyed() && mBannerAd != null) {
                    mBannerAd.destroy();
                }
                Log.i("Yandex Ads",">>> Yandex Ads onAdLoaded!");
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                Log.i("Yandex Ads",">>> Yandex Ads onAdFailedToLoad");
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.i("Yandex Ads",">>> Yandex Ads onAdClicked");
            }

            @Override
            public void onLeftApplication() {
                // Called when user is about to leave application (e.g., to go to the browser), as a result of clicking on the ad.
                Log.i("Yandex Ads",">>> Yandex Ads onLeftApplication");
            }

            @Override
            public void onReturnedToApplication() {
                // Called when user returned to application after click.
                Log.i("Yandex Ads",">>> Yandex Ads onReturnedToApplication");
            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.
                if (impressionData != null) {
                    Log.i("Yandex Ads",">>> Yandex Ads onImpression" + impressionData.getRawData());
                }
            }
        });
        final AdRequest adRequest = new AdRequest.Builder()
                // Methods in the AdRequest.Builder class can be used here to specify individual options settings.
                .build();
        bannerAd.loadAd(adRequest);
        return bannerAd;
    }
    //endregion

    //region Yandex Ads AppOpenAd
    private void loadAppOpenAd() {
        final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(getApplicationContext());
        AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                // The ad was loaded successfully. Now you can show loaded ad.
                mAppOpenAd = appOpenAd;
                if(!isAdShowOnColdStart) {
                    showAppOpenAd();
                    isAdShowOnColdStart = true;
                }
                Log.i("Yandex Ads",">>> Yandex Ads onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                Log.i("Yandex Ads",">>> Yandex Ads onAdFailedToLoad");
            }
        };
        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);

        final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();
        appOpenAdLoader.loadAd(adRequestConfiguration);
    }

    private void showAppOpenAd() {
        AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
            @Override
            public void onAdShown() {
                // Called when ad is shown.
                Log.i("Yandex Ads",">>> Yandex Ads onAdShown");
            }

            @Override
            public void onAdFailedToShow(@NonNull final AdError adError) {
                // Called when ad failed to show.
                clearAppOpenAd();
                loadAppOpenAd();
                Log.i("Yandex Ads",">>> Yandex Ads onAdFailedToShow");
            }

            @Override
            public void onAdDismissed() {
                // Called when ad is dismissed.
                // Clean resources after dismiss and preload new ad.
                clearAppOpenAd();
                loadAppOpenAd();
                Log.i("Yandex Ads",">>> Yandex Ads onAdDismissed");
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.i("Yandex Ads",">>> Yandex Ads onAdClicked");
            }

            @Override
            public void onAdImpression(@Nullable final ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.
                Log.i("Yandex Ads",">>> Yandex Ads onAdImpression" + impressionData);
            }
        };

        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(appOpenAdEventListener);
            mAppOpenAd.show(this);
        }
    }

    private void clearAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(null);
            mAppOpenAd = null;
        }
    }
    //endregion
}