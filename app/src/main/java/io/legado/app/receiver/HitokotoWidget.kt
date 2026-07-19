package io.legado.app.receiver

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import io.legado.app.R
import io.legado.app.help.coroutine.Coroutine
import io.legado.app.help.http.newCallStrResponse
import io.legado.app.help.http.okHttpClient
import io.legado.app.utils.BitmapUtils

/**
 * Implementation of App Widget functionality.
 */
class HitokotoWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidget(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateWidget(context)
    }

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, HitokotoWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isEmpty()) return

            Coroutine.async {
                val hitokoto = try {
                    okHttpClient.newCallStrResponse {
                        url("https://v1.hitokoto.cn/?c=f&encode=text")
                    }.body
                } catch (e: Exception) {
                    context.getString(R.string.hitokoto_fallback)
                }

                val icon = try {
                    BitmapUtils.decodeAssetsBitmap(context, "bg/a_word.png", 100, 100)
                } catch (e: Exception) {
                    null
                }

                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_hitokoto)
                    views.setTextViewText(R.id.tv_content, hitokoto)
                    icon?.let {
                        views.setImageViewBitmap(R.id.iv_icon, it)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
        }
}