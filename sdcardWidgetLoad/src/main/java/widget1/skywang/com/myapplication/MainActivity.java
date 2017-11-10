package widget1.skywang.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private String sdcardApk = "/mnt/sdcard/widgets-debug.apk";

    ViewGroup boxlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boxlayout = (ViewGroup) findViewById(R.id.boxlayout);

    }


    public void start(View view) {

        getPackageInfo();

    }


    private void getPackageInfo() {
        int layoutId = 0x7f030001; //example_appwidget res/layout/example_appwidget.xml

        PackageInfo info = getPackageManager().getPackageArchiveInfo(sdcardApk, 0);

        Context thirdContext;

        try {
            thirdContext = (Context) Context.class.getMethod("createApplicationContext",ApplicationInfo.class, int.class)
                    .invoke(getApplicationContext(), info.applicationInfo, Context.CONTEXT_RESTRICTED);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }



        replaceContextResources(thirdContext, sdcardApk);

        View view = inflateView(thirdContext, layoutId,boxlayout);
        boxlayout.addView(view);
    }


    /**
     * 使用反射的方式，使用Bundle的Resource对象，替换Context的mResources对象
     * @param context
     */
    public void replaceContextResources(Context context,String apkPatch){
        Resources targetRes;
        try {
            AssetManager am = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath",String.class).invoke(am, apkPatch);
            targetRes = new Resources(am, getResources().getDisplayMetrics(), getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Field field = context.getClass().getDeclaredField("mResources");
            field.setAccessible(true);
            field.set(context, targetRes);
            System.out.println("debug:repalceResources succ");
        } catch (Exception e) {
            System.out.println("debug:repalceResources error");
            e.printStackTrace();
        }
    }


    private View inflateView(Context context, int layoutid, ViewGroup parent) {
        // RemoteViews may be built by an application installed in another
        // user. So build a context that loads resources from that user but
        // still returns the current users userId so settings like data / time formats
        // are loaded without requiring cross user persmissions.
        final Context contextForResources = context;
        Context inflationContext = new ContextWrapper(context) {
            @Override
            public Resources getResources() {
                return contextForResources.getResources();
            }
            @Override
            public Resources.Theme getTheme() {
                return contextForResources.getTheme();
            }
            @Override
            public String getPackageName() {
                return contextForResources.getPackageName();
            }
        };

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Clone inflater so we load resources from correct context and
        // we don't add a filter to the static version returned by getSystemService.
        inflater = inflater.cloneInContext(inflationContext);
//        inflater.setFilter(this);
        View v = inflater.inflate(layoutid, parent, false);
//        v.setTagInternal(R.id.widget_frame, rv.getLayoutId());
        return v;
    }




    private void parser() {
    //        ApkCompatLiteParser parser = new ApkCompatLiteParser();
    //        ApkCompatLiteParser.ApkLiteInfo info = parser.parsePackageLite(sdcardApk);

    //        Log.i(TAG, "info is null:" + (info == null));
    }

}
