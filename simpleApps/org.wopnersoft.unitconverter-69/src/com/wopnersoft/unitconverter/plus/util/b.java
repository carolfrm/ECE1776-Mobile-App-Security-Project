package com.wopnersoft.unitconverter.plus.util;

import android.view.View;
import android.view.View.OnClickListener;

// compiled from: ProGuard
class b implements OnClickListener {
    final /* synthetic */ AboutDialog a;

    b(AboutDialog aboutDialog) {
        this.a = aboutDialog;
    }

    public void onClick(View view) {
        this.a.showDialog(0);
    }
}