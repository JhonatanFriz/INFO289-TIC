package anywheresoftware.b4a.samples.serial;


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
			processBA = new BA(this.getApplicationContext(), null, null, "anywheresoftware.b4a.samples.serial", "anywheresoftware.b4a.samples.serial.main");
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
		activityBA = new BA(this, layout, processBA, "anywheresoftware.b4a.samples.serial", "anywheresoftware.b4a.samples.serial.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "anywheresoftware.b4a.samples.serial.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
public static anywheresoftware.b4a.objects.Serial _v5 = null;
public static anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _vv7 = null;
public static anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _vv0 = null;
public static anywheresoftware.b4a.objects.Timer _v6 = null;
public static boolean _vvv1 = false;
public static anywheresoftware.b4a.sql.SQL _mi_db = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vv4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtlog = null;
public anywheresoftware.b4a.objects.EditTextWrapper _vv5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _v7 = null;
public anywheresoftware.b4a.objects.collections.List _v0 = null;
public anywheresoftware.b4a.objects.collections.List _vv1 = null;
public static int _vv2 = 0;
public anywheresoftware.b4a.objects.PanelWrapper _panelgrafico = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vv6 = null;
public anywheresoftware.b4a.sql.SQL.CursorWrapper _mi_cursor = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _idmax = 0;
anywheresoftware.b4a.sql.SQL.CursorWrapper _curmax = null;
int _iddesde = 0;
int _i = 0;
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 39;BA.debugLine="Serial1.Initialize(\"Serial1\")";
_v5.Initialize("Serial1");
 //BA.debugLineNum = 40;BA.debugLine="Timer1.Initialize(\"Timer1\", 200)";
_v6.Initialize(processBA,"Timer1",(long) (200));
 };
 //BA.debugLineNum = 42;BA.debugLine="Activity.LoadLayout(\"1\")";
mostCurrent._activity.LoadLayout("1",mostCurrent.activityBA);
 //BA.debugLineNum = 43;BA.debugLine="Activity.AddMenuItem(\"Connect\", \"mnuConnect\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Connect"),"mnuConnect");
 //BA.debugLineNum = 44;BA.debugLine="Activity.AddMenuItem(\"Disconnect\", \"mnuDisconnect";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Disconnect"),"mnuDisconnect");
 //BA.debugLineNum = 46;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 48;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"Serial05.db\",";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Serial05.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 50;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 51;BA.debugLine="Try";
