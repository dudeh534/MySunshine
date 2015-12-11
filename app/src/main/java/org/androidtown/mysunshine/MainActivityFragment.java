package org.androidtown.mysunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private PagerAdapter adapter;

    public MainActivityFragment() {
    }
//       Layout을 inflater을하여 View작업을 하는 곳.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    /*Fragement에서 리소스의 뷰들을 인플레이션하는 방법은 2가지가 있다.
     1.View rootView를 선언해서 이 안에 해당 프레그먼트의 리소스파일을 인플레이션해서
      그 안에 있는 뷰들을 rootView.findViewById이러한 형식으로 인플레이션 하던지
     2. getView().findViewById를 통해서 인플레이션 하던지*/
//   Fragment에서는 this대신에 getActivity를 통해 해당 Activity를 넘겨준다
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);
        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        adapter = new org.androidtown.mysunshine.PagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

        return rootView;
    }



}
