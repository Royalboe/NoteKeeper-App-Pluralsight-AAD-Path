package com.example.notekeeperapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class AppWidgetRemoteViewsFactory(val context: Context)
    : RemoteViewsService.RemoteViewsFactory {
    // It can be used to initialize app data
    override fun onCreate() {
    }
// If app data changes often
    override fun onDataSetChanged() {
    }

    // View to be shown while data is loading or initializing

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    // Here we return a unique id that pertains to a specific item in our dataset
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    // return true if the same id always refer to the same item in the dataset
    override fun hasStableIds(): Boolean {
       return true
    }

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_note_widget)
        rv.setTextViewText(R.id.note_title, DataManager.notes[p0].title)

        val extras = Bundle()
        extras.putInt(NOTE_POSITION, p0)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return rv
    }

    override fun getCount(): Int {
        return DataManager.notes.size
    }

    // Get view count method, it returns the numbe rof the type of views that we be returning in the factory
    override fun getViewTypeCount(): Int {
        return 1
    }

    // Used to clean out any data or cursor we have opened, this will get called when the last
    // RemoteViews Adapter

    override fun onDestroy() {
    }

}