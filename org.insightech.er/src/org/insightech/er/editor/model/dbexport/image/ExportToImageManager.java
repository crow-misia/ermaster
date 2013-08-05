package org.insightech.er.editor.model.dbexport.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;

public class ExportToImageManager {

	protected Image img;

	private int format;

	private String saveFilePath;

	private String formatName;

	public ExportToImageManager(Image img, int format, String formatName, String saveFilePath) {
		this.img = img;
		this.format = format;
		this.formatName = formatName;
		this.saveFilePath = saveFilePath;
	}

	public void doProcess() throws IOException, InterruptedException {
		writeImage(img, saveFilePath, format, formatName);
	}

	private void writeImage(Image image, String saveFilePath, int format, String formatName)
			throws IOException, InterruptedException {
		try {
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			loader.save(saveFilePath, format);

		} catch (SWTException e) {
			// Eclipse 3.2 では、 PNG が Unsupported or unrecognized format となるため、
			// 以下の代替方法を使用する
			// ただし、この方法では上手く出力できない環境あり

			e.printStackTrace();
			BufferedImage bufferedImage = new BufferedImage(
					image.getBounds().width, image.getBounds().height,
					BufferedImage.TYPE_INT_RGB);

			drawAtBufferedImage(bufferedImage, image, 0, 0);

			ImageIO.write(bufferedImage, formatName, new File(saveFilePath));
		}
	}

	private void drawAtBufferedImage(BufferedImage bimg, Image image, int x,
			int y) throws InterruptedException {

		ImageData data = image.getImageData();
		final int width = image.getBounds().width;
		final int height = image.getBounds().height;

		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {
				int tmp = 4 * (j * width + i);

				if (data.data.length > tmp + 2) {
					int r = 0xff & data.data[tmp + 2];
					int g = 0xff & data.data[tmp + 1];
					int b = 0xff & data.data[tmp];

					bimg.setRGB(i + x, j + y, 0xFF << 24 | r << 16 | g << 8
							| b << 0);
				}

				this.doPostTask();
			}
		}
	}

	protected void doPreTask(HtmlReportPageGenerator<?> pageGenerator,
			Object object) {
	}

	protected void doPostTask() throws InterruptedException {
	}
}