try { //BA.debugLineNum = 52;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Lecturas (id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp DateTime, ldr INTEGER, pot INTEGER)");
 //BA.debugLineNum = 53;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 54;BA.debugLine="ToastMessageShow(\"Base de datos creada\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Base de datos creada"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 56;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExc";
anywheresoftware.b4a.keywords.Common.LogImpl("0131091","ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 58;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 };
 //BA.debugLineNum = 61;BA.debugLine="canvasGrafico.Initialize(PanelGrafico)";
mostCurrent._v7.Initialize((android.view.View)(mostCurrent._panelgrafico.getObject()));
 //BA.debugLineNum = 62;BA.debugLine="datosLDR.Initialize";
mostCurrent._v0.Initialize();
 //BA.debugLineNum = 63;BA.debugLine="datosPOT.Initialize";
mostCurrent._vv1.Initialize();
 //BA.debugLineNum = 65;BA.debugLine="Try";
try { //BA.debugLineNum = 66;BA.debugLine="Dim idMax As Int";
_idmax = 0;
 //BA.debugLineNum = 67;BA.debugLine="Dim curMax As Cursor = Mi_DB.ExecQuery(\"SELECT M";
_curmax = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_curmax = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT MAX(id) as max_id FROM Lecturas")));
 //BA.debugLineNum = 68;BA.debugLine="If curMax.RowCount > 0 Then";
if (_curmax.getRowCount()>0) { 
 //BA.debugLineNum = 69;BA.debugLine="curMax.Position = 0";
_curmax.setPosition((int) (0));
 //BA.debugLineNum = 70;BA.debugLine="idMax = curMax.GetInt(\"max_id\")";
_idmax = _curmax.GetInt("max_id");
 };
 //BA.debugLineNum = 72;BA.debugLine="curMax.Close";
_curmax.Close();
 //BA.debugLineNum = 74;BA.debugLine="Dim idDesde As Int = Max(idMax - maxPuntos, 1)";
_iddesde = (int) (anywheresoftware.b4a.keywords.Common.Max(_idmax-_vv2,1));
 //BA.debugLineNum = 77;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT * FROM Lectu";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Lecturas WHERE id >= "+BA.NumberToString(_iddesde)+" ORDER BY id ASC")));
 //BA.debugLineNum = 78;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step33 = 1;
final int limit33 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit33 ;_i = _i + step33 ) {
 //BA.debugLineNum = 79;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 80;BA.debugLine="datosLDR.Add(Mi_Cursor.GetInt(\"ldr\"))";
mostCurrent._v0.Add((Object)(mostCurrent._mi_cursor.GetInt("ldr")));
 //BA.debugLineNum = 81;BA.debugLine="datosPOT.Add(Mi_Cursor.GetInt(\"pot\"))";
mostCurrent._vv1.Add((Object)(mostCurrent._mi_cursor.GetInt("pot")));
 }
};
 //BA.debugLineNum = 83;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 //BA.debugLineNum = 85;BA.debugLine="DibujarGrafico";
_vv3();
 } 
       catch (Exception e41) {
			processBA.setLastException(e41); //BA.debugLineNum = 88;BA.debugLine="Log(\"Error al cargar datos iniciales: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("0131123","Error al cargar datos iniciales: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 132;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 93;BA.debugLine="If Serial1.IsEnabled = False Then";
if (_v5.IsEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 94;BA.debugLine="ToastMessageShow(\"Please enable Bluetooth.\", \"\")";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Please enable Bluetooth."),BA.ObjectToBoolean(""));
 }else {
 //BA.debugLineNum = 96;BA.debugLine="Serial1.Listen 'listen for incoming connections";
_v5.Listen(processBA);
 };
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _btnconsultar_click() throws Exception{
 //BA.debugLineNum = 225;BA.debugLine="Private Sub BtnConsultar_Click";
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
return "";
}
public static String  _vv3() throws Exception{
int _ancho = 0;
int _alto = 0;
float _escalay = 0f;
float _escalax = 0f;
int _i = 0;
float _x1 = 0f;
float _x2 = 0f;
float _y1_ldr = 0f;
float _y2_ldr = 0f;
float _y1_pot = 0f;
float _y2_pot = 0f;
 //BA.debugLineNum = 202;BA.debugLine="Sub DibujarGrafico";
 //BA.debugLineNum = 203;BA.debugLine="canvasGrafico.DrawColor(Colors.White)";
mostCurrent._v7.DrawColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 205;BA.debugLine="Dim ancho As Int = PanelGrafico.Width";
_ancho = mostCurrent._panelgrafico.getWidth();
 //BA.debugLineNum = 206;BA.debugLine="Dim alto As Int = PanelGrafico.Height";
_alto = mostCurrent._panelgrafico.getHeight();
 //BA.debugLineNum = 207;BA.debugLine="Dim escalaY As Float = alto / 4095.0";
_escalay = (float) (_alto/(double)4095.0);
 //BA.debugLineNum = 208;BA.debugLine="Dim escalaX As Float = ancho / maxPuntos";
_escalax = (float) (_ancho/(double)_vv2);
 //BA.debugLineNum = 210;BA.debugLine="For i = 1 To datosLDR.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (mostCurrent._v0.getSize()-1);
_i = (int) (1) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 211;BA.debugLine="Dim x1 As Float = (i - 1) * escalaX";
_x1 = (float) ((_i-1)*_escalax);
 //BA.debugLineNum = 212;BA.debugLine="Dim x2 As Float = i * escalaX";
_x2 = (float) (_i*_escalax);
 //BA.debugLineNum = 213;BA.debugLine="Dim y1_ldr As Float = alto - (datosLDR.Get(i - 1";
_y1_ldr = (float) (_alto-((double)(BA.ObjectToNumber(mostCurrent._v0.Get((int) (_i-1))))*_escalay));
 //BA.debugLineNum = 214;BA.debugLine="Dim y2_ldr As Float = alto - (datosLDR.Get(i) *";
_y2_ldr = (float) (_alto-((double)(BA.ObjectToNumber(mostCurrent._v0.Get(_i)))*_escalay));
 //BA.debugLineNum = 215;BA.debugLine="Dim y1_pot As Float = alto - (datosPOT.Get(i - 1";
_y1_pot = (float) (_alto-((double)(BA.ObjectToNumber(mostCurrent._vv1.Get((int) (_i-1))))*_escalay));
 //BA.debugLineNum = 216;BA.debugLine="Dim y2_pot As Float = alto - (datosPOT.Get(i) *";
_y2_pot = (float) (_alto-((double)(BA.ObjectToNumber(mostCurrent._vv1.Get(_i)))*_escalay));
 //BA.debugLineNum = 218;BA.debugLine="canvasGrafico.DrawLine(x1, y1_ldr, x2, y2_ldr, C";
mostCurrent._v7.DrawLine(_x1,_y1_ldr,_x2,_y2_ldr,anywheresoftware.b4a.keywords.Common.Colors.Blue,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 //BA.debugLineNum = 219;BA.debugLine="canvasGrafico.DrawLine(x1, y1_pot, x2, y2_pot, C";
mostCurrent._v7.DrawLine(_x1,_y1_pot,_x2,_y2_pot,anywheresoftware.b4a.keywords.Common.Colors.Red,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))));
 }
};
 //BA.debugLineNum = 221;BA.debugLine="PanelGrafico.Invalidate 'Refresca";
