package com.what_the_hack.bsby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FeedBackActivity extends AppCompatActivity {

    // replace this with feebback url
    String feedBackUrl = "https://www.google.com";
    WebView feedBackView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setFeedBackForm();

    }

    public void setFeedBackForm()
    {
        /** simply set webview to url*/
        feedBackView = (WebView) findViewById(R.id.feedback_web_view);
        feedBackView.getSettings().setJavaScriptEnabled(true);
        feedBackView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        feedBackView.setWebViewClient(new feedbackClient());

        try{
            feedBackView.loadUrl("https://bsby.000webhostapp.com/FeedbackForm.html");
        }
        catch (Exception e)
        {
            Log.e("Err","web view");
        }
    }


    public class feedbackClient extends WebViewClient
    {
        /** for settng webviewclient for feedback*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            view.loadUrl(request.toString());
            //return super.shouldOverrideUrlLoading(view, request);
            return true;
        }
    }
}