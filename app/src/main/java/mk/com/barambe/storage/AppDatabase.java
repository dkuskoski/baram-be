package mk.com.barambe.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import mk.com.barambe.model.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final int DATABASE_COUNT_LIMIT = 1000;

    public abstract PostDAO postDAO();
}
