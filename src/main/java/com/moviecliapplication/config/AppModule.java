package com.moviecliapplication.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.moviecliapplication.client.TMDbClient;
import com.moviecliapplication.client.TMDbClientImpl;
import com.moviecliapplication.service.MovieService;
import com.moviecliapplication.service.MovieServiceImpl;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MovieService.class).to(MovieServiceImpl.class);
        bind(TMDbClient.class).to(TMDbClientImpl.class);
        bind(AppConfig.class).in(Singleton.class);
    }
}
