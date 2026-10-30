package local.expenses;
import java.io.*;import java.nio.charset.StandardCharsets;import java.text.SimpleDateFormat;import java.util.*;import java.math.BigDecimal;
public final class CsvExport {
 private static String cell(String s){if(s==null)s="";String t=s.trim();if(!t.isEmpty() && "=+-@\t\r\n".indexOf(t.charAt(0))>=0)s="'"+s;return "\""+s.replace("\"","\"\"")+"\"";}
 public static void write(OutputStream stream,List<Expense> entries)throws IOException {
  Writer w=new OutputStreamWriter(stream,StandardCharsets.UTF_8);w.write('\ufeff');
  w.write("Дата;Время;Категория;Сумма;Валюта;Заметка\r\n");
  SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT),time=new SimpleDateFormat("HH:mm",Locale.ROOT);
  for(Expense e:entries){Date d=new Date(e.time);w.write(cell(date.format(d))+";"+cell(time.format(d))+";"+cell(e.category)+";"+BigDecimal.valueOf(e.cents,2).toPlainString().replace('.',',')+";TJS;"+cell(e.note)+"\r\n");}w.flush();
 }
}
