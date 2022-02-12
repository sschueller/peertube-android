package net.schueller.peertube.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.schueller.peertube.common.Constants
import net.schueller.peertube.feature_server_address.data.data_source.database.ServerAddressDatabase
import net.schueller.peertube.feature_video.data.remote.PeerTubeApi
import net.schueller.peertube.feature_server_address.data.data_source.remote.ServerInstanceApi
import net.schueller.peertube.feature_server_address.data.repository.ServerAddressRepositoryImpl
import net.schueller.peertube.feature_server_address.data.repository.ServerRepositoryImpl
import net.schueller.peertube.feature_video.data.repository.VideoRepositoryImpl
import net.schueller.peertube.feature_server_address.domain.repository.ServerAddressRepository
import net.schueller.peertube.feature_server_address.domain.repository.ServerRepository
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import net.schueller.peertube.feature_server_address.domain.use_case.*
import net.schueller.peertube.feature_settings.settings.data.repository.SettingsRepositoryImpl
import net.schueller.peertube.feature_settings.settings.domain.repository.SettingsRepository
import net.schueller.peertube.feature_video.data.remote.auth.LoginService
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.data.repository.RetrofitInstance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideServerAddressDatabase(app: Application): ServerAddressDatabase {
        return Room.databaseBuilder(
            app,
            ServerAddressDatabase::class.java,
            ServerAddressDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideServerAddressRepository(db: ServerAddressDatabase): ServerAddressRepository {
        return ServerAddressRepositoryImpl(db.serverAddressDao)
    }

    @Provides
    @Singleton
    fun provideServerAddressUseCases(
        repository: ServerAddressRepository,
        @ApplicationContext context: Context,
        session: Session,
        loginService: LoginService,
        retrofitInstance: RetrofitInstance
    ): ServerAddressUseCases {
        return ServerAddressUseCases(
            getServerAddressesUseCase = GetServerAddressesUseCase(repository),
            deleteServerAddressUseCase = DeleteServerAddressUseCase(repository),
            addServerAddressUseCase = AddServerAddressUseCase(repository),
            getServerAddressUseCase = GetServerAddressUseCase(repository),
            selectServerAddressUseCase = SelectServerAddressUseCase(context,session,loginService, retrofitInstance)
        )
    }


    @Provides
    fun providePeerTubeApi(
        retrofitInstance: RetrofitInstance
    ): PeerTubeApi {
        return retrofitInstance.getRetrofitInstance()
    }

    @Provides
    @Singleton
    fun provideVideoRepository(api: PeerTubeApi): VideoRepository {
        return VideoRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideServerInstanceApi(): ServerInstanceApi {
        return Retrofit.Builder()
            .baseUrl(Constants.SERVER_IDX_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerInstanceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideServerRepository(api: ServerInstanceApi): ServerRepository {
        return ServerRepositoryImpl(api)
    }
}