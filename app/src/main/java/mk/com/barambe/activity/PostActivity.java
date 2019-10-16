package mk.com.barambe.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import mk.com.barambe.BaramConstants;
import mk.com.barambe.R;
import mk.com.barambe.adapter.PostPagerAdapter;
import mk.com.barambe.model.Post;
import mk.com.barambe.storage.DatabaseAsyncTask;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = PostActivity.class.getSimpleName();
    private static final String MIME_TYPE_HTML = "text/html";
    private static final String UTF_8 = "UTF-8";
    private Post post;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (getIntent() != null && getIntent().hasExtra(BaramConstants.POST)) {
            post = (Post) getIntent().getSerializableExtra(BaramConstants.POST);
            init();
        } else {
            // TODO notify user
            finish();
        }
    }

    private void init() {
        mViewPager = findViewById(R.id.post_vp);
        new DatabaseAsyncTask(new DatabaseAsyncTask.DataCallback() {
            @Override
            public void onDataReceived(List<Post> postList) {
                for (int i = 0; i < postList.size(); i++) {
                    if (postList.get(i).getId() == post.getId()) {
                        PostPagerAdapter mPostPagerAdapter =
                                new PostPagerAdapter(getSupportFragmentManager(), postList);
                        mViewPager.setAdapter(mPostPagerAdapter);
                        mViewPager.setCurrentItem(i);
                        return;
                    }
                }
                postList.add(0, post);
                PostPagerAdapter mPostPagerAdapter =
                        new PostPagerAdapter(getSupportFragmentManager(), postList);
                mViewPager.setAdapter(mPostPagerAdapter);
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void onError(Exception e) {
                List<Post> postList = new ArrayList<>();
                postList.add(0, post);
                PostPagerAdapter mPostPagerAdapter =
                        new PostPagerAdapter(getSupportFragmentManager(), postList);
                mViewPager.setAdapter(mPostPagerAdapter);
                mViewPager.setCurrentItem(0);
            }
        }).execute(DatabaseAsyncTask.TaskType.GET_ALL);
    }
}
