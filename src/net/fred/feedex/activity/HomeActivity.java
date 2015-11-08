/**
 * FeedEx
 *
 * Copyright (c) 2012-2013 Frederic Julian
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fred.feedex.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.fred.feedex.Constants;
import net.fred.feedex.JSONParser;
import net.fred.feedex.MainApplication;
import net.fred.feedex.R;
import net.fred.feedex.adapter.DrawerAdapter;
import net.fred.feedex.fragment.EntriesListFragment;
import net.fred.feedex.provider.FeedData;
import net.fred.feedex.provider.FeedData.EntryColumns;
import net.fred.feedex.provider.FeedData.FeedColumns;
import net.fred.feedex.service.FetcherService;
import net.fred.feedex.service.RefreshService;
import net.fred.feedex.utils.PrefUtils;
import net.fred.feedex.utils.UiUtils;

public class HomeActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STATE_CURRENT_DRAWER_POS = "STATE_CURRENT_DRAWER_POS";

    private static final String FEED_UNREAD_NUMBER = "(SELECT " + Constants.DB_COUNT + " FROM " + EntryColumns.TABLE_NAME + " WHERE " +
            EntryColumns.IS_READ + " IS NULL AND " + EntryColumns.FEED_ID + '=' + FeedColumns.TABLE_NAME + '.' + FeedColumns._ID + ')';

    private static final String WHERE_UNREAD_ONLY = "(SELECT " + Constants.DB_COUNT + " FROM " + EntryColumns.TABLE_NAME + " WHERE " +
            EntryColumns.IS_READ + " IS NULL AND " + EntryColumns.FEED_ID + "=" + FeedColumns.TABLE_NAME + '.' + FeedColumns._ID + ") > 0" +
            " OR (" + FeedColumns.IS_GROUP + "=1 AND (SELECT " + Constants.DB_COUNT + " FROM " + FeedData.ENTRIES_TABLE_WITH_FEED_INFO +
            " WHERE " + EntryColumns.IS_READ + " IS NULL AND " + FeedColumns.GROUP_ID + '=' + FeedColumns.TABLE_NAME + '.' + FeedColumns._ID +
            ") > 0)";

    private static final int LOADER_ID = 0;
    private static String TAG_EMAIL = "";
    private static final String TAG_USER = "user";
 // Progress Dialog
 		private ProgressDialog pDialog;

 		// Creating JSON Parser object
 		JSONParser jParser = new JSONParser();

 		ArrayList<HashMap<String, String>> productsList;
 	
 		// single product url
 		private static final String url_user = "http://webdev1.econostic.com/bigdig/get_compte.php";
 		
 	// JSON Node names
 			private static final String TAG_SUCCESS = "success";
 			
 			private static final String TAG_PRODUCTS = "products";
 			
 			private static final String TAG_ID = "id";
 			
 			private static final String TAG_URL = "url";
 			
 			private static final String TAG_NOM = "nom";
 			
 			private static final String TAG_FULL = "full";
 			
 			private static final String TAG_COLOR = "color";
 			
 			private static final String TAG_INTERNEXTERN = "internextern";
 			
 			private static final String KEY_ID = "_id";
 			
 			private static final String TAG_USER_ID = "id";
 			
 			private static final String TAG_USER_USERNAME = "username";
 			
 			private static final String TAG_USER_EMAIL = "email";
 			
 			private static final String TAG_USER_NOM = "nom";
 			
 			private static final String TAG_USER_PRENOM = "prenom";
 			
 			private static final String TAG_USER_DURATION = "duration";


 		// products JSONArray
 			JSONArray products = null;
 			
 		// user JSONArray
 			JSONArray user = null;
 			
 			
    private final SharedPreferences.OnSharedPreferenceChangeListener mShowReadListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (PrefUtils.SHOW_READ.equals(key)) {
                getLoaderManager().restartLoader(LOADER_ID, null, HomeActivity.this);
            }
        }
    };

    private EntriesListFragment mEntriesFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DrawerAdapter mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private BitmapDrawable mIcon;
    private int mCurrentDrawerPos;

    private boolean mIsDrawerMoving = false;

    private boolean mCanQuit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UiUtils.setPreferenceTheme(this);
        super.onCreate(savedInstanceState);
        
        
        String possibleEmail="";
		ArrayList t=new ArrayList<String>();
		 
		      
		      
		      try{
		    	  
			       Account[] accounts = AccountManager.get(this).getAccounts();
			       
			       if (accounts.length>0) {
					for (Account account : accounts) {
						/*String type = account.type;

				        if (type.equals("com.google")) {
				            
				        }*/
			    	  t.add(account.name);
				} 
					}else {
					System.exit(0);

				}
			       
			    	 
			       
			  }
		      catch(Exception e)
		      {
		    	   Log.i("Exception", "Exception:"+e) ; 
		      }
		   
		      TAG_EMAIL=t.get(0).toString();
		      
		      
		   // Loading products in Background Thread
      		new LoadAllProducts().execute();
		      
		      
		      
		      
		      

        if (PrefUtils.getBoolean(PrefUtils.LIGHT_THEME, true)) {
            getWindow().setBackgroundDrawableResource(R.color.light_entry_list_background);
        } else {
            getWindow().setBackgroundDrawableResource(R.color.dark_entry_list_background);
        }

        setContentView(R.layout.activity_home);

        mEntriesFragment = (EntriesListFragment) getFragmentManager().findFragmentById(R.id.entries_list_fragment);

        mTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectDrawerItem(position);
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }, 50);
            }
        });
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (mIsDrawerMoving && newState == DrawerLayout.STATE_IDLE) {
                    mIsDrawerMoving = false;
                    invalidateOptionsMenu();
                } else if (!mIsDrawerMoving) {
                    mIsDrawerMoving = true;
                    invalidateOptionsMenu();
                }

                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState != null) {
            mCurrentDrawerPos = savedInstanceState.getInt(STATE_CURRENT_DRAWER_POS);
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);

        if (PrefUtils.getBoolean(PrefUtils.REFRESH_ENABLED, true)) {
            // starts the service independent to this activity
            startService(new Intent(this, RefreshService.class));
        } else {
            stopService(new Intent(this, RefreshService.class));
        }
        if (PrefUtils.getBoolean(PrefUtils.REFRESH_ON_OPEN_ENABLED, false)) {
            if (!PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false)) {
                startService(new Intent(HomeActivity.this, FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS));
            }
        }
    }

    private void refreshTitleAndIcon() {
        getActionBar().setTitle(mTitle);
        switch (mCurrentDrawerPos) {
            case 0:
                getActionBar().setTitle(R.string.all);
                getActionBar().setIcon(R.drawable.ic_statusbar_rss);
                break;
            case 1:
                getActionBar().setTitle(R.string.favorites);
                getActionBar().setIcon(R.drawable.dimmed_rating_important);
                break;
            case 2:
                getActionBar().setTitle(android.R.string.search_go);
                getActionBar().setIcon(R.drawable.action_search);
                break;
            default:
                getActionBar().setTitle(mTitle);
                if (mIcon != null) {
                    getActionBar().setIcon(mIcon);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_DRAWER_POS, mCurrentDrawerPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.registerOnPrefChangeListener(mShowReadListener);
    }

    @Override
    protected void onPause() {
        PrefUtils.unregisterOnPrefChangeListener(mShowReadListener);
        super.onPause();
    }

    @Override
    public void finish() {
        if (mCanQuit) {
            super.finish();
            return;
        }

        Toast.makeText(this, R.string.back_again_to_quit, Toast.LENGTH_SHORT).show();
        mCanQuit = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCanQuit = false;
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean isOpened = mDrawerLayout.isDrawerOpen(mDrawerList);
        if (isOpened && !mIsDrawerMoving || !isOpened && mIsDrawerMoving) {
            getActionBar().setTitle(R.string.app_name);
            getActionBar().setIcon(R.drawable.icon);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.drawer, menu);

            if (!PrefUtils.getBoolean(PrefUtils.SHOW_READ, true)) {
                menu.findItem(R.id.menu_hide_read_main).setTitle(R.string.context_menu_show_read).setIcon(R.drawable.view_reads);
            }

            mEntriesFragment.setHasOptionsMenu(false);
        } else {
            refreshTitleAndIcon();
            mEntriesFragment.setHasOptionsMenu(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_hide_read_main:
                if (!PrefUtils.getBoolean(PrefUtils.SHOW_READ, true)) {
                    PrefUtils.putBoolean(PrefUtils.SHOW_READ, true);
                    item.setTitle(R.string.context_menu_hide_read).setIcon(R.drawable.hide_reads);
                } else {
                    PrefUtils.putBoolean(PrefUtils.SHOW_READ, false);
                    item.setTitle(R.string.context_menu_show_read).setIcon(R.drawable.view_reads);
                }
                return true;
            case R.id.menu_edit_main:
                startActivity(new Intent(this, EditFeedsListActivity.class));
                return true;
            case R.id.menu_refresh_main:
                if (!PrefUtils.getBoolean(PrefUtils.IS_REFRESHING, false)) {
                    MainApplication.getContext().startService(new Intent(MainApplication.getContext(), FetcherService.class).setAction(FetcherService.ACTION_REFRESH_FEEDS));
                }
                return true;
            case R.id.menu_settings_main:
                startActivity(new Intent(this, GeneralPrefsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(this, FeedColumns.GROUPED_FEEDS_CONTENT_URI, new String[]{FeedColumns._ID, FeedColumns.URL, FeedColumns.NAME,
                FeedColumns.IS_GROUP, FeedColumns.GROUP_ID, FeedColumns.ICON, FeedColumns.LAST_UPDATE, FeedColumns.ERROR, FEED_UNREAD_NUMBER},
                PrefUtils.getBoolean(PrefUtils.SHOW_READ, true) ? "" : WHERE_UNREAD_ONLY, null, null
        );
        cursorLoader.setUpdateThrottle(Constants.UPDATE_THROTTLE_DELAY);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mDrawerAdapter != null) {
            mDrawerAdapter.setCursor(cursor);
        } else {
            mDrawerAdapter = new DrawerAdapter(this, cursor);
            mDrawerList.setAdapter(mDrawerAdapter);

            // We don't have any menu yet, we need to display it
            mDrawerList.post(new Runnable() {
                @Override
                public void run() {
                    selectDrawerItem(mCurrentDrawerPos);
                    refreshTitleAndIcon();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mDrawerAdapter.setCursor(null);
    }

    private void selectDrawerItem(int position) {
        mCurrentDrawerPos = position;
        mIcon = null;

        Uri newUri;
        boolean showFeedInfo = true;

        switch (position) {
            case 0:
                newUri = EntryColumns.ALL_ENTRIES_CONTENT_URI;
                break;
            case 1:
                newUri = EntryColumns.FAVORITES_CONTENT_URI;
                break;
            case 2:
                newUri = EntryColumns.SEARCH_URI(mEntriesFragment.getCurrentSearch());
                break;
            default:
                long feedOrGroupId = mDrawerAdapter.getItemId(position);
                if (mDrawerAdapter.isItemAGroup(position)) {
                    newUri = EntryColumns.ENTRIES_FOR_GROUP_CONTENT_URI(feedOrGroupId);
                } else {
                    byte[] iconBytes = mDrawerAdapter.getItemIcon(position);
                    Bitmap bitmap = UiUtils.getScaledBitmap(iconBytes, 24);
                    if (bitmap != null) {
                        mIcon = new BitmapDrawable(getResources(), bitmap);
                    }

                    newUri = EntryColumns.ENTRIES_FOR_FEED_CONTENT_URI(feedOrGroupId);
                    showFeedInfo = false;
                }
                mTitle = mDrawerAdapter.getItemName(position);
                break;
        }

        if (!newUri.equals(mEntriesFragment.getUri())) {
            mEntriesFragment.setData(newUri, showFeedInfo);
        }

        mDrawerList.setItemChecked(position, true);

        // First open => we open the drawer for you
      /* if (PrefUtils.getBoolean(PrefUtils.FIRST_OPEN, true)) {
            PrefUtils.putBoolean(PrefUtils.FIRST_OPEN, false);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }, 500);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.welcome_title)
                    .setItems(new CharSequence[]{getString(R.string.google_news_title), getString(R.string.add_custom_feed)}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 1) {
                                startActivity(new Intent(Intent.ACTION_INSERT).setData(FeedColumns.CONTENT_URI));
                            } else {
                                startActivity(new Intent(HomeActivity.this, AddGoogleNewsActivity.class));
                            }
                        }
                    });
            builder.show();
        }*/
    }
    
    /**
   	 * Background Async Task to Load all product by making HTTP Request
   	 * */
   	class LoadAllProducts extends AsyncTask<String, String, String> {

   		/**
   		 * Before starting background thread Show Progress Dialog
   		 * */
   		@Override
   		protected void onPreExecute() {
   			super.onPreExecute();
   			
   		}

   		/**
   		 * getting All products from url
   		 * */
   		protected String doInBackground(String... args) {
   			// Building Parameters
   			List<NameValuePair> params = new ArrayList<NameValuePair>();
   			
   			params.add(new BasicNameValuePair("email", TAG_EMAIL));
   			
   			// getting JSON string from URL
   			JSONObject json = jParser.makeHttpRequest(url_user, "GET", params);
   			
   			try {
   				// Checking for SUCCESS TAG
   				int success = json.getInt(TAG_SUCCESS);

   				String	msg = json.getString("message");
   				
   				//Log.v("test","==>"+msg);
   				
   				SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
   				
   				if (success == 1) {
   					
   					user = json.getJSONArray(TAG_USER);
   					
   					for (int i = 0; i < user.length(); i++) {
						JSONObject c = user.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_USER_ID);
						String username = c.getString(TAG_USER_USERNAME);
						String email = c.getString(TAG_USER_EMAIL);
						String nom = c.getString(TAG_USER_NOM);
						String prenom = c.getString(TAG_USER_PRENOM);
						String duration = c.getString(TAG_USER_DURATION);
					
   					}
   					
   					products = json.getJSONArray(TAG_PRODUCTS);

   					// looping through All Products
   					for (int i = 0; i < products.length(); i++) {
   						JSONObject c = products.getJSONObject(i);

   						// Storing each json item in variable
   						String id = c.getString(TAG_ID);
   						String url = c.getString(TAG_URL);
   						String nom = c.getString(TAG_NOM);
   						String full = c.getString(TAG_FULL);
   						String color = c.getString(TAG_COLOR);
   						String internextern = c.getString(TAG_INTERNEXTERN);
   						
   						
   						//Log.d("test","==>"+color+"___"+nom);
   						Cursor exsist=db.rawQuery("SELECT * FROM feeds where _id="+id+"", null);
   						int count= exsist.getCount();
   						if (count>0) {
   							
   							ContentValues values = new ContentValues();
   							values.put("name", nom);
   							values.put(TAG_URL, url);
   							values.put(TAG_COLOR, color);
   							values.put(TAG_INTERNEXTERN, internextern);

   							// updating row
   							 db.update("feeds", values,"_id="+id+"" , null);
   						
   						}
   						else{
   						// inserting
   							db.execSQL("INSERT INTO feeds (`_id`, `url`, `name`, `color`, `internextern`) VALUES ("+id+", '"+url+"', '"+nom+"', '"+color+"', '"+internextern+"' );");
   							
   						}
   						
   						
   					}
   					 
   						
   				}else{
   					//Log.v("test","==>error");
   					System.exit(0);
   				}
   				db.close();
   			} catch (JSONException e) {
   				e.printStackTrace();
   			}

   			
   			return null;
   		}

   		/**
   		 * After completing background task Dismiss the progress dialog
   		 * **/
   		protected void onPostExecute(String file_url) {
   			
   		}

   	}
    
    
    
}