package com.jotjjang.kccistc.seoulappv7;

import android.app.ListFragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class VideoListFragment extends ListFragment {

    private final static ArrayList<VideoEntry> VIDEO_LIST = new ArrayList<>();

    private PageAdapter adapter;
    private View videoContainer;
    private View mainContainer;
    private ListView listView;
    private ImageView noMoreImageView;
    private TextView commentCountTextView;

    public void addVideoItem(VideoEntry videoEntry) {
        adapter.addVideoItem(videoEntry);
    }

    public void clearVideoEntries() {
        adapter.clearVideoEntries();
    }

    public void addVideoEntries(ArrayList<VideoEntry> list) {
        adapter.addVideoEntries(list);
    }

    public void setLabelVisibility(boolean visible) {
        adapter.setLabelVisibility(visible);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PageAdapter(getActivity(), VIDEO_LIST);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoContainer = getActivity().findViewById(R.id.video_container);
        mainContainer = getActivity().findViewById(R.id.main_container);
        listView = getListView();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        setListAdapter(adapter);
        //listView.setDrawSelectorOnTop(false);
        //listView.setCacheColorHint(Color.parseColor("#00000000"));
        //listView.setCacheColorHint(Color.TRANSPARENT);
        //listView.setScrollingCacheEnabled(false);
        listView.setItemChecked(0, true);
        listView.setSelector(R.drawable.empty_selector);
        //listView.setFocusable(true);
        //listView.setFocusableInTouchMode(true);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        noMoreImageView = new ImageView(getActivity());
        noMoreImageView.setImageResource(R.drawable.no_more_clip_l);
        noMoreImageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        noMoreImageView.setAdjustViewBounds(true);
        commentCountTextView = getActivity().findViewById(R.id.comment_textView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.releaseLoaders();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(position < VIDEO_LIST.size()) {
            String videoId = VIDEO_LIST.get(position).getVideoId();

            VideoFragment videoFragment =
                    (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment);

            // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
            if (videoContainer.getVisibility() != View.VISIBLE) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Initially translate off the screen so that it can be animated in from below.
                    videoContainer.setTranslationX(mainContainer.getWidth());
                }
                videoContainer.setVisibility(View.VISIBLE);
            }

            // If the fragment is off the screen, we animate it in.
            if (videoContainer.getTranslationX() > 0) {
                videoContainer.animate().translationX(0).setDuration(MainActivity.ANIMATION_DURATION_MILLIS);
            }
            videoFragment.loadVideo(videoId);
            commentCountTextView.setText(VIDEO_LIST.get(position).getCommentCount() + " 개의 댓글");
        }
    }

    public void scrollToTop() {
        listView.requestFocusFromTouch();
        listView.setSelection(0);
    }

    public void setUnFocusCheckdItem() {
        if (listView.getCheckedItemCount() > 0) {
            listView.setItemChecked(listView.getCheckedItemPosition(), false);
        }
    }

    public void addFooterNoMore() {
        listView.addFooterView(noMoreImageView);
    }

    public void removeFooter() {
        listView.removeFooterView(noMoreImageView);
    }

    private void setHighlight(int pos, boolean on) {
        int firstPos = listView.getFirstVisiblePosition();
        int wantedPos = pos-firstPos;
        if (wantedPos < 0 || wantedPos >= listView.getChildCount()) {
            return;
        }
        View childView = listView.getChildAt(wantedPos);
        if(childView == null) {
            return;
        }
        if(on) {
            childView.setBackgroundColor(Color.BLUE);
        } else {
            childView.setBackgroundColor(Color.WHITE);
        }
    }
}
