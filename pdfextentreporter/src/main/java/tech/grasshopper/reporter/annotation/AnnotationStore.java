package tech.grasshopper.reporter.annotation;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import tech.grasshopper.pdf.annotation.Annotation;
import tech.grasshopper.pdf.annotation.FileAnnotation;

@Data
public class AnnotationStore {

	private List<Annotation> testNameAnnotation = new ArrayList<>();

	private List<Annotation> attributeNameAnnotation = new ArrayList<>();

	private List<Annotation> testMediaAnnotation = new ArrayList<>();

	private List<Annotation> testNameMediaAnnotation = new ArrayList<>();

	private List<FileAnnotation> testMediaFileAnnotation = new ArrayList<>();

	public void addTestNameAnnotation(Annotation annotation) {
		testNameAnnotation.add(annotation);
	}

	public void addAttributeNameAnnotation(Annotation annotation) {
		attributeNameAnnotation.add(annotation);
	}

	public void addTestMediaAnnotation(Annotation annotation) {
		testMediaAnnotation.add(annotation);
	}

	public void addTestNameMediaAnnotation(Annotation annotation) {
		testNameMediaAnnotation.add(annotation);
	}

	public void addTestMediaFileAnnotation(FileAnnotation fileAnnotation) {
		testMediaFileAnnotation.add(fileAnnotation);
	}
}
