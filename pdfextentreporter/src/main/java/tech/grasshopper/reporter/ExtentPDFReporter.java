package tech.grasshopper.reporter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aventstack.extentreports.config.external.JsonConfigLoader;
import com.aventstack.extentreports.config.external.XmlConfigLoader;
import com.aventstack.extentreports.model.Report;
import com.aventstack.extentreports.observer.ReportObserver;
import com.aventstack.extentreports.observer.entity.ReportEntity;
import com.aventstack.extentreports.reporter.AbstractFileReporter;
import com.aventstack.extentreports.reporter.ReporterConfigurable;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import tech.grasshopper.reporter.config.ExtentPDFReporterConfig;

public class ExtentPDFReporter extends AbstractFileReporter
		implements ReportObserver<ReportEntity>, ReporterConfigurable {

	private static final Logger logger = Logger.getLogger(ExtentPDFReporter.class.getName());
	private static final String REPORTER_NAME = "pdf";
	private static final String FILE_NAME = "report.pdf";

	private Disposable disposable;
	private Report report;

	protected ExtentPDFReporterConfig config = ExtentPDFReporterConfig.builder().reporter(this).build();

	public ExtentPDFReporter(String path) {
		super(new File(path));
	}

	public ExtentPDFReporter(File f) {
		super(f);
	}

	public ExtentPDFReporterConfig config() {
		return config;
	}

	public ExtentPDFReporter config(ExtentPDFReporterConfig conf) {
		conf.setReporter(this);
		this.config = conf;
		return this;
	}

	@Override
	public void loadJSONConfig(File jsonFile) throws IOException {
		final JsonConfigLoader<ExtentPDFReporterConfig> loader = new JsonConfigLoader<ExtentPDFReporterConfig>(config,
				jsonFile);
		loader.apply();
	}

	@Override
	public void loadJSONConfig(String jsonString) throws IOException {
		final JsonConfigLoader<ExtentPDFReporterConfig> loader = new JsonConfigLoader<ExtentPDFReporterConfig>(config,
				jsonString);
		loader.apply();
	}

	@Override
	public void loadXMLConfig(File xmlFile) throws IOException {
		final XmlConfigLoader<ExtentPDFReporterConfig> loader = new XmlConfigLoader<ExtentPDFReporterConfig>(config,
				xmlFile);
		loader.apply();
	}

	@Override
	public void loadXMLConfig(String xmlFile) throws IOException {
		loadXMLConfig(new File(xmlFile));
	}

	public Observer<ReportEntity> getReportObserver() {
		return new Observer<ReportEntity>() {

			public void onSubscribe(Disposable d) {
				start(d);
			}

			public void onNext(ReportEntity value) {
				flush(value);
			}

			public void onError(Throwable e) {
			}

			public void onComplete() {
			}
		};
	}

	private void start(Disposable d) {
		disposable = d;
	}

	private void flush(ReportEntity value) {
		try {
			report = value.getReport();
			final String filePath = getFileNameAsExt(FILE_NAME, new String[] { ".pdf" });

			ReportGenerator reportGenerator = new ReportGenerator(report, config, new File(filePath));
			reportGenerator.generate();
		} catch (Exception e) {
			disposable.dispose();
			logger.log(Level.SEVERE, "An exception occurred", e);
		}
	}
}
