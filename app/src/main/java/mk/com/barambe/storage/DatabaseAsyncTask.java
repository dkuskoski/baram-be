package mk.com.barambe.storage;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import mk.com.barambe.ApplicationController;
import mk.com.barambe.model.Post;

import static mk.com.barambe.storage.AppDatabase.DATABASE_COUNT_LIMIT;

public class DatabaseAsyncTask extends AsyncTask<DatabaseAsyncTask.TaskType, Void, List<Post>> {


    private List<Post> postList;
    private Post post;
    private String category;
    private DataCallback dataCallback;

    public DatabaseAsyncTask(List<Post> postList) {
        this.postList = postList;
    }

    public DatabaseAsyncTask(Post post) {
        this.post = post;
    }

    public DatabaseAsyncTask(String category, DataCallback dataCallback) {
        this.category = category;
        this.dataCallback = dataCallback;
    }

    public DatabaseAsyncTask(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    @Override
    protected List<Post> doInBackground(@NonNull TaskType... taskTypes) {
        int count = ApplicationController.getAppDatabase().postDAO().getCount();
        if (count > DATABASE_COUNT_LIMIT) {
            ApplicationController.getAppDatabase()
                    .postDAO().clean(count - DATABASE_COUNT_LIMIT);
        }
        switch (taskTypes[0]) {
            case ADD:
                if (post != null) {
                    ApplicationController.getAppDatabase().postDAO().add(post);
                }
                return null;
            case ADD_ALL:
                if (postList != null && postList.size() > 0) {
                    ApplicationController.getAppDatabase().postDAO().addAll(postList);
                }
                return null;

            case GET_ALL:
                try {
                    return ApplicationController.getAppDatabase().postDAO().getAll();
                } catch (Exception e) {
                    if (dataCallback != null)
                        dataCallback.onError(e);
                    return null;
                }
            case GET_ALL_BY_CATEGORY:
                try {
                    return ApplicationController.getAppDatabase()
                            .postDAO().getAllFromCategory(category);
                } catch (Exception e) {
                    if (dataCallback != null)
                        dataCallback.onError(e);
                    return null;
                }

            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(List<Post> postList) {
        super.onPostExecute(postList);
        if (dataCallback != null)
            dataCallback.onDataReceived(postList);
    }

    public enum TaskType {GET_ALL, ADD, ADD_ALL, GET_ALL_BY_CATEGORY}

    public interface DataCallback {
        void onDataReceived(List<Post> postList);

        void onError(Exception e);
    }
}
