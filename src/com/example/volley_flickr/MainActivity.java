package com.example.volley_flickr;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.volley_flickr.ApplicationController;

public class MainActivity extends Activity {
	RequestQueue q;
	static List<JSONItem> items;
	static Bitmap thatBitmap;
	GridView gv;
	int page =0;
	CustomListAdapter adapter;
	String searchStr = "opera%20house";
	public String[] leftmenu;
	private ListView mDrawerList;
	DrawerLayout dLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		
		setContentView(R.layout.activity_main);
		handleIntent(getIntent());
		
		Log.d(this.getLocalClassName(), "onStart");
		items = new ArrayList<JSONItem> () ;
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		gv= (GridView) findViewById(R.id.grid_view);
		gv.setNumColumns(2);
		//ArrayAdapter<JSONItem> adapter = new ArrayAdapter<JSONItem> (this, android.R.layout.simple_list_item_1, items);
		 adapter = new CustomListAdapter(this, items);
		gv.setAdapter(adapter);
		
		leftmenu = getResources().getStringArray(R.array.leftmenu);
		getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, leftmenu));

		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(loadContent(true));
		
        gv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				if (!gv.getAdapter().isEmpty() && gv.getLastVisiblePosition() == gv.getAdapter().getCount() - 1
						&& gv.getChildAt(gv.getChildCount() - 1).getBottom() <= gv.getHeight()) {
							Log.d("List", "End");
							ApplicationController.getInstance().addToRequestQueue(loadContent(false));
						}
				
			}
		});
		gv.setOnItemClickListener(thumbnailClickListener);
		
		
		mDrawerList.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				searchStr = leftmenu[pos];
				ApplicationController.getInstance().addToRequestQueue(loadContent(true));
				dLayout.closeDrawer(mDrawerList);
			}
		});


	}
	
	private AdapterView.OnItemClickListener thumbnailClickListener = new AdapterView.OnItemClickListener() {
        

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos,
				long id) {
			int[] screenLocation = new int[2];
            v.getLocationOnScreen(screenLocation);
            NetworkImageView temp = (NetworkImageView) v.findViewById(R.id.img);
            thatBitmap = ((BitmapDrawable) temp.getDrawable() ).getBitmap();
            Log.d("Main", thatBitmap.getByteCount() + "") ;
            
            Dialog d = new Dialog(MainActivity.this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.activity_details);
            
            
            NetworkImageView iView = (NetworkImageView) d.findViewById(R.id.imageView);
            Drawable drawable = new BitmapDrawable(getResources(),thatBitmap);
            iView.setBackground(drawable);
            
            TextView tv = (TextView) d.findViewById(R.id.description) ;
            tv.setText( ( items.get(pos)).getTitle() );
            
            d.show();
            
		}
    };
	
	@Override
	public boolean onSearchRequested() {
	    Log.d("SRCH","Search start");
	    return super.onSearchRequested();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("SRCH","Search New");
	    setIntent(intent);
	    handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		Log.d("SRCH","Calling handleIntent");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		      String query = intent.getStringExtra(SearchManager.QUERY);
		      // Do work using string
		      Log.d("SRCH", "Searching for" + query);
		      searchStr = query;
		      ApplicationController.getInstance().addToRequestQueue(loadContent(true));
		    }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		
		 SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	        SearchView searchView = (SearchView) menu.findItem(R.id.search)
	                .getActionView();
	        searchView.setSearchableInfo(searchManager
	                .getSearchableInfo(getComponentName()));
	 
	        return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(getResources().getConfiguration().orientation == 1) {
			gv.setNumColumns(2);
		}
		else {
			gv.setNumColumns(3);
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        if (item.getItemId() == android.R.id.home) {
 
            if (dLayout.isDrawerOpen(mDrawerList)) {
            	dLayout.closeDrawer(mDrawerList);
            } else {
            	dLayout.openDrawer(mDrawerList);
            }
        }
 
        return super.onOptionsItemSelected(item);
    }
	
	@SuppressWarnings("deprecation")
	public JsonObjectRequest loadContent(Boolean clearList) {
//		final String URL = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=b2444cabc31f0e961ffb74ae587c6ad5&text=beach&sort=relevance&per_page=10&page="
//				+ (++page) +"&format=json&nojsoncallback=1&api_sig=913ab3f691d8bec6589235465089ded1";
		final String URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+
							URLEncoder.encode(searchStr) +
							"&imgsz=small&rsz=8&start=" + (++page)*8;
		Log.d("LD", "Loading content." + "To clear ?" + clearList);
		setProgressBarIndeterminateVisibility(true);
		JsonObjectRequest req = new JsonObjectRequest(URL, null,
		   new JSONResponseListener(this, adapter, items, gv, clearList), new Response.ErrorListener() {
		       @Override
		       public void onErrorResponse(VolleyError error) {
		           Log.e("Error: ", error.toString());
		           setProgressBarIndeterminateVisibility(false);
		       }
		   });
		return req;
	}
	
	public void whenLoadComplete() {
		this.setProgressBarIndeterminateVisibility(false);
		this.setTitle(searchStr);
	}
	
}
