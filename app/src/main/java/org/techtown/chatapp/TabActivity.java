package org.techtown.chatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TabActivity extends AppCompatActivity
{

    long lastPressed;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText("home");
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("friends");
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText("profile");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    @Override
    public void onBackPressed()
    {
        if(System.currentTimeMillis() - lastPressed < 1500)
        {
            finish();
        }
        else
        {
            Toast.makeText(this,"한번 더 누르면 종료",Toast.LENGTH_SHORT).show();
        }
        lastPressed = System.currentTimeMillis();
    }

}
