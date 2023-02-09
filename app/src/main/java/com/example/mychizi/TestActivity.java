package com.example.mychizi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.views.GestureFrameLayout;

/**
 * @Author：Danny
 * @Time： 2023/1/30 10:45
 * @Description
 */
public class TestActivity extends AppCompatActivity {
    private Button btn_test,btn_test2;
    private FrameLayout layoutInfo;
    private RulerLayoutView rulerLayoutView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutInfo = findViewById(R.id.layoutInfo);
        btn_test = findViewById(R.id.btn_test);
        btn_test2 = findViewById(R.id.btn_test2);
        btn_test.setOnClickListener(v -> {
            rulerLayoutView = new RulerLayoutView(TestActivity.this);
            layoutInfo.addView(rulerLayoutView, Util.getScreenWidth(this), Util.getScreenHeight(this));
        });
        btn_test2.setOnClickListener(v -> {
            layoutInfo.removeAllViews();
        });
    }
}
