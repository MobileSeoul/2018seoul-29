package com.jotjjang.kccistc.seoulappv7;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.util.ArrayList;

public class ExpandableCommentFragment extends Fragment {
    private final static ArrayList<CommentEntry> COMMENT_LIST = new ArrayList<>();
    private final static ArrayList<ArrayList<CommentEntry>> REPLY_LIST = new ArrayList<>();
    private ExpandableCommentAdapter adapter;
    private ExpandableListView expandableListView;
    private VideoFragment videoFragment;
    private ProgressBar progressBar;

    private boolean isScrollEnd;
    private boolean isLoading;

    public static String nextCommentToken;
    public static String nextReplyToken;
    private ImageView noMoreImageView;

    public static int clickedIndex = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExpandableCommentAdapter(getActivity(), COMMENT_LIST, REPLY_LIST);
        isLoading = false;
        isScrollEnd = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoFragment = (VideoFragment) getActivity().getFragmentManager().findFragmentById(R.id.video_fragment);
        progressBar = getActivity().findViewById(R.id.loading_progress_bar);
        noMoreImageView = new ImageView(getActivity());
        noMoreImageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        noMoreImageView.setAdjustViewBounds(true);
        noMoreImageView.setImageResource(R.drawable.no_more_comment_l);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comment,container,false);
        expandableListView = rootView.findViewById(R.id.expandable_list_view);
        //expandableListView.setItemsCanFocus(false);
        //expandableListView.setClickable(false);
        //expandableListView.setSelector(R.drawable.empty_selector);
        //expandableListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        //expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isScrollEnd && isLoading == false) {
                    // 화면이 바닦에 닿을때 처리
                    // 로딩중을 알리는 프로그레스바를 보인다.
                    if(isLoading == false) {
                        isLoading = true;
                        new CommentNextTask().execute();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isScrollEnd = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(COMMENT_LIST.get(groupPosition).getTotalReplyCount() > 5) {
                    clickedIndex = groupPosition;
                    new ReplyTask().execute();
                }
                return false;
            }
        });

        return rootView;
    }

    public void addCommentItem(CommentEntry commentEntry) {
        COMMENT_LIST.add(commentEntry);
        REPLY_LIST.add(commentEntry.getRepliesList());
        adapter.notifyDataSetChanged();
    }

    public void addCommentItemList(ArrayList<CommentEntry> commentEntries) {
        for(int i = 0; i < commentEntries.size(); i++) {
            COMMENT_LIST.add(commentEntries.get(i));
            REPLY_LIST.add(commentEntries.get(i).getRepliesList());
        }
        adapter.notifyDataSetChanged();
    }

    public void clearCommentEntries() {
        COMMENT_LIST.clear();
        REPLY_LIST.clear();
        adapter.notifyDataSetChanged();
    }

    public void addFooterNoMore() {
        expandableListView.addFooterView(noMoreImageView);
    }

    public void removeFooter() {
        expandableListView.removeFooterView(noMoreImageView);
    }

    public ExpandableListView getExpandableListView(){
        return expandableListView;
    }


    private class CommentNextTask extends AsyncTask<Void, Void, Void> {
        ArrayList<CommentEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                resultList = MyJsonParser.parseYoutubeComments(
                        MyJsonParser.getYoutubeComments(
                                videoFragment.getVideoId(), nextCommentToken,10));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(expandableListView.getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                addFooterNoMore();
            } else if(expandableListView.getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                removeFooter();
            }
            progressBar.setVisibility(View.GONE);
            addCommentItemList(resultList);
            isLoading = false;
        }
    }

    private class ReplyTask extends AsyncTask<Void, Void, Void> {
        ArrayList<CommentEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                resultList = MyJsonParser.parseYoutubeReplies(
                        MyJsonParser.getYoutubeReply(
                                COMMENT_LIST.get(clickedIndex).getCommentId(), 50));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            ArrayList<CommentEntry> replyList = REPLY_LIST.get(clickedIndex);
            replyList.clear();
            for(int i = 0; i < resultList.size(); i++) {
                replyList.add(resultList.get(i));
            }
            adapter.notifyDataSetChanged();
            isLoading = false;
        }
    }
}
