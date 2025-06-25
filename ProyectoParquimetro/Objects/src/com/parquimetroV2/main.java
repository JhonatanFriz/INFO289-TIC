package com.parquimetroV2;


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
			processBA = new BA(this.getApplicationContext(), null, null, "com.parquimetroV2", "com.parquimetroV2.main");
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
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
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
		activityBA = new BA(this, layout, processBA, "com.parquimetroV2", "com.parquimetroV2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.parquimetroV2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _vvv5 = null;
public static anywheresoftware.b4a.sql.SQL _mi_db = null;
public anywheresoftware.b4a.sql.SQL.CursorWrapper _mi_cursor = null;
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerusuarios = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextpatente = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextticket = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextdateini = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextdatefin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextnombre = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextclave = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittextrut = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonregistrar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttoniniciarsesion = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btningreso = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlistar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btngeneraticket = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninforme = null;
public anywheresoftware.b4a.objects.ButtonWrapper _calendarini = null;
public anywheresoftware.b4a.objects.ButtonWrapper _calendarfin = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonlogout = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _infoticket = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelactivos = null;
public anywheresoftware.b4a.objects.LabelWrapper _labeltotal = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelpordia = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelusuarioactivo = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelsesion = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelregistro = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview2 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listviewresumen = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxactivos = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxdate = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxsesion = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxregistro = null;
public static int _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = 0;
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = "";
public com.parquimetroV2.b4xdialog _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = null;
public com.parquimetroV2.b4xdatetemplate _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3 = null;
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "";
public b4a.example.dateutils _vvvv0 = null;
public com.parquimetroV2.starter _vvvvv2 = null;
public com.parquimetroV2.xuiviewsutils _vvvvv3 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 44;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 45;BA.debugLine="Activity.Title = \"Parquímetro\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Parquímetro"));
 //BA.debugLineNum = 46;BA.debugLine="TabHost1.AddTab(\"Usuarios\", \"pantaUser\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Usuarios","pantaUser");
 //BA.debugLineNum = 47;BA.debugLine="TabHost1.AddTab(\"Ingreso\", \"pantaIngreso\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Ingreso","pantaIngreso");
 //BA.debugLineNum = 48;BA.debugLine="TabHost1.AddTab(\"Listado\", \"listaVehiculos\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Listado","listaVehiculos");
 //BA.debugLineNum = 49;BA.debugLine="TabHost1.AddTab(\"Ticket\", \"pantaTicket\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Ticket","pantaTicket");
 //BA.debugLineNum = 50;BA.debugLine="TabHost1.AddTab(\"Resumen\", \"pantaResumenTotal\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Resumen","pantaResumenTotal");
 //BA.debugLineNum = 52;BA.debugLine="VerifyCheckboxes";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1();
 //BA.debugLineNum = 54;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 56;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"ParquimetroFi";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ParquimetroFinal.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 58;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 59;BA.debugLine="Try";
