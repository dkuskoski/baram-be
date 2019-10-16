package mk.com.barambe;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import mk.com.barambe.storage.AppDatabase;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationController extends Application {

    private static final String TAG = ApplicationController.class.getSimpleName();
    private static ApiInterface apiInterface;
    private static SharedPreferences sp;
    private static Gson gson;
    private static final String DATABASE_NAME = "app_db";
    private static AppDatabase appDatabase;

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: getTotalSpace " + getCacheDir().getTotalSpace());
        Log.d(TAG, "onCreate: availableSpace " + getCacheDir().getFreeSpace());

        sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        gson = new Gson();
        Retrofit retrofit = createRetrofitInstance();
        apiInterface = retrofit.create(ApiInterface.class);

        initPicasso();

        appDatabase = initAppDatabase();
    }

    private AppDatabase initAppDatabase() {
        return Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    private Retrofit createRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("http://baram.be/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build();
    }

    public static ApiInterface getApiInterface() {
        return apiInterface;
    }

    private OkHttpClient createOkHttpClient(){
        return new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic(BaramConstants.API_USERNAME,
                                BaramConstants.API_PASSWORD));

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();
    }

    public static SharedPreferences getSP() {
        return sp;
    }

    public static Gson getGson() {
        return gson;
    }

    private void initPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);
    }
}
