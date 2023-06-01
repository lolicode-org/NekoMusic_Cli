package org.lolicode.nekomusiccli.cache;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import okhttp3.Cache;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtils {
    private static final String IMG_CACHE_FILE = "image.json";
    private static final String MUSIC_CACHE_FILE = "music.json";
    private static final String LOCK_FILE = "session.lock";
    private FileChannel lockfile = null;
    private FileLock lock = null;
    private final Path cachePath;
    private final Path musicPath;
    private final Path imgPath;
    private final ConcurrentHashMap<String, String> imgCacheMap;
    private final ConcurrentHashMap<String, String> musicCacheMap;
    private final boolean forceDisableCache;

    public CacheUtils(ModConfig config) {
        checkCachePath(config.getCachePath());
        cachePath = Path.of(config.getCachePath());
        musicPath = cachePath.resolve("music");
        imgPath = cachePath.resolve("img");
        try {
            lock();
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to lock cache directory: ", e);
            NekoMusicClient.LOGGER.error("Force disabling cache");
            forceDisableCache = true;
            imgCacheMap = null;
            musicCacheMap = null;
            return;
        }
        imgCacheMap = getCacheMap(IMG_CACHE_FILE);
        musicCacheMap = getCacheMap(MUSIC_CACHE_FILE);
        forceDisableCache = false;
    }

    public Cache getMusicCache() {
        if (forceDisableCache) {
            return null;
        }
        if (NekoMusicClient.config.musicCacheSize == 0) {
            return null;
        }
        return new Cache(musicPath.toFile(),
                 NekoMusicClient.config.musicCacheSize == -1 ?
                         Long.MAX_VALUE : (long) NekoMusicClient.config.musicCacheSize * 1024 * 1024);
    }

    public Cache getImgCache() {
        if (forceDisableCache) {
            return null;
        }
        if (NekoMusicClient.config.imgCacheSize == 0) {
            return null;
        }
        return new Cache(imgPath.toFile(),
                 NekoMusicClient.config.imgCacheSize == -1 ?
                         Long.MAX_VALUE : (long) NekoMusicClient.config.imgCacheSize * 1024 * 1024);
    }

    public String queryImgCache(String hash) {
        if (forceDisableCache) {
            return null;
        }
        return imgCacheMap.get(hash);
    }

    public String queryMusicCache(String hash) {
        if (forceDisableCache) {
            return null;
        }
        return musicCacheMap.get(hash);
    }

    public void updateImgCache(String hash, String url) {
        if (forceDisableCache) {
            return;
        }
        addOrReplace(imgCacheMap, hash, url);
    }

    public void updateMusicCache(String hash, String url) {
        if (forceDisableCache) {
            return;
        }
        addOrReplace(musicCacheMap, hash, url);
    }

    private void addOrReplace(Map<String, String> map, String key, String value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }

    public static boolean checkCachePath(String path) {
        Path p = Path.of(path);
        checkPath(p);
        checkPath(p.resolve("music"));
        checkPath(p.resolve("img"));
        return true;
    }

    private static boolean checkPath(Path path) throws RuntimeException {
        File file = path.toFile();
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException(CacheCheckError.CANT_MAKE_DIR);
            }
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(CacheCheckError.NOT_DIR);
        }
        if (!file.canRead()) {
            throw new RuntimeException(CacheCheckError.CANT_READ);
        }
        if (!file.canWrite()) {
            throw new RuntimeException(CacheCheckError.CANT_WRITE);
        }
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file.getAbsolutePath() + "/README.txt"))) {
            writer.write("This is a cache directory for {}, please do not save any files here manually, as they might be deleted by the program.".replace("{}", NekoMusicClient.MOD_NAME));
            writer.newLine();
            writer.write("这是 {} 的缓存目录，请不要手动保存任何文件到这里，因为它们可能会被程序删除。".replace("{}", NekoMusicClient.MOD_NAME));
            writer.flush();
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to write test file to cache path: " + e.getMessage());
            throw new RuntimeException(CacheCheckError.CANT_WRITE_TEST);
        }
        return true;
    }

    public static String getDefaultCachePath() {
        String path = MinecraftClient.getInstance().runDirectory.toPath().resolve("cache").resolve(NekoMusicClient.MOD_ID).toString();
        checkCachePath(path);
        return path;
    }

    private ConcurrentHashMap<String, String> getCacheMap(String fileName) {
        File file = cachePath.resolve(fileName).toFile();  // Dont save them to subdirs in case being deleted by other things
        if (file.exists()) {
            try {
                return new ConcurrentHashMap<>(readCacheMap(file));
            } catch (IOException e) {
                NekoMusicClient.LOGGER.error("Failed to read cache file: " + fileName, e);
            }
        } else {
            NekoMusicClient.LOGGER.info("Cache file {} not found, creating new one.".replace("{}", fileName));
        }
        return new ConcurrentHashMap<>();
    }

    private static Map<String, String > readCacheMap(File file) throws IOException {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return NekoMusicClient.GSON.fromJson(reader, type);
        }
    }


    private void writeCacheMap(Map<String, String> cacheMap, String cacheFile) {
        if (forceDisableCache) return;
        File file = cachePath.resolve(cacheFile).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            NekoMusicClient.GSON.toJson(cacheMap, writer);
        } catch (IOException e) {
            NekoMusicClient.LOGGER.error("Failed to write cache file: " + cacheFile, e);
        }
    }

    private synchronized void lock() {
        if (lockfile != null) {
            throw new RuntimeException("Cache directory already locked.");
        }
        try {
            lockfile = new FileOutputStream(cachePath.resolve(LOCK_FILE).toFile()).getChannel();
            lock = lockfile.tryLock();
            if (lock == null) throw new RuntimeException("Failed to lock cache directory.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void unlock() {
        if (lockfile == null || lock == null || !lock.isValid()) {
            throw new RuntimeException("Cache directory not locked.");
        }
        try {
            lock.release();
            lockfile.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to unlock cache directory: " + e.getMessage());
        }
        lock = null;
        lockfile = null;
    }

    public synchronized void save() {
        writeCacheMap(imgCacheMap, IMG_CACHE_FILE);
        writeCacheMap(musicCacheMap, MUSIC_CACHE_FILE);
    }

    public void close() {
        save();
        if (lockfile != null) {
            unlock();
        }
    }
}
