package com.lifeflow.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
  final int BG=0xff080e1b, PANEL=0xff131f34, BLUE=0xff418bff, WHITE=0xffeaf2ff, MUTED=0xff9aaac2;
  LinearLayout root, content, tabs; String current="الرئيسية"; JSONArray records=new JSONArray();
  String[] sections={"الرئيسية","المهام","التقويم","العادات","الأهداف","التذكيرات","الملاحظات","المصاريف","التركيز"};
  CountDownTimer timer; long remaining=25*60*1000L; TextView timerText;
  int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);load();show("الرئيسية");}
  GradientDrawable shape(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
  TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.RIGHT);t.setTextDirection(View.TEXT_DIRECTION_RTL);t.setPadding(dp(8),dp(7),dp(8),dp(7));if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
  LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);return l;}
  void load(){try{records=new JSONArray(getPreferences(0).getString("records","[]"));}catch(Exception e){records=new JSONArray();}}
  void save(){getPreferences(0).edit().putString("records",records.toString()).apply();}
  void show(String page){current=page;root=column();root.setBackgroundColor(BG);setContentView(root);
    TextView header=text("LifeFlow  ✦",27,WHITE,true);header.setGravity(Gravity.CENTER);root.addView(header);
    TextView subtitle=text("نظّم حياتك، خطوة بخطوة",13,MUTED,false);subtitle.setGravity(Gravity.CENTER);root.addView(subtitle);
    HorizontalScrollView scroll=new HorizontalScrollView(this);scroll.setHorizontalScrollBarEnabled(false);tabs=new LinearLayout(this);tabs.setPadding(dp(6),dp(10),dp(6),dp(10));for(String s:sections){TextView b=text(s,13,s.equals(page)?WHITE:MUTED,true);b.setBackground(shape(s.equals(page)?BLUE:PANEL,14));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2);p.setMargins(dp(3),0,dp(3),0);tabs.addView(b,p);b.setOnClickListener(v->show(s));}scroll.addView(tabs);root.addView(scroll);
    ScrollView sv=new ScrollView(this);content=column();content.setPadding(dp(16),dp(8),dp(16),dp(20));sv.addView(content);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
    if(page.equals("الرئيسية"))home();else if(page.equals("التركيز"))focus();else list(page);
  }
  void card(String title,String sub,Runnable action){LinearLayout c=column();c.setPadding(dp(14),dp(13),dp(14),dp(13));c.setBackground(shape(PANEL,17));TextView h=text(title,17,WHITE,true);c.addView(h);if(sub!=null&&!sub.isEmpty())c.addView(text(sub,13,MUTED,false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(6),0,dp(6));content.addView(c,lp);if(action!=null)c.setOnClickListener(v->action.run());}
  void home(){content.addView(text("صباح النشاط ✨",23,WHITE,true));int done=0;for(int i=0;i<records.length();i++)if(records.optJSONObject(i).optBoolean("done"))done++;card("ملخص اليوم","عندك "+records.length()+" عناصر محفوظة • "+done+" مكتملة",null);
    for(int i=1;i<sections.length;i++){final String s=sections[i];card("◈  "+s,"افتح القسم ونظّم تفاصيله",()->show(s));}}
  void list(String page){content.addView(text(page,24,WHITE,true));if(!page.equals("التقويم")){
      Button add=new Button(this);add.setText("+ إضافة جديدة");add.setTextColor(Color.WHITE);add.setBackground(shape(BLUE,14));content.addView(add);add.setOnClickListener(v->editor(page,null,-1));
    }else{Button add=new Button(this);add.setText("+ إضافة موعد");add.setTextColor(WHITE);add.setBackground(shape(BLUE,14));content.addView(add);add.setOnClickListener(v->editor(page,null,-1));}
    if(page.equals("المصاريف")){double total=0;for(int i=0;i<records.length();i++){JSONObject r=records.optJSONObject(i);if(page.equals(r.optString("type")))total+=r.optDouble("amount",0);}card("إجمالي المصاريف",""+String.format(Locale.US,"%.2f",total)+" درهم (قيم مدخلة)",null);}
    int count=0;for(int i=records.length()-1;i>=0;i--){JSONObject r=records.optJSONObject(i);if(!page.equals(r.optString("type")))continue;count++;final int idx=i;String title=r.optString("title");String detail=r.optString("detail");String date=r.optString("date");String extra=detail+(date.isEmpty()?"":"  •  "+date);if(page.equals("المصاريف"))extra+="  •  "+r.optDouble("amount",0)+" درهم";
      if(r.optBoolean("done"))title="✓ "+title;card(title,extra,()->itemActions(idx));}
    if(count==0)card("مازال ما كاين حتى عنصر","ضغط على إضافة جديدة باش تبدا",null);
  }
  void itemActions(int idx){JSONObject r=records.optJSONObject(idx);String[] choices={"تعديل","تبديل حالة الإنجاز","حذف"};new AlertDialog.Builder(this).setTitle(r.optString("title")).setItems(choices,(d,which)->{
    if(which==0)editor(r.optString("type"),r,idx);if(which==1){try{r.put("done",!r.optBoolean("done"));save();show(current);}catch(Exception ignored){}}
    if(which==2)new AlertDialog.Builder(this).setMessage("واش متأكد بغيتي تحذف هاد العنصر؟").setPositiveButton("حذف",(a,b)->{JSONArray next=new JSONArray();for(int i=0;i<records.length();i++)if(i!=idx)next.put(records.optJSONObject(i));records=next;save();show(current);}).setNegativeButton("إلغاء",null).show();}).show();}
  EditText field(String hint,String val){EditText e=new EditText(this);e.setHint(hint);e.setText(val);e.setTextColor(WHITE);e.setHintTextColor(MUTED);e.setSingleLine(false);e.setTextDirection(View.TEXT_DIRECTION_RTL);return e;}
  void editor(String type,JSONObject old,int index){LinearLayout form=column();form.setPadding(dp(22),dp(8),dp(22),dp(8));form.setBackgroundColor(PANEL);
    EditText name=field("العنوان",old==null?"":old.optString("title"));form.addView(name);
    EditText detail=field("تفاصيل (اختياري)",old==null?"":old.optString("detail"));form.addView(detail);
    EditText date=field("التاريخ YYYY-MM-DD (اختياري)",old==null?"":old.optString("date"));form.addView(date);
    EditText amount=null;if(type.equals("المصاريف")){amount=field("المبلغ بالدرهم",old==null?"":String.valueOf(old.optDouble("amount",0)));amount.setInputType(8194);form.addView(amount);}final EditText money=amount;
    new AlertDialog.Builder(this).setTitle(type).setView(form).setPositiveButton("حفظ",(dialog,which)->{
      String t=name.getText().toString().trim();if(t.isEmpty()){Toast.makeText(this,"العنوان ضروري",Toast.LENGTH_SHORT).show();return;}
      try{JSONObject r=old==null?new JSONObject():old;r.put("type",type);r.put("title",t);r.put("detail",detail.getText().toString().trim());r.put("date",date.getText().toString().trim());if(money!=null)r.put("amount",Double.parseDouble(money.getText().toString().isEmpty()?"0":money.getText().toString()));if(old==null)records.put(r);save();show(type);
      }catch(Exception e){Toast.makeText(this,"تأكد من المبلغ",Toast.LENGTH_LONG).show();}
    }).setNegativeButton("إلغاء",null).show();}
  void focus(){content.addView(text("مؤقت التركيز ⏱",25,WHITE,true));card("تقنية بومودورو","25 دقيقة تركيز، ثم استراحة قصيرة",null);timerText=text(formatTime(),48,WHITE,true);timerText.setGravity(Gravity.CENTER);content.addView(timerText);
    Button start=new Button(this);start.setText("▶ بدء / متابعة");start.setTextColor(WHITE);start.setBackground(shape(BLUE,14));content.addView(start);start.setOnClickListener(v->{if(timer!=null)timer.cancel();timer=new CountDownTimer(remaining,1000){public void onTick(long ms){remaining=ms;timerText.setText(formatTime());}public void onFinish(){remaining=0;timerText.setText("00:00");Toast.makeText(MainActivity.this,"انتهت جلسة التركيز!",Toast.LENGTH_LONG).show();}}.start();});
    Button pause=new Button(this);pause.setText("إيقاف مؤقت");content.addView(pause);pause.setOnClickListener(v->{if(timer!=null){timer.cancel();timer=null;}});
    Button reset=new Button(this);reset.setText("إعادة 25 دقيقة");content.addView(reset);reset.setOnClickListener(v->{if(timer!=null){timer.cancel();timer=null;}remaining=25*60*1000L;timerText.setText(formatTime());});
    content.addView(text("المؤقت كيخدم مادام التطبيق مفتوح. التنبيهات المجدولة خارج التطبيق مازال ما تفعلاتش فهاد النسخة.",12,MUTED,false));}
  String formatTime(){long secs=remaining/1000;return String.format(Locale.US,"%02d:%02d",secs/60,secs%60);}
  @Override protected void onDestroy(){if(timer!=null)timer.cancel();super.onDestroy();}
}
