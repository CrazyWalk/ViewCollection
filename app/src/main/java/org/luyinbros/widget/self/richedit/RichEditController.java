package org.luyinbros.widget.self.richedit;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;

public interface RichEditController {

    String toHtml();

    void loadHtml(String html);

    void insertPicture(@Nullable File file);

    void insertPicture(@Nullable File file,@Nullable String url);

    void insertPicture(@Nullable Uri uri);

    void insertPicture(@Nullable Uri uri,@Nullable String url);
}
