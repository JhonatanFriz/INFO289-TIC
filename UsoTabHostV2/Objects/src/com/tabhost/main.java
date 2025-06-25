package com.tabhost;


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
			processBA = new BA(this.getApplicationContext(), null, null, "com.tabhost", "com.tabhost.main");
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
		activityBA = new BA(this, layout, processBA, "com.tabhost", "com.tabhost.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.tabhost.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtanimal = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtcolor = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1sf = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imagepaisaje = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _v6 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listviewpaisaje = null;
public anywheresoftware.b4a.objects.EditTextWrapper _vv4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnpromedio = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner2 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner3 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerpaes = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkbox2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext2sf = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext3sf = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtponderado1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtponderado2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtponderado3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _paes1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _paes2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _paes3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _paes4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _paes5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2sf = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3sf = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelpromedio = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelpaisaje = null;
public anywheresoftware.b4a.objects.LabelWrapper _prompaeslabel = null;
public static double _v0 = 0;
public static double _vv1 = 0;
public static double _vv2 = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _btn1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsf = null;
public anywheresoftware.b4a.objects.ButtonWrapper _calcprom = null;
public static int _v7 = 0;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp1 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp2 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp3 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp4 = null;
int _k = 0;
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="Dim bmp1, bmp2,bmp3,bmp4 As Bitmap";
_bmp1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp3 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim k As Int";
_k = 0;
 //BA.debugLineNum = 39;BA.debugLine="bmp1 = LoadBitmap(File.DirAssets, \"ico1.png\")";
_bmp1 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico1.png");
 //BA.debugLineNum = 40;BA.debugLine="bmp2 = LoadBitmap(File.DirAssets, \"ico2.png\")";
_bmp2 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico2.png");
 //BA.debugLineNum = 41;BA.debugLine="bmp3 = LoadBitmap(File.DirAssets, \"ico3.png\")";
_bmp3 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico3.png");
 //BA.debugLineNum = 42;BA.debugLine="bmp4 = LoadBitmap(File.DirAssets, \"transparente.p";
_bmp4 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"transparente.png");
 //BA.debugLineNum = 47;BA.debugLine="TabHost1.AddTab(\"Calcula Prom\", \"pantaPromedio\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Calcula Prom","pantaPromedio");
 //BA.debugLineNum = 48;BA.debugLine="TabHost1.AddTab(\"IMC\", \"pantaCalculadora\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"IMC","pantaCalculadora");
 //BA.debugLineNum = 49;BA.debugLine="TabHost1.AddTab(\"IMCSinFiltro\", \"pantaIMCSinFiltr";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"IMCSinFiltro","pantaIMCSinFiltro");
 //BA.debugLineNum = 50;BA.debugLine="TabHost1.AddTab(\"Paisajes\", \"paisajesSur\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"Paisajes","paisajesSur");
 //BA.debugLineNum = 51;BA.debugLine="TabHost1.AddTab(\"PAES\", \"promPaes\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"PAES","promPaes");
 //BA.debugLineNum = 52;BA.debugLine="Spinner1.Add(\"20\")";
mostCurrent._spinner1.Add("20");
 //BA.debugLineNum = 53;BA.debugLine="Spinner1.Add(\"30\")";
mostCurrent._spinner1.Add("30");
 //BA.debugLineNum = 54;BA.debugLine="Spinner1.Add(\"50\")";
mostCurrent._spinner1.Add("50");
 //BA.debugLineNum = 55;BA.debugLine="Spinner2.Add(\"20\")";
mostCurrent._spinner2.Add("20");
 //BA.debugLineNum = 56;BA.debugLine="Spinner2.Add(\"30\")";
mostCurrent._spinner2.Add("30");
 //BA.debugLineNum = 57;BA.debugLine="Spinner2.Add(\"50\")";
mostCurrent._spinner2.Add("50");
 //BA.debugLineNum = 58;BA.debugLine="Spinner3.Add(\"20\")";
mostCurrent._spinner3.Add("20");
 //BA.debugLineNum = 59;BA.debugLine="Spinner3.Add(\"30\")";
mostCurrent._spinner3.Add("30");
 //BA.debugLineNum = 60;BA.debugLine="Spinner3.Add(\"50\")";
