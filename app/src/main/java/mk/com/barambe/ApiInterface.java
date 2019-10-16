package mk.com.barambe;

import java.util.List;

import mk.com.barambe.model.Post;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("active_posts")
    Call<List<Post>> getActivePosts();

    @GET("post")
    Call<List<Post>> getPost(@Query("id") String id);

    @GET("posts")
    Call<List<Post>> getPosts(@Query("cat") String section,
                              @Query("count") int count,
                              @Query("search") String search);

    @GET("most_viewed")
    Call<List<Post>> getMostViewed(@Query("count") int count);
}
