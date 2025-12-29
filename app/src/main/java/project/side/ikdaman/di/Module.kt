package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.TestDataSource
import project.side.data.repository.TestRepositoryImpl
import project.side.domain.repository.TestRepository
import project.side.domain.usecase.TestUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {
    @Provides
    @Singleton
    fun provideTestRepository(testDataSource: TestDataSource) =
        TestRepositoryImpl(testDataSource)

    @Provides
    @Singleton
    fun provideTestUseCase(testRepository: TestRepository) = TestUseCase(testRepository)
}