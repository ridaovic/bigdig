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

package net.fred.feedex;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.fred.feedex.utils.PrefUtils;

public class MainApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false); // init
        
        

        //la structure des tables
        /*SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
        Cursor c=db.rawQuery(" SELECT sql FROM sqlite_master WHERE name = 'feeds'", null);
        
        c.moveToFirst();
        String sql=c.getString(c.getColumnIndex("sql"));
        Log.v("test", "xxx :"+sql);*/
        
        
        //les flux
       /* SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
        Cursor c=db.rawQuery("SELECT * from feeds", null);
        int count= c.getCount();
        
        c.moveToFirst();
        
         for (Integer j = 0; j < count; j++)
         {
        	 String id=c.getString(c.getColumnIndex("_id"));
        	 String url=c.getString(c.getColumnIndex("url"));
             String name=c.getString(c.getColumnIndex("color"));
             String isgroup=c.getString(c.getColumnIndex("isgroup"));
             String lastupdate=c.getString(c.getColumnIndex("lastupdate"));
             String reallastupdate=c.getString(c.getColumnIndex("reallastupdate"));
             String retrievefulltext=c.getString(c.getColumnIndex("retrievefulltext"));
             //String icon=c.getString(c.getColumnIndex("icon"));
             String error=c.getString(c.getColumnIndex("error"));
             String priority=c.getString(c.getColumnIndex("priority"));
             String fetchmode=c.getString(c.getColumnIndex("fetchmode"));
             
             
             
            Log.v("test", "id : "+id+" \n url : "+url+" \n name : "+name+" \n isgroup : "+isgroup+" \n lastupdate : "+lastupdate+" \n reallastupdate : "+reallastupdate+" \n retrievefulltext : "+retrievefulltext+" \n error : "+error+" \n priority : "+priority+" \n fetchmode : "+fetchmode);
             
            
             
        	 c.moveToNext() ;
         }
         
         db.close();
        
       
        */
        //les filters
        /* SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
         Cursor c=db.rawQuery("SELECT * from filters", null);
         int count= c.getCount();
         
         c.moveToFirst();
         
          for (Integer j = 0; j < count; j++)
          {
        	  String feedid=c.getString(c.getColumnIndex("feedid"));
              String filtertext=c.getString(c.getColumnIndex("filtertext"));
              String isregex=c.getString(c.getColumnIndex("isregex"));
              String isappliedtotitle=c.getString(c.getColumnIndex("isappliedtotitle"));
              String isacceptrule=c.getString(c.getColumnIndex("isacceptrule"));
              
             
         	 
              Log.v("test", "==> feedid : "+feedid+" filtertext : "+filtertext+" isregex : "+isregex+" isappliedtotitle : "+isappliedtotitle+" isacceptrule : "+isacceptrule);
              
         	 c.moveToNext() ;
          }
          
          db.close();
        */
        
        
      //les articles
      /*   SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
         Cursor c=db.rawQuery("SELECT * from entries", null);
         int count= c.getCount();
         
         c.moveToFirst();
         
          for (Integer j = 0; j < count; j++)
          { 
        	  String feedid=c.getString(c.getColumnIndex("feedid"));
              String title=c.getString(c.getColumnIndex("title"));
              String abstractx=c.getString(c.getColumnIndex("abstract"));
              String mobilized=c.getString(c.getColumnIndex("mobilized"));
              String date=c.getString(c.getColumnIndex("date"));
              String fetch_date=c.getString(c.getColumnIndex("fetch_date"));
              String isread=c.getString(c.getColumnIndex("isread"));
              String link=c.getString(c.getColumnIndex("link"));
              String favorite=c.getString(c.getColumnIndex("favorite"));
              String enclosure=c.getString(c.getColumnIndex("enclosure"));
              String guid=c.getString(c.getColumnIndex("guid"));
              String author=c.getString(c.getColumnIndex("author"));
             
              Log.v("test", "==> feedid : "+feedid+" title : "+title+" abstract : "+abstractx+" mobilized : "+mobilized+" date : "+date+" fetch_date : "+fetch_date+" isread : "+isread+" link : "+link+" favorite : "+favorite+" enclosure : "+enclosure+" guid : "+guid+" author : "+author);
              
         	 c.moveToNext() ;
          }
          
          db.close();*/
        
      //les tasks
         /* SQLiteDatabase  db=openOrCreateDatabase("FeedEx.db",MODE_PRIVATE, null);
           Cursor c=db.rawQuery("SELECT * from tasks", null);
           int count= c.getCount();
           
           c.moveToFirst();
           
            for (Integer j = 0; j < count; j++)
            { 
          	    String entryid=c.getString(c.getColumnIndex("entryid"));
                String imgurl_to_dl=c.getString(c.getColumnIndex("imgurl_to_dl"));
                String number_attempt=c.getString(c.getColumnIndex("number_attempt"));
                
               
                
               
                Log.v("test", "==> entryid : "+entryid+" imgurl_to_dl : "+imgurl_to_dl+" number_attempt : "+number_attempt);
                
           	 c.moveToNext() ;
            }
            
            db.close();
        */
    }

    public static Context getContext() {
        return context;
    }
}
