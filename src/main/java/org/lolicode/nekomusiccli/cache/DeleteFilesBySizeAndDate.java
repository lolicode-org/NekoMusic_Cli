package org.lolicode.nekomusiccli.cache;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class DeleteFilesBySizeAndDate {

    // A comparator that compares files by their last modified date
    public static class FileDateComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return Long.compare(f1.lastModified(), f2.lastModified());
        }
    }

    // A method that deletes files in a directory by size and date
    public static void deleteFilesBySizeAndDate(Path dirPath, long sizeLimit) {
        // Get the directory as a File object
        File dir = dirPath.toFile();
        // Check if it is a valid directory
        if (dir.isDirectory()) {
            // Get the files in the directory as an array
            File[] files = dir.listFiles();
            // Sort the files by date using the comparator
            Arrays.sort(files, new FileDateComparator());
            // Initialize a variable to store the total size
            long totalSize = 0;
            // Loop over the files and add their sizes
            for (File file : files) {
                totalSize += file.length();
            }
            // Loop over the files again and delete the oldest ones until under the limit
            for (File file : files) {
                // If the total size is under the limit, break the loop
                if (totalSize <= sizeLimit) {
                    break;
                }
                // Delete the file and subtract its size from the total
                long len = file.length();
                if (file.delete())
                    totalSize -= len;
            }
        } else {
            throw new IllegalArgumentException("The path is not a directory");
        }
    }
}

