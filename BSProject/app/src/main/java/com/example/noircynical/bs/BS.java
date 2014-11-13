package com.example.noircynical.bs;

/* bsAndroid v0.1
 * Copyright (c) 2013 by ProjectBS Committe and contributors.
 * http://www.bsplugin.com All rights reserved.
 * Licensed under the BSD license. See http://opensource.org/licenses/BSD-3-Clause
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BS {

    static public final int NONE = 0;
    static public final int CONTEXT = 1;
    static public final int ACTIVITY = 2;

    static private HashMap<Activity, BS> _pool = new HashMap<Activity, BS>();

    static public BS pool(Activity $act, Bundle $savedInstanceState) {
        if (!_pool.containsKey($act)) _pool.put($act, new BS($act, $savedInstanceState));
        return _pool.get($act);
    }

    static public void pool(BS $v) {
        _pool.remove($v._act);
    }

    static public enum RUN {invisible, visible, visibleToggle}

    static public enum EVENT {
        endedMedia, preparedMedia, focusMusic, focusAudio, focusAlarm,
        clickDialog, yesDialog, noDialog, okDialog,
        updatedAni, endedAni, startedAni, canceledAni, repeatedAni,
        bsTouch, down, up, move, focus, click,
        timeChange, itemClick, itemLongClick, itemSelected, drag, checked, genericMotion, hover, key, longClick, touch
    }

    static public enum DIALOG {show, title, adapter, message, cursor, cursorColume, yes, no, ok, view, array, index}

    static public enum VIEW {
        root, span, scaleX, scaleY, x, y, background, margin, alpha, layout, visible, id, tag, param, parent, focus, next, adapter, view, children, child, self, textColor, checked, image, text, textScaleX, layer,
        lineSpacing, index, rowid, clickable, focusable, longClickable, focusableInTouchMode, enabled, pressed, timePicker24, timePickerHour, timePickerMinute, progressBarMax, progressBarProgress,
        webviewUrl, webviewJs, webviewApiKey, webviewApi, webviewIsJS, webviewFile, webviewZoom, webviewClient, webviewChrome,
        webviewCache, webviewCachePath, webviewAgent, webviewEncoding, webviewHeader, webviewCanGoBack, webviewBack, webviewMultipleWindow, webviewReload, webviewLocalstorage,
        frameLayout, webView
    }

    static public enum ANI {animation, alpha, rotation, rotationX, rotationY, scaleX, scaleY, translationX, translationY, x, y, duration, delay, ease, cancel, start}

    static public enum AUDIO {focusAudio, volumeMusic, volumeAlarm, getStreamVolume, volume}

    static public enum MEDIA {start, prepare, pause, src, uri, current, duration, wake, streamType, prepareAsync, reset, stop, loop}

    static public enum HANDLER {id, run}

    static public enum WEBCHROME {popup, confirm, param, console, message, jsResult, webView, url, dialog, userGesture, self, callback, fullScreen, view, customView, popupClose, fullScreenOrientation, fullScreenOrientationOrigin, alert}

//region CoreUtil

    static private ExecutorService _workers = Executors.newFixedThreadPool(5);

    static {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    }

    static public void worker(Runnable $run) {
        _workers.execute($run);
    }

    static private boolean _isDebug = false;

    static public void debugMode(boolean $is) {
        _isDebug = $is;
    }

    static public String log(String $msg) {
        if (_isDebug) Log.i("BS", $msg);
        return $msg;
    }

    static public String uuid() {
        return android.os.Build.SERIAL;
    }

    static public boolean network(Context $context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) $context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (mobNetInfo != null && mobNetInfo.isConnected()) || (wifi != null && wifi.isConnected());
    }

    public boolean wifiActive() {
        if (_cm == null)
            _cm = (ConnectivityManager) _act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (_wifi == null) _wifi = _cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return _wifi.isConnected();
    }

//endregion
//region sqlite

    public BSSQLite sqlite(String $db, Object... arg) {
        return BSSQLite.pool(_act, $db, arg);
    }

//endregion
//region String casting

    final static private String _strSharp = "#";

    static public String str(final InputStream $v) {
        StringBuilder sb = new StringBuilder(200);
        try {
            byte[] b = new byte[4096];
            for (int n; (n = $v.read(b)) != -1; ) sb.append(new String(b, 0, n));
            $v.close();
        } catch (Exception $e) {
            throw new Error(log("BsStr.str:" + $e.toString()));
        }
        return trim(sb.toString());
    }

    static public String str(final int $v) {
        return Integer.toString($v);
    }

    static public String str(final int[] $v) {
        String t0 = Arrays.toString($v);
        return t0.substring(1, t0.length() - 1);
    }

    static public String str(final float[] $v) {
        String t0 = Arrays.toString($v);
        return t0.substring(1, t0.length() - 1);
    }

    static public String str(final String[] $v) {
        String t0 = Arrays.toString($v);
        return t0.substring(1, t0.length() - 1);
    }

    static public String str(final boolean[] $v) {
        String t0 = Arrays.toString($v);
        return t0.substring(1, t0.length() - 1);
    }

    static public String str(final float $v) {
        return Float.toString($v);
    }

    static public String str(final boolean $v) {
        return $v ? "true" : "false";
    }

    static public int str2int(final String $v) {
        return Integer.parseInt($v, 10);
    }

    static public float str2float(final String $v) {
        return Float.parseFloat($v);
    }

    static public boolean str2bool(final String $v) {
        return $v.equalsIgnoreCase("true") || $v.equals("1") ? Boolean.TRUE : Boolean.FALSE;
    }

    static public int str2color(final String $v) {
        return Color.parseColor($v.charAt(0) == '#' ? $v : _strSharp + $v);
    }

//endregion
//region StringUtil

    static public String right(String $v, int $right) {
        return $v.substring($v.length() - $right);
    }

    static public String trim(String $v) {
        return $v.trim();
    }

    static public String[] trim(String[] $v) {
        for (int i = 0, j = $v.length; i < j; i++) $v[i] = $v[i].trim();
        return $v;
    }

    static public LinkedList<String> trim(LinkedList<String> $v) {
        for (int i = 0, j = $v.size(); i < j; i++) $v.set(i, $v.get(i).trim());
        return $v;
    }

    static public String replace(final String $v, final String $from, final String $to) {
        int i = $v.lastIndexOf($from);
        if (i < 0) return $v;
        {
            StringBuilder sb = new StringBuilder($v.length() + ($to.length() - $from.length()) * 10).append($v);
            int j = $from.length();
            while (i > -1) {
                sb.replace(i, (i + j), $to);
                i = $v.lastIndexOf($from, i - 1);
            }
            return sb.toString();
        }
    }

    @SuppressWarnings("rawtypes")
    static public String join(final LinkedList $v) {
        return join($v, ",");
    }

    @SuppressWarnings("rawtypes")
    static public String join(final LinkedList $v, final String $sep) {
        StringBuilder sb = new StringBuilder($v.get(0).toString());
        for (int i = 1, j = $v.size(); i < j; i++)
            sb.append($sep).append($v.get(i).toString());
        return sb.toString();
    }

    static public String tmpl(final String $v, String[] $data) {
        StringBuilder sb = new StringBuilder($v.length() + 100).append($v);
        for (int i = 0, j = $data.length; i < j; i += 2) {
            String k = "@" + $data[i] + "@";
            do {
                int l = sb.indexOf(k);
                if (l == -1) break;
                sb.replace(l, l + k.length(), $data[i + 1]);
            } while (true);
        }
        return sb.toString();
    }

    static public String tmpl(final String $v, final HashMap<String, String> $data) {
        StringBuilder sb = new StringBuilder($v.length() + 100).append($v);
        for (Map.Entry<String, String> map : $data.entrySet()) {
            String k = "@" + map.getKey() + "@";
            do {
                int l = sb.indexOf(k);
                if (l == -1) break;
                sb.replace(l, l + k.length(), map.getValue());
            } while (true);
        }
        return sb.toString();
    }

    static private LinkedList<String> split(String $v, String $sep, boolean $isTrim) {
        int i = 0, j = $v.indexOf($sep);
        if (j > -1) {
            LinkedList<String> result = new LinkedList<String>();
            for (; j > -1; j = $v.indexOf($sep, i))
                result.add($isTrim ? $v.substring(i++, j).trim() : $v.substring(i++, j));
            if (i < $v.length() - 1)
                result.add($isTrim ? $v.substring(i).trim() : $v.substring(i));
            return result;
        }
        return null;
    }

    static public String strIsNum(final String $str) {
        int k = 0;
        for (int i = 0, j = $str.length(); i < j; i++) {
            switch ($str.charAt(i)) {
                case '-':
                    if (i > 0) return "string";
                    break;
                case '.':
                    if (k > 0) return "string";
                    else k++;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;
                default:
                    return "string";
            }
        }
        return k == 0 ? "int" : "float";
    }

    static public boolean strIsUrl(final String $v) {
        return $v.substring(0, 4).equals("http");
    }

//endregion
//region http
//
//    static public String get(Run $end, String $uri, String... arg) {
//        ArrayList<String> t0 = httpParam(arg);
//        return httpRun($end, "GET", !t0.get(0).equals("") ? $uri + "?" + t0.get(0).equals("") : $uri, t0);
//    }
//
//    static public String post(Run $end, String $uri, String... arg) {
//        return httpRun($end, "POST", $uri, httpParam(arg));
//    }
//
//    static private ArrayList<String> httpParam(String[] arg) {
//        int i = 0, j = arg.length;
//        String t0 = "";
//        ArrayList<String> t1 = new ArrayList<String>();
//        t1.add("");
//        try {
//            while (i < j) {
//                String k = arg[i++];
//                String v = arg[i++];
//                if (k.charAt(0) == '@') {
//                    t1.add(k.substring(1));
//                    t1.add(v);
//                } else {
//                    t0 += "&" + URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8");
//                }
//            }
//            t1.set(0, t0.substring(1));
//        } catch (Exception e) {
//        }
//        return t1;
//    }
//
//    static private String httpRun(final Run<String> $end, final String $method, final String $uri, final ArrayList<String> $data) {
//        final String[] result = {null};
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpURLConnection conn = (HttpURLConnection) new URL($uri).openConnection();
//                    conn.setRequestMethod($method);
//                    conn.setConnectTimeout(10000);
//                    conn.setReadTimeout(10000);
//                    conn.setRequestProperty("Connection", "Keep-Alive");
//                    conn.setUseCaches(false);
//                    conn.setRequestProperty("Accept-Encoding", "gzip");
//                    int i = 1, j = $data.size();
//                    while (i < j) conn.setRequestProperty($data.get(i++), $data.get(i++));
//                    if (!$method.equals("GET")) {
//                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                        String body = $data.get(0);
//                        if (body.length() > 0) {
//                            conn.setDoInput(true);
//                            conn.setDoOutput(true);
//                            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
//                            out.write(body);
//                            out.flush();
//                            out.close();
//                        }
//                    }
//                    conn.connect();
//                    String r = "";
//                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        InputStream is = conn.getInputStream();
//                        String gzip = conn.getHeaderField("gzip");
//                        if (gzip != null && gzip.equalsIgnoreCase("Accept-Encoding"))
//                            is = new GZIPInputStream(is);
//                        r = str(is);
//                    }
//                    if ($end == null) result[0] = r;
//                    else $end.run(r);
//                } catch (Exception e) {
//                    log("http Error:" + $uri + ":" + e.toString());
//                }
//            }
//        };
//        if ($end == null) {
//            run.run();
//            return result[0];
//        } else {
//            worker(run);
//            return null;
//        }
//    }
//
//endregion
//region audio
//
//    {
//        _audio.put(EVENT.focusMusic, new Iattr<AudioManager>() {
//            public Object g(final AudioManager $a) {
//                return $a.getStreamVolume(AudioManager.STREAM_MUSIC);
//            }
//
//            public Object s(final AudioManager $a, Object $v) {
//                if ($v == null) {
//                    return BS.this.p.containsKey(EVENT.focusMusic) && $a.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) BS.this.p.get(EVENT.focusMusic)) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
//                } else {
//                    BS.this.p.put(EVENT.focusMusic, $v);
//                    return $a.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) $v, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
//                }
//            }
//        });
//        _audio.put(EVENT.focusAlarm, new Iattr<AudioManager>() {
//            public Object g(final AudioManager $a) {
//                return $a.getStreamVolume(AudioManager.STREAM_ALARM);
//            }
//
//            public Object s(final AudioManager $a, Object $v) {
//                if ($v == null) {
//                    return BS.this.p.containsKey(EVENT.focusAlarm) && $a.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) BS.this.p.get(EVENT.focusAlarm)) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
//                } else {
//                    BS.this.p.put(EVENT.focusAlarm, $v);
//                    return $a.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) $v, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
//                }
//            }
//        });
//        _audio.put(AUDIO.volumeMusic, new Iattr<AudioManager>() {
//            public Object g(final AudioManager $a) {
//                return $a.getStreamVolume(AudioManager.STREAM_MUSIC);
//            }
//
//            public Object s(final AudioManager $a, Object $v) {
//                $a.adjustStreamVolume(AudioManager.STREAM_MUSIC, (Integer) $v, AudioManager.FLAG_SHOW_UI);
//                return $v;
//            }
//        });
//        _audio.put(AUDIO.volumeAlarm, new Iattr<AudioManager>() {
//            public Object g(final AudioManager $a) {
//                return $a.getStreamVolume(AudioManager.STREAM_ALARM);
//            }
//
//            public Object s(final AudioManager $a, Object $v) {
//                $a.adjustStreamVolume(AudioManager.STREAM_ALARM, (Integer) $v, AudioManager.FLAG_SHOW_UI);
//                return $v;
//            }
//        });
//        _audio.put(AUDIO.volume, new Iattr<AudioManager>() {
//            public Object g(final AudioManager $a) {
//                return BS.this.p.get(AUDIO.volume);
//            }
//
//            public Object s(final AudioManager $a, Object $v) {
//                BS.this.p.put(AUDIO.volume, $v);
//                $a.setStreamVolume((Integer) $v, $a.getStreamMaxVolume((Integer) $v), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                return $v;
//            }
//        });
//    }
//
//    static private HashMap<Object, Iattr<AudioManager>> _audio = new HashMap<Object, Iattr<AudioManager>>();
//
//    public Object audio(Object... arg) {
//        if (_au == null) _au = (AudioManager) _act.getSystemService(Context.AUDIO_SERVICE);
//        int i = 0, j = arg.length;
//        if (j < 2) return _audio.get(arg[i]).g(_au);
//        Object v = null;
//        while (i < j) v = _audio.get(arg[i++]).s(_au, arg[i++]);
//        return v;
//    }
//
//endregion
//region music
//
//    public HashMap<String, String> musicHash() {
//        HashMap<String, String> r = new HashMap<String, String>();
//        Cursor c = _act.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[]{
//                        MediaStore.Audio.Media.ALBUM_ID,
//                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.DATA,
//                        MediaStore.Audio.Media.DISPLAY_NAME,
//                        MediaStore.Audio.Media.SIZE
//                }, null, null, null);
//        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
//            int t0 = c.getColumnIndex(MediaStore.Audio.Media.TITLE), t1 = c.getColumnIndex(MediaStore.Audio.Media.DATA);
//            do {
//                r.put(c.getString(t0), c.getString(t1));
//            } while (c.moveToNext());
//        }
//        if (c != null) c.close();
//        return r;
//    }
//
//    public ArrayList<String> musicList() {
//        ArrayList<String> r = new ArrayList<String>();
//        Cursor c = _act.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[]{
//                        MediaStore.Audio.Media.ALBUM_ID,
//                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.DATA,
//                        MediaStore.Audio.Media.DISPLAY_NAME,
//                        MediaStore.Audio.Media.SIZE
//                }, null, null, null);
//        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
//            int t0 = c.getColumnIndex(MediaStore.Audio.Media.TITLE), t1 = c.getColumnIndex(MediaStore.Audio.Media.DATA);
//            do {
//                r.add(c.getString(t0));
//                r.add(c.getString(t1));
//            } while (c.moveToNext());
//        }
//        if (c != null) c.close();
//        return r;
//    }
//
////endregion
//region alarm
//
//    public static void alarm(Context $context, Class $class, int $pid, int $hour, int $minute, String $day, String[] $data) {
//        AlarmManager am = ((AlarmManager) $context.getSystemService(Context.ALARM_SERVICE));
//        long time = getNextAlarm($hour, $minute, $day);
//
//        Intent intent = new Intent($context, $class);
//        intent.putExtra("data", $data);
//        PendingIntent pi = PendingIntent.getBroadcast($context, $pid, intent, 0);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
//        } else {
//            am.set(AlarmManager.RTC_WAKEUP, time, pi);
//        }
//    }
//
//    public static long getNextAlarm(int $hour, int $minute, String $day) {
//        int i, today, dayplus = 0;
//        Calendar c = Calendar.getInstance();
//        BS.log("현재시간 " + c.getTime().toString());
//        if (c.get(Calendar.HOUR_OF_DAY) > $hour ||
//                (c.get(Calendar.HOUR_OF_DAY) == $hour && c.get(Calendar.MINUTE) >= $minute))
//            dayplus = 1;
//        //noinspection ResourceType
//        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), $hour, $minute, 0);
//
//        if ($day.equals("0000000")) {
//        } else {
//            today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
//            i = today + dayplus;
//            while (i < 7) {
//                if ($day.charAt(i) == '1') break;
//                if (i == 6) i = -1;
//                i++;
//            }
//            if (i == today && dayplus == 1) dayplus = 7;
//            else dayplus = i - today;
//            if (dayplus < 0) dayplus += 7;
//        }
//        BS.log("dayplus" + dayplus);
//        c.add(Calendar.DAY_OF_MONTH, dayplus);
//        BS.log("alam add:" + c.getTime().toString() + " day>" + $day);
//        return c.getTimeInMillis();
//
//    }
//
//    public void alarm(Class $class, int $id) {
//        log("alarm cancel:" + str($id));
//        if (_am == null) _am = (AlarmManager) _act.getSystemService(Context.ALARM_SERVICE);
//        _am.cancel(PendingIntent.getBroadcast(_act, $id, new Intent(_act, $class), 0));
//    }
//
//    public HashMap<String, Uri> alarmRingtonHash() {
//        HashMap<String, Uri> r = new HashMap<String, Uri>();
//        RingtoneManager ringtoneMgr = new RingtoneManager(_act);
//        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
//        Cursor c = ringtoneMgr.getCursor();
//        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
//            do {
//                Uri uri = ringtoneMgr.getRingtoneUri(c.getPosition());
//                Ringtone ringtone = RingtoneManager.getRingtone(_act, uri);
//                r.put(ringtone.getTitle(_act), uri);
//            } while (c.moveToNext());
//        }
//        return r;
//    }
//
//    public ArrayList<String> alarmRingtonList() {
//        ArrayList<String> r = new ArrayList<String>();
//        RingtoneManager ringtoneMgr = new RingtoneManager(_act);
//        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
//        Cursor c = ringtoneMgr.getCursor();
//        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
//            do {
//                Uri uri = ringtoneMgr.getRingtoneUri(c.getPosition());
//                Ringtone ringtone = RingtoneManager.getRingtone(_act, uri);
//                r.add(ringtone.getTitle(_act));
//                r.add(uri.toString());
//            } while (c.moveToNext());
//        }
//        return r;
//    }
//
//endregion
//region dialog
//
//    public bsDialog dialog(String $key, Object... $arg) {
//        return bsDialog.pool(_act, $key, $arg);
//    }
//
//endregion
//region View
//
//    public bsView View(VIEW $v) {
//        return View($v, false);
//    }
//
//    public bsView View(VIEW $v, boolean $isCache) {
//        return bsView.pool(_act, $v, $isCache);
//    }
//
//    public bsView View(View $view) {
//        return View($view, true);
//    }
//
//    public bsView View(View $view, boolean $isCache) {
//        return bsView.pool(_act, $view, $isCache);
//    }
//
//    public bsView View(int $id) {
//        return View($id, true);
//    }
//
//    public bsView View(int $id, boolean $isCache) {
//        return bsView.pool(_act, Rtype($id) == R.layout ? Rlayout($id) : _act.findViewById($id), $isCache);
//    }
//
//    public bsView View(String $id) {
//        return View($id, true);
//    }
//
//    public bsView View(String $id, boolean $isCache) {
//        return bsView.pool(_act, $id, $isCache);
//    }
//
//    public bsView View(String $id, View $finder) {
//        return View($id, $finder, true);
//    }
//
//    public bsView View(String $id, View $finder, boolean $isCache) {
//        return bsView.pool(_act, $finder, $id, $isCache);
//    }
//
//    public bsView View(int $id, View $finder) {
//        return View($id, $finder, true);
//    }
//
//    public bsView View(int $id, View $finder, boolean $isCache) {
//        return bsView.pool(_act, Rtype($id) == R.layout ? Rlayout($id) : $finder.findViewById($id), $isCache);
//    }
//
//    public bsView View(String $id, Object $finder) {
//        return View($id, $finder, true);
//    }
//
//    public bsView View(String $id, Object $finder, boolean $isCache) {
//        if ($finder instanceof bsView) return View($id, ((bsView) $finder).v, $isCache);
//        else if ($finder instanceof View) return View($id, (View) $finder, $isCache);
//        else if ($finder instanceof Integer) return View($id, View((Integer) $finder), $isCache);
//        else if ($finder instanceof String) return View($id, View((String) $finder), $isCache);
//        return View($id);
//    }
//
//    public bsView View(int $id, Object $finder) {
//        return View($id, $finder, true);
//    }
//
//    public bsView View(int $id, Object $finder, boolean $isCache) {
//        if ($finder instanceof bsView) return View($id, ((bsView) $finder).v, $isCache);
//        else if ($finder instanceof View) return View($id, (View) $finder, $isCache);
//        else if ($finder instanceof Integer) return View($id, View((Integer) $finder), $isCache);
//        else if ($finder instanceof String) return View($id, View((String) $finder), $isCache);
//        return View($id, $isCache);
//    }
//
    public FrameLayout root() {
        return (FrameLayout) _win.getDecorView();
    }
//
////endregion
//region Resource

    static public enum R {layout, string}

    static private HashMap<String, R> _Rtype = new HashMap<String, R>();

    static {
        _Rtype.put("layout", R.layout);
        _Rtype.put("string", R.string);
    }

    static public R Rtype(Activity $act, int $id) {
        return _Rtype.get($act.getResources().getResourceTypeName($id));
    }

    public R Rtype(int $id) {
        return _Rtype.get(_res.getResourceTypeName($id));
    }

    static public View Rlayout(Activity $act, String $id) {
        return Rlayout($act, $act.getResources().getIdentifier($id, "layout", $act.getPackageName()));
    }

    static public View Rlayout(Activity $act, int $id) {
        return $act.getLayoutInflater().inflate($id, null);
    }

    public View Rlayout(String $id) {
        return Rlayout(_res.getIdentifier($id, "layout", _packageName));
    }

    public View Rlayout(int $id) {
        return _inflater.inflate($id, null);
    }

    static public Drawable Rdrawable(Activity $act, String $id) {
        return Rdrawable($act, $act.getResources().getIdentifier($id, "drawable", $act.getPackageName()));
    }

    static public Drawable Rdrawable(Activity $act, int $id) {
        return $act.getResources().getDrawable($id);
    }

    public Drawable Rdrawable(String $id) {
        return Rdrawable(_res.getIdentifier($id, "drawable", _packageName));
    }

    public Drawable Rdrawable(int $id) {
        return _res.getDrawable($id);
    }

    static public String Rstring(Activity $act, String $id) {
        int i = $act.getResources().getIdentifier($id, "string", $act.getPackageName());
        return i == 0 ? "" : Rstring($act, i);
    }

    static public String Rstring(Activity $act, int $id) {
        return $act.getResources().getString($id);
    }

    public String Rstring(String $id) {
        int i = _res.getIdentifier($id, "string", _packageName);
        return i == 0 ? "" : Rstring(i);
    }

    public String Rstring(int $id) {
        return _res.getString($id);
    }

    public int Rid(String $id) {
        return _res.getIdentifier($id, "id", _packageName);
    }

//endregion
//region instanceService

    public int orientation() {
        return _act.getRequestedOrientation();
    }

    public void orientation(int $v) {
        _act.setRequestedOrientation($v);
    }

    public boolean fullScreen() {
        return _fullScreen;
    }

    public void fullScreen(boolean $v) {
        _fullScreen = $v;
        handlerFullScreen.sendMessage(Message.obtain(handlerFullScreen));
    }

    private Handler handlerFullScreen = new Handler() {
        @Override
        public void handleMessage(Message $msg) {
            if (_fullScreen) _win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            else _win.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    };

    public void screenOn(boolean $v) {
        _screenOn = $v;
        handlerScreenOn.sendMessage(Message.obtain(handlerScreenOn));
    }

    private Handler handlerScreenOn = new Handler() {
        @Override
        public void handleMessage(Message $msg) {
            if (_screenOn) _win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else _win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    };

    public Context context() {
        return _act;
    }

    public void virtual(final float $width, final float $height) {
        _isVirtual = true;
        virtual_width = $width;
        virtual_height = $height;
        size();
    }

    private void size() {
        _display.getMetrics(_dm);
        width = _dm.widthPixels;
        height = _dm.heightPixels;
        virtual_widthRate = virtual_width / ((float) width);
        virtual_heightRate = virtual_height / ((float) height);
    }

    public void toast(String $msg, int $length) {
        Toast.makeText(_act, $msg, $length).show();
    }

    static public void toast(Context $context, String $msg, int $length) {
        Toast.makeText($context, $msg, $length).show();
    }

    Activity activity() {
        return _act;
    }
//endregion
//region fields

    public P p;
    public Boolean isAlive;
    public int width, height = 0;
    public float virtual_width, virtual_height, virtual_widthRate, virtual_heightRate = 0;

    private Activity _act;
    private Window _win;
    private Display _display;
    private String _packageName;
    private Resources _res;
    private AlarmManager _am;
    private ConnectivityManager _cm;
    private NetworkInfo _wifi;
    private FragmentManager _fm;
    private AudioManager _au;
    private LayoutInflater _inflater;
    private Boolean _screenOn, _fullScreen;
    private DisplayMetrics _dm = new DisplayMetrics();
    private Boolean _isVirtual = false;

//endregion
//region liftCycle

    public void onResume() {
        log("bs.onResume");
        if (_screenOn) _win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onPause() {
        log("bs.onPause");
        if (_screenOn) _win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onDestroy() {
        log("bs.onDestroy");
        _act.finish();
        isAlive = Boolean.FALSE;
    }

    public void onStop() {
        log("bs.onStop");
    }

    public void onActivityResult(int $request, int $result, Intent $data) {
        log("bs.onActivityResult");
    /*
	bsIntent.onActivityResult( $request, $result, $data );
    */
    }

    public void onPostCreate(Bundle $savedInstanceState) {
        log("bs.onPostCreate");
    }

