package com.commandoby.swingImagesBrowser.components;

import java.util.Objects;

public class ImageUnit {
	String absolutePath;
	String imageName;
	String imageExtension;

	public ImageUnit(String absolutePath, String imageName, String imageExtension) {
		this.absolutePath = absolutePath;
		this.imageName = imageName;
		this.imageExtension = imageExtension;
	}

	public ImageUnit(String absolutePath, String imageFullName) {
		this.absolutePath = absolutePath;
		if (imageFullName.lastIndexOf(".") > 0) {
			this.imageName = imageFullName.substring(0, imageFullName.lastIndexOf("."));
			this.imageExtension = imageFullName.substring(imageFullName.lastIndexOf(".") + 1);
		} else {
			this.imageName = imageFullName;
			this.imageExtension = "";
		}
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath, imageExtension, imageName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageUnit other = (ImageUnit) obj;
		return Objects.equals(absolutePath, other.absolutePath) && Objects.equals(imageExtension, other.imageExtension)
				&& Objects.equals(imageName, other.imageName);
	}

	@Override
	public String toString() {
		return "ImageUnit [absolutePath=" + absolutePath + ", imageName=" + imageName + ", imageExtension="
				+ imageExtension + "]";
	}
}
