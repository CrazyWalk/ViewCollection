package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.list.AbstractSimpleListController;
import org.luyinbros.widget.list.ListController;

import java.util.ArrayList;
import java.util.List;

public class SimpleListViewActivity extends AppCompatActivity {
    private InnerController mGridListController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list_view);
        mGridListController = new InnerController((ViewGroup) findViewById(R.id.simpleGridLayout));
    }

    public void onSetInvalidated(View v) {
        mGridListController.textList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mGridListController.textList.add(i + "");
        }
        mGridListController.notifyDataSetInvalidated();
    }

    public void onChangedFirst(View v) {
        mGridListController.textList.set(0, "new 0");
        mGridListController.notifyItemChanged(0);
    }

    public void onChangedLast(View v) {
        mGridListController.textList.add(mGridListController.textList.size() + "");
        mGridListController.notifyItemChanged(mGridListController.textList.size()-1);
    }

    public void onLastInsert(View v) {
        mGridListController.textList.add(mGridListController.textList.size() + "");
        mGridListController.notifyItemInserted(mGridListController.textList.size()-1);
    }

    public void onDelete(View v) {
        mGridListController.textList.remove(2);
        mGridListController.notifyItemRemoved(2);
    }

    public void onInsert(View v) {
        mGridListController.textList.add(2, "new 2");
        mGridListController.notifyItemInserted(2);
    }

    private static class InnerController extends AbstractSimpleListController<ViewHolder> {
        private List<String> textList;

        public InnerController(@NonNull ViewGroup parent) {
            super(parent);
        }

        @NonNull
        @Override
        public SimpleListViewActivity.ViewHolder onCreateHolder(ViewGroup container, int viewType) {
            return new SimpleListViewActivity.ViewHolder(new TextView(container.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull SimpleListViewActivity.ViewHolder holder, int position) {
            holder.textView.setText(textList.get(position));
        }

        @Override
        public int getItemCount() {
            return textList != null ? textList.size() : 0;
        }
    }

    private static class ViewHolder extends ListController.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull TextView textView) {
            super(textView);
            this.textView=textView;
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
