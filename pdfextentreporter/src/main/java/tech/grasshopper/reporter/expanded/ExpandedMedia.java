package tech.grasshopper.reporter.expanded;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.structure.cell.ImageCell;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.medias.Medias;

@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ExpandedMedia extends Medias {

	public ImageCell createImageCell() {
		PDImageXObject image = processPDImage();

		return ImageCell.builder().image(image).width(image.getWidth()).padding(padding).maxHeight(image.getHeight())
				.build();
	}
}