mostCurrent._spinner3.Add("50");
 //BA.debugLineNum = 61;BA.debugLine="VerifyCheckboxes";
_v5();
 //BA.debugLineNum = 62;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 63;BA.debugLine="mp.Initialize";
mostCurrent._v6.Initialize();
 };
 //BA.debugLineNum = 65;BA.debugLine="k=1";
_k = (int) (1);
 //BA.debugLineNum = 66;BA.debugLine="Do While k<= 6";
while (_k<=6) {
 //BA.debugLineNum = 67;BA.debugLine="listViewPaisaje.AddSingleLine(\"Ciudad \" & k)";
mostCurrent._listviewpaisaje.AddSingleLine(BA.ObjectToCharSequence("Ciudad "+BA.NumberToString(_k)));
 //BA.debugLineNum = 68;BA.debugLine="k=k+1";
_k = (int) (_k+1);
 }
;
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _finishing) throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub Activity_Pause (Finishing As Boolean)";
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _btn1_click() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Private Sub btn1_Click";
 //BA.debugLineNum = 158;BA.debugLine="Dim p As Int";
_v7 = 0;
 //BA.debugLineNum = 159;BA.debugLine="If IsNumber(edittext2.Text) = False Or IsNumber(e";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._edittext2.getText())==anywheresoftware.b4a.keywords.Common.False || anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._edittext3.getText())==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 160;BA.debugLine="ToastMessageShow(\"Debes ingresar números válidos";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Debes ingresar números válidos"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 161;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 164;BA.debugLine="kg = edittext2.Text";
_v0 = (double)(Double.parseDouble(mostCurrent._edittext2.getText()));
 //BA.debugLineNum = 165;BA.debugLine="altura = edittext3.Text";
_vv1 = (double)(Double.parseDouble(mostCurrent._edittext3.getText()));
 //BA.debugLineNum = 167;BA.debugLine="If altura <= 0 Then";
