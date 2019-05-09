package com.luyinbros.pdf.cache;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Cache;

public class PdfCache {
    private DiskLruCache mDiskLruCache;

    public PdfCache(Application context) throws IOException {
        mDiskLruCache = DiskLruCache.open(new File(context.getCacheDir(), "PdfCache"), getVersionCode(context), 1, 100 * 1024 * 1024);
    }


    public synchronized File getCacheFile(String key) {
        String cacheKey = fileKey(key);
        try {
            DiskLruCache.Value value = mDiskLruCache.get(cacheKey);
            if (value != null) {
                return value.getFile(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public interface OnSaveListener {
        void onProgress(int progress);

    }

    public synchronized File saveDiskCache(String key, InputStream inputStream, final OnSaveListener onSaveListener, long totalSize) throws IOException {
        OutputStream out = null;

        try {
            String fileKey = fileKey(key);
            DiskLruCache.Editor editor = mDiskLruCache.edit(fileKey);
            if (editor != null) {
                File temp = editor.getFile(0);
                if (!temp.exists()) {
                    boolean create = temp.createNewFile();
                    if (!create) {
                        return null;
                    }
                }
                out = new BufferedOutputStream(new FileOutputStream(temp));
                int b;

                byte[] cache=new byte[8192];
                while ((b = inputStream.read(cache)) != -1) {
                    out.write(b);
                }
                try {
                    editor.commit();
                } finally {
                    editor.abortUnlessCommitted();
                }

                return temp;
            }
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

            } catch (final IOException e) {
                //empty
            }
        }

    }


    private String fileKey(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private int getVersionCode(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                return pi.versionCode;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

}
