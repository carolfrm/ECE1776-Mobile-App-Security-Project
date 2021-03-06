package com.heyzap.sdk.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import com.heyzap.house.impl.AbstractActivity;
import com.heyzap.house.model.AdModel;
import com.heyzap.internal.Utils;

final class h implements Runnable {
    final /* synthetic */ Activity a;
    final /* synthetic */ Class b;
    final /* synthetic */ AdModel c;

    h(Activity activity, Class cls, AdModel adModel) {
        this.a = activity;
        this.b = cls;
        this.c = adModel;
    }

    @SuppressLint({"NewApi"})
    public void run() {
        Intent intent = new Intent(this.a, this.b);
        intent.setFlags(603979776);
        intent.putExtra(AbstractActivity.ACTIVITY_INTENT_IMPRESSION_KEY, this.c.getImpressionId());
        intent.putExtra(AbstractActivity.ACTIVITY_INTENT_CONTEXT_KEY, VideoAd.AD_UNIT);
        intent.putExtra(AbstractActivity.ACTIVITY_INTENT_ACTION_KEY, 1);
        this.a.startActivity(intent);
        if (Utils.getSdkVersion() >= 5) {
            this.a.overridePendingTransition(17432578, 17432579);
        }
    }
}