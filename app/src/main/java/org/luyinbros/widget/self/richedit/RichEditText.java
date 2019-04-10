package org.luyinbros.widget.self.richedit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 目前只支持p标签和img标签
 */
public class RichEditText extends AppCompatEditText implements RichEditController {
    private static final String TAG = "RichEditText";
    private HtmlEditableDelegate mHtmlDelegate;

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

    private void init(@Nullable AttributeSet attributeSet) {
        mHtmlDelegate = new HtmlEditableDelegate(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addTextChangedListener(mHtmlDelegate);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(mHtmlDelegate);
    }

    @Override
    public String toHtml() {
        return mHtmlDelegate.toHtml();
    }

    @Override
    public void loadHtml(String html) {
        mHtmlDelegate.loadHtml(html);
    }

    @Override
    public void insertPicture(@Nullable File file) {
        if (getCursorPosition() != -1 && file != null && file.exists() && file.isFile()) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                if (options.outWidth > 0 && options.outHeight > 0) {
                    final int pictureWidth = Math.min(options.outWidth, getMeasuredWidth());
                    final int pictureHeight = Math.min(options.outHeight, 200);

                    options.inJustDecodeBounds = false;
                    options.outWidth = pictureWidth;
                    options.outHeight = pictureHeight;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    if (bitmap != null) {
                        bitmap.setHeight(pictureHeight);
                        bitmap.setWidth(pictureWidth);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, pictureWidth, pictureHeight);
                        insertDrawable(drawable, "filePath://" + file.getAbsolutePath());
                    }
                }

            } catch (Exception e) {
                //empty
            }
        }
    }

    @Override
    public void insertPicture(@Nullable Uri uri) {
        if (getCursorPosition() != -1 && uri != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri), null, options);
                if (options.outWidth > 0 && options.outHeight > 0) {
                    final int pictureWidth = Math.min(options.outWidth, getMeasuredWidth());
                    final int pictureHeight = Math.min(options.outHeight, 200);

                    options.inJustDecodeBounds = false;
                    options.outWidth = pictureWidth;
                    options.outHeight = pictureHeight;

                    Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri), null, options);
                    if (bitmap != null) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, pictureWidth, pictureHeight);
                        insertDrawable(drawable, uri.toString());
                    }
                }

            } catch (Exception e) {
                //empty
            }
        }

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }


    private void insertDrawable(Drawable drawable, String src) {
        final int cursorPosition = getCursorPosition();
        if (cursorPosition != -1 && drawable != null) {
            int startIndex;
            int endIndex;
            Editable editable = getEditableText();
            if (cursorPosition == 0 || isCharEqual(editable, cursorPosition, '\n')) {
                editable.insert(cursorPosition, " ");
                startIndex = cursorPosition;
                endIndex = startIndex + 1;
            } else {
                editable.insert(cursorPosition, "\n ");
                startIndex = cursorPosition + 1;
                endIndex = startIndex + 1;
            }
            editable.setSpan(new InnerImageSpan(drawable, src), startIndex, endIndex, ImageSpan.ALIGN_BASELINE);
            editable.insert(endIndex, "\n");
            setSelection(++endIndex);
        }
    }

    private boolean isCharEqual(Editable editable, int targetPosition, char c) {
        if (targetPosition <= 0) {
            return false;
        } else {
            char[] lastArray = new char[1];
            editable.getChars(targetPosition - 1, targetPosition, lastArray, 0);
            return lastArray[0] == c;
        }
    }

    private int getCursorPosition() {
        if (isFocusable()) {
            int cursorPosition;
            int selectionEnd = getSelectionEnd();
            if (selectionEnd != 0) {
                cursorPosition = selectionEnd;
            } else {
                cursorPosition = getSelectionStart();
            }
            return cursorPosition;
        } else {
            return -1;
        }
    }

    private static class InnerImageSpan extends ImageSpan {
        private Drawable mDrawable;
        private String mSource;

        private InnerImageSpan(@NonNull Drawable drawable, String source) {
            super(drawable);
            mDrawable = drawable;
            this.mSource = source;
        }

        @Override
        public Drawable getDrawable() {
            Drawable drawable = null;
            if (mDrawable != null) {
                drawable = mDrawable;
            }
            return drawable;
        }

        @Nullable
        @Override
        public String getSource() {
            return mSource;
        }
    }

    private static class HtmlEditableDelegate implements TextWatcher {
        private List<Span> spans;
        private RichEditText richEditText;

        public HtmlEditableDelegate(RichEditText richEditText) {
            this.richEditText = richEditText;
        }

        private Editable getEditableText() {
            return richEditText.getEditableText();
        }

        public String toHtml() {
            Editable editable = getEditableText();
            spans = new ArrayList<>();
            int next;
            final int editLength = editable.length();
            if (editLength > 0) {
                for (int i = 0; i < editLength; i = next) {
                    next = editable.nextSpanTransition(i, editLength, CharacterStyle.class);
                    CharacterStyle[] characterStyles = editable.getSpans(i, next, CharacterStyle.class);
                    if (characterStyles.length == 0) {
                        spans.add(new Span(Span.TEXT, i, next));
                    } else {
                        for (CharacterStyle characterStyle : characterStyles) {
                            spans.add(new Span(characterStyle,
                                    editable.getSpanStart(characterStyle),
                                    editable.getSpanEnd(characterStyle)));
                        }
                    }
                }
                Span $span;
                for (int i = 0; i < spans.size(); i++) {
                    $span = spans.get(i);
                    if ($span.span == Span.TEXT) {

                    }
                }

            }

            return "";
        }

        private Span next(int current) {
            if (spans == null) {
                return null;
            }
            if (current < spans.size() - 1) {
                return spans.get(current + 1);
            } else {
                return null;
            }
        }

        public void loadHtml(String html) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "beforeTextChanged: " + start + " " + after + " " + count);
            if (count != 0) {
                InnerImageSpan[] imageSpans = getEditableText().getSpans(start, start + count, InnerImageSpan.class);
                for (InnerImageSpan imageSpan : imageSpans) {
                    getEditableText().removeSpan(imageSpan);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged: " + start + " " + before + " " + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: ");
        }

        private static class Span {
            private static final Object TEXT = new Object();
            public Object span;
            public int start;
            public int end;

            public Span(Object span, int start, int end) {
                this.span = span;
                this.start = start;
                this.end = end;
            }
        }
    }
}
