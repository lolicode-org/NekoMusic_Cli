package org.lolicode.nekomusiccli.cache;

public class CacheCheckError {
    public static final String CANT_MAKE_DIR = "Failed to create cache directory";
    public static final String NOT_DIR = "Cache path is not a directory";
    public static final String CANT_READ = "Cache path is not readable";
    public static final String CANT_WRITE = "Cache path is not writable";
    public static final String CANT_WRITE_TEST = "Failed to write test file to cache path, this might be caused by permission issues";
}