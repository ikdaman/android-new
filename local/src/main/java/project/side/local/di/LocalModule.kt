package project.side.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.AuthDataStoreSource
import project.side.local.datasource.AuthDataStoreSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    private val Context.AuthDatStore: DataStore<Preferences> by preferencesDataStore(name = "auth_pref")

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.AuthDatStore
    }

    @Provides
    @Singleton
    fun provideAuthDataStoreSource(authDataStore: DataStore<Preferences>): AuthDataStoreSource {
        return AuthDataStoreSourceImpl(authDataStore)
    }
}