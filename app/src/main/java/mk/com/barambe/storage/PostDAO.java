package mk.com.barambe.storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import mk.com.barambe.model.Post;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PostDAO {

    @Insert(onConflict = REPLACE)
    void add(Post post);

    @Insert(onConflict = REPLACE)
    void addAll(List<Post> postList);

    @Query("SELECT * FROM post ORDER BY id DESC LIMIT 1000")
    List<Post> getAll();

    @Query("SELECT * FROM post WHERE section = :category ORDER BY id DESC LIMIT 1000")
    List<Post> getAllFromCategory(String category);

    @Query("DELETE FROM post WHERE id IN (SELECT id FROM post ORDER BY id ASC LIMIT :count)")
    void clean(int count);

    @Query("SELECT COUNT(*) FROM post")
    int getCount();
}
