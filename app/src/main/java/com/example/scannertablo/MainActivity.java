package com.example.scannertablo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static Handler h;
    static Handler h2;
    private TextView tv;
    private TextView tv2;
    static ClipboardManager myClipboard;
    public void onClickRegOk(View v) {
        ServerUDP.setFlag_send();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        tv.setTextSize(20);
        tv2.setTextSize(18);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        h = new Handler(Looper.myLooper()) {
            public void handleMessage(android.os.Message msg) {
                tv.setText((String) msg.obj);
            };
        };

        h2 = new Handler(Looper.myLooper()) {
            public void handleMessage(android.os.Message msg) {
                tv2.setText((String) msg.obj);
            };
        };

        Thread t = new Thread(new ServerUDP());
        t.start();
    }
}