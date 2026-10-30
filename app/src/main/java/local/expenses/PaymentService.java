package local.expenses;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.*;
import android.content.Intent;
import android.os.SystemClock;
public class PaymentService extends AccessibilityService {
 private boolean shown=false; private long last=0; private String shownKey=null;
 private boolean contains(AccessibilityNodeInfo n,String expected){if(n==null)return false; CharSequence t=n.getText(); if(t!=null && t.toString().trim().toLowerCase(java.util.Locale.ROOT).replace('ё','е').equals(expected))return true; for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo c=n.getChild(i); boolean s=contains(c,expected);if(c!=null)c.recycle();if(s)return true;}return false;}
 private void texts(AccessibilityNodeInfo n,java.util.List<String> out){if(n==null)return;if(n.getText()!=null)out.add(n.getText().toString());for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo c=n.getChild(i);texts(c,out);if(c!=null)c.recycle();}}
 private String amount(AccessibilityNodeInfo root){java.util.List<String> values=new java.util.ArrayList<>();texts(root,values);String found=null;for(String text:values){if(text.trim().equalsIgnoreCase("Подробности операции"))break;String v=AmountParser.parse(text);if(v!=null){if(found!=null&&!found.equals(v))return null;found=v;}}return found;}
 private final android.os.Handler handler=new android.os.Handler(android.os.Looper.getMainLooper());
 private final Runnable check=()->scan();
 private long firstSeen=-1, stableSince=-1;
 private String candidate=null;
 private boolean scheduled=false;
 private void schedule(long delay){if(!scheduled){scheduled=true;handler.postDelayed(check,delay);}}
 private void resetWait(){firstSeen=-1;stableSince=-1;candidate=null;handler.removeCallbacks(check);scheduled=false;}
 protected void onServiceConnected(){schedule(600);}
 public void onAccessibilityEvent(AccessibilityEvent e){
  if(!"tj.alif.mobi".contentEquals(e.getPackageName()==null?"":e.getPackageName()))return;
  if(firstSeen<0)schedule(150);
 }
 private void scan(){
  scheduled=false;
  long now=SystemClock.elapsedRealtime();
  AccessibilityNodeInfo root=getRootInActiveWindow();
  if(root==null){if(firstSeen>=0 && now-firstSeen<8000)schedule(350);else resetWait();return;}
  if(!"tj.alif.mobi".contentEquals(root.getPackageName()==null?"":root.getPackageName())){root.recycle();resetWait();return;}
  boolean ok=(contains(root,"платеж отправлен") || contains(root,"оплата прошла успешно")) && contains(root,"на главную");
  String detectedAmount=ok?amount(root):null;
  java.util.List<String> screenTexts=new java.util.ArrayList<>();if(ok)texts(root,screenTexts);
  String dateKey="";for(String t:screenTexts){java.util.regex.Matcher m=java.util.regex.Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4},?\\s+[0-9]{2}:[0-9]{2}").matcher(t);if(m.find()){dateKey=m.group().replace(",", "");break;}}
  String paymentKey=dateKey.isEmpty()?null:dateKey+"|"+detectedAmount;
  root.recycle();
  if(!ok){shown=false;shownKey=null;resetWait();schedule(750);return;}
  if(shown && (paymentKey==null || java.util.Objects.equals(paymentKey,shownKey))){resetWait();schedule(750);return;}
  if(firstSeen<0)firstSeen=now;
  if(!java.util.Objects.equals(candidate,detectedAmount)){candidate=detectedAmount;stableSince=now;}
  boolean ready=now-firstSeen>=2000 && candidate!=null && now-stableSince>=700;
  boolean expired=now-firstSeen>=8000;
  if(!ready && !expired){schedule(350);return;}
  if(now-last<=10000){schedule(350);return;}
  String result=ready?candidate:null;
  shown=true;shownKey=paymentKey;last=now;resetWait();
  android.util.Log.i("ExpenseTrigger","Opening expense form; amount available="+(result!=null));
  startActivity(new Intent(this,MainActivity.class).putExtra("payment",true).putExtra("detectedAmount",result).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
 }
 public void onInterrupt(){resetWait();}
 public void onDestroy(){resetWait();super.onDestroy();}
}
