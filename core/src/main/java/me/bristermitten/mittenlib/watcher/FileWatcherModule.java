package me.bristermitten.mittenlib.watcher;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class FileWatcherModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FileWatcherService.class).in(Singleton.class);
    }
}
