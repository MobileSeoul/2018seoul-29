package com.jotjjang.kccistc.seoulappv7;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SearchOptionState {

    public enum TopicState {
        TOPIC_STATE_SEOUL_NEWS,
        TOPIC_STATE_SEOUL_FESTIVAL,
        TOPIC_STATE_SEOUL_FOOD,
        TOPIC_STATE_HOTCLIP,
        TOPIC_STATE_NEWS,
        TOPIC_STATE_SPORTS,
        TOPIC_STATE_HUMOR,
        TOPIC_STATE_KPOP,
        TOPIC_STATE_ESPORTS,
        TOPIC_STATE_MOST_POPULAR
    }

    public enum DateState {
        DATE_STATE_TODAY,
        DATE_STATE_WEEK,
        DATE_STATE_MONTH,
        DATE_STATE_YEAR,
        DATE_STATE_ALL
    }
    public enum OrderState {
        ORDER_STATE_RELEVANCE,
        ORDER_STATE_VIEWCOUNT,
        ORDER_STATE_RATING
    }

    static  private SearchOptionState.TopicState topicState = TopicState.TOPIC_STATE_HOTCLIP;
    static private SearchOptionState.DateState dateState = DateState.DATE_STATE_TODAY;
    static private SearchOptionState.OrderState orderState = OrderState.ORDER_STATE_RELEVANCE;

    private static Date currentDate;
    private static String currentDateString;
    private static String aDayAgoDateString;
    private static String aWeekAgoDateString;
    private static String aMonthAgoDateString;
    private static String aYearAgoDateString;
    private static String longTimeAgoDateString;

    private static String nextPageToken;

    private static int nextJotJJangInex = 1;

    private static HashMap<String, String> keyWordMap = null;

    public static void setDateTimeForRequest()
    {
        long now = System.currentTimeMillis();
        currentDate = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        currentDateString = sdf.format(currentDate)+"Z";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.HOUR, -36);
        aDayAgoDateString = sdf.format(calendar.getTime())+"Z";
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -7);
        aWeekAgoDateString = sdf.format(calendar.getTime())+"Z";
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        aMonthAgoDateString = sdf.format(calendar.getTime())+"Z";
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, -1);
        aYearAgoDateString = sdf.format(calendar.getTime())+"Z";
        longTimeAgoDateString = "1970-01-01T00:00:00Z";
    }

    public static void setKeyWordMap(HashMap<String, String> map) {
        keyWordMap = map;
    }

    public static TopicState getTopicState() {
        return topicState;
    }

    public static void setTopicState(TopicState topicState) {
        SearchOptionState.topicState = topicState;
    }

    public static String getTopicStateString() {
        if (topicState == TopicState.TOPIC_STATE_HOTCLIP) {
            return "";
        } else if (topicState == TopicState.TOPIC_STATE_SEOUL_NEWS) {
            return keyWordMap.get("SEOUL_NEWS");
        } else if (topicState == TopicState.TOPIC_STATE_SEOUL_FESTIVAL) {
            return keyWordMap.get("SEOUL_FESTIVAL");
        } else if (topicState == TopicState.TOPIC_STATE_SEOUL_FOOD) {
            return keyWordMap.get("SEOUL_FOOD");
        } else if (topicState == TopicState.TOPIC_STATE_NEWS) {
            return keyWordMap.get("NEWS");
        } else if (topicState == TopicState.TOPIC_STATE_SPORTS) {
            return keyWordMap.get("SPORTS");
        } else if (topicState == TopicState.TOPIC_STATE_HUMOR) {
            return keyWordMap.get("HUMOR");
        } else if (topicState == TopicState.TOPIC_STATE_KPOP) {
            return keyWordMap.get("KPOP");
        }else if (topicState == TopicState.TOPIC_STATE_ESPORTS) {
            return keyWordMap.get("ESPORTS");
        }else{
            return "";
        }
    }

    public static DateState getDateState() {
        return dateState;
    }

    public static void setDateState(DateState dateState) {
        SearchOptionState.dateState = dateState;
    }

    public static String getBeforeDateStateString() {
        if(dateState == DateState.DATE_STATE_TODAY)
        {
            return currentDateString;
        } else if (dateState == DateState.DATE_STATE_WEEK)
        {
            return aDayAgoDateString;
        } else if (dateState == DateState.DATE_STATE_MONTH)
        {
            return aWeekAgoDateString;
        } else if (dateState == DateState.DATE_STATE_YEAR)
        {
            return aMonthAgoDateString;
        } else if (dateState == DateState.DATE_STATE_ALL)
        {
            return currentDateString;
        } else
        {
            return "";
        }
    }

    public static String getAfterDateStateString() {
        if(dateState == DateState.DATE_STATE_TODAY)
        {
            return aDayAgoDateString;
        } else if (dateState == DateState.DATE_STATE_WEEK)
        {
            return aWeekAgoDateString;
        } else if (dateState == DateState.DATE_STATE_MONTH)
        {
            return aMonthAgoDateString;
        } else if (dateState == DateState.DATE_STATE_YEAR)
        {
            return aYearAgoDateString;
        } else if (dateState == DateState.DATE_STATE_ALL)
        {
            return longTimeAgoDateString;
        } else
        {
            return "";
        }
    }

    public static OrderState getOrderState() {
        return orderState;
    }

    public static void setOrderState(OrderState orderState) {
        SearchOptionState.orderState = orderState;
    }

    public static String getOrderStateString() {
        if (orderState == OrderState.ORDER_STATE_RELEVANCE) {
            return "relevance";
        } else if (orderState == OrderState.ORDER_STATE_VIEWCOUNT) {
            return "viewCount";
        } else if (orderState == OrderState.ORDER_STATE_RATING) {
            return "rating";
        } else {
            return "";
        }
    }

    public static String getNextPageToken() {
        return nextPageToken;
    }

    public static void setNextPageToken(String nextPageToken) {
        SearchOptionState.nextPageToken = nextPageToken;
    }

    public static int getNextJotJJangIndex() {
        return ++nextJotJJangInex;
    }

    public static void setInitJotJJangIndex() {
        nextJotJJangInex = 1;
    }

    public static Date getCurrentDate() {
        return currentDate;
    }
}
