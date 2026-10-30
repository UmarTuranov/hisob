package local.expenses;
import android.app.Instrumentation;import android.os.Bundle;import android.content.*;import androidx.room.Room;import java.io.*;import java.nio.charset.StandardCharsets;
public class LedgerChecks extends Instrumentation {
 private boolean demo=false;
 public void onCreate(Bundle args){super.onCreate(args);demo=args!=null&&"true".equals(args.getString("demo"));start();}
 private void check(boolean value,String message){if(!value)throw new AssertionError(message);}
 public void onStart(){Bundle result=new Bundle();LedgerDatabase db=null;try{
 if(demo){DemoData.seed(getTargetContext());result.putString("stream","Synthetic demo data ready\n");finish(-1,result);return;}
 Context target=getTargetContext();final String prefsName="migration_test_"+System.nanoTime();Context context=new ContextWrapper(target){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefsName,mode);}};
 SharedPreferences prefs=context.getSharedPreferences("ledger",0);prefs.edit().putString("categories","[\"Обед\",\"Транспорт\"]").putString("expenses","[{\"amount\":\"12.50\",\"category\":\"Обед\",\"time\":1793330000000},{\"amount\":\"1.01\",\"category\":\"Такси\",\"time\":1793330100000}]").putString("lastCategory","Обед").commit();
 db=Room.inMemoryDatabaseBuilder(target,LedgerDatabase.class).build();db.importLegacy(context);
 check(db.ledger().expenses().size()==2,"migration count");check(db.ledger().categories().size()==3,"orphan category restored");check("Обед".equals(db.ledger().setting("lastCategory")),"selection migrated");
 long total=0;for(Expense e:db.ledger().expenses())total+=e.cents;check(total==1351,"exact cents");db.importLegacy(context);check(db.ledger().expenses().size()==2,"idempotent migration");check(prefs.getString("expenses","").contains("12.50"),"legacy retained");
 Expense edit=db.ledger().expenses().get(0);long id=edit.id;edit.cents=777;edit.category="Обед";edit.note="=SUM(A1:A2); \"тест\"\nстрока";edit.time=1793330200000L;check(db.ledger().updateExpense(edit)==1,"update");check(db.ledger().expenses().get(0).id==id&&db.ledger().expenses().get(0).cents==777,"stable id and edit");
 ByteArrayOutputStream out=new ByteArrayOutputStream();CsvExport.write(out,db.ledger().expenses());String csv=new String(out.toByteArray(),StandardCharsets.UTF_8);check(csv.charAt(0)=='\ufeff',"UTF8 BOM");check(csv.contains("7,77;TJS"),"decimal export");check(csv.contains("\"'=SUM(A1:A2); \"\"тест\"\"\nстрока\""),"CSV escaping and formula defense");check(db.ledger().deleteExpense(id)==1&&db.ledger().expenses().size()==1,"delete by ID");
 db.close();db=Room.inMemoryDatabaseBuilder(target,LedgerDatabase.class).build();prefs.edit().putString("expenses","[{\"amount\":\"1.00\",\"category\":\"Обед\",\"time\":1},{\"amount\":\"broken\"}]").commit();boolean rejected=false;try{db.importLegacy(context);}catch(Exception expected){rejected=true;}check(rejected&&db.ledger().expenses().isEmpty()&&db.ledger().setting("legacy_import")==null,"invalid migration atomic");prefs.edit().clear().commit();
 result.putString("stream","PASS: migration, exact money, idempotence, backup retention, edit, delete, CSV encoding/escaping and failed migration rollback\n");finish(-1,result);
 }catch(Throwable e){result.putString("stream","FAIL: "+android.util.Log.getStackTraceString(e));finish(0,result);}finally{if(db!=null)db.close();}}
}
