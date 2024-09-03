package ru.apps.e1em.yandexadstemplate;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

public class YandexAdsAppOpen {

    private final String AD_UNIT_ID = "demo-banner-yandex";
    private Context mContext;
    private Activity mActivity;
    private AppOpenAd mAppOpenAd;
    private boolean mIsAdShowOnColdStart = false;

    public YandexAdsAppOpen(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;
    }

    public void init() {
        loadAppOpenAd();
        final DefaultProcessLifecycleObserver processLifecycleObserver = new DefaultProcessLifecycleObserver() {
            @Override
            public void onProcessCameForeground() {
                showAppOpenAd();
            }
        };
        ProcessLifecycleOwner.get().getLifecycle().addObserver(processLifecycleObserver);
    }

    private void loadAppOpenAd() {
        final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(mContext);
        AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                // The ad was loaded successfully. Now you can show loaded ad.
                mAppOpenAd = appOpenAd;
                if (!mIsAdShowOnColdStart) {
                    showAppOpenAd();
                    mIsAdShowOnColdStart = true;
                }
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdFailedToLoad");
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
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdShown");
            }

            @Override
            public void onAdFailedToShow(@NonNull final AdError adError) {
                // Called when ad failed to show.
                clearAppOpenAd();
                loadAppOpenAd();
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdFailedToShow");
            }

            @Override
            public void onAdDismissed() {
                // Called when ad is dismissed.
                // Clean resources after dismiss and preload new ad.
                clearAppOpenAd();
                loadAppOpenAd();
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdDismissed");
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdClicked");
            }

            @Override
            public void onAdImpression(@Nullable final ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.
                Log.i("Yandex Ads", ">>> Yandex Ads AppOpen onAdImpression" + impressionData);
            }
        };

        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(appOpenAdEventListener);
            mAppOpenAd.show(mActivity);
        }
    }

    private void clearAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(null);
            mAppOpenAd = null;
        }
    }
}
