package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.luyinbros.widget.R;
import org.luyinbros.widget.self.AvatarSetView;
import org.luyinbros.widget.self.HeartTextView;
import org.luyinbros.widget.self.PraiseTextView;

public class SmallWidgetActivity extends AppCompatActivity {
    private HeartTextView heartView;
    private PraiseTextView praiseTextView;
    private AvatarSetView avatarSetView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_widget);
        heartView = findViewById(R.id.heartCountView);
        praiseTextView = findViewById(R.id.praiseTextView);
        avatarSetView = findViewById(R.id.avatarSetView);
        heartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartView.setSelected(!v.isSelected());
            }
        });
        praiseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseTextView.setSelected(!v.isSelected());
            }
        });
        findViewById(R.id.avatarSetViewInsertLastButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        avatarSetView.startAnim();
                    }
                });
        findViewById(R.id.avatarSetViewInsertFirstButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }
}
