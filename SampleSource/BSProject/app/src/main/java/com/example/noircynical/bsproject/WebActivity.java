package com.example.noircynical.bsproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * Created by noirCynical on 14. 11. 13..
 */
public class WebActivity extends Activity{

    private Intent intent;

    private WebView webview;
    private Button btn;
    private WebSettings setting;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web);
    }

    protected void onNewIntent(Intent intent){
        webview= (WebView)findViewById(R.id.webview);
        btn= (Button)findViewById(R.id.back_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        // webview setting
        setting= webview.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        webview.loadUrl("http://"+intent.getExtras().getString("url"));
        webview.setWebViewClient(new WebViewClient());
    }

    protected void onResume(){
        super.onResume();
        onNewIntent(getIntent());
    }

    protected void onStop(){
        super.onStop();
        intent= null;
    }
}
