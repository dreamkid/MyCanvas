package com.demo.hezd.mycanvas.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.demo.hezd.mycanvas.R;
import com.demo.hezd.mycanvas.views.MyCanvas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author hezd
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int STORE_PIC = 0x1;
    private Button mStore2PicBtn;
    private MyCanvas mMyCanvas;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STORE_PIC:
                    String message = (String) msg.obj;
                    Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                    mMyCanvas.clear();
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setListeners();
    }

    private void setListeners() {
        mStore2PicBtn.setOnClickListener(this);
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mStore2PicBtn = (Button) findViewById(R.id.btn_store2pic);
        mMyCanvas = (MyCanvas) findViewById(R.id.mycanvas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_store2pic:
                sotre2Pic();
                break;
        }
    }

    private void sotre2Pic() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"图片"+System.currentTimeMillis()+".png";
        try {
            mMyCanvas.saveToFile(mHandler, filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
