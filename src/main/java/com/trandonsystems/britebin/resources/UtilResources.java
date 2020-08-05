package com.trandonsystems.britebin.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class UtilResources {

	// These are for demo only
	public static final String UPLOAD_FOLDER = "/uploadedFiles/";
	
	/**
	* Utility method to save InputStream data to target location/file
	*
	* @param inStream - InputStream to be saved
	* @param target   - full path to destination file
	*/
	public static void saveToFile(InputStream inStream, String target) throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}
	
	/**
	* Creates a folder to desired location if it not already exists
	*
	* @param dirName - full path to the folder
	* @throws SecurityException - in case you don't have permission to create the
	*                           folder
	*/
	public static void createFolderIfNotExists(String dirName) throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}
	
}
