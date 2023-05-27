package org.lolicode.nekomusiccli.cache;

import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.FileUtils;
import org.lolicode.nekomusiccli.NekoMusicClient;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;

public class CacheUtils {
    private final Path cachePath;
    private final Path musicPath;
    private final Path imgPath;

    public CacheUtils() {
        CheckCachePath(NekoMusicClient.config.cachePath);
        cachePath = Path.of(NekoMusicClient.config.cachePath);
        musicPath = cachePath.resolve("music");
        imgPath = cachePath.resolve("img");
        new Thread(this::checkLimit).start();
    }

    public BufferedInputStream getFromCache(String hash, int type) {
        return switch (type) {
            case CacheType.MUSIC -> getMusicFromCache(hash);
            case CacheType.IMG -> getImgFromCache(hash);
            default -> null;
        };
    }

    public BufferedInputStream getMusicFromCache(String hash) {
        File file = musicPath.resolve(hash).toFile();
        if (file.exists()) {
            try {
                return new BufferedInputStream(FileUtils.openInputStream(file));
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to read music from cache: " + e.getMessage());
            }
        }
        return null;
    }

    public BufferedInputStream getImgFromCache(String hash) {
        File file = imgPath.resolve(hash).toFile();
        if (file.exists()) {
            try {
                return new BufferedInputStream(FileUtils.openInputStream(file));
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to read img from cache: " + e.getMessage());
            }
        }
        return null;
    }

    public void saveToCache(String hash, byte[] data, int type) {
        switch (type) {
            case CacheType.MUSIC -> saveMusicToCache(hash, data);
            case CacheType.IMG -> saveImgToCache(hash, data);
        }
    }

    public void saveMusicToCache(String hash, byte[] data) {
        try {
            if (musicPath.resolve(hash + ".tmp").toFile().exists())
                FileUtils.forceDelete(musicPath.resolve(hash + ".tmp").toFile());
            FileUtils.writeByteArrayToFile(musicPath.resolve(hash + ".tmp").toFile(), data);
            FileUtils.moveFile(musicPath.resolve(hash + ".tmp").toFile(), musicPath.resolve(hash).toFile());
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to save music to cache: " + e.getMessage());
        }
    }

    public void saveImgToCache(String hash, byte[] data) {
        try {
            if (imgPath.resolve(hash + ".tmp").toFile().exists())
                FileUtils.forceDelete(imgPath.resolve(hash + ".tmp").toFile());
            FileUtils.writeByteArrayToFile(imgPath.resolve(hash + ".tmp").toFile(), data);
            FileUtils.moveFile(imgPath.resolve(hash + ".tmp").toFile(), imgPath.resolve(hash).toFile());
        } catch (Exception e) {
            NekoMusicClient.LOGGER.error("Failed to save img to cache: " + e.getMessage());
        }
    }

    private void checkLimit() {
        checkLimit(musicPath, NekoMusicClient.config.musicCacheSize);
        checkLimit(imgPath, NekoMusicClient.config.imgCacheSize);
    }

    private void checkLimit(Path path, int size) {
        if (size == 0) {
            clearCache(path);
        } else if (size > 0) {
            DeleteFilesBySizeAndDate.deleteFilesBySizeAndDate(path, (long) size * 1024 * 1024);
        }
    }

    private void clearCache(Path path) {
        if (path.toFile().exists()) {
            try {
                FileUtils.cleanDirectory(path.toFile());
            } catch (Exception e) {
                NekoMusicClient.LOGGER.error("Failed to clear cache: " + e.getMessage());
            }
        }
    }

    public static boolean CheckCachePath(String path) {
        Path p = Path.of(path);
        CheckPath(p);
        CheckPath(p.resolve("music"));
        CheckPath(p.resolve("img"));
        return true;
    }

    private static boolean CheckPath(Path path) throws RuntimeException {
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
        CheckCachePath(path);
        return path;
    }
}
