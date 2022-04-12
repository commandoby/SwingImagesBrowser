package com.commandoby.swingImagesBrowser.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.commandoby.swingImagesBrowser.components.ImageUnit;
import com.commandoby.swingImagesBrowser.service.ImageReader;

public class ImageReaderImpl implements ImageReader {

	public List<ImageUnit> read(String path) {
		List<ImageUnit> images = new ArrayList<>();

		File[] files = new File(path).listFiles();
		if (files != null && files.length > 0) {
		for (File file : files) {
			if (ifImage(file)) {
				images.add(new ImageUnit(file.getAbsolutePath(), file.getName()));
			}
		}
		}
		return images;
	}

	private boolean ifImage(File file) {
		if (file.getName().lastIndexOf(".") > 0) {
			String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("bmp")) {
				return true;
			}
		}
		return false;
	}
}
