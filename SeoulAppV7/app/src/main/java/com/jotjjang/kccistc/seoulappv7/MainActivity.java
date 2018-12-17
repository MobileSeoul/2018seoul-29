package com.jotjjang.kccistc.seoulappv7;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
                    ,AbsListView.OnScrollListener {

    // 유튜브 플레이어가 밑에서 위로 슬라이딩 되는 시간 조절 상수 입니다
    public static final int ANIMATION_DURATION_MILLIS = 300;
    /** The request code when calling startActivityForResult to recover from an API service error. */
    public static final int RECOVERY_DIALOG_REQUEST = 1;

    public final int CATEGORY_ID_SPORTS = 17;
    public final int CATEGORY_ID_ANIMAL = 15;
    public final int CATEGORY_ID_MUSIC = 10;
    public final int CATEGORY_ID_GAME = 20;
    public final int CATEGORY_ID_ENTERTAINMENT = 24;

    public static String mostPopularPageToken = null;
    public static int category_state = 17;

    //layout contents FOR activity, youtube
    private VideoListFragment videoListFragment;
    private VideoFragment videoFragment;
    private ExpandableCommentFragment commentFragment;
    private View mainContainer;
    private View videoContainer;
    private View listContainer;
    private View closeButton;
    private ImageButton commentButton;
    private TextView commentTextView;
    private TextView titleTextView;

    //progressbar, loading, scroll contents
    private ProgressBar loadingProgressBar;
    private boolean isScrollEnd;

    //data request contents
    AsyncTask<?,?,?> asyncTask;
    private boolean isLoading;

    ArrayList<VideoEntry> hotclip;
    HashMap<String,String> mapKeyword;

    //comment fragment part
    private boolean isCommentOpen;
    private boolean isTransacting;

    private MenuItem navFirstItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //layout contents FOR activity, youtube
        videoFragment = (VideoFragment)getFragmentManager().findFragmentById(R.id.video_fragment);
        videoListFragment = (VideoListFragment)getFragmentManager().findFragmentById(R.id.list_fragment);
        commentFragment = (ExpandableCommentFragment) getFragmentManager().findFragmentById(R.id.comment_fragment);
        getFragmentManager().beginTransaction().hide(commentFragment).commit();
        mainContainer = findViewById(R.id.main_container);
        videoContainer = findViewById(R.id.video_container);
        listContainer = findViewById(R.id.list_container);
        closeButton = findViewById(R.id.close_button);
        commentButton = findViewById(R.id.comment_button);
        commentTextView = findViewById(R.id.comment_textView);
        titleTextView = findViewById(R.id.app_bar_title);
        //videoContainer.setVisibility(View.GONE);

        //progressbar, loading, scroll contents
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        //loadingProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable));
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF4081, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingProgressBar.setVisibility(View.GONE);

        isScrollEnd = false;
        videoListFragment.getListView().setOnScrollListener(this);

        //data request contents
        isLoading = false;
        isCommentOpen = false;
        isTransacting = false;

        SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_HOTCLIP);

        Intent intent = getIntent();
        SearchOptionState.setKeyWordMap((HashMap<String, String>) intent.getSerializableExtra("keyword"));
        hotclip = (ArrayList<VideoEntry>) intent.getSerializableExtra("hotclip");
        videoListFragment.clearVideoEntries();
        videoListFragment.addVideoEntries(hotclip);
        videoFragment.cueVideo(hotclip.get(0).getVideoId());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navFirstItem = navigationView.getMenu().findItem(R.id.nav_hot_clip);
        navFirstItem.setChecked(true);

        commentTextView.setText(hotclip.get(0).getCommentCount()+" 개의 댓글");

        checkYouTubeApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SearchOptionState.setDateTimeForRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Recreate the activity if user performed a recovery action
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (videoFragment.getIsFullScreen()) {
            videoFragment.setFullscreen(false);
        } else if (isCommentOpen == true) {
            if(isTransacting == false) {
                isTransacting = true;
                commentButton.setEnabled(false);
                FragmentManager fragmentManager = getFragmentManager();
                isCommentOpen = false;
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_left_comment, R.animator.slide_right_comment
                        , R.animator.slide_left_comment, R.animator.slide_right_comment)
                        .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_right_list, R.animator.slide_left_list
                        , R.animator.slide_right_list, R.animator.slide_left_list)
                        .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        commentFragment.removeFooter();
                        commentFragment.clearCommentEntries();
                        commentButton.setEnabled(true);
                        isTransacting = false;
                    }
                }, 450);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(SearchOptionState.getTopicState() == SearchOptionState.TopicState.TOPIC_STATE_HOTCLIP) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        } else if(SearchOptionState.getTopicState() == SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        } else {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(setOptionState(item) == true)
        {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean setOptionState(MenuItem item) {
        if(item.isChecked() == false) {
            item.setChecked(true);
            if (item.getGroupId() == R.id.setting_group_date) {
                switch (item.getItemId())
                {
                    case R.id.setting_today:
                        SearchOptionState.setDateState(SearchOptionState.DateState.DATE_STATE_TODAY);
                        break;
                    case R.id.setting_week:
                        SearchOptionState.setDateState(SearchOptionState.DateState.DATE_STATE_WEEK);
                        break;
                    case R.id.setting_month:
                        SearchOptionState.setDateState(SearchOptionState.DateState.DATE_STATE_MONTH);
                        break;
                    case R.id.setting_year:
                        SearchOptionState.setDateState(SearchOptionState.DateState.DATE_STATE_YEAR);
                        break;
                    case R.id.setting_all:
                        SearchOptionState.setDateState(SearchOptionState.DateState.DATE_STATE_ALL);
                        break;
                }
                if(isLoading == false) {
                    isLoading = true;
                    asyncTask = new RequestTask().execute();
                    if (isCommentOpen == true) {
                        if(isTransacting == false) {
                            isTransacting = true;
                            commentButton.setEnabled(false);
                            FragmentManager fragmentManager = getFragmentManager();
                            isCommentOpen = false;
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.animator.slide_left_comment, R.animator.slide_right_comment
                                    , R.animator.slide_left_comment, R.animator.slide_right_comment)
                                    .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.animator.slide_right_list, R.animator.slide_left_list
                                    , R.animator.slide_right_list, R.animator.slide_left_list)
                                    .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    commentFragment.removeFooter();
                                    commentFragment.clearCommentEntries();
                                    commentButton.setEnabled(true);
                                    isTransacting = false;
                                }
                            }, 450);
                        }
                    }
                }
                return true;
            } else if (item.getGroupId() == R.id.setting_group_order) {
                switch (item.getItemId())
                {
                    case R.id.setting_relevance:
                        SearchOptionState.setOrderState(SearchOptionState.OrderState.ORDER_STATE_RELEVANCE);
                        break;
                    case R.id.setting_viewCount:
                        SearchOptionState.setOrderState(SearchOptionState.OrderState.ORDER_STATE_VIEWCOUNT);
                        break;
                    case R.id.setting_rating:
                        SearchOptionState.setOrderState(SearchOptionState.OrderState.ORDER_STATE_RATING);
                        break;
                }
                if(isLoading == false) {
                    isLoading = true;
                    asyncTask = new RequestTask().execute();
                    if (isCommentOpen == true) {
                        if(isTransacting == false) {
                            isTransacting = true;
                            commentButton.setEnabled(false);
                            FragmentManager fragmentManager = getFragmentManager();
                            isCommentOpen = false;
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.animator.slide_left_comment, R.animator.slide_right_comment
                                    , R.animator.slide_left_comment, R.animator.slide_right_comment)
                                    .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.animator.slide_right_list, R.animator.slide_left_list
                                    , R.animator.slide_right_list, R.animator.slide_left_list)
                                    .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    commentFragment.removeFooter();
                                    commentFragment.clearCommentEntries();
                                    commentButton.setEnabled(true);
                                    isTransacting = false;
                                }
                            }, 450);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(navFirstItem.isChecked()==true) navFirstItem.setChecked(false);

        if (id == R.id.nav_hot_clip) {
            if(isLoading == false)
            {
                isLoading = true;
                item.setChecked(true);
                videoListFragment.scrollToTop();
                loadingProgressBar.setVisibility(View.VISIBLE);
                SearchOptionState.setInitJotJJangIndex();
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_HOTCLIP);
                videoListFragment.setUnFocusCheckdItem();
                videoListFragment.clearVideoEntries();
                videoListFragment.addVideoEntries(hotclip);
                loadingProgressBar.setVisibility(View.GONE);
                titleTextView.setText(R.string.category_name_1);
                isLoading = false;
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_seoul_news) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_2);
                item.setChecked(true);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_SEOUL_NEWS);
                asyncTask = new RequestTask().execute();
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_seoul_festival) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_3);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_SEOUL_FESTIVAL);
                asyncTask = new RequestTask().execute();
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_seoul_food) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_4);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_SEOUL_FOOD);
                asyncTask = new RequestTask().execute();
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_news) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_5);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_NEWS);
                asyncTask = new RequestTask().execute();
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_humor) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_6);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_HUMOR);
                asyncTask = new RequestTask().execute();
            } else
            {
                item.setChecked(false);
            }
        } else if (id == R.id.nav_kpop) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_7);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR);
                category_state = CATEGORY_ID_MUSIC;
                asyncTask = new MostPopularTask().execute();
            } else item.setChecked(false);
        } else if (id == R.id.nav_sports) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_8);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR);
                category_state = CATEGORY_ID_SPORTS;
                asyncTask = new MostPopularTask().execute();
            } else item.setChecked(false);
        } else if (id == R.id.nav_animal) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_9);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR);
                category_state = CATEGORY_ID_ANIMAL;
                asyncTask = new MostPopularTask().execute();
            } else item.setChecked(false);
        } else if (id == R.id.nav_entertainment) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_10);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR);
                category_state = CATEGORY_ID_ENTERTAINMENT;
                asyncTask = new MostPopularTask().execute();
            } else item.setChecked(false);
        } else if (id == R.id.nav_game) {
            if(isLoading == false) {
                isLoading = true;
                titleTextView.setText(R.string.category_name_11);
                SearchOptionState.setTopicState(SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR);
                category_state = CATEGORY_ID_GAME;
                asyncTask = new MostPopularTask().execute();
            } else item.setChecked(false);
        }

        if(isTransacting == false) {
            isTransacting = true;
            commentButton.setEnabled(false);
            if (isCommentOpen == true) {
                FragmentManager fragmentManager = getFragmentManager();
                isCommentOpen = false;
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_left_comment, R.animator.slide_right_comment
                        , R.animator.slide_left_comment, R.animator.slide_right_comment)
                        .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_right_list, R.animator.slide_left_list
                        , R.animator.slide_right_list, R.animator.slide_left_list)
                        .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commentFragment.removeFooter();
                        commentFragment.clearCommentEntries();
                        commentButton.setEnabled(true);
                        isTransacting = false;
                    }
                }, 450);
            } else {
                commentButton.setEnabled(true);
                isTransacting = false;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isScrollEnd && isLoading == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            if(isLoading == false && SearchOptionState.getTopicState() == SearchOptionState.TopicState.TOPIC_STATE_HOTCLIP) {
                isLoading = true;
                asyncTask = new HotClipNextTask().execute();
            } else if (isLoading == false && SearchOptionState.getTopicState() == SearchOptionState.TopicState.TOPIC_STATE_MOST_POPULAR) {
                isLoading = true;
                asyncTask = new MostPopularNextTask().execute();
            } else if (isLoading == false && SearchOptionState.getTopicState() != SearchOptionState.TopicState.TOPIC_STATE_HOTCLIP) {
                isLoading = true;
                asyncTask = new NextTask().execute();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isScrollEnd = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    // 비디오 플레이어 닫기 버튼 누를시 호출 됩니다
    public void onClickClose(@SuppressWarnings("unused") View view) {
        if(isCommentOpen == false) {
            videoListFragment.getListView().clearChoices();
            videoFragment.pauseVideo();
            ViewPropertyAnimator animator = videoContainer.animate()
                    .translationXBy(videoContainer.getWidth())
                    .setDuration(ANIMATION_DURATION_MILLIS);
            runOnAnimationEnd(animator, new Runnable() {
                @Override
                public void run() {
                    videoContainer.setVisibility(View.GONE);
                }
            });
        } else
        {
            if(isTransacting == false) {
                isTransacting = true;
                commentButton.setEnabled(false);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_left_comment, R.animator.slide_right_comment
                        , R.animator.slide_left_comment, R.animator.slide_right_comment)
                        .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_right_list, R.animator.slide_left_list
                        , R.animator.slide_right_list, R.animator.slide_left_list)
                        .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                videoListFragment.getListView().clearChoices();
                videoFragment.pauseVideo();
                ViewPropertyAnimator animator = videoContainer.animate()
                        .translationXBy(videoContainer.getWidth())
                        .setDuration(ANIMATION_DURATION_MILLIS);
                runOnAnimationEnd(animator, new Runnable() {
                    @Override
                    public void run() {
                        videoContainer.setVisibility(View.GONE);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commentFragment.removeFooter();
                        commentFragment.clearCommentEntries();
                        commentButton.setEnabled(true);
                        isCommentOpen = false;
                        isTransacting = false;
                    }
                }, 450);
            }
        }
    }

    public void onClickComment(View view) {
        if(isTransacting == false) {
            isTransacting = true;
            commentButton.setEnabled(false);
            FragmentManager fragmentManager = getFragmentManager();
            if (isCommentOpen == false) {
                if(videoFragment.getVideoId() != null) {
                    isCommentOpen = true;
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.animator.slide_left_comment, R.animator.slide_right_comment
                            , R.animator.slide_left_comment, R.animator.slide_right_comment)
                            .show(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.animator.slide_right_list, R.animator.slide_left_list
                            , R.animator.slide_right_list, R.animator.slide_left_list)
                            .hide(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                    asyncTask = new CommentTask().execute();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            commentButton.setEnabled(true);
                            isTransacting = false;
                        }
                    }, 450);
                }
            } else {
                isCommentOpen = false;
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_left_comment, R.animator.slide_right_comment
                        , R.animator.slide_left_comment, R.animator.slide_right_comment)
                        .hide(fragmentManager.findFragmentById(R.id.comment_fragment)).commit();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.slide_right_list, R.animator.slide_left_list
                        , R.animator.slide_right_list, R.animator.slide_left_list)
                        .show(fragmentManager.findFragmentById(R.id.list_fragment)).commit();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        commentFragment.removeFooter();
                        commentFragment.clearCommentEntries();
                        commentButton.setEnabled(true);
                        isTransacting = false;
                    }
                }, 450);
            }
        }
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    // Utility methods for layouting.
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        //params.gravity = gravity;
        view.setLayoutParams(params);
    }

    //유튜브 api 상태 체크
    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private class RequestTask extends AsyncTask<Void, Void, Void> {
        ArrayList<VideoEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                 MyJsonParser.parseJsonData(resultList,
                        MyJsonParser.getYoutubeData(
                                SearchOptionState.getTopicStateString()
                                , SearchOptionState.getBeforeDateStateString()
                                , SearchOptionState.getAfterDateStateString()
                                , SearchOptionState.getOrderStateString()
                                , 10));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<String> idList = new ArrayList<>();
            for(int i = 0; i < resultList.size(); i++) {
                idList.add(resultList.get(i).getVideoId());
            }

            HashMap<String,StatisticsItem> statisticsItemHashMap = new HashMap<>();
            try {
                statisticsItemHashMap = MyJsonParser.parseYoutubeStatistics(MyJsonParser.getYoutubeStatistics(idList));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < resultList.size(); i++) {
                VideoEntry videoEntry = resultList.get(i);
                videoEntry.setViewCount(statisticsItemHashMap.get(videoEntry.getVideoId()).getViewCount());
                videoEntry.setCommentCount(statisticsItemHashMap.get(videoEntry.getVideoId()).getCommentCount());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(videoListFragment.getListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                videoListFragment.addFooterNoMore();
            } else if(videoListFragment.getListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                videoListFragment.removeFooter();
            }
            videoListFragment.setUnFocusCheckdItem();
            videoListFragment.clearVideoEntries();
            videoListFragment.addVideoEntries(resultList);
            videoListFragment.scrollToTop();
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }

    private class NextTask extends AsyncTask<Void, Void, Void> {
        ArrayList<VideoEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                MyJsonParser.parseJsonData(resultList,
                        MyJsonParser.getYoutubeData(
                                SearchOptionState.getTopicStateString()
                                , SearchOptionState.getBeforeDateStateString()
                                , SearchOptionState.getAfterDateStateString()
                                , SearchOptionState.getOrderStateString()
                                , 5, SearchOptionState.getNextPageToken()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<String> idList = new ArrayList<>();
            for(int i = 0; i < resultList.size(); i++) {
                idList.add(resultList.get(i).getVideoId());
            }

            HashMap<String,StatisticsItem> statisticsItemHashMap = new HashMap<>();
            try {
                statisticsItemHashMap = MyJsonParser.parseYoutubeStatistics(MyJsonParser.getYoutubeStatistics(idList));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < resultList.size(); i++) {
                VideoEntry videoEntry = resultList.get(i);
                videoEntry.setViewCount(statisticsItemHashMap.get(videoEntry.getVideoId()).getViewCount());
                videoEntry.setCommentCount(statisticsItemHashMap.get(videoEntry.getVideoId()).getCommentCount());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(videoListFragment.getListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                videoListFragment.addFooterNoMore();
            } else if(videoListFragment.getListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                videoListFragment.removeFooter();
            }
            videoListFragment.addVideoEntries(resultList);
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }

    private class HotClipNextTask extends AsyncTask<Void, Void, Void> {
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<VideoEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = MyJsonParser.getJotJJangNext(SearchOptionState.getNextJotJJangIndex());
            try {
                MyJsonParser.parseHotClip(idList, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                resultList = MyJsonParser.parseYoutubeItems(MyJsonParser.getYoutubeItems(idList));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(videoListFragment.getListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                videoListFragment.addFooterNoMore();
            } else if(videoListFragment.getListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                videoListFragment.removeFooter();
            }
            videoListFragment.addVideoEntries(resultList);
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }

    private class CommentTask extends AsyncTask<Void, Void, Void> {
        ArrayList<CommentEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                resultList = MyJsonParser.parseYoutubeComments(
                        MyJsonParser.getYoutubeComments(videoFragment.getVideoId(),10));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(commentFragment.getExpandableListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                commentFragment.addFooterNoMore();
            } else if(commentFragment.getExpandableListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                commentFragment.removeFooter();
            }

            loadingProgressBar.setVisibility(View.GONE);
            commentFragment.addCommentItemList(resultList);
        }
    }

    private class MostPopularTask extends AsyncTask<Void, Void, Void> {
        ArrayList<VideoEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                resultList = MyJsonParser.parseYoutubeMostPopular(
                        MyJsonParser.getYoutubeMostPopular(category_state,10));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(videoListFragment.getListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                videoListFragment.addFooterNoMore();
            } else if(videoListFragment.getListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                videoListFragment.removeFooter();
            }
            videoListFragment.setUnFocusCheckdItem();
            videoListFragment.clearVideoEntries();
            videoListFragment.addVideoEntries(resultList);
            videoListFragment.scrollToTop();
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }

    private class MostPopularNextTask extends AsyncTask<Void, Void, Void> {
        ArrayList<VideoEntry> resultList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                resultList = MyJsonParser.parseYoutubeMostPopular(
                        MyJsonParser.getYoutubeMostPopular(category_state, MainActivity.mostPopularPageToken, 5));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(videoListFragment.getListView().getFooterViewsCount() == 0 && resultList.size() == 0)
            {
                videoListFragment.addFooterNoMore();
            } else if(videoListFragment.getListView().getFooterViewsCount() > 0 && resultList.size() > 0)
            {
                videoListFragment.removeFooter();
            }
            videoListFragment.addVideoEntries(resultList);
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
        }
    }
}
