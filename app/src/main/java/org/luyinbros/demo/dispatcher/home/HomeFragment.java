package org.luyinbros.demo.dispatcher.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.luyinbros.fragment.ArrayFragmentManager;
import org.luyinbros.widget.R;
import org.luyinbros.widget.Toasts;

public class HomeFragment extends Fragment {
    private View tab1View;
    private View tab2View;
    private View tab3View;
    private ArrayFragmentManager arrayFragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayFragmentManager = new ArrayFragmentManager(getChildFragmentManager(), R.id.contentContainer);
        arrayFragmentManager.registerFragment(HomeIndex1Fragment.class);
        arrayFragmentManager.registerFragment(HomeIndex2Fragment.class);
        arrayFragmentManager.registerFragment(HomeIndex3Fragment.class);
        tab1View = view.findViewById(R.id.index1TabView);
        tab2View = view.findViewById(R.id.index2TabView);
        tab3View = view.findViewById(R.id.index3TabView);
        if (savedInstanceState == null) {
            arrayFragmentManager.showFragment(0);
        }
        Toasts.show(getContext(), "show Index:" + arrayFragmentManager.getCurrentIndex());
        tab1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayFragmentManager.showFragment(0);
            }
        });
        tab2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayFragmentManager.showFragment(1);
            }
        });
        tab3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayFragmentManager.showFragment(2);
            }
        });
    }

}
