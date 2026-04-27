package project.side.remote.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.SocialAuthDataSource
import project.side.remote.datasource.SocialAuthDataSourceImpl
import project.side.remote.login.CurrentActivityHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocialAuthModule {

    @Provides
    @Singleton
    fun provideSocialAuthDataSource(activityHolder: CurrentActivityHolder): SocialAuthDataSource =
        SocialAuthDataSourceImpl(activityHolder)
}
