package cn.nexus6p.QQMusicNotify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import org.json.JSONArray;

import cn.nexus6p.QQMusicNotify.Base.HookInterface;
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.qiwu.MusicNotification.NotificationHook;
import name.mikanoshi.customiuizer.mods.System;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class initHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Context context = (Context) param.args[0];
                if (context == null) {
                    Log.d("XposedMusicNotify", lpparam.packageName + ": Context is null!");
                    return;
                }
                ClassLoader classLoader = context.getClassLoader();
                if (classLoader == null) {
                    Log.d("XposedMusicNotify", lpparam.packageName + ": classloader is null!");
                    return;
                }

                if (lpparam.packageName.equals("cn.nexus6p.QQMusicNotify")) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    findAndHookMethod("cn.nexus6p.QQMusicNotify.Utils.HookStatue", lpparam.classLoader, "isEnabled", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            Log.d("XposedMusicNotify", "模块已激活");
                            return true;
                        }
                    });
                    return;
                }
                /*if (lpparam.packageName.equals("me.singleneuron.originalmusicnotification_debugtool")) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    new cn.nexus6p.QQMusicNotify.Hook.mesingleneuronoriginalmusicnotificationdebugtool().setClassLoader(lpparam.classLoader).setContext(context).init();
                }*/

                if (lpparam.packageName.equals("com.android.systemui")) {
                    if (new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE, null, context).getBoolean("miuiModify", false)) {
                        try {
                            cn.nexus6p.removewhitenotificationforbugme.main.handleLoadPackage(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (new ContentProviderPreference(ContentProvider.CONTENT_PROVIDER_DEVICE_PROTECTED_PREFERENCE, null, context).getBoolean("miuiForceExpand", false)) {
                        try {
                            System.ExpandNotificationsHook(lpparam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                if (isHookEnabled(lpparam.packageName, context)) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    Class c = Class.forName("cn.nexus6p.QQMusicNotify.Hook." + lpparam.packageName.replace(".", ""));
                    HookInterface hookInterface = (HookInterface) c.newInstance();
                    hookInterface.setClassLoader(classLoader).setContext(context).init();
                }

                if (PreferenceUtil.getPreference(context).getBoolean("styleModify", false)) {
                    XposedBridge.log("XposedMusicNotify：加载包" + lpparam.packageName);
                    try {
                        new NotificationHook().init(lpparam.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isHookEnabled(String packageName, Context context) {
        JSONArray jsonArray = GeneralUtils.getSupportPackages(context);
        if (jsonArray == null) {
            Log.d("XposedMusicNotify", "加载配置文件失败：" + packageName);
            return false;
        }
        return (GeneralUtils.isStringInJSONArray(packageName, jsonArray) && (PreferenceUtil.getPreference(context).getBoolean(packageName + ".enabled", true)));
    }

}