if (_vv1<=0) { 
 //BA.debugLineNum = 168;BA.debugLine="ToastMessageShow(\"La altura debe ser mayor que 0";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("La altura debe ser mayor que 0"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 169;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 172;BA.debugLine="imc = kg / (altura * altura)";
_vv2 = _v0/(double)(_vv1*_vv1);
 //BA.debugLineNum = 174;BA.debugLine="If imc < 18.5 Then";
if (_vv2<18.5) { 
 //BA.debugLineNum = 175;BA.debugLine="p = 0";
_v7 = (int) (0);
 }else if(_vv2<=24.9) { 
 //BA.debugLineNum = 177;BA.debugLine="p = 1";
_v7 = (int) (1);
 }else if(_vv2<=29.9) { 
 //BA.debugLineNum = 179;BA.debugLine="p = 2";
_v7 = (int) (2);
 }else {
 //BA.debugLineNum = 181;BA.debugLine="p = 3";
_v7 = (int) (3);
 };
 //BA.debugLineNum = 183;BA.debugLine="label2.Text = \"IMC: \" & NumberFormat(imc, 1, 2)";
mostCurrent._label2.setText(BA.ObjectToCharSequence("IMC: "+anywheresoftware.b4a.keywords.Common.NumberFormat(_vv2,(int) (1),(int) (2))));
 //BA.debugLineNum = 184;BA.debugLine="Select p";
switch (_v7) {
case 0: {
 //BA.debugLineNum = 186;BA.debugLine="imageview1.Bitmap = LoadBitmap(File.DirAssets,";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flaco.jpg").getObject()));
 break; }
case 1: {
 //BA.debugLineNum = 188;BA.debugLine="imageview1.Bitmap = LoadBitmap(File.DirAssets,";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bien.jpg").getObject()));
 break; }
case 2: {
 //BA.debugLineNum = 190;BA.debugLine="imageview1.Bitmap = LoadBitmap(File.DirAssets,";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gordo.jpg").getObject()));
 break; }
case 3: {
 //BA.debugLineNum = 192;BA.debugLine="imageview1.Bitmap = LoadBitmap(File.DirAssets,";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"obeso.png").getObject()));
 break; }
}
;
 //BA.debugLineNum = 194;BA.debugLine="End Sub";
return "";
}
public static String  _btndone_click() throws Exception{
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
 //BA.debugLineNum = 199;BA.debugLine="Sub btnDone_Click";
 //BA.debugLineNum = 200;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 201;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 202;BA.debugLine="sb.Append(\"Usted a ingresado:\").Append(CRLF)";
_sb.Append("Usted a ingresado:").Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 203;BA.debugLine="sb.Append(\"Nombre \").Append(txtName.Text).Append(";
_sb.Append("Nombre ").Append(mostCurrent._txtname.getText()).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 204;BA.debugLine="sb.Append(\"Color: \").Append(txtColor.Text).Append";
_sb.Append("Color: ").Append(mostCurrent._txtcolor.getText()).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 205;BA.debugLine="sb.Append(\"Animal: \").Append(txtAnimal.Text)";
_sb.Append("Animal: ").Append(mostCurrent._txtanimal.getText());
 //BA.debugLineNum = 206;BA.debugLine="Msgbox(sb.ToString, \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_sb.ToString()),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 207;BA.debugLine="imageview1.Bitmap =LoadBitmap(File.DirAssets,\"t";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"transparente.png").getObject()));
 //BA.debugLineNum = 208;BA.debugLine="End Sub";
return "";
}
public static String  _btnnext1_click() throws Exception{
 //BA.debugLineNum = 80;BA.debugLine="Sub btnNext1_Click";
 //BA.debugLineNum = 81;BA.debugLine="TabHost1.CurrentTab = 1 'move to next tab";
mostCurrent._tabhost1.setCurrentTab((int) (1));
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _btnnext2_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub btnNext2_Click";
 //BA.debugLineNum = 85;BA.debugLine="TabHost1.CurrentTab = 2 'move to next tab";
mostCurrent._tabhost1.setCurrentTab((int) (2));
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _btnpromedio_click() throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Private Sub btnPromedio_Click";
 //BA.debugLineNum = 138;BA.debugLine="If IsNumber(txtPonderado1.text) And IsNumber(txtP";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._txtponderado1.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._txtponderado2.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._txtponderado3.getText())) { 
 //BA.debugLineNum = 139;BA.debugLine="If (txtPonderado1.text > 0) And (txtPonderado2.t";
if (((double)(Double.parseDouble(mostCurrent._txtponderado1.getText()))>0) && ((double)(Double.parseDouble(mostCurrent._txtponderado2.getText()))>0) && ((double)(Double.parseDouble(mostCurrent._txtponderado3.getText()))>0)) { 
 //BA.debugLineNum = 140;BA.debugLine="If Checkbox1.Checked = True And Checkbox2.Check";
if (mostCurrent._checkbox1.getChecked()==anywheresoftware.b4a.keywords.Common.True && mostCurrent._checkbox2.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 141;BA.debugLine="labelPromedio.text = NumberFormat(((txtPondera";
mostCurrent._labelpromedio.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat((((double)(Double.parseDouble(mostCurrent._txtponderado1.getText()))+(double)(Double.parseDouble(mostCurrent._txtponderado2.getText()))+(double)(Double.parseDouble(mostCurrent._txtponderado3.getText())))/(double)3),(int) (1),(int) (2))));
 }else if(mostCurrent._checkbox2.getChecked()==anywheresoftware.b4a.keywords.Common.True && mostCurrent._checkbox1.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 143;BA.debugLine="If (Spinner1.SelectedItem + Spinner2.SelectedI";
if (((double)(Double.parseDouble(mostCurrent._spinner1.getSelectedItem()))+(double)(Double.parseDouble(mostCurrent._spinner2.getSelectedItem()))+(double)(Double.parseDouble(mostCurrent._spinner3.getSelectedItem())))==100) { 
 //BA.debugLineNum = 144;BA.debugLine="labelPromedio.text = NumberFormat(((txtPonder";
mostCurrent._labelpromedio.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat((((double)(Double.parseDouble(mostCurrent._txtponderado1.getText()))*(double)(Double.parseDouble(mostCurrent._spinner1.getSelectedItem()))+(double)(Double.parseDouble(mostCurrent._txtponderado2.getText()))*(double)(Double.parseDouble(mostCurrent._spinner2.getSelectedItem()))+(double)(Double.parseDouble(mostCurrent._txtponderado3.getText()))*(double)(Double.parseDouble(mostCurrent._spinner3.getSelectedItem())))/(double)100),(int) (1),(int) (2))));
 }else {
 //BA.debugLineNum = 146;BA.debugLine="labelPromedio.Text = \"Suma de % debe ser 100\"";
mostCurrent._labelpromedio.setText(BA.ObjectToCharSequence("Suma de % debe ser 100"));
 };
 };
 };
 }else {
 //BA.debugLineNum = 152;BA.debugLine="labelPromedio.Text = \"Notas deben ser numéricas\"";
mostCurrent._labelpromedio.setText(BA.ObjectToCharSequence("Notas deben ser numéricas"));
 };
 //BA.debugLineNum = 154;BA.debugLine="End Sub";
