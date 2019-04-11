package org.luyinbros.widget.self.richedit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

    }

    @Override
    public void insertPicture(@Nullable File file, @Nullable String url) {
        if (getCursorPosition() != -1 && file != null && file.exists() && file.isFile()) {
            try {
                Drawable drawable = outputDrawable(BitmapFactory.decodeFile(file.getAbsolutePath(), getBitmapOption()));
                if (drawable!=null){
                    insertDrawable(drawable, "filePath://" + file.getAbsolutePath(), url);
                }
            } catch (Exception e) {
                //empty
            }
        }
    }

    @Override
    public void insertPicture(@Nullable Uri uri) {
        insertPicture(uri, null);
    }

    @Override
    public void insertPicture(@Nullable Uri uri, @Nullable String url) {
        if (getCursorPosition() != -1 && uri != null) {
            try {
                Drawable drawable = outputDrawable(BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri), null, getBitmapOption()));
                if (drawable!=null){
                    insertDrawable(drawable, uri.toString(), url);
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

    private BitmapFactory.Options getBitmapOption() {
        return new BitmapFactory.Options();
    }

    private Drawable outputDrawable(Bitmap src) {
        if (src == null) {
            return null;
        }
        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();
        final int parentWidth = getMeasuredWidth();

        if (srcWidth <= parentWidth) {
            Drawable drawable = new BitmapDrawable(getResources(), src);
            drawable.setBounds(0, 0, srcWidth, srcHeight);
            return drawable;
        } else if (srcHeight <= parentWidth) {
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            // 围绕原地进行旋转
            Bitmap newBitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, false);
            Drawable drawable = new BitmapDrawable(getResources(), newBitmap);
            drawable.setBounds(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
            if (!newBitmap.equals(src)) {
                src.recycle();
            }
            return drawable;
        } else {
            if (srcWidth <= srcHeight) {
                Matrix matrix = new Matrix();
                float scale = 1.0f * parentWidth / srcWidth;
                matrix.postScale(scale, scale);
                Bitmap newBitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, false);
                Drawable drawable = new BitmapDrawable(getResources(), newBitmap);
                drawable.setBounds(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
                if (!newBitmap.equals(src)) {
                    src.recycle();
                }
                return drawable;
            } else {
                Matrix matrix = new Matrix();
                float scale = 1.0f * parentWidth / srcHeight;
                matrix.postScale(scale, scale);

                matrix.setRotate(90);
                Bitmap newBitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, false);
                newBitmap.setWidth((int) (srcHeight*scale));
                newBitmap.setHeight((int) (srcWidth*scale));
                Drawable drawable = new BitmapDrawable(getResources(), newBitmap);
                drawable.setBounds(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
                if (!newBitmap.equals(src)) {
                    src.recycle();
                }
                return drawable;
            }
        }
    }

    private void outputPictureSize(BitmapFactory.Options options) {
        final int parentWidth = getMeasuredWidth();
        if (options.outWidth <= parentWidth) {
            return;
        }
        if (options.outHeight <= parentWidth) {
            return;
        }

        final int srcWith = options.outWidth;

    }

    private void insertDrawable(Drawable drawable, String src, String url) {
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
            editable.setSpan(new InnerImageSpan(drawable, src, url), startIndex, endIndex, ImageSpan.ALIGN_BASELINE);
            if (endIndex + 1 < editable.length()) {
                if (editable.charAt(endIndex + 1) != '\n') {
                    editable.insert(endIndex, "\n");
                }
            } else {
                editable.insert(endIndex, "\n");
            }
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
        private String url;

        private InnerImageSpan(@NonNull Drawable drawable, String source, String url) {
            super(drawable);
            mDrawable = drawable;
            this.mSource = source;
            this.url = url;
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

        public String getUrl() {
            return url;
        }
    }

    private static class HtmlEditableDelegate implements TextWatcher {
        private List<Span> spans;
        private RichEditText richEditText;

        private HtmlEditableDelegate(RichEditText richEditText) {
            this.richEditText = richEditText;
        }

        private Editable getEditableText() {
            return richEditText.getEditableText();
        }

        private String toHtml() {
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
                Span $nextSpan;
                Span $preSpan;
                char $char;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < spans.size(); i++) {
                    $span = spans.get(i);
                    if ($span.span instanceof InnerImageSpan) {
                        stringBuilder.append("<img src=\"http://pic15.nipic.com/20110628/1369025_192645024000_2.jpg\" alt=\"\"/>");
                    } else {
                        if ($span.start < $span.end) {
                            $nextSpan = next(i);
                            $preSpan = pre(i);
                            if ($preSpan == null && $nextSpan == null) {
                                withContent(stringBuilder, editable.subSequence($span.start, $span.end));
                                break;
                            } else {
                                int start = $span.start;
                                int end = $span.end;
                                if ($preSpan != null) {
                                    if ($preSpan.span instanceof InnerImageSpan) {
                                        $char = editable.charAt(start);
                                        if ($char == '\n') {
                                            start++;
                                        }
                                    }
                                }
                                if ($nextSpan != null) {
                                    if ($nextSpan.span instanceof InnerImageSpan) {
                                        $char = editable.charAt(end);
                                        if ($char == '\n') {
                                            end--;
                                        }
                                    }
                                }
                                if (start < end) {
                                    withContent(stringBuilder, editable.subSequence(start, end));
                                }
                            }
                        }
                    }
                }
                Log.d(TAG, "toHtml: " + stringBuilder.toString());
                return stringBuilder.toString();
            }
            Log.d(TAG, "toHtml: " + "");
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

        private Span pre(int current) {
            if (spans == null) {
                return null;
            }
            if (current < spans.size() - 1 && current > 0) {
                return spans.get(current - 1);
            } else {
                return null;
            }
        }

        public void loadHtml(String html) {

        }

        private void withContent(StringBuilder sb, CharSequence content) {
            final int length = content.length();
            if (length == 0) {
                return;
            }
            int startIndex = 0;
            boolean isPClose = true;
            if (content.charAt(startIndex) != '\n') {
                sb.append("<p>");
                isPClose = false;
            } else {
                startIndex += 1;
            }
            char $char;
            for (int cI = startIndex; cI < length; cI++) {
                $char = content.charAt(cI);
                if ($char == '\n') {
                    if (isPClose) {
                        if (cI > 0 && cI < length - 1) {
                            if (content.charAt(cI - 1) == '\n' && content.charAt(cI + 1) == '\n') {
                                sb.append("<p> </p>");
                            } else {
                                sb.append("<p>");
                                isPClose = false;
                            }
                        } else {
                            sb.append("<p>");
                            isPClose = false;
                        }

                    } else {
                        sb.append("</p>");
                        isPClose = true;
                    }
                } else {
                    if (isPClose) {
                        sb.append("<p>");
                        isPClose = false;
                    }
                    sb.append(htmlChar($char));
                }
            }
            if (!isPClose) {
                sb.append("</p>");
            }
        }

        private String htmlChar(char c) {
            if (c == '<') {
                return "&lt;";
            } else if (c == '>') {
                return "&gt;";
            } else {
                return String.valueOf(c);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //    Log.d(TAG, "beforeTextChanged: " + start + " " + after + " " + count);
            if (count != 0) {
                InnerImageSpan[] imageSpans = getEditableText().getSpans(start, start + count, InnerImageSpan.class);
                for (InnerImageSpan imageSpan : imageSpans) {
                    getEditableText().removeSpan(imageSpan);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //   Log.d(TAG, "onTextChanged: " + start + " " + before + " " + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: ");
        }

        private static class Span {
            private static final Object TEXT = new Object();
            private Object span;
            private int start;
            private int end;

            private Span(Object span, int start, int end) {
                this.span = span;
                this.start = start;
                this.end = end;
            }
        }
    }
}
