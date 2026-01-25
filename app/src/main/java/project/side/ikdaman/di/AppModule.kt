package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.HistoryDataSource
import project.side.data.datasource.TestDataSource
import project.side.data.repository.HistoryRepositoryImpl
import project.side.data.repository.TestRepositoryImpl
import project.side.data.repository.UserRepositoryImpl
import project.side.domain.repository.HistoryRepository
import project.side.domain.repository.TestRepository
import project.side.domain.repository.UserRepository
import project.side.domain.usecase.GetHistoryBooksUseCase
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
    fun provideTestUseCase(testRepository: TestRepository) = TestUseCase(testRepository)

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
    fun provideHistoryBookRepository(historyDataSource: HistoryDataSource): HistoryRepository =
        HistoryRepositoryImpl(historyDataSource)

    @Provides
    @Singleton
    fun provideGetHistoryBooksUseCase(historyRepository: HistoryRepository) =
        GetHistoryBooksUseCase(historyRepository)
}