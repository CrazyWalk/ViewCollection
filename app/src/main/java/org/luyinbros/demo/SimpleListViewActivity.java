package org.luyinbros.demo;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.luyinbros.utils.Dimens;
import org.luyinbros.widget.R;
import org.luyinbros.widget.list.AbstractSimpleListController;
import org.luyinbros.widget.list.ListController;
import org.luyinbros.widget.list.SimpleGridLayout;

import java.util.ArrayList;
import java.util.List;

public class SimpleListViewActivity extends AppCompatActivity {
    private InnerController mGridListController;
    private SimpleGridLayout mSimpleGridLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list_view);
        mGridListController = new InnerController((ViewGroup) findViewById(R.id.simpleGridLayout));
        mSimpleGridLayout = findViewById(R.id.mSimpleGridLayout);

        ListController<InnerViewHolder> linearLayoutController = new AbstractSimpleListController<InnerViewHolder>((ViewGroup) findViewById(R.id.labelListView)) {
            @NonNull
            @Override
            public SimpleListViewActivity.InnerViewHolder onCreateHolder(ViewGroup container, int viewType) {
                TextView textView = new TextView(container.getContext());
                textView.setPadding(Dimens.px(container.getContext(), 6f),
                        Dimens.px(container.getContext(), 2f),
                        Dimens.px(container.getContext(), 6f),
                        Dimens.px(container.getContext(), 2f));
                textView.setTextSize(10f);
                textView.setTextColor(0xFFFFFFFF);
                textView.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                GradientDrawable backgroundView = new GradientDrawable();
                backgroundView.setColor(0X1a3d3b3b);
                float radius = Dimens.px(container.getContext(), 9f);
                backgroundView.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
                backgroundView.setStroke(Dimens.px(container.getContext(), 1f), 0xFFFFFFFF);
                textView.setBackground(backgroundView);
                return new SimpleListViewActivity.InnerViewHolder(textView);
            }

            @Override
            public void onBindViewHolder(@NonNull SimpleListViewActivity.InnerViewHolder holder, int position) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
                marginLayoutParams.setMarginEnd(10);
                holder.itemView.setLayoutParams(marginLayoutParams);
                holder.textView.setText("" + position);
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        };
         linearLayoutController.notifyDataSetInvalidated();

        ListController<ViewHolder> listController = new AbstractSimpleListController<ViewHolder>(mSimpleGridLayout) {
            private int selectedIndex = 0;

            @NonNull
            @Override
            public SimpleListViewActivity.ViewHolder onCreateHolder(ViewGroup container, int viewType) {
                TextView textView = new TextView(container.getContext());
                textView.setBackgroundColor(Color.YELLOW);
                textView.setPadding(0, 20, 0, 20);
                final SimpleListViewActivity.ViewHolder holder = new SimpleListViewActivity.ViewHolder(textView);

                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int oldIndex = selectedIndex;
                        selectedIndex = holder.getPosition();
                        if (oldIndex != -1) {
                            notifyItemChanged(oldIndex);
                        }
                        notifyItemChanged(selectedIndex);

                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull SimpleListViewActivity.ViewHolder holder, int position) {
                if (selectedIndex == position) {
                    holder.textView.setTextColor(Color.RED);
                } else {
                    holder.textView.setTextColor(Color.BLACK);
                }
                holder.textView.setText(position + "");
            }

            @Override
            public int getItemCount() {
                return 5;
            }
        };
        listController.notifyDataSetInvalidated();
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
        mGridListController.notifyItemChanged(mGridListController.textList.size() - 1);
    }

    public void onLastInsert(View v) {
        mGridListController.textList.add(mGridListController.textList.size() + "");
        mGridListController.notifyItemInserted(mGridListController.textList.size() - 1);
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
            this.textView = textView;
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private static class InnerViewHolder extends ListController.ViewHolder {
        private TextView textView;

        public InnerViewHolder(@NonNull TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }
}
