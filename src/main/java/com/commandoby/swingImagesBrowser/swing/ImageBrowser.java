package com.commandoby.swingImagesBrowser.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;

import com.commandoby.swingImagesBrowser.Application;
import com.commandoby.swingImagesBrowser.components.ImageUnit;
import com.commandoby.swingImagesBrowser.service.ImageReader;
import com.commandoby.swingImagesBrowser.service.impl.ImageReaderImpl;

public class ImageBrowser implements Runnable, ActionListener {
	private static final int MIN_WIDTH = 800;
	private static final int MIN_HEIGHT = 600;

	private static final ImageReader imageReader = new ImageReaderImpl();
	private static final Logger log = Logger.getLogger(ImageBrowser.class);

	JFrame frame;
	JPanel upContents;
	JPanel centerContents;
	JPanel downContents;
	JTextField textField = new JTextField(20);
	boolean searchField = false;
	String fullImagePath = "";
	int page = 0;
	int maxPage = 0;

	@Override
	public void run() {
		frame = new JFrame("DT Developer Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		addFrameChangeSizeListener();

		startUpContents();
		startCenterContents();
		startDownContents();
	}

	private void startUpContents() {
		upContents = new JPanel(new BorderLayout());

		JButton openButton = new JButton("Open");
		openButton.addActionListener(this);
		openButton.setActionCommand("open");
		upContents.add(openButton, BorderLayout.WEST);

		if (searchField) {
			textField.setActionCommand("textField");
			textField.addActionListener(this);
			upContents.add(textField, BorderLayout.EAST);
		} else {
			JButton searchButton = new JButton("Search");
			searchButton.setActionCommand("search");
			searchButton.addActionListener(this);
			upContents.add(searchButton, BorderLayout.EAST);
		}

		upContents.add(new JLabel(Application.images.size() + " images in " + Application.path), BorderLayout.CENTER);

		frame.add(upContents, BorderLayout.NORTH);
	}

	private void startCenterContents() {
		centerContents = new JPanel(new FlowLayout(FlowLayout.CENTER));

		getButtonsFromImageList(centerContents);

		frame.add(centerContents, BorderLayout.CENTER);
	}

	private void startDownContents() {
		downContents = new JPanel(new FlowLayout(FlowLayout.CENTER));

		if (page > 0) {
			if (page > 1) {
				addPaginationButton(1);
			}

			if (page > 4) {
				downContents.add(new JLabel("..."));
			}

			if (page > 3) {
				addPaginationButton(page - 2);
			}

			if (page > 2) {
				addPaginationButton(page - 1);
			}

			JButton pageButton = new JButton(String.valueOf(page));
			pageButton.setEnabled(false);
			downContents.add(pageButton);

			if (maxPage - page > 1) {
				addPaginationButton(page + 1);
			}

			if (maxPage - page > 2) {
				addPaginationButton(page + 2);
			}

			if (maxPage - page > 3) {
				downContents.add(new JLabel("..."));
			}

			if (maxPage != page) {
				addPaginationButton(maxPage);
			}

			frame.add(downContents, BorderLayout.SOUTH);
		}
	}

	private void imageUpContents() {
		upContents = new JPanel(new BorderLayout());

		JButton searchButton = new JButton("Back");
		searchButton.setActionCommand("back");
		searchButton.addActionListener(this);
		upContents.add(searchButton, BorderLayout.EAST);

		upContents.add(new JLabel(" " + fullImagePath), BorderLayout.CENTER);

		frame.add(upContents, BorderLayout.NORTH);
	}

	private void imageCenterContents() {
		centerContents = new JPanel(new BorderLayout());

		Dimension screenSize = frame.getSize();
		JLabel label = new JLabel(getIcon(fullImagePath, screenSize.width - 10, screenSize.height - 100));
		centerContents.add(label);

		frame.add(centerContents, BorderLayout.CENTER);
	}

	private void getButtonsFromImageList(JPanel panel) {
		int pageSize = getPageSize(frame.getSize());
		List<ImageUnit> preImageUnitList = null;

		if (textField == null || textField.getText().equals("")) {
			preImageUnitList = Application.images;
		} else {
			preImageUnitList = getSearchImageUnitList();
		}

		List<ImageUnit> imageUnitList = null;
		if (pageSize >= preImageUnitList.size()) {
			imageUnitList = preImageUnitList;
			page = 0;
			maxPage = 0;
		} else {
			imageUnitList = getPageImageUnitList(preImageUnitList, pageSize);
		}

		final List<ImageUnit> finalImageUnitList = imageUnitList;

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < finalImageUnitList.size(); i++) {
					JButton button = new JButton(finalImageUnitList.get(i).getImageName(),
							getIcon(finalImageUnitList.get(i).getAbsolutePath(), 160, 160));
					button.setPreferredSize(new Dimension(200, 200));
					button.setVerticalTextPosition(SwingConstants.BOTTOM);
					button.setHorizontalTextPosition(SwingConstants.CENTER);

					button.setActionCommand(finalImageUnitList.get(i).getAbsolutePath());
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fullImagePath = e.getActionCommand();
							frame.remove(upContents);
							frame.remove(centerContents);
							frame.remove(downContents);
							imageUpContents();
							imageCenterContents();
							frame.revalidate();
						}
					});

					panel.add(button);
					frame.revalidate();
				}
			}

		}, "imageLoader");
		thread.start();
	}

	private int getPageSize(Dimension screenSize) {
		int widthSize = screenSize.width / 209;
		int heightSize = (screenSize.height - 90) / 209;
		return widthSize * heightSize;
	}

	private List<ImageUnit> getSearchImageUnitList() {
		List<ImageUnit> imageUnitList = new ArrayList<>();

		for (ImageUnit imageUnit : Application.images) {
			if (imageUnit.getImageName().matches(".*" + textField.getText() + ".*")) {
				imageUnitList.add(imageUnit);
			}
		}

		return imageUnitList;
	}

	private List<ImageUnit> getPageImageUnitList(List<ImageUnit> preImageUnitList, int pageSize) {
		if (page == 0)
			page = 1;
		maxPage = preImageUnitList.size() / pageSize + 1;
		if (page > maxPage)
			page = maxPage;

		List<ImageUnit> imageUnitList = new ArrayList<>();
		for (int i = 0; i < pageSize; i++) {
			if ((page - 1) * pageSize + i < preImageUnitList.size()) {
				imageUnitList.add(preImageUnitList.get((page - 1) * pageSize + i));
			}
		}

		return imageUnitList;
	}

	private ImageIcon getIcon(String path, int maxWidth, int maxHeight) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			log.error(e);
		}
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		double scaleWidth = maxWidth / (double) imageWidth;
		double scaleHeight = maxHeight / (double) imageHeight;
		double scale = scaleWidth <= scaleHeight ? scaleWidth : scaleHeight;

		return new ImageIcon(image.getScaledInstance((int) (imageWidth * scale), (int) (imageHeight * scale), 1));
	}

	private void addPaginationButton(int newPage) {
		JButton button = new JButton(String.valueOf(newPage));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				page = newPage;
				frame.remove(centerContents);
				frame.remove(downContents);
				startCenterContents();
				startDownContents();
				frame.revalidate();
			}
		});

		downContents.add(button);
	}

	private void addFrameChangeSizeListener() {
		frame.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				frame.remove(centerContents);
				frame.remove(downContents);
				if (fullImagePath == "") {
					startCenterContents();
					startDownContents();
				} else {
					imageCenterContents();
				}
				frame.revalidate();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("open")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				Application.path = jfc.getSelectedFile().getAbsolutePath();
				Application.images = imageReader.read(Application.path);
				page = 0;
				maxPage = 0;
				frame.remove(upContents);
				frame.remove(centerContents);
				frame.remove(downContents);
				startUpContents();
				startCenterContents();
				startDownContents();
			}
		}

		if (e.getActionCommand().equals("search")) {
			searchField = true;
			frame.remove(upContents);
			startUpContents();
		}

		if (e.getActionCommand().equals("textField")) {
			if (textField.getText().equals("")) {
				searchField = false;
				frame.remove(upContents);
				startUpContents();
			}
			frame.remove(centerContents);
			frame.remove(downContents);
			startCenterContents();
			startDownContents();
		}

		if (e.getActionCommand().equals("back")) {
			fullImagePath = "";
			frame.remove(upContents);
			frame.remove(centerContents);
			frame.remove(downContents);
			startUpContents();
			startCenterContents();
			startDownContents();
		}

		frame.revalidate();
	}
}