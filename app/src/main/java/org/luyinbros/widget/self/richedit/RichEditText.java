package org.luyinbros.widget.self.richedit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

public class RichEditText extends AppCompatEditText {
    private static final String TAG = "RichEditText";

    public RichEditText(Context context) {
        super(context);
        init(null);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }
    private void init(@Nullable AttributeSet attributeSet){
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: s:" + s + " start:" + start + " count:" + count + " after:" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: s:" + s + " start:" + start + " count:" + count + " before:" + before);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged:");
            }
        });

    }
}
