package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.AladinBookSearchSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.HistoryDataSource
import project.side.data.datasource.TestDataSource

import project.side.data.repository.AladinRepositoryImpl
import project.side.data.repository.HistoryRepositoryImpl
import project.side.data.repository.AuthEventRepositoryImpl
import project.side.data.repository.TestRepositoryImpl
import project.side.data.repository.UserRepositoryImpl
import project.side.domain.repository.AladinRepository
import project.side.domain.repository.HistoryRepository
import project.side.domain.repository.AuthEventRepository
import project.side.domain.repository.TestRepository
import project.side.domain.repository.UserRepository
import project.side.domain.usecase.GetHistoryBooksUseCase
import project.side.domain.usecase.GetAuthEventUseCase
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.TestUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTestRepository(testDataSource: TestDataSource): TestRepository =
        TestRepositoryImpl(testDataSource)

    @Provides
    @Singleton
    fun provideAladinRepository(aladinBookSearchSource: AladinBookSearchSource): AladinRepository =
        AladinRepositoryImpl(aladinBookSearchSource)


    @Provides
    @Singleton
    fun provideTestUseCase(testRepository: TestRepository) = TestUseCase(testRepository)

    @Provides
    @Singleton
    fun provideBackendRepository(backendDataSource: project.side.data.datasource.BackendDataSource): project.side.domain.repository.BackendRepository =
        project.side.data.repository.BackendRepositoryImpl(backendDataSource)

    @Provides
    @Singleton
    fun provideSaveManualBookInfoUseCase(backendRepository: project.side.domain.repository.BackendRepository) =
        project.side.domain.usecase.SaveManualBookInfoUseCase(backendRepository)

    @Provides
    @Singleton
    fun provideUserRepository(authDataStoreSource: AuthDataStoreSource): UserRepository =
        UserRepositoryImpl(authDataStoreSource)

    @Provides
    @Singleton
    fun provideGetLoginStateUseCase(userRepository: UserRepository) =
        GetLoginStateUseCase(userRepository)

    @Provides
    @Singleton
    fun provideAuthEventRepository(): AuthEventRepository = AuthEventRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthEventUseCase(authEventRepository: AuthEventRepository): GetAuthEventUseCase =
        GetAuthEventUseCase(authEventRepository)

    @Provides
    @Singleton
    fun provideHistoryBookRepository(historyDataSource: HistoryDataSource): HistoryRepository =
        HistoryRepositoryImpl(historyDataSource)

    @Provides
    @Singleton
    fun provideGetHistoryBooksUseCase(historyRepository: HistoryRepository) =
        GetHistoryBooksUseCase(historyRepository)
}
