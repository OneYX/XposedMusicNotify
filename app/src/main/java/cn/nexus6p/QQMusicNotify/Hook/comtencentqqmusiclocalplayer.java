package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicNotification;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Keep
public class comtencentqqmusiclocalplayer extends BasicNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {
        final Class notifyClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.business.k.s", classLoader);
        final Class infoClazz = XposedHelpers.findClass("com.tencent.qqmusiclocalplayer.c.e", classLoader);
        final Class clazz3 = XposedHelpers.findClass("com.tencent.a.d.t", classLoader);
        final Class clazzO = XposedHelpers.findClass("com.tencent.qqmusicsdk.a.o", classLoader);
        findAndHookMethod(notifyClazz, "b", Context.class, infoClazz, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Notification replaceHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                basicParam.setIconID(0x7f020099);
                basicParam.setContext((Context) param.args[0]);
                basicParam.setBitmap((Bitmap) param.args[2]);
                basicParam.setStatue((!(Boolean) XposedHelpers.callStaticMethod(clazzO, "a") && ((Boolean) XposedHelpers.callStaticMethod(clazzO, "e") || (Boolean) XposedHelpers.callStaticMethod(clazzO, "b"))));
                if (mTOKEN == null)
                    mTOKEN = new MediaSession(basicParam.getContext(), "mbr").getSessionToken();
                basicParam.setToken(mTOKEN);
                basicParam.setTitleString((CharSequence) XposedHelpers.callMethod(param.args[1], "getName"));
                basicParam.setTextString((CharSequence) XposedHelpers.callMethod(param.args[1], "getSinger"));
                preSongIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_PREVIOUS_TASKBAR");
                playIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_TOGGLEPAUSE_TASKBAR");
                nextSongIntent = new Intent("com.tencent.qqmusicsdk.ACTION_SERVICE_NEXT_TASKBAR");
                contentIntent = new Intent("android.intent.action.MAIN");
                contentIntent.addCategory("android.intent.category.LAUNCHER").setClassName(basicParam.getContext(), (String) XposedHelpers.callStaticMethod(clazz3, "d", basicParam.getContext()));
                return build();
            }
        });
    }

}
