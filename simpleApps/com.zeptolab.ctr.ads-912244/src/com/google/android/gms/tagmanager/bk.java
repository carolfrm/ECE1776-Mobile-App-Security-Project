package com.google.android.gms.tagmanager;

import android.content.Context;
import android.provider.Settings.Secure;
import com.google.android.gms.internal.a;
import com.google.android.gms.internal.d;
import java.util.Map;

class bk extends aj {
    private static final String ID;
    private final Context mContext;

    static {
        ID = a.ao.toString();
    }

    public bk(Context context) {
        super(ID, new String[0]);
        this.mContext = context;
    }

    protected String G(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }

    public boolean iy() {
        return true;
    }

    public d.a u(Map map) {
        String G = G(this.mContext);
        return G == null ? di.ku() : di.r(G);
    }
}