package mk.com.barambe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import mk.com.barambe.ApplicationController;
import mk.com.barambe.R;
import mk.com.barambe.model.Post;
import mk.com.barambe.storage.DatabaseAsyncTask;
import mk.com.barambe.storage.Storage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown.";
//  To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.

    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd;
    private boolean adExpired;
    private boolean dataFetched;
    private boolean mostViewedFetched;
    private static DatabaseAsyncTask databaseAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
//        mInterstitialAd = newInterstitialAd();
//        loadInterstitial();
        adExpired = true;

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
//        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();

        getData();
    }

    private void getData() {

        // Get home content
        ApplicationController.getApiInterface().getActivePosts()
                .enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call,
                                   @NonNull Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new DatabaseAsyncTask(response.body()).execute(DatabaseAsyncTask.TaskType.ADD_ALL);
//                    Storage.saveActivePosts(response.body());
                }
                dataFetched = true;
                goToNextLevel();
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                dataFetched = true;
                goToNextLevel();
            }
        });

        ApplicationController.getApiInterface().getMostViewed(100)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Post>> call,
                                           @NonNull Response<List<Post>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Storage.saveMostViewed(response.body());
                            new DatabaseAsyncTask(response.body())
                                    .execute(DatabaseAsyncTask.TaskType.ADD_ALL);
                        }
                        mostViewedFetched = true;
                        goToNextLevel();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        mostViewedFetched = true;
                        goToNextLevel();
                    }
                });
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "AD loaded.");
                showInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "AD failed to load.");
                adExpired = true;
                goToNextLevel();
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                adExpired = true;
                goToNextLevel();
            }

        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "showInterstitial: ad error");
            adExpired = true;
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void goToNextLevel() {
        if (adExpired && dataFetched && mostViewedFetched) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(BaramConstants.HOME, new Gson().toJson(data));
//            intent.putExtra(BaramConstants.MOST_VIEWED, new Gson().toJson(mostViewed));
            startActivity(intent);
        }
    }
}
