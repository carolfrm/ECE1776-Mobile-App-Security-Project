package com.google.android.gms.maps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.c;
import com.google.android.gms.dynamic.d;
import com.google.android.gms.internal.er;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.internal.q;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public class MapFragment extends Fragment {
    private final b Pm;
    private GoogleMap Pn;

    static class a implements LifecycleDelegate {
        private final Fragment Po;
        private final IMapFragmentDelegate Pp;

        public a(Fragment fragment, IMapFragmentDelegate iMapFragmentDelegate) {
            this.Pp = (IMapFragmentDelegate) er.f(iMapFragmentDelegate);
            this.Po = (Fragment) er.f(fragment);
        }

        public IMapFragmentDelegate gV() {
            return this.Pp;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onCreate(android.os.Bundle r4) {
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.maps.MapFragment.a.onCreate(android.os.Bundle):void");
            /*
            r3 = this;
            if (r4 != 0) goto L_0x0007;
        L_0x0002:
            r4 = new android.os.Bundle;	 Catch:{ RemoteException -> 0x0028 }
            r4.<init>();	 Catch:{ RemoteException -> 0x0028 }
        L_0x0007:
            r0 = r3.Po;	 Catch:{ RemoteException -> 0x0028 }
            r0 = r0.getArguments();	 Catch:{ RemoteException -> 0x0028 }
            if (r0 == 0) goto L_0x0022;
        L_0x000f:
            r1 = "MapOptions";
            r1 = r0.containsKey(r1);	 Catch:{ RemoteException -> 0x0028 }
            if (r1 == 0) goto L_0x0022;
        L_0x0017:
            r1 = "MapOptions";
            r2 = "MapOptions";
            r0 = r0.getParcelable(r2);	 Catch:{ RemoteException -> 0x0028 }
            com.google.android.gms.maps.internal.p.a(r4, r1, r0);	 Catch:{ RemoteException -> 0x0028 }
        L_0x0022:
            r0 = r3.Pp;	 Catch:{ RemoteException -> 0x0028 }
            r0.onCreate(r4);	 Catch:{ RemoteException -> 0x0028 }
            return;
        L_0x0028:
            r0 = move-exception;
            r1 = new com.google.android.gms.maps.model.RuntimeRemoteException;
            r1.<init>(r0);
            throw r1;
            */
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            try {
                return (View) c.b(this.Pp.onCreateView(c.h(layoutInflater), c.h(viewGroup), bundle));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroy() {
            try {
                this.Pp.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            try {
                this.Pp.onDestroyView();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
            try {
                this.Pp.onInflate(c.h(activity), (GoogleMapOptions) bundle.getParcelable("MapOptions"), bundle2);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onLowMemory() {
            try {
                this.Pp.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.Pp.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.Pp.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            try {
                this.Pp.onSaveInstanceState(bundle);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
        }

        public void onStop() {
        }
    }

    static class b extends com.google.android.gms.dynamic.a {
        private final Fragment Po;
        protected d Pq;
        private Activity nd;

        b(Fragment fragment) {
            this.Po = fragment;
        }

        private void setActivity(Activity activity) {
            this.nd = activity;
            gW();
        }

        protected void a(d dVar) {
            this.Pq = dVar;
            gW();
        }

        public void gW() {
            if (this.nd != null && this.Pq != null && fj() == null) {
                try {
                    MapsInitializer.initialize(this.nd);
                    this.Pq.a(new a(this.Po, q.A(this.nd).f(c.h(this.nd))));
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    public MapFragment() {
        this.Pm = new b(this);
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public static MapFragment newInstance(GoogleMapOptions googleMapOptions) {
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("MapOptions", googleMapOptions);
        mapFragment.setArguments(bundle);
        return mapFragment;
    }

    protected IMapFragmentDelegate gV() {
        this.Pm.gW();
        return this.Pm.fj() == null ? null : ((a) this.Pm.fj()).gV();
    }

    public final GoogleMap getMap() {
        IMapFragmentDelegate gV = gV();
        if (gV == null) {
            return null;
        }
        try {
            IGoogleMapDelegate map = gV.getMap();
            if (map == null) {
                return null;
            }
            if (this.Pn == null || this.Pn.gM().asBinder() != map.asBinder()) {
                this.Pn = new GoogleMap(map);
            }
            return this.Pn;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void onActivityCreated(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onActivityCreated(bundle);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.Pm.setActivity(activity);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.Pm.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.Pm.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void onDestroy() {
        this.Pm.onDestroy();
        super.onDestroy();
    }

    public void onDestroyView() {
        this.Pm.onDestroyView();
        super.onDestroyView();
    }

    public void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        super.onInflate(activity, attributeSet, bundle);
        this.Pm.setActivity(activity);
        Parcelable createFromAttributes = GoogleMapOptions.createFromAttributes(activity, attributeSet);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("MapOptions", createFromAttributes);
        this.Pm.onInflate(activity, bundle2, bundle);
    }

    public void onLowMemory() {
        this.Pm.onLowMemory();
        super.onLowMemory();
    }

    public void onPause() {
        this.Pm.onPause();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.Pm.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MapFragment.class.getClassLoader());
        }
        super.onSaveInstanceState(bundle);
        this.Pm.onSaveInstanceState(bundle);
    }

    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
    }
}