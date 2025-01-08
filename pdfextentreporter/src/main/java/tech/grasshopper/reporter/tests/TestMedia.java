package tech.grasshopper.reporter.tests;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.structure.cell.ImageCell;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.medias.Medias;

@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TestMedia extends Medias {

	private float width;
	private float height;

	public ImageCell createImageCell() {
		PDImageXObject image = processPDImage();

		return ImageCell.builder().image(image).width(width).padding(padding).maxHeight(height).build();
	}
}
