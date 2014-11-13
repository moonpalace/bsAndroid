package com.example.noircynical.bs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;

class bsWebChromeClient extends WebChromeClient {

    static private HashMap<BS.WEBCHROME, Run<P>> _attr = new HashMap<BS.WEBCHROME, Run<P>>();

    {
//        _attr.put(BS.WEBCHROME.alert, new Run<P>() {
//            @Override
//            public void run(P p) {
//                bsDialog.pool(p.bs, "@_webChromeAlertOk").s(BS.DIALOG.message, p.get(BS.WEBCHROME.message), BS.DIALOG.ok, "확인", BS.EVENT.okDialog, new EclickDialog() {
//                    @Override
//                    public void run(DialogInterface $v, P $p) {
//                        ((JsResult) p.get(BS.WEBCHROME.jsResult)).confirm();
//                    }
//                }, BS.DIALOG.show);
//            }
//        });
//        _attr.put(BS.WEBCHROME.confirm, new Run<P>() {
//            @Override
//            public void run(P p) {
//                bsDialog.pool(p.bs, "@_webChromeConfirm").s(BS.DIALOG.message, p.get(BS.WEBCHROME.message),
//                        BS.DIALOG.yes, "확인", BS.EVENT.yesDialog, new EclickDialog() {
//                            @Override
//                            public void run(DialogInterface $v, P $p) {
//                                ((JsResult) p.get(BS.WEBCHROME.jsResult)).confirm();
//                            }
//                        }, BS.DIALOG.no, "취소", BS.EVENT.noDialog, new EclickDialog() {
//                            @Override
//                            public void run(DialogInterface $v, P $p) {
//                                ((JsResult) p.get(BS.WEBCHROME.jsResult)).cancel();
//                            }
//                        }, BS.DIALOG.show);
//            }
//        });
        _attr.put(BS.WEBCHROME.console, new Run<P>() {
            @Override
            public void run(P p) {
                ConsoleMessage t0 = (ConsoleMessage) p.get(BS.WEBCHROME.message);
                BS.log("javascript::" + t0.message() + " line-" + BS.str(t0.lineNumber()));
            }
        });
        _attr.put(BS.WEBCHROME.popup, new Run<P>() {
            @Override
            public void run(P p) {
                bsWebChromeClient v = (bsWebChromeClient) p.get(BS.WEBCHROME.self);
                if (v.popup == null) v.popup = new ArrayList<WebView>();
//                WebView view = (WebView) p.bs.View(BS.VIEW.webView).s(
//                        BS.VIEW.layout, new int[]{ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT},
//                        BS.VIEW.webviewClient, BS.NONE, BS.VIEW.webviewIsJS, true, BS.VIEW.webviewChrome, p.get(BS.WEBCHROME.self), BS.VIEW.parent, p.bs.root(), BS.VIEW.view);
//                v.popup.add(view);
//                ((WebView.WebViewTransport) p.MESSAGE(BS.WEBCHROME.message).obj).setWebView(view);
                ((Message) p.get(BS.WEBCHROME.message)).sendToTarget();
            }
        });
        _attr.put(BS.WEBCHROME.fullScreen, new Run<P>() {
            @Override
            public void run(P p) {
                p.bs.fullScreen(true);
//                p.bs.root().addView((View) p.bs.View(BS.VIEW.frameLayout).s(
//                        BS.VIEW.tag, BS.WEBCHROME.fullScreen,
//                        BS.VIEW.background, "#000000", BS.VIEW.layout, bsView.layout,
//                        BS.EVENT.touch, new View.OnTouchListener() {
//                            public boolean onTouch(View v, MotionEvent event) {
//                                return true;
//                            }
//                        },
//                        BS.VIEW.child, p.bs.View((View) p.get(BS.WEBCHROME.customView)).s(BS.VIEW.layout, bsView.layout, BS.VIEW.view),
//                        BS.VIEW.view
//                ));
            }
        });
    }
//region override

    @Override
    public void onShowCustomView(View $customView, CustomViewCallback $callback) {
        BS.log("AAAAAAAAA" + (p.containsKey(BS.WEBCHROME.fullScreenOrientation) ? "!!!" : "2222"));
        if (p.containsKey(BS.WEBCHROME.fullScreenOrientation)) {
            p.put(BS.WEBCHROME.fullScreenOrientationOrigin, p.bs.orientation());
            BS.log("ORIENT:" + BS.str((Integer) p.get(BS.WEBCHROME.fullScreenOrientation)));
            p.bs.orientation((Integer) p.get(BS.WEBCHROME.fullScreenOrientation));
        }
        Object v = p.get(BS.WEBCHROME.fullScreen);
        if (v == null) super.onShowCustomView($customView, $callback);
        else if (p.get(BS.WEBCHROME.callback) != null) {
            ((CustomViewCallback) p.get(BS.WEBCHROME.callback)).onCustomViewHidden();
            isFullScreen = false;
        } else {
            p.bs.fullScreen(true);
            p.put(BS.WEBCHROME.customView, $customView);
            p.put(BS.WEBCHROME.callback, $callback);
            ((Run<P>) v).run(p);
            isFullScreen = true;
        }
    }

