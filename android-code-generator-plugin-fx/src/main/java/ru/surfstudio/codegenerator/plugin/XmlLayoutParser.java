package ru.surfstudio.codegenerator.plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.surfstudio.codegenerator.Generator;
import ru.surfstudio.codegenerator.Generator.OnListViewDetectedListener;
import ru.surfstudio.codegenerator.data.Constants;
import ru.surfstudio.codegenerator.dialog.ItemListDialog;

public class XmlLayoutParser {

	private ILog log;

	private String basePackage;

	private Generator generator;

	public XmlLayoutParser(String basePackage) {
		this.basePackage = basePackage;
		generator = Generator.getInstance(basePackage);
	}

	public ILog getLog() {
		return log;
	}

	public void setLog(ILog log) {
		this.log = log;
	}

	public void generateCode(final String rootPath, InputStream is, String layoutName, final IProject project,
			final java.io.File[] files) throws XPathExpressionException, ParserConfigurationException, SAXException,
			IOException, CoreException {

		final String baseName = getBaseName(layoutName);
		String packageActivity = Constants.getActivityPackage(basePackage);
		String path = new StringBuilder("src/").append(packageActivity.replace(".", "/")).append("/")
				.append(getActivityFileName(baseName)).toString();
		IFile iFile = project.getFile(path);
		InputStream generatedCode = generator.generateActivity(is, layoutName, new OnListViewDetectedListener() {

			@Override
			public void onListViewDetected() {
				generateAdapter(rootPath, baseName, files, project);
			}
		});
		iFile.create(generatedCode, false, null);
	}

	private void generateAdapter(final String rootPath, final String baseName, final java.io.File[] files,
			final IProject project) {
		ItemListDialog dialog = new ItemListDialog(null, baseName);
		dialog.setFiles(files);
		dialog.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(String name) {
				java.io.File item = new java.io.File(rootPath + "/res/layout/" + name);
				try {
					InputStream is = new FileInputStream(item);
					String packageAdapter = Constants.getAdapterPackage(basePackage);
					String pathAdapter = new StringBuilder("src/").append(packageAdapter.replace(".", "/")).append("/")
							.append(getAdapterFileName(baseName)).toString();
					IFile iFileAdapter = project.getFile(pathAdapter);
					InputStream generatedCode = generator.generateAdapter(is, item.getName(), baseName);
					// iFileAdapter.delete(true, null);
					iFileAdapter.create(generatedCode, false, null);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		dialog.open();
	}

	private String getBaseName(String fileName) {
		fileName = fileName.replace(".xml", "");
		fileName = fileName.replace("activity_", "");
		String[] words = fileName.split("_");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			stringBuilder.append(Character.toUpperCase(word.charAt(0)) + word.substring(1));
		}
		return stringBuilder.toString();
	}

	private String getActivityFileName(String baseName) {
		return new StringBuilder(baseName).append("Activity.java").toString();
	}

	private String getAdapterFileName(String baseName) {
		return new StringBuilder(baseName).append("Adapter.java").toString();
	}

	public interface OnItemSelectedListener {

		public void onItemSelected(String name);

	}

}