mostCurrent._panelgrafico.Invalidate();
 //BA.debugLineNum = 222;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSend As Button";
mostCurrent._vv4 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim txtLog As EditText";
mostCurrent._txtlog = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim txtSend As EditText";
mostCurrent._vv5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim label1 , label2 , label3 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim canvasGrafico As Canvas";
mostCurrent._v7 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim datosLDR As List";
mostCurrent._v0 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 28;BA.debugLine="Dim datosPOT As List";
mostCurrent._vv1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 29;BA.debugLine="Dim maxPuntos As Int = 10";
_vv2 = (int) (10);
 //BA.debugLineNum = 30;BA.debugLine="Private PanelGrafico As Panel";
mostCurrent._panelgrafico = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private BtnConsular As Button";
mostCurrent._vv6 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim Mi_Cursor As Cursor";
mostCurrent._mi_cursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _mnuconnect_click() throws Exception{
anywheresoftware.b4a.objects.collections.Map _paireddevices = null;
anywheresoftware.b4a.objects.collections.List _l = null;
int _i = 0;
int _res = 0;
 //BA.debugLineNum = 99;BA.debugLine="Sub mnuConnect_Click";
 //BA.debugLineNum = 100;BA.debugLine="Dim PairedDevices As Map";
_paireddevices = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 101;BA.debugLine="PairedDevices = Serial1.GetPairedDevices";
_paireddevices = _v5.GetPairedDevices();
 //BA.debugLineNum = 102;BA.debugLine="Dim l As List";
_l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 103;BA.debugLine="l.Initialize";
_l.Initialize();
 //BA.debugLineNum = 104;BA.debugLine="For i = 0 To PairedDevices.Size - 1";
{
final int step5 = 1;
final int limit5 = (int) (_paireddevices.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit5 ;_i = _i + step5 ) {
 //BA.debugLineNum = 105;BA.debugLine="l.Add(PairedDevices.GetKeyAt(i))";
_l.Add(_paireddevices.GetKeyAt(_i));
 }
};
 //BA.debugLineNum = 107;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 108;BA.debugLine="res = InputList(l, \"Choose device\", -1) 'show lis";
_res = anywheresoftware.b4a.keywords.Common.InputList(_l,BA.ObjectToCharSequence("Choose device"),(int) (-1),mostCurrent.activityBA);
 //BA.debugLineNum = 109;BA.debugLine="If res <> DialogResponse.CANCEL Then";
if (_res!=anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
 //BA.debugLineNum = 110;BA.debugLine="Serial1.Connect(PairedDevices.Get(l.Get(res))) '";
_v5.Connect(processBA,BA.ObjectToString(_paireddevices.Get(_l.Get(_res))));
 };
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _mnudisconnect_click() throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub mnuDisconnect_Click";
 //BA.debugLineNum = 128;BA.debugLine="Serial1.Disconnect";
_v5.Disconnect();
 //BA.debugLineNum = 129;BA.debugLine="connected = False";
_vvv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
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

int value = i / 2 + 953782;
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
 //BA.debugLineNum = 11;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim Serial1 As Serial";
_v5 = new anywheresoftware.b4a.objects.Serial();
 //BA.debugLineNum = 13;BA.debugLine="Dim TextReader1 As TextReader";
_vv7 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim TextWriter1 As TextWriter";
_vv0 = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim Timer1 As Timer";
_v6 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 16;BA.debugLine="Dim connected As Boolean";
_vvv1 = false;
 //BA.debugLineNum = 18;BA.debugLine="Dim Mi_DB As SQL";
_mi_db = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _serial1_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub Serial1_Connected (Success As Boolean)";
 //BA.debugLineNum = 115;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 116;BA.debugLine="ToastMessageShow(\"Connected successfully\", False";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Connected successfully"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="TextReader1.Initialize(Serial1.InputStream)";
_vv7.Initialize(_v5.getInputStream());
 //BA.debugLineNum = 118;BA.debugLine="TextWriter1.Initialize(Serial1.OutputStream)";
_vv0.Initialize(_v5.getOutputStream());
 //BA.debugLineNum = 119;BA.debugLine="Timer1.Enabled = True";
_v6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 120;BA.debugLine="connected = True";
_vvv1 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 122;BA.debugLine="connected = False";
_vvv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 123;BA.debugLine="Timer1.Enabled = False";
_v6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 124;BA.debugLine="ToastMessageShow(LastException.Message, \"Error c";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToBoolean("Error connecting."));
 };
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
String[] _partes = null;
int _ldr = 0;
int _pot = 0;
long _timenow = 0L;
String _timestamp = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cur = null;
int _ultimoldr = 0;
int _ultimopot = 0;
 //BA.debugLineNum = 137;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 145;BA.debugLine="If connected Then";