return "";
}
public static String  _btnsf_click() throws Exception{
int _psf = 0;
 //BA.debugLineNum = 258;BA.debugLine="Private Sub btnsf_Click";
 //BA.debugLineNum = 259;BA.debugLine="Dim psf As Int";
_psf = 0;
 //BA.debugLineNum = 260;BA.debugLine="If IsNumber(edittext2sf.Text) = False Or IsNumber";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._edittext2sf.getText())==anywheresoftware.b4a.keywords.Common.False || anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._edittext3sf.getText())==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 261;BA.debugLine="ToastMessageShow(\"Debes ingresar números válidos";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Debes ingresar números válidos"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 262;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 265;BA.debugLine="kg = edittext2sf.Text";
_v0 = (double)(Double.parseDouble(mostCurrent._edittext2sf.getText()));
 //BA.debugLineNum = 266;BA.debugLine="altura = edittext3sf.Text";
_vv1 = (double)(Double.parseDouble(mostCurrent._edittext3sf.getText()));
 //BA.debugLineNum = 268;BA.debugLine="If altura <= 0 Then";
if (_vv1<=0) { 
 //BA.debugLineNum = 269;BA.debugLine="ToastMessageShow(\"La altura debe ser mayor que 0";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("La altura debe ser mayor que 0"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 270;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 273;BA.debugLine="imc = kg / (altura * altura)";
_vv2 = _v0/(double)(_vv1*_vv1);
 //BA.debugLineNum = 275;BA.debugLine="If imc < 18.5 Then";
if (_vv2<18.5) { 
 //BA.debugLineNum = 276;BA.debugLine="psf = 0";
_psf = (int) (0);
 }else if(_vv2<=24.9) { 
 //BA.debugLineNum = 278;BA.debugLine="psf = 1";
_psf = (int) (1);
 }else if(_vv2<=29.9) { 
 //BA.debugLineNum = 280;BA.debugLine="psf = 2";
_psf = (int) (2);
 }else {
 //BA.debugLineNum = 282;BA.debugLine="psf = 3";
_psf = (int) (3);
 };
 //BA.debugLineNum = 284;BA.debugLine="label2sf.Text = \"IMC: \" & NumberFormat(imc, 1, 2)";
mostCurrent._label2sf.setText(BA.ObjectToCharSequence("IMC: "+anywheresoftware.b4a.keywords.Common.NumberFormat(_vv2,(int) (1),(int) (2))));
 //BA.debugLineNum = 285;BA.debugLine="Select psf";
switch (_psf) {
case 0: {
 //BA.debugLineNum = 287;BA.debugLine="imageview1sf.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imageview1sf.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"skinny.gif").getObject()));
 //BA.debugLineNum = 288;BA.debugLine="label3sf.Text = (\"FALTAN CAZUELAS!!\")";
mostCurrent._label3sf.setText(BA.ObjectToCharSequence(("FALTAN CAZUELAS!!")));
 //BA.debugLineNum = 289;BA.debugLine="mp.Stop";
mostCurrent._v6.Stop();
 //BA.debugLineNum = 290;BA.debugLine="mp.Load(File.DirAssets, \"llegoelpavo.mp3\")";
mostCurrent._v6.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"llegoelpavo.mp3");
 //BA.debugLineNum = 291;BA.debugLine="mp.Play";
mostCurrent._v6.Play();
 break; }
case 1: {
 //BA.debugLineNum = 293;BA.debugLine="imageview1sf.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imageview1sf.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fine.gif").getObject()));
 //BA.debugLineNum = 294;BA.debugLine="label3sf.Text = (\"Peso adecuado, por ahora...\")";
mostCurrent._label3sf.setText(BA.ObjectToCharSequence(("Peso adecuado, por ahora...")));
 //BA.debugLineNum = 295;BA.debugLine="mp.Stop";
mostCurrent._v6.Stop();
 break; }
case 2: {
 //BA.debugLineNum = 297;BA.debugLine="imageview1sf.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imageview1sf.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"yougordo.gif").getObject()));
 //BA.debugLineNum = 298;BA.debugLine="label3sf.Text = (\"Vamos... Estamos gorditos, he";
mostCurrent._label3sf.setText(BA.ObjectToCharSequence(("Vamos... Estamos gorditos, he")));
 //BA.debugLineNum = 299;BA.debugLine="mp.Stop";
mostCurrent._v6.Stop();
 break; }
case 3: {
 //BA.debugLineNum = 301;BA.debugLine="imageview1sf.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imageview1sf.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"obesidad.jpg").getObject()));
 //BA.debugLineNum = 302;BA.debugLine="label3sf.Text = (\"Recomiendo bajar ahora!!!\")";
mostCurrent._label3sf.setText(BA.ObjectToCharSequence(("Recomiendo bajar ahora!!!")));
 //BA.debugLineNum = 303;BA.debugLine="mp.Stop";
mostCurrent._v6.Stop();
 //BA.debugLineNum = 304;BA.debugLine="mp.Load(File.DirAssets, \"oyegelda.mp3\")";
mostCurrent._v6.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"oyegelda.mp3");
 //BA.debugLineNum = 305;BA.debugLine="mp.Play";
mostCurrent._v6.Play();
 break; }
}
;
 //BA.debugLineNum = 307;BA.debugLine="End Sub";
