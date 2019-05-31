package org.luyinbros.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.luyinbros.widget.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    public void onSimpleShow1Click(View v) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("channel1", "channel1", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("1")
                    .setContentText("111")
                    .setGroup("group")
                    .setGroupSummary(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
            intentClick.setAction("notification_clicked");
            intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 0);
            intentClick.putExtra("MESSAGE", "消息");

            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
            //cancle广播监听
            Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
            intentCancel.setAction("notification_cancelled");
            intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 1);
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

            notification.contentIntent = pendingIntentClick;
            notification.deleteIntent = pendingIntentCancel;
        } else {
            notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("1")
                    .setContentText("111")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        }
        manager.notify(1, notification);
    }

    public void onSimpleShow2Click(View v) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("channel1", "channel1", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("1")
                    .setContentText("111")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setGroup("group")
                    .setGroupSummary(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("1")
                    .setContentText("111")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setGroup("1")
                    .build();
        }
        manager.notify(2, notification);
    }

    public void onSimpleShowCustomerClick(View v) {
        final NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Observable.create(new ObservableOnSubscribe<Bitmap>() {
                @Override
                public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                    emitter.onNext(Glide.with(getApplicationContext())
                            .asBitmap()
                            .load("http://pic18.nipic.com/20120204/8339340_144203764154_2.jpg")
                            .apply(new RequestOptions().override(50,50))
                            .submit()
                            .get());
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            NotificationChannel mChannel = new NotificationChannel("channel1", "channel1", NotificationManager.IMPORTANCE_LOW);
                            manager.createNotificationChannel(mChannel);

                            RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notification_with_image);
                            remoteViews.setImageViewBitmap(R.id.img,bitmap);
                            Notification notification1 = new NotificationCompat.Builder(NotificationActivity.this, "channel1")
                                    .setGroup("group")
                                    .setGroupSummary(true)
                                    .setWhen(System.currentTimeMillis())
                                    .setContent(remoteViews)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .build();
                            manager.notify(1, notification1);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        } else {
            notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("1")
                    .setContentText("111")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            manager.notify(1, notification);
        }

    }
}
