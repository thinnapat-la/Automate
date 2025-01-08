package tech.grasshopper.reporter.structure;

import java.util.function.Supplier;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageCreator {

	public static PDPage createPotraitPage() {
		return new PDPage(PDRectangle.A4);
	}

	public static PDPage createLandscapePage() {
		return new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
	}

	public static PDPage createPotraitPageAndAddToDocument(PDDocument document) {
		PDPage page = createPotraitPage();
		document.addPage(page);
		return page;
	}

	public static PDPage createLandscapePageAndAddToDocument(PDDocument document) {
		PDPage page = createLandscapePage();
		document.addPage(page);
		return page;
	}

	public static Supplier<PDPage> potraitPageSupplier() {
		return () -> createPotraitPage();
	}

	public static Supplier<PDPage> landscapePageSupplier() {
		return () -> createLandscapePage();
	}
}