try { //BA.debugLineNum = 60;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Vehiculos (id_dato INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, hora TEXT, patente TEXT, isActive INTEGER, id_usuario INTEGER)");
 //BA.debugLineNum = 61;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Pagos (id_pago INTEGER PRIMARY KEY AUTOINCREMENT, id_dato INTEGER, salida_fecha TEXT, salida_hora TEXT, monto INTEGER, id_usuario INTEGER, FOREIGN KEY (id_dato) REFERENCES Vehiculos(id_dato))");
 //BA.debugLineNum = 62;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Usuarios (id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, rut Text UNIQUE, clave TEXT)");
 //BA.debugLineNum = 63;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 64;BA.debugLine="ToastMessageShow(\"Base de datos creada\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Base de datos creada"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); //BA.debugLineNum = 66;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExc";
anywheresoftware.b4a.keywords.Common.LogImpl("0131095","ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 68;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 };
 //BA.debugLineNum = 71;BA.debugLine="dialog.Initialize(Activity)";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2._initialize /*String*/ (mostCurrent.activityBA,(anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(mostCurrent._activity.getObject())));
 //BA.debugLineNum = 72;BA.debugLine="dateTemplate.Initialize";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._initialize /*String*/ (mostCurrent.activityBA);
 //BA.debugLineNum = 73;BA.debugLine="dateTemplate.FirstDay = 1";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvvvvvvvvvvvv3 /*int*/  = (int) (1);
 //BA.debugLineNum = 74;BA.debugLine="CompletaSpinnerUsuarios";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4();
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 78;BA.debugLine="MostrarSesion";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 79;BA.debugLine="CompletaSpinnerUsuarios";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4();
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _btngeneraticket_click() throws Exception{
String _fechaentrada = "";
String _horaentrada = "";
int _id_dato = 0;
String[] _partesfecha = null;
String[] _parteshora = null;
int _año = 0;
int _mes = 0;
int _dia = 0;
int _hora = 0;
int _minuto = 0;
int _segundo = 0;
long _dtentrada = 0L;
long _dtsalida = 0L;
int _minutos = 0;
int _montopago = 0;
 //BA.debugLineNum = 158;BA.debugLine="Private Sub BtnGeneraTicket_Click";
 //BA.debugLineNum = 159;BA.debugLine="If Not(ValidaSesion) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6())) { 
 //BA.debugLineNum = 160;BA.debugLine="EditTextTicket.Text = \"\"";
mostCurrent._edittextticket.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 161;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 164;BA.debugLine="If EditTextTicket.Text<>\"\" Then";
if ((mostCurrent._edittextticket.getText()).equals("") == false) { 
 //BA.debugLineNum = 165;BA.debugLine="Try";
try { //BA.debugLineNum = 166;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT id_dato, f";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT id_dato, fecha, hora FROM Vehiculos WHERE patente = ? AND  isActive = 1 LIMIT 1",new String[]{mostCurrent._edittextticket.getText().trim().toUpperCase()})));
 //BA.debugLineNum = 169;BA.debugLine="If Mi_Cursor.RowCount = 0 Then";
if (mostCurrent._mi_cursor.getRowCount()==0) { 
 //BA.debugLineNum = 170;BA.debugLine="InfoTicket.Text = \"Vehículo no encontrado o ya";
mostCurrent._infoticket.setText(BA.ObjectToCharSequence("Vehículo no encontrado o ya retirado"));
 //BA.debugLineNum = 171;BA.debugLine="ToastMessageShow(\"NO ENCONTRADO\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("NO ENCONTRADO"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 172;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 174;BA.debugLine="Mi_Cursor.Position = 0";
mostCurrent._mi_cursor.setPosition((int) (0));
 //BA.debugLineNum = 175;BA.debugLine="Dim fechaEntrada As String = Mi_Cursor.GetStrin";
_fechaentrada = mostCurrent._mi_cursor.GetString("fecha");
 //BA.debugLineNum = 176;BA.debugLine="Dim horaEntrada As String = Mi_Cursor.GetString";
_horaentrada = mostCurrent._mi_cursor.GetString("hora");
 //BA.debugLineNum = 177;BA.debugLine="Dim id_dato As Int = Mi_Cursor.GetInt(\"id_dato\"";
_id_dato = mostCurrent._mi_cursor.GetInt("id_dato");
 //BA.debugLineNum = 179;BA.debugLine="DateTime.DateFormat = \"dd/MM/yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd/MM/yyyy");
 //BA.debugLineNum = 180;BA.debugLine="DateTime.TimeFormat = \"HH:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm:ss");
 //BA.debugLineNum = 182;BA.debugLine="Log(\"Fecha entrada TICKET: \" & fechaEntrada)";
anywheresoftware.b4a.keywords.Common.LogImpl("0458776","Fecha entrada TICKET: "+_fechaentrada,0);
 //BA.debugLineNum = 183;BA.debugLine="Log(\"Hora entrada TICKET: \" & horaEntrada)";
anywheresoftware.b4a.keywords.Common.LogImpl("0458777","Hora entrada TICKET: "+_horaentrada,0);
 //BA.debugLineNum = 184;BA.debugLine="Dim partesFecha() As String = Regex.Split(\"/\",";
_partesfecha = anywheresoftware.b4a.keywords.Common.Regex.Split("/",_fechaentrada);
 //BA.debugLineNum = 185;BA.debugLine="Dim partesHora() As String = Regex.Split(\":\", h";
_parteshora = anywheresoftware.b4a.keywords.Common.Regex.Split(":",_horaentrada);
 //BA.debugLineNum = 187;BA.debugLine="Dim año As Int = partesFecha(2)";
_año = (int)(Double.parseDouble(_partesfecha[(int) (2)]));
 //BA.debugLineNum = 188;BA.debugLine="Dim mes As Int = partesFecha(1)";
_mes = (int)(Double.parseDouble(_partesfecha[(int) (1)]));
 //BA.debugLineNum = 189;BA.debugLine="Dim dia As Int = partesFecha(0)";
_dia = (int)(Double.parseDouble(_partesfecha[(int) (0)]));
 //BA.debugLineNum = 191;BA.debugLine="Dim hora As Int = partesHora(0)";
_hora = (int)(Double.parseDouble(_parteshora[(int) (0)]));
 //BA.debugLineNum = 192;BA.debugLine="Dim minuto As Int = partesHora(1)";
_minuto = (int)(Double.parseDouble(_parteshora[(int) (1)]));
 //BA.debugLineNum = 193;BA.debugLine="Dim segundo As Int = partesHora(2)";
_segundo = (int)(Double.parseDouble(_parteshora[(int) (2)]));
 //BA.debugLineNum = 195;BA.debugLine="Dim dtEntrada As Long = DateUtils.SetDateAndTim";
_dtentrada = mostCurrent._vvvv0._setdateandtime(mostCurrent.activityBA,_año,_mes,_dia,_hora,_minuto,_segundo);
 //BA.debugLineNum = 197;BA.debugLine="Log(\"d Entrada \"&dtEntrada)";
anywheresoftware.b4a.keywords.Common.LogImpl("0458791","d Entrada "+BA.NumberToString(_dtentrada),0);
 //BA.debugLineNum = 198;BA.debugLine="Dim dtSalida As Long = DateTime.Now";
_dtsalida = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 199;BA.debugLine="Dim minutos As Int = Round((dtSalida - dtEntrad";
_minutos = (int) (anywheresoftware.b4a.keywords.Common.Round((_dtsalida-_dtentrada)/(double)anywheresoftware.b4a.keywords.Common.DateTime.TicksPerMinute));
 //BA.debugLineNum = 200;BA.debugLine="ToastMessageShow(minutos, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_minutos),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 201;BA.debugLine="Log(\"Entrada: \" & DateTime.Date(dtEntrada) & \"";
anywheresoftware.b4a.keywords.Common.LogImpl("0458795","Entrada: "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtentrada)+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtentrada),0);
 //BA.debugLineNum = 202;BA.debugLine="Log(\"Salida: \" & DateTime.Date(dtSalida) & \" \"";
anywheresoftware.b4a.keywords.Common.LogImpl("0458796","Salida: "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtsalida)+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtsalida),0);
 //BA.debugLineNum = 203;BA.debugLine="Dim montoPago As Int";
_montopago = 0;
 //BA.debugLineNum = 204;BA.debugLine="Log(\" MIN-utOOOOOOOOOOOOS \" & minutos)";
anywheresoftware.b4a.keywords.Common.LogImpl("0458798"," MIN-utOOOOOOOOOOOOS "+BA.NumberToString(_minutos),0);
 //BA.debugLineNum = 205;BA.debugLine="If minutos <= 15 Then";
if (_minutos<=15) { 
 //BA.debugLineNum = 206;BA.debugLine="montoPago = 500";
_montopago = (int) (500);
 }else {
 //BA.debugLineNum = 208;BA.debugLine="montoPago = 500 + ((minutos - 15) * 30)";
_montopago = (int) (500+((_minutos-15)*30));
 };
 //BA.debugLineNum = 211;BA.debugLine="InfoTicket.Text = \"                    Parquíme";
mostCurrent._infoticket.setText(BA.ObjectToCharSequence("                    Parquímetro Austral "+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"RUT: 00.000.000-0"+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"----------------- DATOS VEHICULO -----------------"+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+" PATENTE: "+mostCurrent._edittextticket.getText()+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"--------------- INGRESO ---------------"+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"Fecha : "+_fechaentrada+"  Hora : "+_horaentrada+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"----------------- SALIDA -----------------"+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"Fecha : "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtsalida)+"  Hora : "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtsalida)+anywheresoftware.b4a.keywords.Common.CRLF+""+anywheresoftware.b4a.keywords.Common.CRLF+"Tiempo: "+BA.NumberToString(_minutos)+" minutos"+anywheresoftware.b4a.keywords.Common.CRLF+"Monto: $"+BA.NumberToString(_montopago)));
 //BA.debugLineNum = 230;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Pagos (id_dato";
