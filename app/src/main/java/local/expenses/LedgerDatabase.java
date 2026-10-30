package local.expenses;
import androidx.room.*;
import android.content.Context;
import org.json.*;
import java.math.*;
import java.util.concurrent.*;
@Database(entities={Category.class,Expense.class,Setting.class},version=1,exportSchema=true)
public abstract class LedgerDatabase extends RoomDatabase {
 public abstract LedgerDao ledger();
 public static final ExecutorService IO=Executors.newSingleThreadExecutor();
 private static volatile LedgerDatabase instance;
 public static synchronized LedgerDatabase get(Context context){
  if(instance==null)instance=Room.databaseBuilder(context.getApplicationContext(),LedgerDatabase.class,"expenses.db").build();
  return instance;
 }
 public void importLegacy(Context context) throws Exception {
  if("done".equals(ledger().setting("legacy_import")))return;
  android.content.SharedPreferences old=context.getSharedPreferences("ledger",0);
  JSONArray categories=new JSONArray(old.getString("categories","[]"));
  JSONArray expenses=new JSONArray(old.getString("expenses","[]"));
  java.util.List<Category> cc=new java.util.ArrayList<>();
  java.util.List<Expense> ee=new java.util.ArrayList<>();
  for(int i=0;i<categories.length();i++)cc.add(new Category(categories.getString(i)));
  for(int i=0;i<expenses.length();i++){
   JSONObject o=expenses.getJSONObject(i);Expense e=new Expense();
   e.cents=new BigDecimal(o.getString("amount")).movePointRight(2).longValueExact();
   e.category=o.getString("category");e.time=o.getLong("time");e.note=o.optString("note","");
   if(e.cents<=0)throw new IllegalStateException("Invalid legacy amount");ee.add(e);
  }
  runInTransaction(()->{
   if("done".equals(ledger().setting("legacy_import")))return;
   for(Category c:cc)ledger().addCategory(c);
   for(Expense e:ee){ledger().addCategory(new Category(e.category));ledger().addExpense(e);}
   ledger().setting(new Setting("lastCategory",old.getString("lastCategory","")));
   ledger().setting(new Setting("legacy_import","done"));
  });
 }
}
