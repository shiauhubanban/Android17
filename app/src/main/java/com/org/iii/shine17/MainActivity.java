package com.org.iii.shine17;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private LocationManager lmgr;
    private MyGPSListerner myGPSListerner;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView =(TextView)findViewById(R.id.mesg);
        webView = (WebView) findViewById(R.id.webView);

        //取得權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

        } else {
            init();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }


    public void init() {
        initWebView();

        //LocationManager 取得位置
        lmgr = (LocationManager) getSystemService(LOCATION_SERVICE);

//        不一定要加
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        myGPSListerner = new MyGPSListerner();
        //0越低越耗電
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myGPSListerner);

    }

    private class MyGPSListerner implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //取得經緯度
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            webView.loadUrl("javascript:goto(" +lat+", " + lng + ")");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    @Override
    public void finish() {

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//           return;
//        }
        lmgr.removeUpdates(myGPSListerner);
        super.finish();
    }

    private void initWebView(){
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/map.html");
        webView.addJavascriptInterface(new MyJS(),"android");
    }
    public class MyJS{
        @JavascriptInterface
        public void getLatLng(String lat,String lng){
            Log.v("shine", lat+":"+lng);
            textView.setText(lat+":"+lng);
        }
    }

    public void gotoWhere(View v){
        webView.loadUrl("javaScript:goto(36.043782, 32.721547)");
    }
}
