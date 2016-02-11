package android.support.v4.text;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ICUCompatIcs {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;

    static {
        try {
            Class forName = Class.forName("libcore.icu.ICU");
            if (forName != null) {
                sGetScriptMethod = forName.getMethod("getScript", new Class[]{String.class});
                sAddLikelySubtagsMethod = forName.getMethod("addLikelySubtags", new Class[]{String.class});
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    ICUCompatIcs() {
    }

    public static String addLikelySubtags(String str) {
        try {
            if (sAddLikelySubtagsMethod != null) {
                return (String) sAddLikelySubtagsMethod.invoke(null, new Object[]{str});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return str;
    }

    public static String getScript(String str) {
        try {
            if (sGetScriptMethod != null) {
                return (String) sGetScriptMethod.invoke(null, new Object[]{str});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return null;
    }
}