return "";
}
public static String  _calcprom_click() throws Exception{
int _rs = 0;
 //BA.debugLineNum = 309;BA.debugLine="Private Sub calcprom_Click";
 //BA.debugLineNum = 310;BA.debugLine="Dim rs As Int";
_rs = 0;
 //BA.debugLineNum = 311;BA.debugLine="If IsNumber(paes1.Text) And IsNumber(paes2.Text)";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._paes1.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._paes2.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._paes3.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._paes4.getText()) && anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._paes5.getText())) { 
 //BA.debugLineNum = 313;BA.debugLine="rs = (paes1.Text + paes2.Text + paes3.Text + pae";
_rs = (int) (((double)(Double.parseDouble(mostCurrent._paes1.getText()))+(double)(Double.parseDouble(mostCurrent._paes2.getText()))+(double)(Double.parseDouble(mostCurrent._paes3.getText()))+(double)(Double.parseDouble(mostCurrent._paes4.getText()))+(double)(Double.parseDouble(mostCurrent._paes5.getText())))/(double)5);
 //BA.debugLineNum = 315;BA.debugLine="prompaeslabel.Text = rs";
mostCurrent._prompaeslabel.setText(BA.ObjectToCharSequence(_rs));
 //BA.debugLineNum = 316;BA.debugLine="Spinnerpaes.Add(paes1.Text)";
mostCurrent._spinnerpaes.Add(mostCurrent._paes1.getText());
 //BA.debugLineNum = 317;BA.debugLine="Spinnerpaes.Add(paes2.Text)";
mostCurrent._spinnerpaes.Add(mostCurrent._paes2.getText());
 //BA.debugLineNum = 318;BA.debugLine="Spinnerpaes.Add(paes3.Text)";
mostCurrent._spinnerpaes.Add(mostCurrent._paes3.getText());
 //BA.debugLineNum = 319;BA.debugLine="Spinnerpaes.Add(paes4.Text)";
mostCurrent._spinnerpaes.Add(mostCurrent._paes4.getText());
 //BA.debugLineNum = 320;BA.debugLine="Spinnerpaes.Add(paes5.Text)";
mostCurrent._spinnerpaes.Add(mostCurrent._paes5.getText());
 };
 //BA.debugLineNum = 323;BA.debugLine="End Sub";
