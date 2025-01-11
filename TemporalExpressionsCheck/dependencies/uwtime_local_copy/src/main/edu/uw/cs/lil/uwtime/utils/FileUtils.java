package edu.uw.cs.lil.uwtime.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	private FileUtils() {
	}

	public static File streamToFile(InputStream stream, String name) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile(name, "tmp");
			tempFile.deleteOnExit();
			int n;
			final int length = 2048;
			final byte[] buffer = new byte[length];
			try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
				while ((n = stream.read(buffer, 0, length)) != -1) {
					outputStream.write(buffer, 0, n);
				}
			}
		} catch (final IOException e) {
			System.err.println("Unable to create temporary file");
		}
		return tempFile;
	}
}
