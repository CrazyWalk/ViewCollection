package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.luyinbros.widget.R;
import org.luyinbros.widget.self.A2ZSideBar;

public class A2ZActivity extends AppCompatActivity {
    private A2ZSideBar mSideBar;
    private A2ZSideBar.OnLetterSelectedListener onLetterSelectedListener;
    private static final String TAG = "A2ZActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_a_2_z_view);
        mSideBar = findViewById(R.id.mSideBar);
        mSideBar.setOnLetterSelectedListener(new A2ZSideBar.OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(int position) {
                if (position!=-1){
                    Log.d(TAG, "onLetterSelected: " + mSideBar.mLetters.get(position));
                }

            }
        });
    }
}
