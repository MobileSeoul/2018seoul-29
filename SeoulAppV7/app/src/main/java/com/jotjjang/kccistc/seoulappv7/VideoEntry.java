package com.jotjjang.kccistc.seoulappv7;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VideoEntry implements Serializable{
    private final String title;
    private final String videoId;
    //private final String etag;
    //private final String channelId;
    private final String publishedDate;
    private final String description;
    private final String channelTitle;
    private final String thumbnailUrl_default;
    //private final String thumbnailUrl_medium;
    //private final String thumbnailUrl_high;
    //private final String liveBroadcastContent;
    private int viewCount;
    private int commentCount;
    private String viewCountString;
    private String publishedDateString;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Date currentDate = SearchOptionState.getCurrentDate();
    private long curLong = System.currentTimeMillis();

    public VideoEntry(String title, String videoId, String publishedDate,
                      String description, String channelTitle, String thumbnailUrl_default)
    {
        this.title = title;
        this.videoId = videoId;
        this.publishedDate = publishedDate;
        this.description = description;
        this.channelTitle = channelTitle;
        this.thumbnailUrl_default = thumbnailUrl_default;
        this.viewCount = 0;
        this.commentCount = 0;
        try {
            this.publishedDateString = convertToStringPublishedDate(publishedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

//    public VideoEntry(String title, String videoId, String publishedDate,
//                      String description, String channelTitle, String thumbnailUrl_default, int viewCount)
//    {
//        this.title = title;
//        this.videoId = videoId;
//        this.publishedDate = publishedDate;
//        this.description = description;
//        this.channelTitle = channelTitle;
//        this.thumbnailUrl_default = thumbnailUrl_default;
//        this.viewCount = viewCount;
//    }

    public VideoEntry(String title, String videoId, String publishedDate,
                      String description, String channelTitle, String thumbnailUrl_default, int viewCount, int commentCount)
    {
        this.title = title;
        this.videoId = videoId;
        this.publishedDate = publishedDate;
        this.description = description;
        this.channelTitle = channelTitle;
        this.thumbnailUrl_default = thumbnailUrl_default;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        try {
            this.publishedDateString = convertToStringPublishedDate(publishedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.viewCountString = convertToStringViewCount(viewCount);
    }

    public String getThumbnailUrl_default() {
        return thumbnailUrl_default;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public int getViewCount() { return viewCount; }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
        this.viewCountString = convertToStringViewCount(viewCount);
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getViewCountString() {
        return viewCountString;
    }

    public String getPublishedDateString() {
        return publishedDateString;
    }

    private String convertToStringPublishedDate(String dateString) throws ParseException {
        Date registDate = simpleDateFormat.parse(dateString);
        long regTime = registDate.getTime();
        long diffTime = (curLong - regTime) / 1000;

        String msg = "x";
        if(diffTime < TimeMaximum.SEC) {
            // sec
            msg = diffTime + "초전";
        } else if ((diffTime /= TimeMaximum.SEC) < TimeMaximum.MIN) {
            // min
            msg = diffTime + "분전";
        } else if ((diffTime /= TimeMaximum.MIN) < TimeMaximum.HOUR) {
            // hour
            msg = (diffTime ) + "시간전";
        } else if ((diffTime /= TimeMaximum.HOUR) < TimeMaximum.DAY) {
            // day
            msg = (diffTime ) + "일전";
        } else if ((diffTime /= TimeMaximum.DAY) < TimeMaximum.MONTH) {
            // day
            msg = (diffTime ) + "달전";
        } else {
            diffTime /= TimeMaximum.MONTH;
            msg = (diffTime) + "년전";
        }
        return msg;
    }

    private String convertToStringViewCount(int viewCount) {
        String msg = "x";
        if (viewCount <= 1000) {
            msg = viewCount +"";
        } else if (viewCount < 10000) {
            msg = (viewCount / 1000)+"천"+(viewCount%1000);
        } else if (viewCount < 1000000) {
            msg = (viewCount / 10000)+"만";
        } else if (viewCount < 10000000) {
            msg = (viewCount / 1000000)+"백만";
        } else if (viewCount < 100000000) {
            msg = (viewCount / 10000000)+"천만";
        } else {
            msg = (viewCount / 100000000)+"억";
        }
        return msg;
    }
}
