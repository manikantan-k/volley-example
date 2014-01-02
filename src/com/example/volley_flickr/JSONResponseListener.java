package com.example.volley_flickr;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response.Listener;

public class JSONResponseListener implements Listener<JSONObject> {
	// Main activity context reference
	private Context context;

	// JSON feed items
	List<JSONItem> items;
	GridView gv;
	// List adapter reference
	private CustomListAdapter adapter;
	private Boolean clear;

	// Initialize all the attributes
	public JSONResponseListener(Context context, CustomListAdapter adapter,
			List<JSONItem> items, GridView _gv, Boolean clearList) {
		this.context = context;
		this.items = items;
		this.adapter = adapter;
		gv = _gv;
		clear = clearList;

	}

	// Called when JSON data is ready to be parsed
	public void onResponse(JSONObject response) {

		JSONArray jsonArray = new JSONArray();

		try {

			jsonArray = response.getJSONObject("responseData").getJSONArray(
					"results");
			if (clear) {
				items.clear();
				adapter.clear();
			}
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonPostObject = jsonArray.getJSONObject(i);
				JSONItem item = new JSONItem();

				item.setTitle(jsonPostObject.optString("titleNoFormatting"));

				item.setUrl(jsonPostObject.optString("unescapedUrl"));

				items.add(item);
				adapter.add(item);

			}


		} catch (JSONException je) {
			Log.d(this.getClass().getName(), je.toString());
		}

		adapter.notifyDataSetChanged();
		if (adapter.getCount() == 8) {
			Toast.makeText(context, "Loading done !", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Loaded More !", Toast.LENGTH_LONG).show();
		}
		((MainActivity) context).whenLoadComplete() ;

	}
}
