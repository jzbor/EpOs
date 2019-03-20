package de.jzbor.epos;


import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import de.jzbor.epos.activities.InfoActivity;

public class MyBrowser extends WebViewClient {

    private InfoActivity activity;

    public MyBrowser(InfoActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(activity, "Page currently unavailable:\n" + error.getDescription(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, "Page currently unavailable", Toast.LENGTH_LONG).show();
        }
        activity.onBackPressed();
    }
}
