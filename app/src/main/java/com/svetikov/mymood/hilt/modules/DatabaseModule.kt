package com.svetikov.mymood.hilt.modules

import android.content.Context
import androidx.room.Room
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providerDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "periodic_app_db"
        ).build()
    }

    @Provides
    fun provideActionDao(database: AppDatabase):ActionDao{
        return database.actionDao()
    }
}