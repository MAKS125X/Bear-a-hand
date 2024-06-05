package com.example.network.networkMonitor.di

//@Module
//class NetworkModule {
//    @AppScope
//    @Provides
//    fun provideHttpClient(
//        httpLoggingInterceptor: HttpLoginInterceptor,
//        liveNetworkMonitor: NetworkMonitorInterceptor,
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(httpLoggingInterceptor)
//            .addInterceptor(liveNetworkMonitor)
//            .build()
//    }
//
//    @Provides
//    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        return interceptor
//    }
//
//    @Singleton
//    @Provides
//    fun provideRetrofit(
//        okHttpClient: OkHttpClient,
//    ): Retrofit {
//        val gson = GsonBuilder()
//            .registerTypeAdapter(
//                CategoryNetworkDeserializer.typeToken,
//                CategoryNetworkDeserializer(),
//            )
//            .registerTypeAdapter(
//                EventsNetworkDeserializer.typeToken,
//                EventsNetworkDeserializer(),
//            )
//            .registerTypeAdapter(
//                EventNetworkDeserializer.objectType,
//                EventNetworkDeserializer(),
//            )
//            .create()
//
//        return Retrofit.Builder()
//            .baseUrl("https://mock.apidog.com/m1/509685-468980-default/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//
//    @Provides
//    fun provideCategoryService(retrofit: Retrofit): CategoryService =
//        retrofit.create(CategoryService::class.java)
//
//    @Provides
//    fun provideEventService(retrofit: Retrofit): EventService =
//        retrofit.create(EventService::class.java)
//}