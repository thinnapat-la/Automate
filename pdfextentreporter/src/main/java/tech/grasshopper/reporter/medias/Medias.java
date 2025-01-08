package tech.grasshopper.reporter.medias;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.structure.cell.ImageCell;

import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.service.MediaService;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Medias {

	protected Media media;
	protected PDDocument document;
	protected PDImageXObject image;
	protected String[] locations;

	protected float padding;

	@Getter
	protected boolean imageNotAvailable = false;

	protected static PDImageXObject imageNotFound = null;

	private void initializeNotFoundImage() throws IOException {
		if (imageNotFound == null) {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("not-found-image.png");
			byte[] targetArray = new byte[is.available()];
			is.read(targetArray);

			imageNotFound = PDImageXObject.createFromByteArray(document, targetArray, "not-found-image");
		}
	}

	private void generatePDImage() throws IOException {
		// create base64 image file
		if (MediaService.isBase64(media))
			image = imageNotFound;
		else {
			String path = media.getResolvedPath();
			if (path == null || path.isEmpty())
				path = media.getPath();

			if (!new File(path).exists()) {
				// System.out.println(media.getPath());
				// Media m = new Media(media.getPath(), "", media.getResolvedPath(), new
				// HashMap<String, Object>());
				MediaService.tryResolveMediaPath(media, locations);
				if (media.getResolvedPath() != null)
					path = media.getResolvedPath();
			}
			// System.out.println("pdf path - " + path);
			image = PDImageXObject.createFromFile(path, document);
		}
	}

	protected PDImageXObject processPDImage() {
		try {
			initializeNotFoundImage();
			generatePDImage();
		} catch (Exception e) {
			// Todo write logger
			image = imageNotFound;
			imageNotAvailable = true;
		}
		return image;
	}

	public abstract ImageCell createImageCell();
}