//endregion
//region handler

    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message $msg) {
            P p = (P) $msg.obj;
            p.RUN_P(HANDLER.run).run(p);
            P.pool(p);
        }
    };

    public void sendMessage(Run<P> $run, Object $id, Object... $arg) {
        Message msg = Message.obtain(_handler);
        P p = P.pool();
        p.put(HANDLER.run, $run);
        p.put(HANDLER.id, $id);
        int i = 0, j = $arg.length;
        while (i < j) p.put($arg[i++], $arg[i++]);
        msg.obj = p;
        _handler.sendMessage(msg);
    }

//endregion

    private BS(Activity $activity, Bundle $savedInstanceState) {
        log("bs.onCreate");
        p = P.pool();
        _act = $activity;
        _win = _act.getWindow();
        _display = _act.getWindowManager().getDefaultDisplay();
        _packageName = _act.getPackageName();
        _res = _act.getResources();
        _inflater = _act.getLayoutInflater();
        isAlive = Boolean.TRUE;
        _screenOn = Rstring("bs_screenOn").equals("true") ? Boolean.TRUE : Boolean.FALSE;
        if (Rstring("bs_fullScreen").equals("true"))
            _win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Rstring("bs_titleBar").equals("false"))
            _act.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String t0 = Rstring("bs_orientation");
        if (!t0.equals("") && !t0.equals("false")) {
            int i = -1;
            if (t0.equals("landscape")) i = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            else if (t0.equals("landscape_sensor"))
                i = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            else if (t0.equals("landscape_reverse"))
                i = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            else if (t0.equals("portrait")) i = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            else if (t0.equals("portrait_sensor"))
                i = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
            else if (t0.equals("portrait_reverse"))
                i = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            if (i > -1) _act.setRequestedOrientation(i);
        }
        size();
    }

}