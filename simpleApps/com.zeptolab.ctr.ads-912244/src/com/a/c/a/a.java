package com.a.c.a;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public final class a extends Animation {
    public static final boolean a;
    private static final WeakHashMap b;
    private final WeakReference c;
    private final Camera d;
    private boolean e;
    private float f;
    private float g;
    private float h;
    private float i;
    private float j;
    private float k;
    private float l;
    private float m;
    private float n;
    private float o;
    private final RectF p;
    private final RectF q;
    private final Matrix r;

    static {
        a = Integer.valueOf(VERSION.SDK).intValue() < 11;
        b = new WeakHashMap();
    }

    private a(View view) {
        this.d = new Camera();
        this.f = 1.0f;
        this.l = 1.0f;
        this.m = 1.0f;
        this.p = new RectF();
        this.q = new RectF();
        this.r = new Matrix();
        setDuration(0);
        setFillAfter(true);
        view.setAnimation(this);
        this.c = new WeakReference(view);
    }

    public static a a_(View view) {
        Animation animation = (a) b.get(view);
        if (animation != null && animation == view.getAnimation()) {
            return animation;
        }
        a aVar = new a(view);
        b.put(view, aVar);
        return aVar;
    }

    private void a_(Matrix matrix, View view) {
        float width = (float) view.getWidth();
        float height = (float) view.getHeight();
        boolean z = this.e;
        float f = z ? this.g : width / 2.0f;
        float f2 = z ? this.h : height / 2.0f;
        float f3 = this.i;
        float f4 = this.j;
        float f5 = this.k;
        if (!(f3 == 0.0f && f4 == 0.0f && f5 == 0.0f)) {
            Camera camera = this.d;
            camera.save();
            camera.rotateX(f3);
            camera.rotateY(f4);
            camera.rotateZ(-f5);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-f, -f2);
            matrix.postTranslate(f, f2);
        }
        f3 = this.l;
        f4 = this.m;
        if (!(f3 == 1.0f && f4 == 1.0f)) {
            matrix.postScale(f3, f4);
            matrix.postTranslate((-(f / width)) * ((f3 * width) - width), (-(f2 / height)) * ((f4 * height) - height));
        }
        matrix.postTranslate(this.n, this.o);
    }

    private void a_(RectF rectF, View view) {
        rectF.set(BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_RED, (float) view.getWidth(), (float) view.getHeight());
        Matrix matrix = this.r;
        matrix.reset();
        a(matrix, view);
        this.r.mapRect(rectF);
        rectF.offset((float) view.getLeft(), (float) view.getTop());
        if (rectF.right < rectF.left) {
            float f = rectF.right;
            rectF.right = rectF.left;
            rectF.left = f;
        }
        if (rectF.bottom < rectF.top) {
            f = rectF.top;
            rectF.top = rectF.bottom;
            rectF.bottom = f;
        }
    }

    private void o() {
        View view = (View) this.c.get();
        if (view != null) {
            a(this.p, view);
        }
    }

    private void p() {
        View view = (View) this.c.get();
        if (view != null && view.getParent() != null) {
            RectF rectF = this.q;
            a(rectF, view);
            rectF.union(this.p);
            ((View) view.getParent()).invalidate((int) Math.floor((double) rectF.left), (int) Math.floor((double) rectF.top), (int) Math.ceil((double) rectF.right), (int) Math.ceil((double) rectF.bottom));
        }
    }

    public float a_() {
        return this.f;
    }

    public void a_(float f) {
        if (this.f != f) {
            this.f = f;
            View view = (View) this.c.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public void a_(int i) {
        View view = (View) this.c.get();
        if (view != null) {
            view.scrollTo(i, view.getScrollY());
        }
    }

    protected void applyTransformation(float f, Transformation transformation) {
        View view = (View) this.c.get();
        if (view != null) {
            transformation.setAlpha(this.f);
            a(transformation.getMatrix(), view);
        }
    }

    public float b() {
        return this.g;
    }

    public void b(float f) {
        if (!this.e || this.g != f) {
            o();
            this.e = true;
            this.g = f;
            p();
        }
    }

    public void b(int i) {
        View view = (View) this.c.get();
        if (view != null) {
            view.scrollTo(view.getScrollX(), i);
        }
    }

    public float c() {
        return this.h;
    }

    public void c(float f) {
        if (!this.e || this.h != f) {
            o();
            this.e = true;
            this.h = f;
            p();
        }
    }

    public float d() {
        return this.k;
    }

    public void d(float f) {
        if (this.k != f) {
            o();
            this.k = f;
            p();
        }
    }

    public float e() {
        return this.i;
    }

    public void e(float f) {
        if (this.i != f) {
            o();
            this.i = f;
            p();
        }
    }

    public float f() {
        return this.j;
    }

    public void f(float f) {
        if (this.j != f) {
            o();
            this.j = f;
            p();
        }
    }

    public float g() {
        return this.l;
    }

    public void g(float f) {
        if (this.l != f) {
            o();
            this.l = f;
            p();
        }
    }

    public float h() {
        return this.m;
    }

    public void h(float f) {
        if (this.m != f) {
            o();
            this.m = f;
            p();
        }
    }

    public int i() {
        View view = (View) this.c.get();
        return view == null ? 0 : view.getScrollX();
    }

    public void i(float f) {
        if (this.n != f) {
            o();
            this.n = f;
            p();
        }
    }

    public int j() {
        View view = (View) this.c.get();
        return view == null ? 0 : view.getScrollY();
    }

    public void j(float f) {
        if (this.o != f) {
            o();
            this.o = f;
            p();
        }
    }

    public float k() {
        return this.n;
    }

    public void k(float f) {
        View view = (View) this.c.get();
        if (view != null) {
            i(f - ((float) view.getLeft()));
        }
    }

    public float l() {
        return this.o;
    }

    public void l(float f) {
        View view = (View) this.c.get();
        if (view != null) {
            j(f - ((float) view.getTop()));
        }
    }

    public float m() {
        View view = (View) this.c.get();
        return view == null ? BitmapDescriptorFactory.HUE_RED : ((float) view.getLeft()) + this.n;
    }

    public float n() {
        View view = (View) this.c.get();
        return view == null ? BitmapDescriptorFactory.HUE_RED : ((float) view.getTop()) + this.o;
    }
}