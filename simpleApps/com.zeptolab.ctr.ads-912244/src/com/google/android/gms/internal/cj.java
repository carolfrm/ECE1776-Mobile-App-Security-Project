package com.google.android.gms.internal;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.text.TextUtils;
import com.admarvel.android.ads.Constants;
import com.brightcove.player.media.CuePointFields;
import com.google.android.gms.plus.PlusShare;
import com.inmobi.commons.analytics.db.AnalyticsSQLiteHelper;
import com.zeptolab.ctr.billing.google.utils.IabHelper;
import com.zeptolab.ctr.scorer.GoogleScorer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class cj {
    private static final SimpleDateFormat ow;

    static {
        ow = new SimpleDateFormat("yyyyMMdd");
    }

    public static cf a(Context context, cd cdVar, String str) {
        try {
            long j;
            cf cfVar;
            List list;
            List list2;
            List list3;
            JSONObject jSONObject = new JSONObject(str);
            String optString = jSONObject.optString("ad_base_url", null);
            String optString2 = jSONObject.optString("ad_url", null);
            String optString3 = jSONObject.optString("ad_size", null);
            String optString4 = jSONObject.optString("ad_html", null);
            long j2 = -1;
            j = jSONObject.has("interstitial_timeout") ? (long) (jSONObject.getDouble("interstitial_timeout") * 1000.0d) : -1;
            String optString5 = jSONObject.optString("orientation", null);
            int i = -1;
            if (DeviceInfo.ORIENTATION_PORTRAIT.equals(optString5)) {
                i = cv.aU();
            } else if (DeviceInfo.ORIENTATION_LANDSCAPE.equals(optString5)) {
                i = cv.aT();
            }
            if (TextUtils.isEmpty(optString4)) {
                if (TextUtils.isEmpty(optString2)) {
                    da.w("Could not parse the mediation config: Missing required ad_html or ad_url field.");
                    return new cf(0);
                } else {
                    cf a = ci.a(context, cdVar.kN.pU, optString2);
                    optString = a.nw;
                    optString4 = a.oi;
                    j2 = a.oo;
                    cfVar = a;
                }
            } else if (TextUtils.isEmpty(optString)) {
                da.w("Could not parse the mediation config: Missing required ad_base_url field");
                return new cf(0);
            } else {
                cfVar = null;
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("click_urls");
            List list4 = cfVar == null ? null : cfVar.mt;
            if (optJSONArray != null) {
                if (list4 == null) {
                    list4 = new LinkedList();
                }
                int i2 = 0;
                while (i2 < optJSONArray.length()) {
                    list4.add(optJSONArray.getString(i2));
                    i2++;
                }
                list = list4;
            } else {
                list = list4;
            }
            JSONArray optJSONArray2 = jSONObject.optJSONArray("impression_urls");
            list4 = cfVar == null ? null : cfVar.mu;
            if (optJSONArray2 != null) {
                if (list4 == null) {
                    list4 = new LinkedList();
                }
                int i3 = 0;
                while (i3 < optJSONArray2.length()) {
                    list4.add(optJSONArray2.getString(i3));
                    i3++;
                }
                list2 = list4;
            } else {
                list2 = list4;
            }
            optJSONArray2 = jSONObject.optJSONArray("manual_impression_urls");
            list4 = cfVar == null ? null : cfVar.om;
            if (optJSONArray2 != null) {
                if (list4 == null) {
                    list4 = new LinkedList();
                }
                int i4 = 0;
                while (i4 < optJSONArray2.length()) {
                    list4.add(optJSONArray2.getString(i4));
                    i4++;
                }
                list3 = list4;
            } else {
                list3 = list4;
            }
            if (cfVar != null) {
                if (cfVar.orientation != -1) {
                    i = cfVar.orientation;
                }
                if (cfVar.oj > 0) {
                    j = cfVar.oj;
                }
            }
            return new cf(optString, optString4, list, list2, j, false, -1, list3, -1, i, optString3, j2);
        } catch (JSONException e) {
            da.w("Could not parse the mediation config: " + e.getMessage());
            return new cf(0);
        }
    }

    public static String a(cd cdVar, cm cmVar, Location location) {
        try {
            Map hashMap = new HashMap();
            if (cdVar.ob != null) {
                hashMap.put("ad_pos", cdVar.ob);
            }
            a((HashMap)hashMap, cdVar.oc);
            hashMap.put("format", cdVar.kQ.ln);
            if (cdVar.kQ.width == -1) {
                hashMap.put("smart_w", Constants.NATIVE_AD_FULL_ELEMENT);
            }
            if (cdVar.kQ.height == -2) {
                hashMap.put("smart_h", "auto");
            }
            if (cdVar.kQ.lp != null) {
                StringBuilder stringBuilder = new StringBuilder();
                ab[] abVarArr = cdVar.kQ.lp;
                int length = abVarArr.length;
                int i = 0;
                while (i < length) {
                    ab abVar = abVarArr[i];
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append("|");
                    }
                    stringBuilder.append(abVar.width == -1 ? (int) (((float) abVar.widthPixels) / cmVar.pc) : abVar.width);
                    stringBuilder.append("x");
                    stringBuilder.append(abVar.height == -2 ? (int) (((float) abVar.heightPixels) / cmVar.pc) : abVar.height);
                    i++;
                }
                hashMap.put("sz", stringBuilder);
            }
            hashMap.put("slotname", cdVar.adUnitId);
            hashMap.put("pn", cdVar.applicationInfo.packageName);
            if (cdVar.od != null) {
                hashMap.put("vc", Integer.valueOf(cdVar.od.versionCode));
            }
            hashMap.put("ms", cdVar.oe);
            hashMap.put("seq_num", cdVar.of);
            hashMap.put("session_id", cdVar.og);
            hashMap.put("js", cdVar.kN.pU);
            a((HashMap)hashMap, cmVar);
            if (cdVar.oc.versionCode >= 2 && cdVar.oc.ll != null) {
                a((HashMap)hashMap, cdVar.oc.ll);
            }
            if (cdVar.versionCode >= 2) {
                hashMap.put("quality_signals", cdVar.oh);
            }
            if (da.n(GoogleScorer.CLIENT_PLUS)) {
                da.v("Ad Request JSON: " + cv.m(hashMap).toString(GoogleScorer.CLIENT_PLUS));
            }
            return cv.m(hashMap).toString();
        } catch (JSONException e) {
            da.w("Problem serializing ad request to JSON: " + e.getMessage());
            return null;
        }
    }

    private static void a(HashMap hashMap, Location location) {
        HashMap hashMap2 = new HashMap();
        Float valueOf = Float.valueOf(location.getAccuracy() * 1000.0f);
        Long valueOf2 = Long.valueOf(location.getTime() * 1000);
        Long valueOf3 = Long.valueOf((long) (location.getLatitude() * 1.0E7d));
        Long valueOf4 = Long.valueOf((long) (location.getLongitude() * 1.0E7d));
        hashMap2.put("radius", valueOf);
        hashMap2.put("lat", valueOf3);
        hashMap2.put("long", valueOf4);
        hashMap2.put(CuePointFields.TIME, valueOf2);
        hashMap.put("uule", hashMap2);
    }

    private static void a(HashMap hashMap, am amVar) {
        Object obj;
        Object obj2 = null;
        if (Color.alpha(amVar.lI) != 0) {
            hashMap.put("acolor", m(amVar.lI));
        }
        if (Color.alpha(amVar.backgroundColor) != 0) {
            hashMap.put("bgcolor", m(amVar.backgroundColor));
        }
        if (!(Color.alpha(amVar.lJ) == 0 || Color.alpha(amVar.lK) == 0)) {
            hashMap.put("gradientto", m(amVar.lJ));
            hashMap.put("gradientfrom", m(amVar.lK));
        }
        if (Color.alpha(amVar.lL) != 0) {
            hashMap.put("bcolor", m(amVar.lL));
        }
        hashMap.put("bthick", Integer.toString(amVar.lM));
        switch (amVar.lN) {
            case IabHelper.BILLING_RESPONSE_RESULT_OK:
                obj = "none";
                break;
            case GoogleScorer.CLIENT_GAMES:
                obj = "dashed";
                break;
            case GoogleScorer.CLIENT_PLUS:
                obj = "dotted";
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                obj = "solid";
                break;
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            hashMap.put("btype", obj);
        }
        switch (amVar.lO) {
            case IabHelper.BILLING_RESPONSE_RESULT_OK:
                obj2 = "light";
                break;
            case GoogleScorer.CLIENT_GAMES:
                obj2 = "medium";
                break;
            case GoogleScorer.CLIENT_PLUS:
                obj2 = "dark";
                break;
        }
        if (obj2 != null) {
            hashMap.put("callbuttoncolor", obj2);
        }
        if (amVar.lP != null) {
            hashMap.put("channel", amVar.lP);
        }
        if (Color.alpha(amVar.lQ) != 0) {
            hashMap.put("dcolor", m(amVar.lQ));
        }
        if (amVar.lR != null) {
            hashMap.put("font", amVar.lR);
        }
        if (Color.alpha(amVar.lS) != 0) {
            hashMap.put("hcolor", m(amVar.lS));
        }
        hashMap.put("headersize", Integer.toString(amVar.lT));
        if (amVar.lU != null) {
            hashMap.put("q", amVar.lU);
        }
    }

    private static void a(HashMap hashMap, cm cmVar) {
        hashMap.put(AnalyticsSQLiteHelper.EVENT_LIST_AM, Integer.valueOf(cmVar.oM));
        hashMap.put("cog", j(cmVar.oN));
        hashMap.put("coh", j(cmVar.oO));
        if (!TextUtils.isEmpty(cmVar.oP)) {
            hashMap.put("carrier", cmVar.oP);
        }
        hashMap.put("gl", cmVar.oQ);
        if (cmVar.oR) {
            hashMap.put("simulator", Integer.valueOf(1));
        }
        hashMap.put("ma", j(cmVar.oS));
        hashMap.put("sp", j(cmVar.oT));
        hashMap.put("hl", cmVar.oU);
        if (!TextUtils.isEmpty(cmVar.oV)) {
            hashMap.put("mv", cmVar.oV);
        }
        hashMap.put("muv", Integer.valueOf(cmVar.oW));
        if (cmVar.oX != -2) {
            hashMap.put("cnt", Integer.valueOf(cmVar.oX));
        }
        hashMap.put("gnt", Integer.valueOf(cmVar.oY));
        hashMap.put("pt", Integer.valueOf(cmVar.oZ));
        hashMap.put("rm", Integer.valueOf(cmVar.pa));
        hashMap.put("riv", Integer.valueOf(cmVar.pb));
        hashMap.put("u_sd", Float.valueOf(cmVar.pc));
        hashMap.put("sh", Integer.valueOf(cmVar.pe));
        hashMap.put("sw", Integer.valueOf(cmVar.pd));
    }

    private static void a(HashMap hashMap, z zVar) {
        String aR = cs.aR();
        if (aR != null) {
            hashMap.put("abf", aR);
        }
        if (zVar.le != -1) {
            hashMap.put("cust_age", ow.format(new Date(zVar.le)));
        }
        if (zVar.extras != null) {
            hashMap.put("extras", zVar.extras);
        }
        if (zVar.lf != -1) {
            hashMap.put("cust_gender", Integer.valueOf(zVar.lf));
        }
        if (zVar.lg != null) {
            hashMap.put("kw", zVar.lg);
        }
        if (zVar.tagForChildDirectedTreatment != -1) {
            hashMap.put("tag_for_child_directed_treatment", Integer.valueOf(zVar.tagForChildDirectedTreatment));
        }
        if (zVar.lh) {
            hashMap.put("adtest", "on");
        }
        if (zVar.versionCode >= 2) {
            if (zVar.li) {
                hashMap.put("d_imp_hdr", Integer.valueOf(1));
            }
            if (!TextUtils.isEmpty(zVar.lj)) {
                hashMap.put("ppid", zVar.lj);
            }
            if (zVar.lk != null) {
                a(hashMap, zVar.lk);
            }
        }
        if (zVar.versionCode >= 3 && zVar.lm != null) {
            hashMap.put(PlusShare.KEY_CALL_TO_ACTION_URL, zVar.lm);
        }
    }

    private static Integer j(boolean z) {
        return Integer.valueOf(z ? 1 : 0);
    }

    private static String m(int i) {
        return String.format(Locale.US, "#%06x", new Object[]{Integer.valueOf(16777215 & i)});
    }
}