return "";
}
public static String  _vv3() throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Public Sub calculaIMC ()";
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Private Sub CheckBox1_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 93;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 94;BA.debugLine="Checkbox2.Checked = False";
mostCurrent._checkbox2.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 95;BA.debugLine="Spinner1.Visible= False";
mostCurrent._spinner1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="Spinner2.Visible= False";
mostCurrent._spinner2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 97;BA.debugLine="Spinner3.Visible= False";
mostCurrent._spinner3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 98;BA.debugLine="txtPonderado1.Visible = True";
mostCurrent._txtponderado1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 99;BA.debugLine="txtPonderado2.Visible = True";
mostCurrent._txtponderado2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 100;BA.debugLine="txtPonderado3.Visible = True";
mostCurrent._txtponderado3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 101;BA.debugLine="labelPromedio.Visible = True";
mostCurrent._labelpromedio.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 102;BA.debugLine="btnPromedio.Visible = True";
mostCurrent._btnpromedio.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 104;BA.debugLine="VerifyCheckboxes";
_v5();
 };
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _checkbox2_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Private Sub CheckBox2_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 108;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 109;BA.debugLine="Checkbox1.Checked = False";
mostCurrent._checkbox1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 110;BA.debugLine="Spinner1.Visible= True";
mostCurrent._spinner1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 111;BA.debugLine="Spinner2.Visible= True";
mostCurrent._spinner2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="Spinner3.Visible= True";
mostCurrent._spinner3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 113;BA.debugLine="txtPonderado1.Visible = True";
mostCurrent._txtponderado1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="txtPonderado2.Visible = True";
mostCurrent._txtponderado2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 115;BA.debugLine="txtPonderado3.Visible = True";
mostCurrent._txtponderado3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="labelPromedio.Visible = True";
mostCurrent._labelpromedio.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 117;BA.debugLine="btnPromedio.Visible = True";
mostCurrent._btnpromedio.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 119;BA.debugLine="VerifyCheckboxes";
_v5();
 };
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim txtName, txtAnimal, txtColor As EditText";
mostCurrent._txtname = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._txtanimal = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._txtcolor = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim imageview1, imageview1sf, imagePaisaje As Ima";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._imageview1sf = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._imagepaisaje = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim mp As MediaPlayer";
mostCurrent._v6 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private listViewPaisaje As ListView";
mostCurrent._listviewpaisaje = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private edittext1, edittext2, edittext3 As EditTe";
mostCurrent._vv4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private btnPromedio As Button";
mostCurrent._btnpromedio = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private Spinner1, Spinner2, Spinner3, Spinnerpaes";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
mostCurrent._spinner2 = new anywheresoftware.b4a.objects.SpinnerWrapper();
mostCurrent._spinner3 = new anywheresoftware.b4a.objects.SpinnerWrapper();
mostCurrent._spinnerpaes = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Checkbox1, Checkbox2 As CheckBox";
mostCurrent._checkbox1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
mostCurrent._checkbox2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private edittext1, edittext2, edittext3, edittext";
mostCurrent._vv4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext2sf = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edittext3sf = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private txtPonderado1, txtPonderado2, txtPonderad";
mostCurrent._txtponderado1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._txtponderado2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._txtponderado3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._paes1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._paes2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._paes3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._paes4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._paes5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private label1, label2, label2sf,label3sf, labelP";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label2sf = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label3sf = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelpromedio = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._labelpaisaje = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._prompaeslabel = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private kg, altura As Double";
_v0 = 0;
_vv1 = 0;
 //BA.debugLineNum = 29;BA.debugLine="Private imc As Double";
_vv2 = 0;
 //BA.debugLineNum = 30;BA.debugLine="Private btn1, btnsf, calcprom As Button";