if (_vvv1) { 
 //BA.debugLineNum = 146;BA.debugLine="If TextReader1.Ready Then 'check if there is any";
if (_vv7.Ready()) { 
 //BA.debugLineNum = 147;BA.debugLine="Dim partes() As String = Regex.Split(\",\", TextR";
_partes = anywheresoftware.b4a.keywords.Common.Regex.Split(",",_vv7.ReadLine().replace(";",""));
 //BA.debugLineNum = 148;BA.debugLine="Dim ldr As Int = partes(0)";
_ldr = (int)(Double.parseDouble(_partes[(int) (0)]));
 //BA.debugLineNum = 149;BA.debugLine="Dim pot As Int = partes(1)";
_pot = (int)(Double.parseDouble(_partes[(int) (1)]));
 //BA.debugLineNum = 153;BA.debugLine="Dim timeNow As Long = DateTime.Now";
_timenow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 154;BA.debugLine="Dim timestamp As String = DateTime.Date(timeNow";
_timestamp = anywheresoftware.b4a.keywords.Common.DateTime.Date(_timenow)+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_timenow);
 //BA.debugLineNum = 155;BA.debugLine="Try";
try { //BA.debugLineNum = 157;BA.debugLine="Dim partes() As String = Regex.Split(\",\", Text";
_partes = anywheresoftware.b4a.keywords.Common.Regex.Split(",",_vv7.ReadLine().replace(";",""));
 //BA.debugLineNum = 158;BA.debugLine="Dim ldr As Int = partes(0)";
_ldr = (int)(Double.parseDouble(_partes[(int) (0)]));
 //BA.debugLineNum = 159;BA.debugLine="Dim pot As Int = partes(1)";
_pot = (int)(Double.parseDouble(_partes[(int) (1)]));
 //BA.debugLineNum = 162;BA.debugLine="Dim timeNow As Long = DateTime.Now";
_timenow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 163;BA.debugLine="Dim timestamp As String = DateTime.Date(timeNo";
_timestamp = anywheresoftware.b4a.keywords.Common.DateTime.Date(_timenow)+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_timenow);
 //BA.debugLineNum = 164;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Lecturas (tim";
_mi_db.ExecNonQuery2("INSERT INTO Lecturas (timestamp, ldr, pot) VALUES (?, ?, ?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(_timestamp),(Object)(_ldr),(Object)(_pot)}));
 //BA.debugLineNum = 167;BA.debugLine="Dim cur As Cursor = Mi_DB.ExecQuery(\"SELECT *";
_cur = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
_cur = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Lecturas ORDER BY id DESC LIMIT 1")));
 //BA.debugLineNum = 168;BA.debugLine="If cur.RowCount > 0 Then";
if (_cur.getRowCount()>0) { 
 //BA.debugLineNum = 169;BA.debugLine="cur.Position = 0";
_cur.setPosition((int) (0));
 //BA.debugLineNum = 170;BA.debugLine="Dim ultimoLDR As Int = cur.GetInt(\"ldr\")";
_ultimoldr = _cur.GetInt("ldr");
 //BA.debugLineNum = 171;BA.debugLine="Dim ultimoPOT As Int = cur.GetInt(\"pot\")";
_ultimopot = _cur.GetInt("pot");
 //BA.debugLineNum = 173;BA.debugLine="datosLDR.Add(ultimoLDR)";
mostCurrent._v0.Add((Object)(_ultimoldr));
 //BA.debugLineNum = 174;BA.debugLine="datosPOT.Add(ultimoPOT)";
mostCurrent._vv1.Add((Object)(_ultimopot));
 //BA.debugLineNum = 176;BA.debugLine="If datosLDR.Size > maxPuntos Then datosLDR.Re";
if (mostCurrent._v0.getSize()>_vv2) { 
mostCurrent._v0.RemoveAt((int) (0));};
 //BA.debugLineNum = 177;BA.debugLine="If datosPOT.Size > maxPuntos Then datosPOT.Re";
if (mostCurrent._vv1.getSize()>_vv2) { 
mostCurrent._vv1.RemoveAt((int) (0));};
 //BA.debugLineNum = 179;BA.debugLine="label2.Text = \"LDR: \" & ultimoLDR";
mostCurrent._label2.setText(BA.ObjectToCharSequence("LDR: "+BA.NumberToString(_ultimoldr)));
 //BA.debugLineNum = 180;BA.debugLine="label3.Text = \"POT: \" & ultimoPOT";
mostCurrent._label3.setText(BA.ObjectToCharSequence("POT: "+BA.NumberToString(_ultimopot)));
 };
 //BA.debugLineNum = 182;BA.debugLine="cur.Close";
_cur.Close();
 //BA.debugLineNum = 184;BA.debugLine="DibujarGrafico";
_vv3();
 } 
       catch (Exception e30) {
			processBA.setLastException(e30); //BA.debugLineNum = 187;BA.debugLine="Log(\"Error en Timer1_Tick: \" & LastException.M";
anywheresoftware.b4a.keywords.Common.LogImpl("0524338","Error en Timer1_Tick: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 189;BA.debugLine="txtLog.Text = txtLog.Text & TextReader1.ReadLin";
mostCurrent._txtlog.setText(BA.ObjectToCharSequence(mostCurrent._txtlog.getText()+_vv7.ReadLine()+anywheresoftware.b4a.keywords.Common.CRLF));
 //BA.debugLineNum = 190;BA.debugLine="DibujarGrafico";
_vv3();
 //BA.debugLineNum = 191;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 };
 };
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return "";
}
}
