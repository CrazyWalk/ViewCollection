package org.luyinbros.widget.self.richedit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.util.AttributeSet;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 目前只支持p标签和img标签
 */
public class RichEditText extends AppCompatEditText implements RichEditController {
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

    private void init(@Nullable AttributeSet attributeSet) {

    }

    @Override
    public String toHtml() {
        Editable editable = getEditableText();
        int next;
        final int editLength = editable.length();
        if (editLength > 0) {
            StringBuilder textBuilder = new StringBuilder();
            for (int i = 0; i < editLength; i = next) {
                next = editable.nextSpanTransition(i, editLength, ImageSpan.class);
                ImageSpan[] imageSpanArray = editable.getSpans(i, next, ImageSpan.class);
                Log.d(TAG, "toHtml: " + Arrays.toString(imageSpanArray));
                Log.d(TAG, "toHtml: " + i + "  " + next);
                if (imageSpanArray.length != 0) {
                    for (int imageIndex = 0; imageIndex < imageSpanArray.length; imageIndex++) {
                        textBuilder.append("<image src=\"")
                                .append(imageSpanArray[imageIndex].getSource())
                                .append("\"/>");
                    }

                } else {
                    withContent(textBuilder, editable.subSequence(i, next));
                }
            }
            Log.d(TAG, "toHtml: " + textBuilder.toString());
            return textBuilder.toString();
        }

        return "";
    }

    private void withContent(StringBuilder textBuilder, CharSequence text) {
        final int textLength = text.length();
        if (textLength != 0) {
            char c;
            char lc;
            char rc;
            boolean pComplete = true;
            c = text.charAt(0);
            if (c != '\n') {
                pComplete = false;
                textBuilder.append("#");
            }

            for (int j = 0; j < textLength; j++) {
                c = text.charAt(j);
                if (c == '\n') {
                    textBuilder.append("#");
                } else {
                    if (pComplete) {
                        textBuilder.append("#");
                        pComplete = false;
                    }
                    if (c == '<') {
                        textBuilder.append("&lt;");
                    } else if (c == '>') {
                        textBuilder.append("&gt;");
                    } else {
                        textBuilder.append(c);
                    }
                }
            }

            if (textLength > 1) {
                c = text.charAt(textLength - 1);
                if (c != '\n') {
                    textBuilder.append("#");
                }
            }
        }

    }

    @Override
    public void loadHtml(String html) {

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
                    final int pictureHeight = options.outHeight;

                    options.inJustDecodeBounds = false;
                    options.outWidth = pictureWidth;
                    options.outHeight = pictureHeight;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    if (bitmap != null) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, pictureWidth, pictureHeight);
                        insertDrawable(drawable, file.getAbsolutePath());
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
                    final int pictureHeight = options.outHeight;

                    options.inJustDecodeBounds = false;
                    options.outWidth = pictureWidth;
                    options.outHeight = pictureHeight;
                    Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri), null, options);
                    if (bitmap != null) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, pictureWidth, pictureHeight);
                        insertDrawable(drawable,uri.toString());
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
            editable.setSpan(new ImageSpan(drawable, src), startIndex, endIndex, ImageSpan.ALIGN_BASELINE);
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
}
