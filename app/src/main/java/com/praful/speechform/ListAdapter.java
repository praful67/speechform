package com.praful.speechform;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    Activity activity;
    List<Item> items;

    public ListAdapter(Activity activity, List<Item> items) {
        this.activity = activity;
        this.items = items;
    }

    LayoutInflater inflater;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {

        inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.item, null);

        FirebaseApp.initializeApp(activity.getBaseContext());
        final TextView itemkey = (TextView) itemView.findViewById(R.id.itemkey);
        final TextView itemvalue = (TextView) itemView.findViewById(R.id.itemvalue);


        itemkey.setText(items.get(i).getKey());
        itemvalue.setText(items.get(i).getValue());

        return itemView;
    }
}
