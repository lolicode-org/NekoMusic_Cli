package org.lolicode.nekomusiccli.utils;

import org.lolicode.nekomusiccli.NekoMusicClient;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class InstanceLock {
    private static final Path LOCK_FILE = Path.of(System.getProperty("java.io.tmpdir"), "nekomusiccli.lock");
    private static FileChannel lockfile = null;
    private static FileLock lock = null;

    private synchronized static void lock() throws IOException {
        lockfile = FileChannel.open(LOCK_FILE, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        lock = lockfile.tryLock();
        if (lock == null) {
            throw new IOException("Failed to lock instance");
        }
    }

    private synchronized static void unlock() throws IOException {
        if (lock != null) {
            lock.release();
        }
        if (lockfile != null) {
            lockfile.close();
        }

        lock = null;
        lockfile = null;
    }

    // if no other instance is running, lock the instance and return true
    public static boolean checkLock() {
        if (lockfile != null && lock != null) {
            return true;
        }
        try {
            lock();
            return true;
        } catch (OverlappingFileLockException e) {
            NekoMusicClient.LOGGER.warn("NekoMusic: Instance lock is already held by another thread: ", e);
            return true;
        } catch (IOException e) {
            NekoMusicClient.LOGGER.info("NekoMusic: Failed to lock instance, another instance is running");
            return false;
        }
    }

    public static void release() {
        try {
            unlock();
        } catch (IOException e) {
            NekoMusicClient.LOGGER.error("Failed to release lock: ", e);
        }
    }
}
