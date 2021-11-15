package com.example.notekeeperapp

import android.content.Intent
import android.widget.RemoteViewsService

class AppWidgetRemoteViewService: RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return AppWidgetRemoteViewsFactory(applicationContext)
    }

}