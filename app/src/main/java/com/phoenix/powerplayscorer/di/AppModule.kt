package com.phoenix.powerplayscorer.di

import android.content.Context
import androidx.room.Room
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDatabase
import com.phoenix.powerplayscorer.feature_editor.data.repository.AuthRepositoryImpl
import com.phoenix.powerplayscorer.feature_editor.data.repository.NewRepositoryImpl
import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.*
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext appContext: Context
    ): MatchDatabase {
        return Room.databaseBuilder(
            appContext,
            MatchDatabase::class.java,
            MatchDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(
        db: MatchDatabase,
        authUseCases: AuthUseCases,
    ): Repository {
        return NewRepositoryImpl(
            db.matchDao,
            authUseCases,
        )
    }

    @Provides
    @Singleton
    fun provideMatchUseCases(repository: Repository): MatchUseCases {
        return MatchUseCases(
            getMatches = GetMatches(repository),
            getMatch = GetMatch(repository),
            saveMatch = SaveMatch(repository),
            saveMatches = SaveMatches(repository),
            deleteMatches = DeleteMatches(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repo: AuthRepository): AuthUseCases {
        return AuthUseCases(
            isUserSignedIn = IsUserSignedIn(repo),
            loginOnline = LoginOnline(repo),
            register = Register(repo),
            getUserId = GetUserId(repo),
            signInOffline = SignInOffline(repo),
            signOut = SignOut(repo),
            getUserIdFlow = GetUserIdFlow(repo)
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }
}