package project.side.widget.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import project.side.widget.data.GlanceWidgetUpdateNotifier
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUpdateNotifier
import project.side.widget.data.WidgetUpdater
import project.side.widget.data.WidgetUpdaterImpl

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class WidgetCacheStore
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class WidgetPrefsStore

private val Context.widgetCacheStore by preferencesDataStore(name = "widget_cache")
private val Context.widgetPrefsStore by preferencesDataStore(name = "widget_prefs")

@Module
@InstallIn(SingletonComponent::class)
object WidgetProvideModule {
    @Provides @Singleton @WidgetCacheStore
    fun provideWidgetCacheStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.widgetCacheStore

    @Provides @Singleton @WidgetPrefsStore
    fun provideWidgetPrefsStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.widgetPrefsStore

    @Provides @Singleton
    fun provideWidgetCache(@WidgetCacheStore store: DataStore<Preferences>): WidgetCache =
        WidgetCache(store)

    @Provides @Singleton
    fun provideWidgetPreferences(@WidgetPrefsStore store: DataStore<Preferences>): WidgetPreferences =
        WidgetPreferences(store)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetBindModule {
    @Binds @Singleton
    abstract fun bindWidgetUpdater(impl: WidgetUpdaterImpl): WidgetUpdater

    @Binds @Singleton
    abstract fun bindWidgetUpdateNotifier(impl: GlanceWidgetUpdateNotifier): WidgetUpdateNotifier
}
