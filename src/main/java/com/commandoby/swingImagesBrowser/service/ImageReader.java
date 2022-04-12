package com.commandoby.swingImagesBrowser.service;

import java.util.List;

import com.commandoby.swingImagesBrowser.components.ImageUnit;

public interface ImageReader {
	public List<ImageUnit> read(String path);
}
