package local.expenses;
import android.app.Instrumentation;import android.os.*;import java.util.*;
/** Synthetic screenshot fixtures. Refuses to run on a physical phone. */
public class DemoData {
 public static void seed(android.content.Context context) throws Exception {
  if(!Build.FINGERPRINT.contains("generic")&&!Build.MODEL.contains("sdk_gphone"))throw new IllegalStateException("Emulator only");
  LedgerDatabase db=LedgerDatabase.get(context);db.importLegacy(context);
  db.runInTransaction(()->{db.clearAllTables();String[] cats={"Продукты","Кафе","Транспорт","Подписки"};for(String c:cats)db.ledger().addCategory(new Category(c));long[] cents={24590,4200,1800,7900,12850};String[] names={"Продукты","Кафе","Транспорт","Подписки","Продукты"};Calendar day=Calendar.getInstance();day.set(Calendar.HOUR_OF_DAY,13);day.set(Calendar.MINUTE,25);for(int i=0;i<cents.length;i++){Expense e=new Expense();e.cents=cents[i];e.category=names[i];e.time=day.getTimeInMillis()-i*86400000L;e.note=i==0?"Покупки на неделю · демо":"Демонстрационная запись";db.ledger().addExpense(e);}db.ledger().setting(new Setting("legacy_import","done"));db.ledger().setting(new Setting("lastCategory","Продукты"));});
 }
}
