package project.side.remote.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import project.side.data.datasource.SocialAuthDataSource
import project.side.remote.datasource.SocialAuthDataSourceImpl

@Module
@InstallIn(ActivityComponent::class)
object SocialAuthModule {

    @Provides
    @ActivityScoped
    fun provideSocialAuthDataSource(@ActivityContext context: Context): SocialAuthDataSource = SocialAuthDataSourceImpl(context)
}