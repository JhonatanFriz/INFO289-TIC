package b4a.EjemploSQLiteVF2;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.EjemploSQLiteVF2", "b4a.EjemploSQLiteVF2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.EjemploSQLiteVF2", "b4a.EjemploSQLiteVF2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.EjemploSQLiteVF2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.sql.SQL _mi_db = null;
public anywheresoftware.b4a.sql.SQL.CursorWrapper _mi_cursor = null;
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext7 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext8 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext9 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext10 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _v5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _ingresac = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview2 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelrut = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelmontos = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelinfo = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelfin = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelformatodate = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox2 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="TabHost1.AddTab(\"Ingreso Datos\", \"Principal\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Ingreso Datos","Principal");
 //BA.debugLineNum = 41;BA.debugLine="TabHost1.AddTab(\"Consultas\", \"consultas\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Consultas","consultas");
 //BA.debugLineNum = 42;BA.debugLine="TabHost1.AddTab(\"Compras\", \"compras\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Compras","compras");
 //BA.debugLineNum = 43;BA.debugLine="Spinner1.Add(\"Compras por fecha\")";
mostCurrent._spinner1.Add("Compras por fecha");
 //BA.debugLineNum = 44;BA.debugLine="Spinner1.Add(\"Compras entre fecha\")";
mostCurrent._spinner1.Add("Compras entre fecha");
 //BA.debugLineNum = 45;BA.debugLine="Spinner1.Add(\"Cuenta por rut\")";
mostCurrent._spinner1.Add("Cuenta por rut");
 //BA.debugLineNum = 46;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 49;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"CtaCorrienteF";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"CtaCorrienteF.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 52;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 53;BA.debugLine="Try";
