package org.lolicode.nekomusiccli.cache;

import net.minecraft.client.MinecraftClient;
import org.lolicode.nekomusiccli.NekoMusicClient;
import org.lolicode.nekomusiccli.config.ModConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;

public class CacheUtils {
    private final Path cachePath;
    private final Path musicPath;
    private final Path imgPath;

    public CacheUtils(ModConfig config) {
        checkCachePath(config.getCachePath());
        cachePath = Path.of(config.getCachePath());
        musicPath = cachePath.resolve("music");
        imgPath = cachePath.resolve("img");
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

    public File getMusicCachePath() {
        return musicPath.toFile();
    }

    public File getImgCachePath() {
        return imgPath.toFile();
    }
}
