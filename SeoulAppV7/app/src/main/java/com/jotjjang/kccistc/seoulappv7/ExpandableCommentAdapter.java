package com.jotjjang.kccistc.seoulappv7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ExpandableCommentAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<CommentEntry> groupList = null;
    private ArrayList<ArrayList<CommentEntry>> childList = null;
    private LayoutInflater inflater = null;

    public ExpandableCommentAdapter(Context c, ArrayList<CommentEntry> groupList,
                                 ArrayList<ArrayList<CommentEntry>> childList){
        super();
        this.context = c;
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public CommentEntry getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public CommentEntry getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        CommentEntry entry = groupList.get(groupPosition);

        if (view == null) {
            view = inflater.inflate(R.layout.comment_item, parent, false);
        }

        RelativeLayout label = view.findViewById(R.id.comment_label);
        ImageView imageViewProfile = view.findViewById(R.id.comment_authorProfileImageUrl);
        TextView textViewAuthorName = view.findViewById(R.id.comment_authorName);
        TextView textViewPublishedAt = view.findViewById(R.id.comment_publishedAt);
        TextView textViewContentText = view.findViewById(R.id.comment_content_text);
        final TextView textViewReplyCount = view.findViewById(R.id.comment_reply_count);

        Glide.with(context).load(entry.getAuthorProfileImageUrl()).into(imageViewProfile);
        textViewAuthorName.setText(entry.getAuthorName());
        textViewPublishedAt.setText(entry.getPublishedDateString());
        textViewContentText.setText(entry.getCommentText());


        if(entry.getTotalReplyCount() > 0) {
            textViewReplyCount.setText(entry.getTotalReplyCount() + " 개의 답글");
            textViewReplyCount.setVisibility(View.VISIBLE);
            textViewReplyCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            textViewReplyCount.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        CommentEntry entry = childList.get(groupPosition).get(childPosition);

        if (view == null) {
            view = inflater.inflate(R.layout.comment_reply_item, parent, false);
        }

        RelativeLayout label = view.findViewById(R.id.reply_label);
        ImageView imageViewProfile = view.findViewById(R.id.reply_authorProfileImageUrl);
        TextView textViewAuthorName = view.findViewById(R.id.reply_authorName);
        TextView textViewPublishedAt = view.findViewById(R.id.reply_publishedAt);
        TextView textViewContentText = view.findViewById(R.id.reply_content_text);

        Glide.with(context).load(entry.getAuthorProfileImageUrl()).into(imageViewProfile);
        textViewAuthorName.setText(entry.getAuthorName());
        textViewPublishedAt.setText(entry.getPublishedDateString());
        textViewContentText.setText(entry.getCommentText());

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
