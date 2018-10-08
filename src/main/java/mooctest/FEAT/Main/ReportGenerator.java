package mooctest.FEAT.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

/**
 * This example creates a HTML report for eclipse like projects based on a
 * single execution data store called jacoco.exec. The report contains no
 * grouping information.
 * 
 * The class files under test must be compiled with debug information, otherwise
 * source highlighting will not work.
 */
public class ReportGenerator {

	private final String title;

	
	
	private final File classesDirectory;
	private final File sourceDirectory;
	private final File reportDirectory;

	private ExecFileLoader execFileLoader;

	private String patt;

	private String[] regexes;



	private String filename;

	private File projectDirectory;

	/**
	 * Create a new generator based for the given project.
	 * 
	 *
	 */

	public ReportGenerator(String filename, String classesDir, String sourceDir, String reportDir, String projectDir) {

		projectDirectory = new File(projectDir);
		this.title = projectDirectory.getName();

		patt = "**/R.class,**/R$*.class,**/BuildConfig.*,**/Manifest*.*,**/*Test*.*,android/**/*.*";
		regexes = patt.split(",");
		this.filename = filename;
		this.classesDirectory = new File(projectDirectory, classesDir != null ? classesDir : "/app/build/intermediates/classes");
		this.sourceDirectory = new File(projectDirectory, sourceDir != null ? sourceDir : "/app/src/main/java");
		this.reportDirectory = new File(projectDirectory, reportDir != null ? reportDir : "../coveragereport");
	}
	/**
	 * Create the report.
	 * 
	 * @throws IOException
	 */
	public void create() throws IOException {

		// Read the jacoco.exec file. Multiple data files could be merged
		// at this point
		loadExecutionData();

		// Run the structure analyzer on a single class folder to build up
		// the coverage model. The process would be similar if your classes
		// were in a jar file. Typically you would create a bundle for each
		// class folder and each jar you want in your report. If you have
		// more than one bundle you will need to add a grouping node to your
		// report
		final IBundleCoverage bundleCoverage = analyzeStructure();

		createReport(bundleCoverage);

	}

	private void createReport(final IBundleCoverage bundleCoverage) throws IOException {

		// Create a concrete report visitor based on some supplied
		// configuration. In this case we use the defaults
		final HTMLFormatter htmlFormatter = new HTMLFormatter();
		
		IReportVisitor visitor = htmlFormatter.createVisitor(new FileMultiReportOutput(reportDirectory));

		// Initialize the report with all of the execution and session
		// information. At this point the report doesn't know about the
		// structure of the report being created
		visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
				execFileLoader.getExecutionDataStore().getContents());

		// Populate the report structure with the bundle coverage information.
		// Call visitGroup if you need groups in your report.
		visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(sourceDirectory, "utf-8", 4));

		// Signal end of structure information to allow report to write all
		// information out
		visitor.visitEnd();
		
		
		XMLFormatter formatter = new XMLFormatter();
		
		File xml = new File(projectDirectory.getAbsolutePath() + "report.xml");
		FileOutputStream fileOutputStream = new FileOutputStream(xml);
		visitor = formatter.createVisitor(fileOutputStream);

		// Initialize the report with all of the execution and session
		// information. At this point the report doesn't know about the
		// structure of the report being created
		visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
				execFileLoader.getExecutionDataStore().getContents());

		// Populate the report structure with the bundle coverage information.
		// Call visitGroup if you need groups in your report.
		visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(sourceDirectory, "utf-8", 4));

		// Signal end of structure information to allow report to write all
		// information out
		visitor.visitEnd();
		

	}

	private void loadExecutionData() throws IOException {
		String[] files = filename.split(",");
		execFileLoader = new ExecFileLoader();
		for (String file : files) {
			File executionDataFile = new File(projectDirectory, file != null ? file : "coverage.exec");
			execFileLoader.load(executionDataFile);
		}
	}

	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);
		ArrayList<File> fileNames = new ArrayList<File>();

		List<File> files = getFileNames(fileNames, classesDirectory.toPath());
		for (File file : files) {
			analyzer.analyzeAll(file);
		}
		// analyzer.analyzeAll(classesDirectory);
		return coverageBuilder.getBundle(title);
	}

	private List<File> getFileNames(List<File> fileNames, Path dir) {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if (path.toFile().isDirectory()) {
					getFileNames(fileNames, path);
				} else {
					boolean match = false;
					for (String pattern : regexes) {
						PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
						if (matcher.matches(path)) {
							match = true;
							break;
						}
					}
					if (!match) {
						fileNames.add(path.toFile());
					}
				}
			}
		} catch (

		IOException e) {
			e.printStackTrace();
		}
		return fileNames;
	}

}

