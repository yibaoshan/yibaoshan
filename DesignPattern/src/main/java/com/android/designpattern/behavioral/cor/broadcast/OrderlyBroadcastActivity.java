package com.android.designpattern.behavioral.cor.broadcast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.designpattern.R;

@SuppressLint("SetTextI18n")
public class OrderlyBroadcastActivity extends AppCompatActivity {

    private final String ACTION_BUY_SOY_SAUCE = "ACTION_BUY_SOY_SAUCE";
    private final String INTENT_KEY_MONEY = "INTENT_KEY_MONEY";
    private final String INTENT_KEY_COMMAND = "INTENT_KEY_COMMAND";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderly_broadcast);
        initBroadcastReceiver();
        findViewById(R.id.btn_buy_soy_sauce).setOnClickListener(v -> sendBuySoySauceCommand());
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_BUY_SOY_SAUCE);
        intentFilter.setPriority(10001);
        registerReceiver(new BigBrother(), intentFilter);
        intentFilter.setPriority(10002);
        registerReceiver(new SecondBrother(), intentFilter);
        intentFilter.setPriority(10003);
        registerReceiver(new ThirdBrother(), intentFilter);
    }

    private void sendBuySoySauceCommand() {
        Intent intent = new Intent();
        intent.setAction(ACTION_BUY_SOY_SAUCE);
        intent.putExtra(INTENT_KEY_MONEY, 20);
        intent.putExtra(INTENT_KEY_COMMAND, "买酱油");
        sendBroadcast(intent);
    }

    private class BigBrother extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textView = findViewById(R.id.tv_big_brother);
            int money = intent.getIntExtra(INTENT_KEY_MONEY, 0);
            money -= 5;
            intent.putExtra(INTENT_KEY_MONEY, money);
            String command = intent.getStringExtra(INTENT_KEY_COMMAND);
            textView.setText("给你" + money + "块钱让你" + command);
        }
    }

    private class SecondBrother extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textView = findViewById(R.id.tv_second_brother);
            int money = intent.getIntExtra(INTENT_KEY_MONEY, 0);
            money -= 10;
            intent.putExtra(INTENT_KEY_MONEY, money);
            String command = intent.getStringExtra(INTENT_KEY_COMMAND);
            textView.setText("给你" + money + "块钱让你" + command);
        }
    }

    private class ThirdBrother extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textView = findViewById(R.id.tv_third_brother);
            int money = intent.getIntExtra(INTENT_KEY_MONEY, 0);
            String command = intent.getStringExtra(INTENT_KEY_COMMAND);
            textView.setText("给你" + money + "块钱让你" + command);
        }
    }

}
