package org.luyinbros.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.recyclerview.CellHolder;
import org.luyinbros.widget.recyclerview.ViewCellAdapter;
import org.luyinbros.widget.refresh.RxRefreshListDelegate;
import org.luyinbros.widget.refresh.UniverseRefreshRecyclerView;
import org.luyinbros.widget.refresh.update.SingleRefreshListCell;
import org.luyinbros.widget.refresh.update.SingleRefreshListUpdater;
import org.luyinbros.widget.status.StatusViewFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RefreshListActivity extends AppCompatActivity {
    private UI mUI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUI = new UI(this);
        setContentView(mUI);
        mUI.setRefreshListObservable(new RxRefreshListDelegate.RefreshObservable() {
            @Override
            public Disposable onPageRefresh() {
                return getStringListObservable(mUI.getRefreshListUpdater().getInitPage(),
                        mUI.getRefreshListUpdater().getPageSize())
                        .subscribe(new Consumer<List<String>>() {
                            @Override
                            public void accept(List<String> strings) throws Exception {
                                mUI.getRefreshListUpdater().updatePageRefreshSuccess(strings);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mUI.getRefreshListUpdater().notifyPageRefreshFailure();
                            }
                        });
            }

            @Override
            public Disposable onPullUpRefresh() {
                return getStringListObservable(mUI.getRefreshListUpdater().getNextPage(),
                        mUI.getRefreshListUpdater().getPageSize())
                        .subscribe(new Consumer<List<String>>() {
                            @Override
                            public void accept(List<String> strings) throws Exception {
                                mUI.getRefreshListUpdater().updatePullUpRefreshSuccess(strings);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mUI.getRefreshListUpdater().notifyPullUpRefreshFailure();
                            }
                        });
            }

            @Override
            public Disposable onPullDownRefresh() {
                return getStringListObservable(mUI.getRefreshListUpdater().getInitPage(),
                        mUI.getRefreshListUpdater().getPageSize())
                        .subscribe(new Consumer<List<String>>() {
                            @Override
                            public void accept(List<String> strings) throws Exception {
                                mUI.getRefreshListUpdater().updatePullDownRefreshSuccess(strings);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mUI.getRefreshListUpdater().notifyPullDownRefreshFailure();
                            }
                        });
            }

            @Override
            public boolean isLoadMoreVisible() {
                return mUI.getRefreshListUpdater().getDataSize() > 0;
            }
        });
        mUI.mRefreshListDelegate.showRefreshPage();
    }

    final int maxPage = 4;

    private Observable<List<String>> getStringListObservable(final int page, final int pageSize) {
        if (page <= maxPage) {
            return Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .flatMap(new Function<Long, ObservableSource<List<String>>>() {
                        @Override
                        public ObservableSource<List<String>> apply(Long aLong) throws Exception {
                            List<String> list = new ArrayList<>();
                            for (int i = 0; i < pageSize; i++) {
                                list.add("page:" + page + " " + "childIndex" + i);
                            }
                            return Observable.just(list);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .flatMap(new Function<Long, ObservableSource<List<String>>>() {
                        @Override
                        public ObservableSource<List<String>> apply(Long aLong) throws Exception {
                            List<String> list = new ArrayList<>();
                            return Observable.just(list);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    private static class UI extends LinearLayout {
        private UniverseRefreshRecyclerView mRecyclerView;
        private SingleCell singleCell;
        private RxRefreshListDelegate mRefreshListDelegate;

        public UI(Context context) {
            super(context);
            inflate(context, R.layout.activity_refresh_list, this);
            mRecyclerView = findViewById(R.id.mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRefreshListDelegate = new RxRefreshListDelegate(mRecyclerView);
            mRefreshListDelegate.setAllowLoadMore(true);
            mRefreshListDelegate.addStatusPage(StatusViewFactory.defaultEmptyPage(context));
            mRefreshListDelegate.addStatusPage(StatusViewFactory.defaultFailurePage(context));
            mRefreshListDelegate.addStatusPage(StatusViewFactory.defaultRefreshPage(context));
            singleCell = new SingleCell(mRefreshListDelegate);
            ViewCellAdapter viewCellAdapter = new ViewCellAdapter(context);
            viewCellAdapter.addCell(singleCell);
            mRecyclerView.setAdapter(viewCellAdapter);

        }

        public SingleRefreshListUpdater<String> getRefreshListUpdater() {
            return singleCell;
        }

        public void setRefreshListObservable(RxRefreshListDelegate.RefreshObservable observer) {
            mRefreshListDelegate.setRefreshObservable(observer);
        }

        private static class SingleCell extends SingleRefreshListCell<String, ViewHolder> {

            private SingleCell(RxRefreshListDelegate refreshListDelegate) {
                super(refreshListDelegate);
            }

            @Override
            public ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return new ViewHolder(new TextView(inflater.getContext()));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.textView.setText(getDataItem(position));
            }

        }

        private static class ViewHolder extends CellHolder {
            private TextView textView;

            public ViewHolder(TextView textView) {
                super(textView);
                this.textView=textView;
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                textView.setGravity(Gravity.CENTER);
            }
        }
    }
}