try { //BA.debugLineNum = 55;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Mi_Tabla1 (id_dato INTEGER PRIMARY KEY AUTOINCREMENT, rut TEXT, fecha DATE, numero INTEGER, compras INTEGER, pagos INTEGER)");
 //BA.debugLineNum = 56;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 57;BA.debugLine="ToastMessageShow(\"Crea la base\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Crea la base"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 59;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExc";
anywheresoftware.b4a.keywords.Common.LogImpl("0131094","ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 61;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 };
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
int _i = 0;
String _fecha = "";
String _rut = "";
String _numero = "";
int _compras = 0;
int _pagos = 0;
String _linea = "";
 //BA.debugLineNum = 76;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 77;BA.debugLine="If EditText1.Text<>\"\" And EditText2.Text <> \"\" An";
if ((mostCurrent._edittext1.getText()).equals("") == false && (mostCurrent._edittext2.getText()).equals("") == false && (mostCurrent._edittext3.getText()).equals("") == false && (mostCurrent._edittext4.getText()).equals("") == false) { 
 //BA.debugLineNum = 79;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 80;BA.debugLine="Try";
try { //BA.debugLineNum = 81;BA.debugLine="If CheckBox1.Checked Then";
if (mostCurrent._checkbox1.getChecked()) { 
 //BA.debugLineNum = 82;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Mi_tabla1 (ru";
_mi_db.ExecNonQuery2("INSERT INTO Mi_tabla1 (rut, fecha, numero, compras, pagos) VALUES (?,?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{mostCurrent._edittext1.getText(),mostCurrent._edittext2.getText(),mostCurrent._edittext3.getText(),mostCurrent._edittext4.getText(),BA.NumberToString(0)}));
 //BA.debugLineNum = 83;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 }else if(mostCurrent._checkbox2.getChecked()) { 
 //BA.debugLineNum = 85;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Mi_tabla1 (ru";
_mi_db.ExecNonQuery2("INSERT INTO Mi_tabla1 (rut, fecha, numero, compras, pagos) VALUES (?,?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{mostCurrent._edittext1.getText(),mostCurrent._edittext2.getText(),mostCurrent._edittext3.getText(),BA.NumberToString(0),mostCurrent._edittext4.getText()}));
 //BA.debugLineNum = 86;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 }else {
 //BA.debugLineNum = 88;BA.debugLine="ToastMessageShow(\"Selecciona si es COMPRA o PA";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Selecciona si es COMPRA o PAGO"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 89;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 91;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 92;BA.debugLine="ToastMessageShow(\"Transacción guardada\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Transacción guardada"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e17) {
			processBA.setLastException(e17); //BA.debugLineNum = 94;BA.debugLine="Log(\"catch: \" & LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("0327698","catch: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 96;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 99;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT id_dato, rut,";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT id_dato, rut, fecha, numero, compras, pagos FROM Mi_tabla1 ORDER BY rut ASC")));
 //BA.debugLineNum = 101;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 102;BA.debugLine="EditText1.Text=\"\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 103;BA.debugLine="EditText2.Text=\"\"";
mostCurrent._edittext2.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 104;BA.debugLine="EditText3.Text=\"\"";
mostCurrent._edittext3.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 105;BA.debugLine="EditText4.Text=\"\"";
mostCurrent._edittext4.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 106;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 107;BA.debugLine="CheckBox2.Checked = False";
mostCurrent._checkbox2.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 109;BA.debugLine="ListView1.AddSingleLine(\"Fecha       | Rut";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("Fecha       | Rut         | Nº | Compra | Pago"));
 //BA.debugLineNum = 111;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT rut, fecha,";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT rut, fecha, numero, compras, pagos FROM Mi_tabla1 ORDER BY rut ASC")));
 //BA.debugLineNum = 114;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step30 = 1;
final int limit30 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit30 ;_i = _i + step30 ) {
 //BA.debugLineNum = 115;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 117;BA.debugLine="Dim fecha As String = Mi_Cursor.GetString(\"fecha";
_fecha = mostCurrent._mi_cursor.GetString("fecha");
 //BA.debugLineNum = 118;BA.debugLine="Dim rut As String = Mi_Cursor.GetString(\"rut\")";
_rut = mostCurrent._mi_cursor.GetString("rut");
 //BA.debugLineNum = 119;BA.debugLine="Dim numero As String = Mi_Cursor.GetInt(\"numero\"";
_numero = BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"));
 //BA.debugLineNum = 120;BA.debugLine="Dim compras As Int = Mi_Cursor.GetInt(\"compras\")";
_compras = mostCurrent._mi_cursor.GetInt("compras");
 //BA.debugLineNum = 121;BA.debugLine="Dim pagos As Int = Mi_Cursor.GetInt(\"pagos\")";
_pagos = mostCurrent._mi_cursor.GetInt("pagos");
 //BA.debugLineNum = 122;BA.debugLine="Dim linea As String = fecha & \"| \" & rut & \"|\" &";
_linea = _fecha+"| "+_rut+"|"+_numero+"|"+BA.NumberToString(_compras)+"|"+BA.NumberToString(_pagos);
 //BA.debugLineNum = 123;BA.debugLine="ListView1.AddSingleLine(linea)";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 };
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 159;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"ClientesProf.d";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ClientesProf.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 163;BA.debugLine="Try";
try { //BA.debugLineNum = 164;BA.debugLine="Mi_DB.ExecNonQuery(\"DROP TABLE Mi_Tabla1\")";
_mi_db.ExecNonQuery("DROP TABLE Mi_Tabla1");
 //BA.debugLineNum = 165;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 167;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExce";
anywheresoftware.b4a.keywords.Common.LogImpl("0458762","ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 169;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 246;BA.debugLine="Private Sub CheckBox1_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 247;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 248;BA.debugLine="CheckBox2.Checked = False";
mostCurrent._checkbox2.setChecked(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox2_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Private Sub CheckBox2_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 255;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 256;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 260;BA.debugLine="End Sub";
return "";
}
public static String  _consultar_click() throws Exception{
String _dato = "";
String _fechafinal = "";
String _filtro = "";
int _i = 0;
String _linea = "";
int _totalcompras = 0;
int _totalpagos = 0;
int _saldo = 0;
int _compra = 0;
int _pago = 0;
 //BA.debugLineNum = 178;BA.debugLine="Sub Consultar_Click";
 //BA.debugLineNum = 179;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 181;BA.debugLine="Dim dato As String = EditText5.Text";
_dato = mostCurrent._edittext5.getText();
 //BA.debugLineNum = 182;BA.debugLine="Dim fechaFinal As String = EditText6.Text";
_fechafinal = mostCurrent._edittext6.getText();
 //BA.debugLineNum = 183;BA.debugLine="Dim filtro As String = Spinner1.SelectedIndex";
_filtro = BA.NumberToString(mostCurrent._spinner1.getSelectedIndex());
 //BA.debugLineNum = 184;BA.debugLine="LabelRut.Text = \"\"";
mostCurrent._labelrut.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 185;BA.debugLine="LabelMontos.Text = \"\"";
mostCurrent._labelmontos.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 186;BA.debugLine="ListView2.Clear";
mostCurrent._listview2.Clear();
 //BA.debugLineNum = 187;BA.debugLine="If dato = \"\" Then";
if ((_dato).equals("")) { 
 //BA.debugLineNum = 188;BA.debugLine="ToastMessageShow(\"Ingrese valor\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese valor"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 189;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 192;BA.debugLine="Select filtro";
switch (BA.switchObjectToInt(_filtro,BA.NumberToString(0),BA.NumberToString(1),BA.NumberToString(2))) {
case 0: {
 //BA.debugLineNum = 194;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT * FROM Mi_";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT * FROM Mi_tabla1 WHERE fecha = ? AND compras <> 0",new String[]{_dato})));
 //BA.debugLineNum = 195;BA.debugLine="ListView2.AddSingleLine(\"FECHA     |Rut    |Nº";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence("FECHA     |Rut    |Nº   |COMPRA"));
 //BA.debugLineNum = 197;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step16 = 1;
final int limit16 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit16 ;_i = _i + step16 ) {
 //BA.debugLineNum = 198;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 199;BA.debugLine="Dim linea As String = _ 				Mi_Cursor.GetStrin";
_linea = mostCurrent._mi_cursor.GetString("fecha")+" | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("rut"))+"  | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"))+"  | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("compras"));
 //BA.debugLineNum = 204;BA.debugLine="ListView2.AddSingleLine(linea)";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 break; }
case 1: {
 //BA.debugLineNum = 208;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT * FROM Mi_";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT * FROM Mi_tabla1 WHERE fecha BETWEEN ? AND ? AND compras > 0",new String[]{_dato,_fechafinal})));
 //BA.debugLineNum = 209;BA.debugLine="ListView2.AddSingleLine(\"FECHA     |Rut    |Nº";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence("FECHA     |Rut    |Nº   |COMPRA"));
 //BA.debugLineNum = 211;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step24 = 1;
final int limit24 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit24 ;_i = _i + step24 ) {
 //BA.debugLineNum = 212;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 213;BA.debugLine="Dim linea As String = _ 				Mi_Cursor.GetStrin";
_linea = mostCurrent._mi_cursor.GetString("fecha")+" | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("rut"))+"  | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"))+"  | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("compras"));
 //BA.debugLineNum = 218;BA.debugLine="ListView2.AddSingleLine(linea)";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 break; }
case 2: {
 //BA.debugLineNum = 221;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT * FROM Mi_";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT * FROM Mi_tabla1 WHERE rut = ?",new String[]{_dato})));
 //BA.debugLineNum = 222;BA.debugLine="Dim totalCompras, totalPagos, saldo As Int";
_totalcompras = 0;
_totalpagos = 0;
_saldo = 0;
 //BA.debugLineNum = 223;BA.debugLine="LabelRut.Text = \"Rut: \" & dato";
mostCurrent._labelrut.setText(BA.ObjectToCharSequence("Rut: "+_dato));
 //BA.debugLineNum = 224;BA.debugLine="ListView2.AddSingleLine(\"FECHA     | Nº      |";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence("FECHA     | Nº      | COMPRA | PAGO"));
 //BA.debugLineNum = 225;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step34 = 1;
final int limit34 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit34 ;_i = _i + step34 ) {
 //BA.debugLineNum = 226;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 228;BA.debugLine="Dim compra As Int = Mi_Cursor.GetInt(\"compras\"";
_compra = mostCurrent._mi_cursor.GetInt("compras");
 //BA.debugLineNum = 229;BA.debugLine="Dim pago As Int = Mi_Cursor.GetInt(\"pagos\")";
_pago = mostCurrent._mi_cursor.GetInt("pagos");
 //BA.debugLineNum = 230;BA.debugLine="totalCompras = totalCompras + compra";
_totalcompras = (int) (_totalcompras+_compra);
 //BA.debugLineNum = 231;BA.debugLine="totalPagos = totalPagos + pago";
_totalpagos = (int) (_totalpagos+_pago);
 //BA.debugLineNum = 233;BA.debugLine="Dim linea As String = Mi_Cursor.GetString(\"fec";
_linea = mostCurrent._mi_cursor.GetString("fecha")+" | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"))+"  | "+BA.NumberToString(_compra)+"    | "+BA.NumberToString(_pago);
 //BA.debugLineNum = 234;BA.debugLine="ListView2.AddSingleLine(linea)";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 //BA.debugLineNum = 236;BA.debugLine="saldo = totalPagos - totalCompras";
_saldo = (int) (_totalpagos-_totalcompras);
 //BA.debugLineNum = 238;BA.debugLine="LabelMontos.Text = \"Total Compras:\" &totalCompr";
mostCurrent._labelmontos.setText(BA.ObjectToCharSequence("Total Compras:"+BA.NumberToString(_totalcompras)+" |Total Pagos:"+BA.NumberToString(_totalpagos)+" |Saldo:"+BA.NumberToString(_saldo)));
 break; }
}
;
 //BA.debugLineNum = 241;BA.debugLine="EditText5.Text=\"\"";
mostCurrent._edittext5.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 242;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 25;BA.debugLine="Dim Mi_Cursor As Cursor";
mostCurrent._mi_cursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private EditText1, EditText2, EditText3, EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext6 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext7 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext8 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext9 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext10 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private Button1 ,Button2, Button3, IngresaC As  B";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._v5 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._ingresac = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private ListView1, ListView2, ListView3 As ListVi";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
mostCurrent._listview2 = new anywheresoftware.b4a.objects.ListViewWrapper();
mostCurrent._listview3 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private Label1, Label2, Label3, LabelRut, LabelMo";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelrut = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelmontos = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelinfo = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelfin = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelformatodate = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private CheckBox1, CheckBox2 As CheckBox";
mostCurrent._checkbox1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
mostCurrent._checkbox2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Spinner1 As Spinner";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _ingresac_click() throws Exception{
int _cupo = 0;
int _totalcompras = 0;
int _totalpagos = 0;
int _saldo = 0;
int _i = 0;
int _compra = 0;
int _pago = 0;
String _linea = "";
String _fecha = "";
String _rut = "";
String _numero = "";
int _compras = 0;
int _pagos = 0;
 //BA.debugLineNum = 281;BA.debugLine="Private Sub IngresaC_Click";
 //BA.debugLineNum = 282;BA.debugLine="If EditText7.Text<>\"\" And EditText8.Text <> \"\" An";
if ((mostCurrent._edittext7.getText()).equals("") == false && (mostCurrent._edittext8.getText()).equals("") == false && (mostCurrent._edittext9.getText()).equals("") == false && (mostCurrent._edittext10.getText()).equals("") == false) { 
 //BA.debugLineNum = 283;BA.debugLine="Dim cupo As Int = 70000";
_cupo = (int) (70000);
 //BA.debugLineNum = 285;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 286;BA.debugLine="Try";
try { //BA.debugLineNum = 287;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT * FROM Mi_";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT * FROM Mi_tabla1 WHERE rut = ?",new String[]{mostCurrent._edittext7.getText()})));
 //BA.debugLineNum = 288;BA.debugLine="Dim totalCompras, totalPagos, saldo As Int";
_totalcompras = 0;
_totalpagos = 0;
_saldo = 0;
 //BA.debugLineNum = 289;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step7 = 1;
final int limit7 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
 //BA.debugLineNum = 290;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 292;BA.debugLine="Dim compra As Int = Mi_Cursor.GetInt(\"compras\"";
_compra = mostCurrent._mi_cursor.GetInt("compras");
 //BA.debugLineNum = 293;BA.debugLine="Dim pago As Int = Mi_Cursor.GetInt(\"pagos\")";
_pago = mostCurrent._mi_cursor.GetInt("pagos");
 //BA.debugLineNum = 294;BA.debugLine="totalCompras = totalCompras + compra";
_totalcompras = (int) (_totalcompras+_compra);
 //BA.debugLineNum = 295;BA.debugLine="totalPagos = totalPagos + pago";
_totalpagos = (int) (_totalpagos+_pago);
 //BA.debugLineNum = 297;BA.debugLine="Dim linea As String = Mi_Cursor.GetString(\"fec";
_linea = mostCurrent._mi_cursor.GetString("fecha")+" | "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"))+"  | "+BA.NumberToString(_compra)+"    | "+BA.NumberToString(_pago);
 //BA.debugLineNum = 298;BA.debugLine="ListView3.AddSingleLine(linea)";
mostCurrent._listview3.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 //BA.debugLineNum = 300;BA.debugLine="saldo = totalPagos - totalCompras";
_saldo = (int) (_totalpagos-_totalcompras);
 //BA.debugLineNum = 301;BA.debugLine="saldo = saldo + cupo";
_saldo = (int) (_saldo+_cupo);
 //BA.debugLineNum = 302;BA.debugLine="ToastMessageShow(saldo, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_saldo),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 303;BA.debugLine="If (saldo)> 0 Then";
if ((_saldo)>0) { 
 //BA.debugLineNum = 304;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Mi_tabla1 (ru";
_mi_db.ExecNonQuery2("INSERT INTO Mi_tabla1 (rut, fecha, numero, compras, pagos) VALUES (?,?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{mostCurrent._edittext7.getText(),mostCurrent._edittext8.getText(),mostCurrent._edittext9.getText(),mostCurrent._edittext10.getText(),BA.NumberToString(0)}));
 //BA.debugLineNum = 305;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 306;BA.debugLine="ToastMessageShow(\"Transacción guardada\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Transacción guardada"),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 308;BA.debugLine="ToastMessageShow(\"Excede Cupo\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Excede Cupo"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 309;BA.debugLine="Return";
if (true) return "";
 };
 } 
       catch (Exception e28) {
			processBA.setLastException(e28); //BA.debugLineNum = 313;BA.debugLine="Log(\"catch: \" & LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("0917536","catch: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 315;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 318;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT id_dato, rut,";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT id_dato, rut, fecha, numero, compras, pagos FROM Mi_tabla1 ORDER BY rut ASC")));
 //BA.debugLineNum = 320;BA.debugLine="ListView3.Clear";
mostCurrent._listview3.Clear();
 //BA.debugLineNum = 321;BA.debugLine="EditText7.Text=\"\"";
mostCurrent._edittext7.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 322;BA.debugLine="EditText8.Text=\"\"";
mostCurrent._edittext8.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 323;BA.debugLine="EditText9.Text=\"\"";
mostCurrent._edittext9.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 324;BA.debugLine="EditText10.Text=\"\"";
mostCurrent._edittext10.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 326;BA.debugLine="ListView3.AddSingleLine(\"Fecha       | Rut";
mostCurrent._listview3.AddSingleLine(BA.ObjectToCharSequence("Fecha       | Rut         | Nº | Compra | Pago"));
 //BA.debugLineNum = 328;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT rut, fecha,";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT rut, fecha, numero, compras, pagos FROM Mi_tabla1 ORDER BY rut ASC")));
 //BA.debugLineNum = 331;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step39 = 1;
final int limit39 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit39 ;_i = _i + step39 ) {
 //BA.debugLineNum = 332;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 334;BA.debugLine="Dim fecha As String = Mi_Cursor.GetString(\"fech";
_fecha = mostCurrent._mi_cursor.GetString("fecha");
 //BA.debugLineNum = 335;BA.debugLine="Dim rut As String = Mi_Cursor.GetString(\"rut\")";
_rut = mostCurrent._mi_cursor.GetString("rut");
 //BA.debugLineNum = 336;BA.debugLine="Dim numero As String = Mi_Cursor.GetInt(\"numero";
_numero = BA.NumberToString(mostCurrent._mi_cursor.GetInt("numero"));
 //BA.debugLineNum = 337;BA.debugLine="Dim compras As Int = Mi_Cursor.GetInt(\"compras\"";
_compras = mostCurrent._mi_cursor.GetInt("compras");
 //BA.debugLineNum = 338;BA.debugLine="Dim pagos As Int = Mi_Cursor.GetInt(\"pagos\")";
_pagos = mostCurrent._mi_cursor.GetInt("pagos");
 //BA.debugLineNum = 339;BA.debugLine="Dim linea As String = fecha & \"| \" & rut & \"|\"";
_linea = _fecha+"| "+_rut+"|"+_numero+"|"+BA.NumberToString(_compras)+"|"+BA.NumberToString(_pagos);
 //BA.debugLineNum = 340;BA.debugLine="ListView3.AddSingleLine(linea)";
mostCurrent._listview3.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 };
 //BA.debugLineNum = 344;BA.debugLine="End Sub";
return "";
}
public static boolean  _v6() throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Private Sub IsChecked1 As Boolean";
 //BA.debugLineNum = 175;BA.debugLine="Return CheckBox1.Checked";
if (true) return mostCurrent._checkbox1.getChecked();
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return false;
}
public static String  _listview1_itemlongclick(int _position,Object _value) throws Exception{
String[] _a = null;
 //BA.debugLineNum = 141;BA.debugLine="Sub ListView1_ItemLongClick (Position As Int, Valu";
 //BA.debugLineNum = 143;BA.debugLine="Dim a() As String";
_a = new String[(int) (0)];
java.util.Arrays.fill(_a,"");
 //BA.debugLineNum = 144;BA.debugLine="a=Regex.Split (\"-\",Value)";
_a = anywheresoftware.b4a.keywords.Common.Regex.Split("-",BA.ObjectToString(_value));
 //BA.debugLineNum = 146;BA.debugLine="Try";
try { //BA.debugLineNum = 147;BA.debugLine="Mi_DB.ExecNonQuery(\"DELETE FROM Mi_tabla1 WHERE";
_mi_db.ExecNonQuery("DELETE FROM Mi_tabla1 WHERE id_dato = "+_a[(int) (4)].trim());
 //BA.debugLineNum = 148;BA.debugLine="ToastMessageShow(\"Registro eliminado\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Registro eliminado"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 149;BA.debugLine="CheckBox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 150;BA.debugLine="Consultar_Click";
_consultar_click();
 } 
       catch (Exception e9) {
			processBA.setLastException(e9); //BA.debugLineNum = 152;BA.debugLine="ToastMessageShow(\"Error al eliminar\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al eliminar"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 154;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

private static byte[][] bb;

public static String vvv13(final byte[] _b, final int i) throws Exception {
Runnable r = new Runnable() {
{

int value = i / 5 + 953390;
if (bb == null) {
		
                bb = new byte[4][];
				bb[0] = BA.packageName.getBytes("UTF8");
                bb[1] = BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionName.getBytes("UTF8");
                if (bb[1].length == 0)
                    bb[1] = "jsdkfh".getBytes("UTF8");
                bb[2] = new byte[] { (byte)BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionCode };			
        }
        bb[3] = new byte[] {
                    (byte) (value >>> 24),
						(byte) (value >>> 16),
						(byte) (value >>> 8),
						(byte) value};
				try {
					for (int __b = 0;__b < (3 + 1);__b ++) {
						for (int b = 0;b<_b.length;b++) {
							_b[b] ^= bb[__b][b % bb[__b].length];
						}
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
                

            
}
public void run() {
}
};
return new String(_b, "UTF8");
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim Mi_DB As SQL";
_mi_db = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _spinner1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 264;BA.debugLine="Private Sub Spinner1_ItemClick (Position As Int, V";
 //BA.debugLineNum = 265;BA.debugLine="Select Position";
switch (_position) {
case 0: {
 //BA.debugLineNum = 267;BA.debugLine="EditText6.Visible = False";
mostCurrent._edittext6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 268;BA.debugLine="LabelFin.Visible = False";
mostCurrent._labelfin.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 269;BA.debugLine="LabelFormatoDate.Visible = True";
mostCurrent._labelformatodate.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 1: {
 //BA.debugLineNum = 271;BA.debugLine="EditText6.Visible = True";
mostCurrent._edittext6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 272;BA.debugLine="LabelFin.Visible = True";
mostCurrent._labelfin.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 273;BA.debugLine="LabelFormatoDate.Visible = True";
mostCurrent._labelformatodate.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 2: {
 //BA.debugLineNum = 275;BA.debugLine="EditText6.Visible = False";
mostCurrent._edittext6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 276;BA.debugLine="LabelFin.Visible = False";
mostCurrent._labelfin.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 277;BA.debugLine="LabelFormatoDate.Visible = False";
mostCurrent._labelformatodate.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
}
;
 //BA.debugLineNum = 279;BA.debugLine="End Sub";
return "";
}
}
