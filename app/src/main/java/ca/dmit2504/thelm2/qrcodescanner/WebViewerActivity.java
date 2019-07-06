package ca.dmit2504.thelm2.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewerActivity extends AppCompatActivity
{
	WebView webviewer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_viewer);

		webviewer = findViewById(R.id.web_viewer_webview);

		webviewer.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				view.loadUrl(url);
				return false;
			}
		});

		WebSettings webSettings = webviewer.getSettings();
		webSettings.setJavaScriptEnabled(true);

		webviewer.loadUrl(getIntent().getStringExtra("URL"));
	}
}
