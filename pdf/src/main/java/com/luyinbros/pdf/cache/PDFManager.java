package com.luyinbros.pdf.cache;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class PDFManager {
    private DiskLruCache mDiskLruCache;
    private static volatile SoftReference<OkHttpClient> clientSoftReference;
    private static volatile PDFManager INSTANCE;


    private PDFManager(Application context) {
        try {
            mDiskLruCache = DiskLruCache.open(new File(context.getCacheDir(), "PDFManager"), getVersionCode(context), 1, 100 * 1024 * 1024);
        } catch (IOException e) {
            //no
        }

    }

    public static PDFManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PDFManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PDFManager((Application) context.getApplicationContext());
                }
            }
        }
        return INSTANCE;

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

    public SaveSession openSession() {
        return new DefaultSaveSession(this);
    }

    public interface SaveSession {

        void setOnSaveListener(OnSaveListener onSaveListener);

        void start(String url);

        boolean isStart();

        void dispose();
    }


    public interface OnSaveListener {

        void onStart();

        void onProgress(int progress);

        void onSuccess(File file);

        void onFailure(Exception e);

    }

    private static class DefaultSaveSession implements SaveSession {

        private OnSaveListener mOnSaveListener;
        private PDFManager PDFManager;
        private Call mCall;
        private BufferedSource mBufferedSource;
        private BufferedSink mSink;
        private static final int START = 0;
        private static final int PROGRESS = 1;
        private static final int SUCCESS = 2;
        private static final int FAILURE = 3;
        @SuppressLint("HandlerLeak")
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START:
                        if (mOnSaveListener != null) {
                            mOnSaveListener.onStart();
                        }
                        break;
                    case PROGRESS:
                        if (mOnSaveListener != null) {
                            mOnSaveListener.onProgress(msg.arg1);
                        }
                        break;
                    case SUCCESS:
                        if (mOnSaveListener != null) {
                            mOnSaveListener.onSuccess((File) msg.obj);
                        }
                        break;
                    case FAILURE:
                        if (mOnSaveListener != null) {
                            mOnSaveListener.onFailure((Exception) msg.obj);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        private DefaultSaveSession(PDFManager PDFManager) {
            this.PDFManager = PDFManager;
        }

        @Override
        public void setOnSaveListener(OnSaveListener onSaveListener) {
            mOnSaveListener = onSaveListener;
        }

        @Override
        public void start(final String url) {
            try {
                final String fileKey = PDFManager.fileKey(url);
                final DiskLruCache.Editor editor = PDFManager.mDiskLruCache.edit(fileKey);
                if (editor != null) {
                    mHandler.sendEmptyMessage(START);
                    mCall = getClient().newCall(new Request.Builder().url(url).build());
                    mCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            sendErrorMessage(e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            ResponseBody responseBody = response.body();
                            if (responseBody == null) {
                                if (mOnSaveListener != null) {
                                    mOnSaveListener.onFailure(new Exception());
                                }
                            } else {
                                final File temp = editor.getFile(0);
                                if (!temp.exists()) {
                                    boolean create = temp.createNewFile();
                                    if (!create) {
                                        return;
                                    }
                                }
                                mSink = Okio.buffer(Okio.sink(temp));
                                Buffer buffer = mSink.buffer();
                                mBufferedSource = responseBody.source();
                                final long contentLength = responseBody.contentLength();
                                long total = 0;
                                long len;
                                int bufferSize = 200 * 1024; //200kb

                                while ((len = mBufferedSource.read(buffer, bufferSize)) != -1) {
                                    mSink.emit();
                                    total += len;
                                    if (contentLength > 1) {
                                        sendProgress((int) (100.0f * total / contentLength));
                                    } else {
                                        sendProgress(-1);
                                    }
                                }
                                mBufferedSource.close();
                                mSink.close();
                                mBufferedSource = null;
                                mSink = null;
                                mCall = null;
                                try {
                                    editor.commit();
                                } finally {
                                    editor.abortUnlessCommitted();
                                }
                                sendSuccessMessage(PDFManager.getCacheFile(url));
                            }

                        }
                    });


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void sendSuccessMessage(File file) {
            Message message = new Message();
            message.what = SUCCESS;
            message.obj = file;
            mHandler.sendMessage(message);
        }

        private void sendErrorMessage(Exception e) {
            Message message = new Message();
            message.what = FAILURE;
            message.obj = e;
            mHandler.sendMessage(message);
        }

        private void sendProgress(int progress) {
            Message message = new Message();
            message.what = PROGRESS;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }

        private void cancel() {
            if (mCall != null && !mCall.isCanceled()) {
                mCall.cancel();
            }
            if (mSink != null) {
                try {
                    mSink.close();
                } catch (IOException e) {

                    // e.printStackTrace();
                }
            }
            if (mBufferedSource != null) {
                try {
                    mBufferedSource.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }

        @Override
        public boolean isStart() {
            return mCall != null;
        }

        @Override
        public void dispose() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cancel();
                }
            }).start();

        }
    }

    @NonNull
    private static synchronized OkHttpClient getClient() {
        if (clientSoftReference != null) {
            OkHttpClient okHttpClient = clientSoftReference.get();
            if (okHttpClient != null) {
                return okHttpClient;
            }
        }
        OkHttpClient client = new OkHttpClient();
        clientSoftReference = new SoftReference<>(client);
        return client;
    }

}