_mi_db.ExecNonQuery2("INSERT INTO Pagos (id_dato, salida_fecha, salida_hora, monto, id_usuario) VALUES (?,?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(_id_dato),(Object)(anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtsalida)),(Object)(anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtsalida)),(Object)(_montopago),(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7)}));
 //BA.debugLineNum = 233;BA.debugLine="Mi_DB.ExecNonQuery2(\"UPDATE Vehiculos SET isAct";
_mi_db.ExecNonQuery2("UPDATE Vehiculos SET isActive = 0 WHERE id_dato = ?",anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(_id_dato)}));
 //BA.debugLineNum = 234;BA.debugLine="EditTextTicket.Text=\"\"";
mostCurrent._edittextticket.setText(BA.ObjectToCharSequence(""));
 } 
       catch (Exception e48) {
			processBA.setLastException(e48); //BA.debugLineNum = 237;BA.debugLine="Log(\"ERROR EN BtnGeneraTicket: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("0458831","ERROR EN BtnGeneraTicket: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 238;BA.debugLine="ToastMessageShow(\"Error al generar el ticket: \"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al generar el ticket: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 241;BA.debugLine="End Sub";
return "";
}
public static String  _btninforme_click() throws Exception{
String _fechainicio = "";
String _fechafin = "";
int _totalrecaudado = 0;
String _usuarioseleccionado = "";
boolean _filtroporusuario = false;
int _idseleccionado = 0;
String _query = "";
anywheresoftware.b4a.objects.collections.List _args = null;
String[] _param = null;
int _i = 0;
int _monto = 0;
String _linea = "";
 //BA.debugLineNum = 253;BA.debugLine="Private Sub BtnInforme_Click";
 //BA.debugLineNum = 254;BA.debugLine="ListViewResumen.Clear";
mostCurrent._listviewresumen.Clear();
 //BA.debugLineNum = 255;BA.debugLine="LabelTotal.Text = \"\"";
mostCurrent._labeltotal.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 256;BA.debugLine="Dim fechaInicio As String = EditTextDateIni.Text.";
_fechainicio = mostCurrent._edittextdateini.getText().trim();
 //BA.debugLineNum = 257;BA.debugLine="Dim fechaFin As String = EditTextDateFin.Text.Tri";
_fechafin = mostCurrent._edittextdatefin.getText().trim();
 //BA.debugLineNum = 258;BA.debugLine="DateTime.DateFormat = \"dd/MM/yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd/MM/yyyy");
 //BA.debugLineNum = 260;BA.debugLine="If fechaInicio=\"\" Then";
if ((_fechainicio).equals("")) { 
 //BA.debugLineNum = 261;BA.debugLine="ToastMessageShow(\"Ingrese fecha\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese fecha"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 262;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 265;BA.debugLine="Dim totalRecaudado As Int = 0";
_totalrecaudado = (int) (0);
 //BA.debugLineNum = 266;BA.debugLine="Dim usuarioSeleccionado As String = SpinnerUsuari";
_usuarioseleccionado = mostCurrent._spinnerusuarios.getSelectedItem();
 //BA.debugLineNum = 267;BA.debugLine="Dim filtroPorUsuario As Boolean = (usuarioSelecci";
_filtroporusuario = ((_usuarioseleccionado).equals("Todos") == false);
 //BA.debugLineNum = 268;BA.debugLine="Dim idSeleccionado As Int = -1";
_idseleccionado = (int) (-1);
 //BA.debugLineNum = 270;BA.debugLine="If filtroPorUsuario Then";
if (_filtroporusuario) { 
 //BA.debugLineNum = 271;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT id_usuario";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT id_usuario FROM Usuarios WHERE rut = ?",new String[]{_usuarioseleccionado})));
 //BA.debugLineNum = 272;BA.debugLine="If Mi_Cursor.RowCount > 0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 273;BA.debugLine="Mi_Cursor.Position = 0";
mostCurrent._mi_cursor.setPosition((int) (0));
 //BA.debugLineNum = 274;BA.debugLine="idSeleccionado = Mi_Cursor.GetInt(\"id_usuario\")";
_idseleccionado = mostCurrent._mi_cursor.GetInt("id_usuario");
 };
 //BA.debugLineNum = 276;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 };
 //BA.debugLineNum = 278;BA.debugLine="Try";
try { //BA.debugLineNum = 279;BA.debugLine="Dim query As String";
_query = "";
 //BA.debugLineNum = 280;BA.debugLine="Dim args As List";
_args = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 281;BA.debugLine="args.Initialize";
_args.Initialize();
 //BA.debugLineNum = 283;BA.debugLine="If CheckboxDate.Checked Then";
if (mostCurrent._checkboxdate.getChecked()) { 
 //BA.debugLineNum = 284;BA.debugLine="query = \"SELECT u.rut, v.patente, p.salida_fech";
_query = "SELECT u.rut, v.patente, p.salida_fecha, p.monto FROM Pagos p INNER JOIN Vehiculos v ON v.id_dato = p.id_dato INNER JOIN Usuarios u ON u.id_usuario = p.id_usuario WHERE p.salida_fecha = ?";
 //BA.debugLineNum = 285;BA.debugLine="args.Add(fechaInicio)";
_args.Add((Object)(_fechainicio));
 }else {
 //BA.debugLineNum = 287;BA.debugLine="If fechaFin = \"\" Then";
if ((_fechafin).equals("")) { 
 //BA.debugLineNum = 288;BA.debugLine="ToastMessageShow(\"Ingrese fecha final\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese fecha final"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 289;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 291;BA.debugLine="query = \"SELECT u.rut, v.patente, p.salida_fech";
_query = "SELECT u.rut, v.patente, p.salida_fecha, p.monto FROM Pagos p INNER JOIN Vehiculos v ON v.id_dato = p.id_dato INNER JOIN Usuarios u ON u.id_usuario = p.id_usuario WHERE p.salida_fecha BETWEEN ? AND ?";
 //BA.debugLineNum = 292;BA.debugLine="args.Add(fechaInicio)";
_args.Add((Object)(_fechainicio));
 //BA.debugLineNum = 293;BA.debugLine="args.Add(fechaFin)";
_args.Add((Object)(_fechafin));
 };
 //BA.debugLineNum = 296;BA.debugLine="If filtroPorUsuario Then";
if (_filtroporusuario) { 
 //BA.debugLineNum = 297;BA.debugLine="query = query & \"AND p.id_usuario = ?\"";
_query = _query+"AND p.id_usuario = ?";
 //BA.debugLineNum = 298;BA.debugLine="args.Add(idSeleccionado)";
_args.Add((Object)(_idseleccionado));
 };
 //BA.debugLineNum = 300;BA.debugLine="Dim param(args.Size) As String";
_param = new String[_args.getSize()];
java.util.Arrays.fill(_param,"");
 //BA.debugLineNum = 301;BA.debugLine="For i = 0 To args.Size -1";
{
final int step43 = 1;
final int limit43 = (int) (_args.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit43 ;_i = _i + step43 ) {
 //BA.debugLineNum = 302;BA.debugLine="param(i) = args.Get(i)";
_param[_i] = BA.ObjectToString(_args.Get(_i));
 }
};
 //BA.debugLineNum = 304;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(query,param)";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2(_query,_param)));
 //BA.debugLineNum = 308;BA.debugLine="If Mi_Cursor.RowCount = 0 Then";
if (mostCurrent._mi_cursor.getRowCount()==0) { 
 //BA.debugLineNum = 309;BA.debugLine="ListViewResumen.AddSingleLine(\"Sin resultados e";
mostCurrent._listviewresumen.AddSingleLine(BA.ObjectToCharSequence("Sin resultados en ese rango"));
 }else {
 //BA.debugLineNum = 311;BA.debugLine="ListViewResumen.AddSingleLine(\"RUT| PATENTE | f";
mostCurrent._listviewresumen.AddSingleLine(BA.ObjectToCharSequence("RUT| PATENTE | fecha Salida  | Monto "));
 //BA.debugLineNum = 312;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step51 = 1;
final int limit51 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit51 ;_i = _i + step51 ) {
 //BA.debugLineNum = 313;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 314;BA.debugLine="Dim monto As Int = Mi_Cursor.GetInt(\"monto\")";
_monto = mostCurrent._mi_cursor.GetInt("monto");
 //BA.debugLineNum = 315;BA.debugLine="totalRecaudado = totalRecaudado + monto";
_totalrecaudado = (int) (_totalrecaudado+_monto);
 //BA.debugLineNum = 316;BA.debugLine="Dim linea As String =   Mi_Cursor.GetString(\"r";
_linea = mostCurrent._mi_cursor.GetString("rut")+" | "+mostCurrent._mi_cursor.GetString("patente")+" | "+mostCurrent._mi_cursor.GetString("salida_fecha")+" | "+BA.NumberToString(_monto);
 //BA.debugLineNum = 320;BA.debugLine="ListViewResumen.AddSingleLine(linea)";
mostCurrent._listviewresumen.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 //BA.debugLineNum = 323;BA.debugLine="LabelTotal.Text= \" Total recaudado: \" & totalRe";
mostCurrent._labeltotal.setText(BA.ObjectToCharSequence(" Total recaudado: "+BA.NumberToString(_totalrecaudado)));
 };
 //BA.debugLineNum = 325;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 327;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("0589898",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 //BA.debugLineNum = 328;BA.debugLine="ToastMessageShow(\"Error al consultar\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al consultar"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 330;BA.debugLine="End Sub";
return "";
}
public static String  _btningreso_click() throws Exception{
long _dtentrada = 0L;
int _i = 0;
String _linea = "";
 //BA.debugLineNum = 88;BA.debugLine="Private Sub BtnIngreso_Click";
 //BA.debugLineNum = 89;BA.debugLine="If Not(ValidaSesion) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6())) { 
 //BA.debugLineNum = 90;BA.debugLine="EditTextPatente.Text = \"\"";
mostCurrent._edittextpatente.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 91;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 94;BA.debugLine="If EditTextPatente.Text<>\"\" Then";
if ((mostCurrent._edittextpatente.getText()).equals("") == false) { 
 //BA.debugLineNum = 96;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 97;BA.debugLine="Try";
try { //BA.debugLineNum = 98;BA.debugLine="Dim dtEntrada As Long = DateTime.Now";
_dtentrada = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 99;BA.debugLine="DateTime.DateFormat = \"dd/MM/yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd/MM/yyyy");
 //BA.debugLineNum = 100;BA.debugLine="DateTime.TimeFormat = \"HH:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("HH:mm:ss");
 //BA.debugLineNum = 101;BA.debugLine="Log(\"Fecha: \" & DateTime.Date(dtEntrada))";
anywheresoftware.b4a.keywords.Common.LogImpl("0327693","Fecha: "+anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtentrada),0);
 //BA.debugLineNum = 102;BA.debugLine="Log(\"Hora: \" & DateTime.Time(dtEntrada))";
anywheresoftware.b4a.keywords.Common.LogImpl("0327694","Hora: "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtentrada),0);
 //BA.debugLineNum = 104;BA.debugLine="Log(\"dtEntrada al INGRESO \"&dtEntrada)";
anywheresoftware.b4a.keywords.Common.LogImpl("0327696","dtEntrada al INGRESO "+BA.NumberToString(_dtentrada),0);
 //BA.debugLineNum = 105;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Vehiculos (fec";
_mi_db.ExecNonQuery2("INSERT INTO Vehiculos (fecha, hora, patente, isActive, id_usuario) VALUES (?,?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(anywheresoftware.b4a.keywords.Common.DateTime.Date(_dtentrada)),(Object)(anywheresoftware.b4a.keywords.Common.DateTime.Time(_dtentrada)),(Object)(mostCurrent._edittextpatente.getText().toUpperCase()),(Object)(1),(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7)}));
 //BA.debugLineNum = 106;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 107;BA.debugLine="ToastMessageShow(\"Vehiculo ingresado\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Vehiculo ingresado"),anywheresoftware.b4a.keywords.Common.False);
 } 
       catch (Exception e18) {
			processBA.setLastException(e18); //BA.debugLineNum = 109;BA.debugLine="Log(\"catch: \" & LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("0327701","catch: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 110;BA.debugLine="ToastMessageShow(\"Error al ingresar\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al ingresar"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 112;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 113;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT id_dato, fec";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT id_dato, fecha, hora, patente FROM Vehiculos WHERE isActive=1 ORDER BY fecha DESC")));
 //BA.debugLineNum = 114;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 116;BA.debugLine="EditTextPatente.Text=\"\"";
mostCurrent._edittextpatente.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 117;BA.debugLine="ListView1.AddSingleLine(\"FECHA        | HORA";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("FECHA        | HORA    | PATENTE"));
 //BA.debugLineNum = 118;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step26 = 1;
final int limit26 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit26 ;_i = _i + step26 ) {
 //BA.debugLineNum = 119;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 120;BA.debugLine="Dim linea As String = Mi_Cursor.GetString(\"fech";
_linea = mostCurrent._mi_cursor.GetString("fecha")+"|  "+mostCurrent._mi_cursor.GetString("hora")+"     | "+mostCurrent._mi_cursor.GetString("patente");
 //BA.debugLineNum = 121;BA.debugLine="ListView1.AddSingleLine(linea)";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 //BA.debugLineNum = 123;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 }else {
 //BA.debugLineNum = 125;BA.debugLine="ToastMessageShow(\"Completar todos los datos\", Tr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Completar todos los datos"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _btnlistar_click() throws Exception{
int _i = 0;
String _linea = "";
 //BA.debugLineNum = 129;BA.debugLine="Private Sub BtnListar_Click";
 //BA.debugLineNum = 130;BA.debugLine="ListView2.Clear";
mostCurrent._listview2.Clear();
 //BA.debugLineNum = 131;BA.debugLine="Try";
try { //BA.debugLineNum = 132;BA.debugLine="If CheckBoxActivos.Checked Then";
if (mostCurrent._checkboxactivos.getChecked()) { 
 //BA.debugLineNum = 133;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT id_dato, fe";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT id_dato, fecha, hora, patente, isActive FROM Vehiculos WHERE isActive = 1 ORDER BY id_dato DESC")));
 //BA.debugLineNum = 134;BA.debugLine="ListView2.AddSingleLine(\"FECHA        | HORA";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence("FECHA        | HORA    | PATENTE | Estado"));
 //BA.debugLineNum = 135;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step6 = 1;
final int limit6 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 136;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 137;BA.debugLine="Dim linea As String = Mi_Cursor.GetString(\"fec";
_linea = mostCurrent._mi_cursor.GetString("fecha")+"|  "+mostCurrent._mi_cursor.GetString("hora")+"     | "+mostCurrent._mi_cursor.GetString("patente")+" |   "+mostCurrent._mi_cursor.GetString("isActive");
 //BA.debugLineNum = 138;BA.debugLine="ListView2.AddSingleLine(linea)";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 }else {
 //BA.debugLineNum = 142;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT id_dato, fe";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT id_dato, fecha, hora, patente, isActive FROM Vehiculos ORDER BY id_dato DESC")));
 //BA.debugLineNum = 143;BA.debugLine="ListView2.AddSingleLine(\"FECHA        | HORA";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence("FECHA        | HORA    | PATENTE | Estado"));
 //BA.debugLineNum = 144;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step14 = 1;
final int limit14 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit14 ;_i = _i + step14 ) {
 //BA.debugLineNum = 145;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 146;BA.debugLine="Dim linea As String = Mi_Cursor.GetString(\"fec";
_linea = mostCurrent._mi_cursor.GetString("fecha")+"|  "+mostCurrent._mi_cursor.GetString("hora")+"     | "+mostCurrent._mi_cursor.GetString("patente")+" |   "+BA.NumberToString(mostCurrent._mi_cursor.GetInt("isActive"));
 //BA.debugLineNum = 147;BA.debugLine="ListView2.AddSingleLine(linea)";
mostCurrent._listview2.AddSingleLine(BA.ObjectToCharSequence(_linea));
 }
};
 };
 //BA.debugLineNum = 150;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 } 
       catch (Exception e22) {
			processBA.setLastException(e22); //BA.debugLineNum = 154;BA.debugLine="ToastMessageShow(\"Error al cargar vehiculos\", Tr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al cargar vehiculos"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _buttoniniciarsesion_click() throws Exception{
String _rut = "";
String _clave = "";
String _nombre = "";
 //BA.debugLineNum = 392;BA.debugLine="Private Sub ButtonIniciarSesion_Click";
 //BA.debugLineNum = 393;BA.debugLine="Dim rut As String = EditTextRut.Text.Trim";
_rut = mostCurrent._edittextrut.getText().trim();
 //BA.debugLineNum = 394;BA.debugLine="Dim clave As String = EditTextClave.Text.Trim";
_clave = mostCurrent._edittextclave.getText().trim();
 //BA.debugLineNum = 396;BA.debugLine="If rut = \"\" Or clave = \"\" Then";
if ((_rut).equals("") || (_clave).equals("")) { 
 //BA.debugLineNum = 397;BA.debugLine="ToastMessageShow(\"Ingrese rut y contraseña\", Tru";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese rut y contraseña"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 398;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 401;BA.debugLine="Try";
try { //BA.debugLineNum = 402;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT id_usuario,";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT id_usuario, nombre FROM Usuarios WHERE rut = ? AND clave = ?",new String[]{_rut,_clave})));
 //BA.debugLineNum = 404;BA.debugLine="If Mi_Cursor.RowCount > 0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 405;BA.debugLine="ControlesDeSesion(False)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 406;BA.debugLine="Mi_Cursor.Position = 0";
mostCurrent._mi_cursor.setPosition((int) (0));
 //BA.debugLineNum = 407;BA.debugLine="UsuarioActivoID = Mi_Cursor.GetInt(\"id_usuario\"";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = mostCurrent._mi_cursor.GetInt("id_usuario");
 //BA.debugLineNum = 408;BA.debugLine="Dim nombre As String = Mi_Cursor.GetString(\"nom";
_nombre = mostCurrent._mi_cursor.GetString("nombre");
 //BA.debugLineNum = 409;BA.debugLine="ToastMessageShow(\"Bienvenido \" & nombre, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Bienvenido "+_nombre),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 410;BA.debugLine="UsuarioActivoNombre = nombre";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = _nombre;
 //BA.debugLineNum = 412;BA.debugLine="TabHost1.CurrentTab = 1";
mostCurrent._tabhost1.setCurrentTab((int) (1));
 //BA.debugLineNum = 413;BA.debugLine="MostrarSesion";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 414;BA.debugLine="EditTextRut.Text = \"\"";
mostCurrent._edittextrut.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 415;BA.debugLine="EditTextClave.Text = \"\"";
mostCurrent._edittextclave.setText(BA.ObjectToCharSequence(""));
 }else {
 //BA.debugLineNum = 417;BA.debugLine="ToastMessageShow(\"Credenciales incorrectas\", Tr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Credenciales incorrectas"),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 419;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 } 
       catch (Exception e25) {
			processBA.setLastException(e25); //BA.debugLineNum = 421;BA.debugLine="ToastMessageShow(\"ERRORRRR\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("ERRORRRR"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 422;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("0917534",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 424;BA.debugLine="End Sub";
return "";
}
public static String  _buttonlogout_click() throws Exception{
 //BA.debugLineNum = 478;BA.debugLine="Private Sub ButtonLogout_Click";
 //BA.debugLineNum = 479;BA.debugLine="UsuarioActivoID = -1";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = (int) (-1);
 //BA.debugLineNum = 480;BA.debugLine="UsuarioActivoNombre = \"\"";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = "";
 //BA.debugLineNum = 481;BA.debugLine="MostrarSesion";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 482;BA.debugLine="TabHost1.CurrentTab = 0";
mostCurrent._tabhost1.setCurrentTab((int) (0));
 //BA.debugLineNum = 483;BA.debugLine="ControlesDeSesion(True)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 484;BA.debugLine="End Sub";
return "";
}
public static String  _buttonregistrar_click() throws Exception{
String _nombre = "";
String _rut = "";
String _clave = "";
 //BA.debugLineNum = 356;BA.debugLine="Private Sub ButtonRegistrar_Click";
 //BA.debugLineNum = 357;BA.debugLine="Dim nombre As String = EditTextNombre.Text.Trim";
_nombre = mostCurrent._edittextnombre.getText().trim();
 //BA.debugLineNum = 358;BA.debugLine="Dim rut As String = EditTextRut.Text.Trim";
_rut = mostCurrent._edittextrut.getText().trim();
 //BA.debugLineNum = 359;BA.debugLine="Dim clave As String = EditTextClave.Text.Trim";
_clave = mostCurrent._edittextclave.getText().trim();
 //BA.debugLineNum = 361;BA.debugLine="If nombre = \"\" Or clave = \"\" Or rut = \"\" Then";
if ((_nombre).equals("") || (_clave).equals("") || (_rut).equals("")) { 
 //BA.debugLineNum = 362;BA.debugLine="ToastMessageShow(\"Ingresar nombre, Rut y clave\",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingresar nombre, Rut y clave"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 363;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 366;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery2(\"SELECT 1 FROM Usuar";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery2("SELECT 1 FROM Usuarios WHERE rut = ? LIMIT 1",new String[]{_rut})));
 //BA.debugLineNum = 367;BA.debugLine="If Mi_Cursor.RowCount > 0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 368;BA.debugLine="ToastMessageShow(\"Usuario ya registrado\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Usuario ya registrado"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 369;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 //BA.debugLineNum = 370;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 372;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 //BA.debugLineNum = 374;BA.debugLine="Try";
try { //BA.debugLineNum = 375;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Usuarios (nombr";
_mi_db.ExecNonQuery2("INSERT INTO Usuarios (nombre, rut, clave) VALUES (?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{_nombre,_rut,_clave}));
 //BA.debugLineNum = 376;BA.debugLine="ToastMessageShow(\"Usuario registrado\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Usuario registrado"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 377;BA.debugLine="CompletaSpinnerUsuarios";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4();
 //BA.debugLineNum = 378;BA.debugLine="EditTextRut.Text = \"\"";
mostCurrent._edittextrut.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 379;BA.debugLine="EditTextClave.Text = \"\"";
mostCurrent._edittextclave.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 380;BA.debugLine="EditTextNombre.Text = \"\"";
mostCurrent._edittextnombre.setText(BA.ObjectToCharSequence(""));
 } 
       catch (Exception e23) {
			processBA.setLastException(e23); //BA.debugLineNum = 382;BA.debugLine="ToastMessageShow(\"ERROR: \"& LastException.Messag";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("ERROR: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 383;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("0851995",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 388;BA.debugLine="End Sub";
return "";
}
public static String  _calendarfin_click() throws Exception{
 //BA.debugLineNum = 338;BA.debugLine="Private Sub calendarFin_Click";
 //BA.debugLineNum = 339;BA.debugLine="fechaSeleccionada = \"fin\"";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "fin";
 //BA.debugLineNum = 340;BA.debugLine="MostrarCalendario";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3();
 //BA.debugLineNum = 341;BA.debugLine="End Sub";
return "";
}
public static String  _calendarini_click() throws Exception{
 //BA.debugLineNum = 333;BA.debugLine="Private Sub calendarIni_Click";
 //BA.debugLineNum = 334;BA.debugLine="fechaSeleccionada = \"ini\"";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "ini";
 //BA.debugLineNum = 335;BA.debugLine="MostrarCalendario";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3();
 //BA.debugLineNum = 336;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxdate_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 243;BA.debugLine="Private Sub CheckBoxDate_CheckedChange(Checked As";
 //BA.debugLineNum = 244;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 245;BA.debugLine="EditTextDateFin.Visible = False";
mostCurrent._edittextdatefin.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 246;BA.debugLine="calendarFin.Visible = False";
mostCurrent._calendarfin.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 248;BA.debugLine="EditTextDateFin.Visible = True";
mostCurrent._edittextdatefin.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 249;BA.debugLine="calendarFin.Visible = True";
mostCurrent._calendarfin.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 251;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxregistro_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 439;BA.debugLine="Private Sub CheckBoxRegistro_CheckedChange(Checked";
 //BA.debugLineNum = 440;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 441;BA.debugLine="CheckboxSesion.Checked = False";
mostCurrent._checkboxsesion.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 442;BA.debugLine="EditTextNombre.Visible = True";
mostCurrent._edittextnombre.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 443;BA.debugLine="EditTextRut.Visible = True";
mostCurrent._edittextrut.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 444;BA.debugLine="EditTextClave.Visible = True";
mostCurrent._edittextclave.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 445;BA.debugLine="ButtonRegistrar.Visible = True";
mostCurrent._buttonregistrar.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 446;BA.debugLine="ButtonIniciarSesion.Visible = False";
mostCurrent._buttoniniciarsesion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 448;BA.debugLine="VerifyCheckboxes";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1();
 };
 //BA.debugLineNum = 450;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxsesion_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 426;BA.debugLine="Private Sub CheckBoxSesion_CheckedChange(Checked A";
 //BA.debugLineNum = 427;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 428;BA.debugLine="CheckboxRegistro.Checked = False";
mostCurrent._checkboxregistro.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 429;BA.debugLine="EditTextNombre.Visible = False";
mostCurrent._edittextnombre.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 430;BA.debugLine="EditTextRut.Visible = True";
mostCurrent._edittextrut.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 431;BA.debugLine="EditTextClave.Visible = True";
mostCurrent._edittextclave.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 432;BA.debugLine="ButtonRegistrar.Visible = False";
mostCurrent._buttonregistrar.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 433;BA.debugLine="ButtonIniciarSesion.Visible = True";
mostCurrent._buttoniniciarsesion.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 435;BA.debugLine="VerifyCheckboxes";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1();
 };
 //BA.debugLineNum = 437;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4() throws Exception{
int _i = 0;
 //BA.debugLineNum = 506;BA.debugLine="Private Sub CompletaSpinnerUsuarios";
 //BA.debugLineNum = 507;BA.debugLine="SpinnerUsuarios.Clear";
mostCurrent._spinnerusuarios.Clear();
 //BA.debugLineNum = 508;BA.debugLine="SpinnerUsuarios.Add(\"Todos\")";
mostCurrent._spinnerusuarios.Add("Todos");
 //BA.debugLineNum = 509;BA.debugLine="Mi_Cursor = Mi_DB.ExecQuery(\"SELECT rut FROM Usua";
mostCurrent._mi_cursor = (anywheresoftware.b4a.sql.SQL.CursorWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.sql.SQL.CursorWrapper(), (android.database.Cursor)(_mi_db.ExecQuery("SELECT rut FROM Usuarios ORDER BY rut")));
 //BA.debugLineNum = 510;BA.debugLine="For i = 0 To Mi_Cursor.RowCount - 1";
{
final int step4 = 1;
final int limit4 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit4 ;_i = _i + step4 ) {
 //BA.debugLineNum = 511;BA.debugLine="Mi_Cursor.Position = i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 512;BA.debugLine="SpinnerUsuarios.Add(Mi_Cursor.GetString(\"rut\"))";
mostCurrent._spinnerusuarios.Add(mostCurrent._mi_cursor.GetString("rut"));
 }
};
 //BA.debugLineNum = 514;BA.debugLine="Mi_Cursor.Close";
mostCurrent._mi_cursor.Close();
 //BA.debugLineNum = 515;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0(boolean _visible) throws Exception{
 //BA.debugLineNum = 486;BA.debugLine="Private Sub ControlesDeSesion(visible As Boolean)";
 //BA.debugLineNum = 487;BA.debugLine="ButtonIniciarSesion.Visible = visible";
mostCurrent._buttoniniciarsesion.setVisible(_visible);
 //BA.debugLineNum = 488;BA.debugLine="ButtonRegistrar.Visible = visible";
mostCurrent._buttonregistrar.setVisible(_visible);
 //BA.debugLineNum = 489;BA.debugLine="CheckboxSesion.Visible = visible";
mostCurrent._checkboxsesion.setVisible(_visible);
 //BA.debugLineNum = 490;BA.debugLine="CheckboxRegistro.Visible = visible";
mostCurrent._checkboxregistro.setVisible(_visible);
 //BA.debugLineNum = 491;BA.debugLine="EditTextRut.Visible = visible";
mostCurrent._edittextrut.setVisible(_visible);
 //BA.debugLineNum = 492;BA.debugLine="EditTextClave.Visible = visible";
mostCurrent._edittextclave.setVisible(_visible);
 //BA.debugLineNum = 493;BA.debugLine="EditTextNombre.Visible = visible";
mostCurrent._edittextnombre.setVisible(_visible);
 //BA.debugLineNum = 494;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim Mi_Cursor As Cursor";
mostCurrent._mi_cursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private SpinnerUsuarios As Spinner";
mostCurrent._spinnerusuarios = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private EditTextPatente, EditTextTicket, EditText";
mostCurrent._edittextpatente = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittextticket = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittextdateini = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittextdatefin = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private EditTextNombre, EditTextClave, EditTextRu";
mostCurrent._edittextnombre = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittextclave = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittextrut = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private ButtonRegistrar, ButtonIniciarSesion As B";
mostCurrent._buttonregistrar = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._buttoniniciarsesion = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private BtnIngreso, BtnListar, BtnGeneraTicket, B";
mostCurrent._btningreso = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnlistar = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btngeneraticket = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btninforme = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._calendarini = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._calendarfin = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._buttonlogout = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private Label1, InfoTicket, LabelActivos, LabelTo";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._infoticket = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelactivos = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labeltotal = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelpordia = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelusuarioactivo = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private LabelSesion, LabelRegistro As Label";
mostCurrent._labelsesion = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelregistro = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private ListView1, ListView2, ListViewResumen As";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
mostCurrent._listview2 = new anywheresoftware.b4a.objects.ListViewWrapper();
mostCurrent._listviewresumen = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private CheckBoxActivos, CheckboxDate, CheckboxSe";
mostCurrent._checkboxactivos = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
mostCurrent._checkboxdate = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
mostCurrent._checkboxsesion = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
mostCurrent._checkboxregistro = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private UsuarioActivoID As Int = -1";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = (int) (-1);
 //BA.debugLineNum = 36;BA.debugLine="Private UsuarioActivoNombre As String = \"\"";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = "";
 //BA.debugLineNum = 37;BA.debugLine="Private dialog As B4XDialog";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = new com.parquimetroV2.b4xdialog();
 //BA.debugLineNum = 38;BA.debugLine="Private dateTemplate As B4XDateTemplate";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3 = new com.parquimetroV2.b4xdatetemplate();
 //BA.debugLineNum = 39;BA.debugLine="Private fechaSeleccionada As String";
mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "";
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static void  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3() throws Exception{
ResumableSub_MostrarCalendario rsub = new ResumableSub_MostrarCalendario(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_MostrarCalendario extends BA.ResumableSub {
public ResumableSub_MostrarCalendario(com.parquimetroV2.main parent) {
this.parent = parent;
}
com.parquimetroV2.main parent;
int _result = 0;
String _fechaelegida = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 344;BA.debugLine="Wait For (dialog.ShowTemplate(dateTemplate, \"OK\",";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, parent.mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2._vvvvvvvvvvvvvvvvvvvvv5 /*anywheresoftware.b4a.keywords.Common.ResumableSubWrapper*/ ((Object)(parent.mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3),(Object)("OK"),(Object)(""),(Object)("Cancelar")));
this.state = 11;
return;
case 11:
//C
this.state = 1;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 345;BA.debugLine="If Result = xui.DialogResponse_Positive Then";
if (true) break;

case 1:
//if
this.state = 10;
if (_result==parent._vvv5.DialogResponse_Positive) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 346;BA.debugLine="DateTime.DateFormat = \"dd/MM/yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd/MM/yyyy");
 //BA.debugLineNum = 347;BA.debugLine="Dim fechaElegida As String = DateTime.Date(dateT";
_fechaelegida = anywheresoftware.b4a.keywords.Common.DateTime.Date(parent.mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._getvvvvvvvvvvvvvvvv0 /*long*/ ());
 //BA.debugLineNum = 348;BA.debugLine="If fechaSeleccionada = \"ini\" Then";
if (true) break;

case 4:
//if
this.state = 9;
if ((parent.mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2).equals("ini")) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 349;BA.debugLine="EditTextDateIni.Text = fechaElegida";
parent.mostCurrent._edittextdateini.setText(BA.ObjectToCharSequence(_fechaelegida));
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 351;BA.debugLine="EditTextDateFin.Text = fechaElegida";
parent.mostCurrent._edittextdatefin.setText(BA.ObjectToCharSequence(_fechaelegida));
 if (true) break;

case 9:
//C
this.state = 10;
;
 if (true) break;

case 10:
//C
this.state = -1;
;
 //BA.debugLineNum = 354;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _complete(int _result) throws Exception{
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 462;BA.debugLine="Private Sub MostrarSesion";
 //BA.debugLineNum = 463;BA.debugLine="If UsuarioActivoID <> -1 Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7!=-1) { 
 //BA.debugLineNum = 464;BA.debugLine="LabelUsuarioActivo.Text = \"Usuario: \" & UsuarioA";
mostCurrent._labelusuarioactivo.setText(BA.ObjectToCharSequence("Usuario: "+mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1));
 //BA.debugLineNum = 465;BA.debugLine="Log(UsuarioActivoNombre)";
anywheresoftware.b4a.keywords.Common.LogImpl("01179651",mostCurrent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1,0);
 //BA.debugLineNum = 466;BA.debugLine="Log(UsuarioActivoID)";
anywheresoftware.b4a.keywords.Common.LogImpl("01179652",BA.NumberToString(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7),0);
 //BA.debugLineNum = 467;BA.debugLine="LabelUsuarioActivo.Visible = True";
mostCurrent._labelusuarioactivo.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 468;BA.debugLine="ButtonLogout.Visible = True";
mostCurrent._buttonlogout.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 471;BA.debugLine="LabelUsuarioActivo.Text = \"Sin sesión activa\"";
mostCurrent._labelusuarioactivo.setText(BA.ObjectToCharSequence("Sin sesión activa"));
 //BA.debugLineNum = 472;BA.debugLine="LabelUsuarioActivo.Visible = True";
mostCurrent._labelusuarioactivo.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 473;BA.debugLine="ButtonLogout.Visible = False";
mostCurrent._buttonlogout.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 476;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
starter._process_globals();
xuiviewsutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

private static byte[][] bb;

public static String vvv13(final byte[] _b, final int i) throws Exception {
Runnable r = new Runnable() {
{

int value = i / 3 + 535960;
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
 //BA.debugLineNum = 18;BA.debugLine="Private xui As XUI";
_vvv5 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 19;BA.debugLine="Dim Mi_DB As SQL";
_mi_db = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6() throws Exception{
 //BA.debugLineNum = 496;BA.debugLine="Private Sub ValidaSesion As Boolean";
 //BA.debugLineNum = 497;BA.debugLine="If UsuarioActivoID = -1 Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7==-1) { 
 //BA.debugLineNum = 498;BA.debugLine="ToastMessageShow(\"Debe iniciar sesión\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Debe iniciar sesión"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 499;BA.debugLine="TabHost1.CurrentTab = 0";
mostCurrent._tabhost1.setCurrentTab((int) (0));
 //BA.debugLineNum = 500;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 502;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 504;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1() throws Exception{
 //BA.debugLineNum = 452;BA.debugLine="Private Sub VerifyCheckboxes()";
 //BA.debugLineNum = 453;BA.debugLine="If CheckboxSesion.Checked = False And CheckboxReg";
if (mostCurrent._checkboxsesion.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._checkboxregistro.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 454;BA.debugLine="EditTextNombre.Visible = False";
mostCurrent._edittextnombre.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 455;BA.debugLine="EditTextRut.Visible = False";
mostCurrent._edittextrut.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 456;BA.debugLine="EditTextClave.Visible = False";
mostCurrent._edittextclave.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 457;BA.debugLine="ButtonRegistrar.Visible = False";
mostCurrent._buttonregistrar.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 458;BA.debugLine="ButtonIniciarSesion.Visible = False";
mostCurrent._buttoniniciarsesion.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 460;BA.debugLine="End Sub";
return "";
}
}
