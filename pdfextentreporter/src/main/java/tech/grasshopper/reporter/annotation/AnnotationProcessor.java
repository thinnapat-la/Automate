package tech.grasshopper.reporter.annotation;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;
import tech.grasshopper.reporter.destination.Destination;
import tech.grasshopper.reporter.destination.Destination.DestinationStore;

@Data
@Builder
public class AnnotationProcessor {

	private static final Logger logger = Logger.getLogger(AnnotationProcessor.class.getName());

	@NonNull
	private AnnotationStore annotations;

	@NonNull
	private DestinationStore destinations;

	protected ExtentPDFReporterConfig config;

	public void processAnnotations() {
		if (config.isDisplayAttributeSummary() && config.isDisplayAttributeDetails())
			processAttributeNameAnnotation();

		if (config.isDisplayAttributeDetails() && config.isDisplayTestDetails())
			processTestNameAnnotation();

		if (config.isDisplayTestDetails() && config.isDisplayExpandedMedia())
			processExpandedMediaAnnotation();
	}

	private void processTestNameAnnotation() {
		destinations.getTestDestinations().forEach(d -> {
			List<Annotation> matchedAnnotations = annotations.getTestNameAnnotation().stream()
					.filter(a -> a.getId() == d.getId()).collect(Collectors.toList());
			processMatchedAnnotations(matchedAnnotations, d);
		});
	}

	private void processAttributeNameAnnotation() {
		destinations.getAttributeDetailDestinations().forEach(d -> {
			List<Annotation> matchedAnnotations = annotations.getAttributeNameAnnotation().stream()
					.filter(a -> a.getTitle().equals(d.getName())).collect(Collectors.toList());
			processMatchedAnnotations(matchedAnnotations, d);
		});
	}

	private void processExpandedMediaAnnotation() {
		destinations.getTestMediaDestinations().forEach(d -> {
			List<Annotation> matchedAnnotations = annotations.getTestMediaAnnotation().stream()
					.filter(a -> a.getId() == d.getId()).collect(Collectors.toList());
			processMatchedAnnotations(matchedAnnotations, d);
		});

		destinations.getTestDestinations().forEach(d -> {
			List<Annotation> matchedAnnotations = annotations.getTestNameMediaAnnotation().stream()
					.filter(a -> a.getId() == d.getId()).collect(Collectors.toList());
			processMatchedAnnotations(matchedAnnotations, d);
		});
	}

	private void processMatchedAnnotations(List<Annotation> matchedAnnotation, Destination destination) {

		matchedAnnotation.forEach(a -> {
			PDActionGoTo action = new PDActionGoTo();
			action.setDestination(destination.createPDPageDestination());

			PDAnnotationLink annotationLink = a.createPDAnnotationLink();
			annotationLink.setAction(action);

			try {
				a.getPage().getAnnotations().add(annotationLink);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Unable to create annotation link", e);
			}
		});
	}

}
