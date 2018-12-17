package com.jotjjang.kccistc.seoulappv7;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyJsonParser {

    public static JSONObject getYoutubeData(String searchKeyWord, int count) {
        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&q=" + searchKeyWord
                        + "&key=" + DeveloperKey.DEVELOPER_KEY
                        + "&maxResults=" + count);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while((b = stream.read()) != -1)
            {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeData(String searchKeyWord, String beforeDateKeyWord, String afterDateKeyWord, String orderKeyWord, int count) {
        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&q=" + searchKeyWord
                        + "&key=" + DeveloperKey.DEVELOPER_KEY
                        + "&publishedBefore=" + beforeDateKeyWord
                        + "&publishedAfter=" + afterDateKeyWord
                        + "&order=" + orderKeyWord
                        + "&maxResults=" + count
                        + "&regionCode=KR"
                        + "&type=video"
                        + "&safeSearch=strict"
                        + "&videoEmbeddable=true");

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while((b = stream.read()) != -1)
            {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeData(String searchKeyWord, String beforeDateKeyWord, String afterDateKeyWord, String orderKeyWord, int count, String nextPageToken) {
        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&q=" + searchKeyWord
                        + "&key=" + DeveloperKey.DEVELOPER_KEY
                        + "&publishedBefore=" + beforeDateKeyWord
                        + "&publishedAfter=" + afterDateKeyWord
                        + "&order=" + orderKeyWord
                        + "&maxResults=" + count
                        + "&regionCode=KR"
                        + "&pageToken=" + nextPageToken
                        + "&type=video"
                        + "&safeSearch=strict"
                        + "&videoEmbeddable=true");

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while((b = stream.read()) != -1)
            {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static void parseJsonData(ArrayList<VideoEntry> list, JSONObject jsonObject) throws JSONException{

        SearchOptionState.setNextPageToken(jsonObject.getString("nextPageToken"));

        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++)
        {
            String title;
            String videoId;
            String publishedDate;
            String description;
            String channelTitle;
            JSONObject c = contacts.getJSONObject(i);
            String kind = c.getJSONObject("id").getString("kind");
            if(kind.equals("youtube#video")) {
                videoId = c.getJSONObject("id").getString("videoId");
            } else {
                videoId = c.getJSONObject("id").getString("playlistId");
            }
            title = c.getJSONObject("snippet").getString("title");
            description = c.getJSONObject("snippet").getString("description");
            channelTitle = c.getJSONObject("snippet").getString("channelTitle");

            String changedTitle = "";
            String changedDescription = "";
            String changedChannelTitle = "";
            try {
                changedTitle = new String(title.getBytes("8859_1"), "utf-8");
                changedDescription = new String(description.getBytes("8859_1"), "utf-8");
                changedChannelTitle = new String(channelTitle.getBytes("8859_1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            publishedDate = c.getJSONObject("snippet").getString("publishedAt").substring(0,19);
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");

            list.add(new VideoEntry(changedTitle, videoId,publishedDate,changedDescription,changedChannelTitle,imgUrl));
        }
    }

    public static JSONObject getJotJJang() {
        HttpGet httpGet = new HttpGet("http://jotjjang.com/seoulApp");

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static void parseKeyWord(HashMap<String, String> map, JSONObject jsonObject) throws JSONException {
        JSONArray contacts = jsonObject.getJSONArray("keyword");
        for (int i = 0; i < contacts.length(); i++)
        {
            JSONObject c = contacts.getJSONObject(i);
            String key = c.getString("key");
            String val = c.getString("val");
            map.put(key,val);
        }
    }

    public static void parseHotClip(ArrayList<String> idList, JSONObject jsonObject) throws JSONException {
        JSONArray contacts = jsonObject.getJSONArray("hotclip");
        for (int i = 0; i < contacts.length(); i++)
        {
            JSONObject c = contacts.getJSONObject(i);
            String id = c.getString("id");
            idList.add(id);
        }
    }

    public static JSONObject getOneYoutube(String videoId) {
        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/videos"
                    + "?part=snippet,statistics"
                    + "&id=" + videoId
                    + "&key=" + DeveloperKey.DEVELOPER_KEY);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static VideoEntry parseOneYoutube(JSONObject jsonObject) throws JSONException {
        JSONArray contacts = jsonObject.getJSONArray("items");
        JSONObject c = contacts.getJSONObject(0);
        JSONObject snippet = c.getJSONObject("snippet");
        JSONObject statistics = c.getJSONObject("statistics");

        String title = null;
        String description = null;
        String channelTitle = null;
        title = snippet.getString("title");
        description = snippet.getString("description");
        channelTitle = snippet.getString("channelTitle");
        String videoId = c.getString("id");
        String publishedDate = snippet.getString("publishedAt").substring(0,19);
        String imgUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
        int viewCount = Integer.parseInt(statistics.getString("viewCount"));
        int commentCount = 0;
        if(statistics.has("commentCount")) {
            commentCount = Integer.parseInt(statistics.getString("commentCount"));
        }
        return new VideoEntry(title,videoId,publishedDate,description,channelTitle,imgUrl,viewCount,commentCount);
    }

    public static JSONObject getYoutubeItems(ArrayList<String> idList) {
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=snippet,statistics"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&id=";
        for(int i = 0; i < idList.size();i++) {
            url = url + idList.get(i);
            if(i != idList.size()-1) {
                url = url +",";
            }
        }
        HttpGet httpGet = new HttpGet(url);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<VideoEntry> parseYoutubeItems(JSONObject jsonObject) throws JSONException {
        ArrayList<VideoEntry> videoEntryArrayList = new ArrayList<>();
        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            JSONObject snippet = c.getJSONObject("snippet");
            JSONObject statistics = c.getJSONObject("statistics");
            String title = null;
            String description = null;
            String channelTitle = null;
            title = snippet.getString("title");
            description = snippet.getString("description");
            channelTitle = snippet.getString("channelTitle");
            String videoId = c.getString("id");
            String publishedDate = snippet.getString("publishedAt").substring(0,19);
            String imgUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
            int viewCount = Integer.parseInt(statistics.getString("viewCount"));
            int commentCount = 0;
            if(statistics.has("commentCount")) {
                commentCount = Integer.parseInt(statistics.getString("commentCount"));
            }
            videoEntryArrayList.add(new VideoEntry(title,videoId,publishedDate,description,channelTitle,imgUrl,viewCount,commentCount));
        }

        return videoEntryArrayList;
    }

    public static JSONObject getJotJJangNext(int index) {
        HttpGet httpGet = new HttpGet("http://jotjjang.com/seoulApp/welcome"+index+".htm");

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeStatistics(ArrayList<String> idList) {
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=statistics"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&id=";
        for(int i = 0; i < idList.size();i++) {
            url = url + idList.get(i);
            if(i != idList.size()-1) {
                url = url +",";
            }
        }
        HttpGet httpGet = new HttpGet(url);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static HashMap<String, StatisticsItem> parseYoutubeStatistics(JSONObject jsonObject) throws JSONException {
        HashMap<String,StatisticsItem> statisticsHashMap = new HashMap<>();
        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            JSONObject statistics = c.getJSONObject("statistics");
            String videoId = c.getString("id");
            int viewCount = Integer.parseInt(statistics.getString("viewCount"));
            int commentCount = 0;
            if(statistics.has("commentCount")) {
                commentCount = Integer.parseInt(statistics.getString("commentCount"));
            }
            statisticsHashMap.put(videoId, new StatisticsItem(viewCount,commentCount));
        }
        return statisticsHashMap;
    }

    public static JSONObject getYoutubeComments(String videoId, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/commentThreads"
                + "?part=id,replies,snippet"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&videoId=" + videoId
                + "&textFormat=plainText"
                + "&maxResults=" + maxResults;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeComments(String videoId, String pageToken, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/commentThreads"
                + "?part=id,replies,snippet"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&videoId=" + videoId
                + "&textFormat=plainText"
                + "&maxResults=" + maxResults
                + "&pageToken=" + pageToken;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<CommentEntry> parseYoutubeComments(JSONObject jsonObject) throws JSONException {
        if(jsonObject.isNull("nextPageToken") == false) {
            ExpandableCommentFragment.nextCommentToken = jsonObject.getString("nextPageToken");
        } else {
            ExpandableCommentFragment.nextCommentToken = null;
        }
        ArrayList<CommentEntry> commentEntryArrayList = new ArrayList<>();
        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String kind = c.getString("kind");
            if (kind.equals("youtube#commentThread")) {
                String commentId = c.getString("id");
                JSONObject snippet = c.getJSONObject("snippet");
                String videoId = snippet.getString("videoId");
                JSONObject topLevelComment = snippet.getJSONObject("topLevelComment");
                JSONObject snippet2 = topLevelComment.getJSONObject("snippet");
                String authorName = snippet2.getString("authorDisplayName");
                String authorProfileImageUrl = snippet2.getString("authorProfileImageUrl");
                String commentText = snippet2.getString("textDisplay");
                int likeCount = snippet2.getInt("likeCount");
                String publishedAt = snippet2.getString("publishedAt").substring(0, 19);
                boolean canReply = snippet.getBoolean("canReply");
                int totalReplyCount = snippet.getInt("totalReplyCount");

                boolean isPublic = snippet.getBoolean("isPublic");

                CommentEntry commentEntry = new CommentEntry(
                        commentId, authorName, authorProfileImageUrl
                        , commentText, videoId, likeCount, publishedAt
                        , canReply, totalReplyCount, isPublic);
                if (totalReplyCount > 0) {
                    JSONObject replies = c.getJSONObject("replies");
                    JSONArray comments = replies.getJSONArray("comments");
                    for (int j = 0; j < comments.length(); j++) {
                        JSONObject replyItem = comments.getJSONObject(j);
                        String reply_commentId = replyItem.getString("id");
                        JSONObject reply_snippet = replyItem.getJSONObject("snippet");
                        String reply_authorName = reply_snippet.getString("authorDisplayName");
                        String reply_authorProfileImageUrl = reply_snippet.getString("authorProfileImageUrl");
                        String reply_videoId = reply_snippet.getString("videoId");
                        String reply_commentText = reply_snippet.getString("textDisplay");
                        int reply_likeCount = reply_snippet.getInt("likeCount");
                        String reply_publishedAt = reply_snippet.getString("publishedAt").substring(0, 19);

                        CommentEntry replyEntry = new CommentEntry(reply_commentId, reply_authorName
                                , reply_authorProfileImageUrl, reply_commentText, reply_videoId
                                , reply_likeCount, reply_publishedAt, false, 0, true);
                        commentEntry.getRepliesList().add(replyEntry);
                    }
                }
                commentEntryArrayList.add(commentEntry);
            }
        }
        return commentEntryArrayList;
    }

    public static JSONObject getYoutubeMostPopular(int categoryId, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=id,snippet,statistics"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&chart=mostPopular"
                + "&regionCode=KR"
                + "&maxResults=" + maxResults
                + "&videoCategoryId=" + categoryId;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeMostPopular(int categoryId, String pageToken, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=id,snippet,statistics"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&chart=mostPopular"
                + "&regionCode=KR"
                + "&maxResults=" + maxResults
                + "&videoCategoryId=" + categoryId
                + "&pageToken=" + pageToken;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<VideoEntry> parseYoutubeMostPopular(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("nextPageToken") == true) {
           MainActivity.mostPopularPageToken = jsonObject.getString("nextPageToken");
        } else {
            MainActivity.mostPopularPageToken = null;
        }
        ArrayList<VideoEntry> videoEntryArrayList = new ArrayList<>();
        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            JSONObject snippet = c.getJSONObject("snippet");
            JSONObject statistics = c.getJSONObject("statistics");
            String title = null;
            String description = null;
            String channelTitle = null;
            title = snippet.getString("title");
            description = snippet.getString("description");
            channelTitle = snippet.getString("channelTitle");
            String videoId = c.getString("id");
            String publishedDate = snippet.getString("publishedAt").substring(0,19);
            String imgUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
            int viewCount = Integer.parseInt(statistics.getString("viewCount"));
            int commentCount = 0;
            if(statistics.has("commentCount")) {
                commentCount = Integer.parseInt(statistics.getString("commentCount"));
            }
            videoEntryArrayList.add(new VideoEntry(title,videoId,publishedDate,description,channelTitle,imgUrl,viewCount,commentCount));
        }
        return videoEntryArrayList;
    }

    public static JSONObject getYoutubeReply(String commentId, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/comments"
                + "?part=id,snippet"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&parentId=" + commentId
                + "&textFormat=plainText"
                + "&maxResults=" + maxResults;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getYoutubeReply(String commentId, String pageToken, int maxResults) {
        String url = "https://www.googleapis.com/youtube/v3/comments"
                + "?part=id,snippet"
                + "&key=" + DeveloperKey.DEVELOPER_KEY
                + "&parentId=" + commentId
                + "&textFormat=plainText"
                + "&maxResults=" + maxResults
                + "&pageToken=" + pageToken;

        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String b;
            while((b = reader.readLine()) != null)
            {
                stringBuilder.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<CommentEntry> parseYoutubeReplies(JSONObject jsonObject) throws JSONException {
        if(jsonObject.isNull("nextPageToken") == false) {
            ExpandableCommentFragment.nextReplyToken = jsonObject.getString("nextPageToken");
        } else {
            ExpandableCommentFragment.nextReplyToken = null;
        }
        ArrayList<CommentEntry> commentEntryArrayList = new ArrayList<>();
        JSONArray contacts = jsonObject.getJSONArray("items");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String kind = c.getString("kind");
            if (kind.equals("youtube#comment")) {
                String commentId = c.getString("id");
                JSONObject snippet = c.getJSONObject("snippet");
                String authorName = snippet.getString("authorDisplayName");
                String authorProfileImageUrl = snippet.getString("authorProfileImageUrl");
                String commentText = snippet.getString("textDisplay");
                String publishedAt = snippet.getString("publishedAt").substring(0, 19);
                CommentEntry commentEntry = new CommentEntry(
                        commentId, authorName, authorProfileImageUrl
                        , commentText, null, 0, publishedAt
                        , false, 0, true);
                commentEntryArrayList.add(commentEntry);
            }
        }
        return commentEntryArrayList;
    }
}
