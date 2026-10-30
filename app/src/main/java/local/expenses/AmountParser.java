package local.expenses;
import java.math.BigDecimal;
import java.util.regex.*;
public final class AmountParser {
 private static final Pattern MONEY=Pattern.compile("^([0-9]+(?:[ \\u00a0\\u202f][0-9]{3})*(?:[.,][0-9]{1,2})?)\\s*(?:с\\.?|сомони|TJS)$",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
 public static String parse(String text){
  if(text==null)return null;
  Matcher m=MONEY.matcher(text.trim().replace('\u00a0',' ').replace('\u202f',' '));
  if(!m.matches())return null;
  try{BigDecimal v=new BigDecimal(m.group(1).replace(" ","").replace(',','.'));if(v.signum()<=0||v.compareTo(new BigDecimal("999999999"))>0)return null;return v.setScale(2).toPlainString();}catch(Exception e){return null;}
 }
}