mostCurrent._btn1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btnsf = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._calcprom = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private p As Int";
_v7 = 0;
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _listviewpaisaje_itemclick(int _position,Object _value) throws Exception{
String _pos = "";
 //BA.debugLineNum = 232;BA.debugLine="Private Sub listViewPaisaje_ItemClick (Position As";
 //BA.debugLineNum = 233;BA.debugLine="Dim pos = Position";
_pos = BA.NumberToString(_position);
 //BA.debugLineNum = 234;BA.debugLine="Select pos";
switch (BA.switchObjectToInt(_pos,BA.NumberToString(0),BA.NumberToString(1),BA.NumberToString(2),BA.NumberToString(3),BA.NumberToString(4),BA.NumberToString(5))) {
case 0: {
 //BA.debugLineNum = 236;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"osorno.jpg").getObject()));
 //BA.debugLineNum = 237;BA.debugLine="labelPaisaje.Text = \"Volcán Osorno\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Volcán Osorno"));
 break; }
case 1: {
 //BA.debugLineNum = 239;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"puertovaras.jpg").getObject()));
 //BA.debugLineNum = 240;BA.debugLine="labelPaisaje.Text = \"Ciudad de Puerto Varas\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Ciudad de Puerto Varas"));
 break; }
case 2: {
 //BA.debugLineNum = 242;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"puertonatales.jpg").getObject()));
 //BA.debugLineNum = 243;BA.debugLine="labelPaisaje.Text = \"Puerto Natales\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Puerto Natales"));
 break; }
case 3: {
 //BA.debugLineNum = 245;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"torresdelpaine.jpg").getObject()));
 //BA.debugLineNum = 246;BA.debugLine="labelPaisaje.Text = \"Torres del Paine\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Torres del Paine"));
 break; }
case 4: {
 //BA.debugLineNum = 248;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"huilohuilo.jpg").getObject()));
 //BA.debugLineNum = 249;BA.debugLine="labelPaisaje.Text = \"Río Huilo-Huilo\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Río Huilo-Huilo"));
 break; }
case 5: {
 //BA.debugLineNum = 251;BA.debugLine="imagePaisaje.Bitmap = LoadBitmap(File.DirAssets";
mostCurrent._imagepaisaje.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"patagonia.jpg").getObject()));
 //BA.debugLineNum = 252;BA.debugLine="labelPaisaje.Text = \"Patagonia\"";
mostCurrent._labelpaisaje.setText(BA.ObjectToCharSequence("Patagonia"));
 break; }
}
;
 //BA.debugLineNum = 254;BA.debugLine="End Sub";
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

int value = i / 5 + 299796;
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
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _tabhost1_tabchanged() throws Exception{
 //BA.debugLineNum = 210;BA.debugLine="Sub TabHost1_TabChanged";
 //BA.debugLineNum = 212;BA.debugLine="Dim p As Int";
_v7 = 0;
 //BA.debugLineNum = 213;BA.debugLine="p = TabHost1.CurrentTab";
_v7 = mostCurrent._tabhost1.getCurrentTab();
 //BA.debugLineNum = 214;BA.debugLine="Select p";
switch (_v7) {
case 0: {
 //BA.debugLineNum = 216;BA.debugLine="Activity.Title = \"Calculadora de Promedio\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Calculadora de Promedio"));
 break; }
case 1: {
 //BA.debugLineNum = 218;BA.debugLine="Activity.Title = \"Calculadora de IMC\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Calculadora de IMC"));
 break; }
case 2: {
 //BA.debugLineNum = 220;BA.debugLine="Activity.Title = \"Mejor usa la otra...\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Mejor usa la otra..."));
 break; }
case 3: {
 //BA.debugLineNum = 222;BA.debugLine="Activity.Title = \"Ciudades del Sur de Chile\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Ciudades del Sur de Chile"));
 break; }
case 4: {
 //BA.debugLineNum = 224;BA.debugLine="Activity.Title = \"Promedio 5 PAES\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Promedio 5 PAES"));
 break; }
}
;
 //BA.debugLineNum = 228;BA.debugLine="End Sub";
return "";
}
public static String  _v5() throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Private Sub VerifyCheckboxes()";
 //BA.debugLineNum = 124;BA.debugLine="If Checkbox1.Checked = False And Checkbox2.Checke";
if (mostCurrent._checkbox1.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._checkbox2.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 125;BA.debugLine="Spinner1.Visible= False";
mostCurrent._spinner1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 126;BA.debugLine="Spinner2.Visible= False";
mostCurrent._spinner2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="Spinner3.Visible= False";
mostCurrent._spinner3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 128;BA.debugLine="txtPonderado1.Visible = False";
mostCurrent._txtponderado1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 129;BA.debugLine="txtPonderado2.Visible = False";
mostCurrent._txtponderado2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 130;BA.debugLine="txtPonderado3.Visible = False";
mostCurrent._txtponderado3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 131;BA.debugLine="labelPromedio.Visible = False";
mostCurrent._labelpromedio.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 132;BA.debugLine="btnPromedio.Visible = False";
mostCurrent._btnpromedio.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
}
