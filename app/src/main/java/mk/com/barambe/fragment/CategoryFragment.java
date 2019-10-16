package mk.com.barambe.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import mk.com.barambe.ApplicationController;
import mk.com.barambe.BaramConstants;
import mk.com.barambe.R;
import mk.com.barambe.activity.PostActivity;
import mk.com.barambe.adapter.CategoryAdapter;
import mk.com.barambe.model.Post;
import mk.com.barambe.storage.DatabaseAsyncTask;
import mk.com.barambe.storage.Storage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private static final String ARG_1 = "section_name";
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;
    private static final String TAG = CategoryFragment.class.getSimpleName();
    private static final int FETCH_SIZE = 6;

    private RecyclerView category_rv;
    private String categoryName;
    private int dataSize = FETCH_SIZE;
    private boolean fetching;
    private boolean syncedWithDatabase;
    private FrameLayout progress;
    private FrameLayout wrapper;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(String name) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_1, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        category_rv = rootView.findViewById(R.id.category_rv);
        category_rv.setLayoutManager(createStaggeredLayoutManager());
        progress = rootView.findViewById(R.id.category_pb);
        wrapper = rootView.findViewById(R.id.category_wrapper);

        if (getArguments() != null) {
            categoryName = getArguments().getString(ARG_1);
        }

        dataSize = FETCH_SIZE;
        syncedWithDatabase = false;
        showProgress();
        getData();

        return rootView;
    }

    private void getData() {
        switch (categoryName) {
            case BaramConstants.HOME:
                populateHome();
                break;
            case BaramConstants.MOST_VIEWED:
                populateMostViewed();
                break;
            default:
                populateCategory();
        }
    }

    private void hideProgress() {
        if (progress.getVisibility() != View.GONE)
            progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);
    }

    private void populateCategory() {
        ApplicationController.getApiInterface().getPosts(categoryName,
                dataSize, null).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call,
                                   @NonNull Response<List<Post>> response) {
                Log.d(TAG, "onResponse: " + dataSize);
                if (response.isSuccessful() && response.body() != null) {
                    if (dataSize < 1000) {
                        if (!syncedWithDatabase) {
                            syncWithDatabase(response.body());
                        } else {
                            Log.d(TAG, "onResponse: fetching data for " + categoryName);
                            setData(response.body());
                        }
                    } else {
                        if (category_rv.getAdapter() != null) {
                            ((CategoryAdapter) category_rv.getAdapter()).setFetchData(false);
                            hideProgress();
                        }
                    }
                }
                dataSize += FETCH_SIZE;
                fetching = false;
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                syncWithDatabase(null);
                fetching = false;
            }
        });
    }

    private void populateMostViewed() {
        createAdapter(Storage.getMostViewed());
        if (category_rv.getAdapter() != null) {
            ((CategoryAdapter) category_rv.getAdapter()).setFetchData(false);
        }
    }

    private void populateHome() {
        new DatabaseAsyncTask(new DatabaseAsyncTask.DataCallback() {
            @Override
            public void onDataReceived(List<Post> postList) {
                createAdapter(postList);
                Log.d(TAG, "onDataReceived: " + postList.size());
                if (category_rv.getAdapter() != null) {
                    ((CategoryAdapter) category_rv.getAdapter()).setFetchData(false);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
            }
        }).execute(DatabaseAsyncTask.TaskType.GET_ALL);
    }

    private void syncWithDatabase(final List<Post> data) {
        new DatabaseAsyncTask(categoryName, new DatabaseAsyncTask.DataCallback() {
            @Override
            public void onDataReceived(List<Post> postList) {
                boolean dbUpToDate = false;
                if (data != null && data.size() > 0) {
                    for (Post post : postList) {
                        if (data.get(0).getId() == post.getId()) {
                            dbUpToDate = true;
                            break;
                        }
                    }
                } else {
                    dbUpToDate = true;
                }
                if (dbUpToDate) {
                    syncedWithDatabase = true;
                    setData(postList);
                    if (category_rv.getAdapter() != null) {
                        dataSize = category_rv.getAdapter().getItemCount() + FETCH_SIZE;
                    } else {
                        dataSize = postList.size() + FETCH_SIZE;
                    }
                    Log.d(TAG, "syncWithDatabase: gettingFromDB " + postList.size());
                } else {
                    new DatabaseAsyncTask(data)
                            .execute(DatabaseAsyncTask.TaskType.ADD_ALL);
                    setData(data);
                }
            }

            @Override
            public void onError(Exception e) {
                setData(data);
                Log.e(TAG, "syncWithDatabase onError: ", e);
            }
        }).execute(DatabaseAsyncTask.TaskType.GET_ALL_BY_CATEGORY);
    }

    private void setData(List<Post> data) {
        if (category_rv.getAdapter() != null) {
            updateAdapter(data);
        } else {
            createAdapter(data);
        }
    }

    private void createAdapter(List<Post> data) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), data,
                new CategoryAdapter.CategoryCallback() {
                    @Override
                    public void fetchData() {
                        if (!fetching) {
                            fetching = true;
                            showProgress();
                            getData();
                        }
                    }

                    @Override
                    public void itemClick(Post post, int position, View view) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(BaramConstants.POST, post);
                        startActivity(intent);
                    }
                });
        category_rv.setAdapter(categoryAdapter);
        hideProgress();
    }

    private void updateAdapter(List<Post> data) {
        for (Post post : data) {
            ((CategoryAdapter) category_rv.getAdapter()).updateData(post);
        }
        hideProgress();
    }

    private StaggeredGridLayoutManager createStaggeredLayoutManager() {
        int spanCount = SPAN_COUNT_PORTRAIT;
        if (getActivity() != null && getActivity().getResources().getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = SPAN_COUNT_LANDSCAPE;
        }
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.VERTICAL);
        lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        return lm;
    }
}