    @Override
    public void onHideCustomView() {
        BS.log("onHideCustomView");
        if (p.containsKey(BS.WEBCHROME.fullScreenOrientationOrigin)) {
            p.bs.orientation((Integer) p.get(BS.WEBCHROME.fullScreenOrientationOrigin));
            p.remove(BS.WEBCHROME.fullScreenOrientationOrigin);
        }
        Object v = p.get(BS.WEBCHROME.fullScreen);
        if (v == null) super.onHideCustomView();
        else if (p.containsKey(BS.WEBCHROME.customView)) {
            p.bs.fullScreen(false);
//            View[] t0 = (View[]) p.bs.View(p.bs.root()).s(BS.VIEW.children);
//            for (int i = 0, j = t0.length; i < j; i++) {
//                if (t0[i].getTag() == BS.WEBCHROME.fullScreen) p.bs.root().removeView(t0[i]);
//            }
            ((CustomViewCallback) p.get(BS.WEBCHROME.callback)).onCustomViewHidden();
            p.remove(BS.WEBCHROME.callback);
            p.remove(BS.WEBCHROME.customView);
        }
        isFullScreen = false;
    }

    @Override
    public boolean onCreateWindow(WebView $v, boolean $dialog, boolean $userGesture, Message $msg) {
        Object v = p.get(BS.WEBCHROME.popup);
        if (v == null) super.onCreateWindow($v, $dialog, $userGesture, $msg);
        else {
            p.put(BS.WEBCHROME.webView, $v);
            p.put(BS.WEBCHROME.dialog, $dialog);
            p.put(BS.WEBCHROME.message, $msg);
            p.put(BS.WEBCHROME.userGesture, $userGesture);
            ((Run<P>) v).run(p);
        }
        return true;
    }

    @Override
    public void onCloseWindow(WebView $v) {
//        Object v = p.get(BS.WEBCHROME.popup);
//        if (v == null) super.onCloseWindow($v);
//        else {
//            bsWebChromeClient t0 = (bsWebChromeClient) p.get(BS.WEBCHROME.self);
//            if (t0.popup != null) {
//                int i = t0.popup.indexOf($v);
//                if (i > -1) {
//                    t0.popup.remove(i);
//                    p.bs.View($v).d();
//                }
//            }
//            v = p.get(BS.WEBCHROME.popupClose);
//            if (v != null) {
//                p.put(BS.WEBCHROME.webView, $v);
//                ((Run<P>) v).run(p);
//            }
//        }
    }

    @Override
    public boolean onJsAlert(WebView $v, String $url, String $msg, final JsResult $result) {
        Object v = p.get(BS.WEBCHROME.alert);
        if (v == null) super.onJsAlert($v, $url, $msg, $result);
        else {
            p.put(BS.WEBCHROME.webView, $v);
            p.put(BS.WEBCHROME.url, $url);
            p.put(BS.WEBCHROME.message, $msg);
            p.put(BS.WEBCHROME.jsResult, $result);
            ((Run<P>) v).run(p);
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView $v, String $url, String $msg, final JsResult $result) {
        Object v = p.get(BS.WEBCHROME.confirm);
        if (v == null) super.onJsConfirm($v, $url, $msg, $result);
        else {
            p.put(BS.WEBCHROME.webView, $v);
            p.put(BS.WEBCHROME.url, $url);
            p.put(BS.WEBCHROME.message, $msg);
            p.put(BS.WEBCHROME.jsResult, $result);
            ((Run<P>) v).run(p);
        }
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage $msg) {
        Object v = p.get(BS.WEBCHROME.console);
        if (v == null) super.onConsoleMessage($msg);
        else {
            p.put(BS.WEBCHROME.message, $msg);
            ((Run<P>) v).run(p);
        }
        return true;
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        super.getVisitedHistory(callback);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
    }

//endregion

    P p = P.pool();
    ArrayList<WebView> popup;
    boolean isFullScreen = false;

    bsWebChromeClient(BS $bs, Object[] $arg) {
        if ($arg.length == 1 && $arg[0] instanceof Object[]) $arg = (Object[]) $arg[0];
        int i = 0, j = $arg.length;
        p.bs = $bs;
        p.put(BS.WEBCHROME.self, this);
        while (i < j) {
            BS.WEBCHROME k = (BS.WEBCHROME) $arg[i++];
            Object v = $arg[i++];
            if (_attr.containsKey(k)) {
                Run<P> t0 = null;
                if (v instanceof Run) t0 = (Run<P>) v;
                else if (v instanceof Boolean && (Boolean) v) t0 = _attr.get(k);
                p.put(k, t0);
            } else {
                p.put(k, v);
            }
        }
    }

}
