package local.expenses;
import androidx.room.*;
import java.util.List;
@Dao public interface LedgerDao {
 @Query("SELECT * FROM expenses ORDER BY time DESC, id DESC") List<Expense> expenses();
 @Query("SELECT * FROM categories ORDER BY rowid") List<Category> categories();
 @Insert(onConflict=OnConflictStrategy.IGNORE) long addCategory(Category c);
 @Insert long addExpense(Expense e);
 @Update int updateExpense(Expense e);
 @Query("DELETE FROM expenses WHERE id=:id") int deleteExpense(long id);
 @Query("SELECT value FROM settings WHERE `key`=:key") String setting(String key);
 @Insert(onConflict=OnConflictStrategy.REPLACE) void setting(Setting s);
}
