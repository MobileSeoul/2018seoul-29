package com.jotjjang.kccistc.seoulappv7;

public class StatisticsItem {
    private int viewCount;
    private int commentCount;

    public StatisticsItem(int viewCount, int commentCount) {
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
}
