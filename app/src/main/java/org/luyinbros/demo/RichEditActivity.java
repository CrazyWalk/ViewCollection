package org.luyinbros.demo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.self.richedit.RichEditText;

import java.io.FileNotFoundException;
import java.net.URL;

public class RichEditActivity extends AppCompatActivity {
    private View insertPictureButton;
    private View printHtmlButton;
    private View testReplaceButton;
    private ImageView insertPictureImageView;
    private RichEditText richEditText;
    private final int mSelectedPictureRequestCode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_edit);
        insertPictureButton = findViewById(R.id.insertPictureButton);
        richEditText = findViewById(R.id.richEditText);
        printHtmlButton = findViewById(R.id.printHtmlButton);
        insertPictureImageView = findViewById(R.id.insertPictureImageView);
        testReplaceButton = findViewById(R.id.testReplaceButton);
        insertPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(i, "选择图片"), mSelectedPictureRequestCode);
            }
        });
        printHtmlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.toHtml();
            }
        });
        testReplaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               richEditText.getEditableText().replace(1, 7, "89");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mSelectedPictureRequestCode &&
                resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
//            if (uri!=null){
//                try {
//                    insertPictureImageView.setImageDrawable(Drawable.createFromStream(getContentResolver().openInputStream(uri), ""));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }

            richEditText.insertPicture(data.getData());
        }
    }
}
