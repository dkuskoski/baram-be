package mk.com.barambe.storage;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import mk.com.barambe.ApplicationController;
import mk.com.barambe.model.Post;

public class Storage {

    private static final String ACTIVE_POSTS = "activePosts";
    private static final String MOST_VIEWED = "mostViewed";

    public static void saveActivePosts(List<Post> data) {
        ApplicationController.getSP().edit().putString(ACTIVE_POSTS,
                ApplicationController.getGson().toJson(data)).apply();
    }

    public static List<Post> getActivePosts() {
        return ApplicationController.getGson().fromJson(
                ApplicationController.getSP().getString(ACTIVE_POSTS, null),
                new TypeToken<List<Post>>() {
                }.getType());
    }

    public static void saveMostViewed(List<Post> data) {
        ApplicationController.getSP().edit().putString(MOST_VIEWED,
                ApplicationController.getGson().toJson(data)).apply();
    }

    public static List<Post> getMostViewed() {
        return ApplicationController.getGson().fromJson(
                ApplicationController.getSP().getString(MOST_VIEWED, null),
                new TypeToken<List<Post>>() {
                }.getType());
    }
}
