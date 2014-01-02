package com.example.volley_flickr;

import java.util.List;

import com.android.volley.toolbox.NetworkImageView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<JSONItem> {

	private  Activity context;
	private List<JSONItem> items;

	public CustomListAdapter(Activity _context, List<JSONItem> _items) {
		// TODO Auto-generated constructor stub
		super(_context, android.R.layout.simple_list_item_1);
		context = _context;
		items = _items;			
	}
	@Override
	public JSONItem getItem(int position) {
        return items.get(position);
    }
	
	@Override
    public long getItemId(int position) {
        return position;
    }
	
	@Override
	public View getView(int position, View rowView, ViewGroup parent) {
		
		if(rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, null, true);
		}
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		NetworkImageView imageView = (NetworkImageView) rowView.findViewById(R.id.img);
		
		txtTitle.setText( (items.get(position)).getTitle());
		imageView.setImageUrl( (items.get(position)).getUrl(), ApplicationController.getImageLoader());
		

		return rowView;
	}
}
