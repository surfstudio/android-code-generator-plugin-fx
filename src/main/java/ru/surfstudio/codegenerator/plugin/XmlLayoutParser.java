package ru.surfstudio.codegenerator.plugin;

import java.io.ByteArrayInputStream;
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

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.surfstudio.codegenerator.data.ActivityResource;
import ru.surfstudio.codegenerator.data.Constants;
import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.data.WidgetResource;
import ru.surfstudio.codegenerator.dialog.ItemListDialog;
import ru.surfstudio.codegenerator.logic.ActivityGenerator;
import ru.surfstudio.codegenerator.logic.AdapterGenerator;

public class XmlLayoutParser {

	private TypesAdapter typesAdapter;

	private ILog log;

	public XmlLayoutParser(TypesAdapter typesAdapter) {
		this.typesAdapter = typesAdapter;
	}

	public ILog getLog() {
		return log;
	}

	public void setLog(ILog log) {
		this.log = log;
	}

	/**
	 * You can retrieve widget name from Node by calling
	 * <code>getNodeName()</code> on it. You can retrieve any attribute by
	 * calling <code>getAttributes().getNamedItem("attr_name")</code>. You need
	 * to pass full attribute name (with namespace). For attribute
	 * <i>android:id</i> <blockquote>
	 * <code>getAttributes().getNamedItem("android:id")</code> </blockquote>
	 * 
	 * @param pShortMode
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws CoreException
	 */
	public void generateCode(String rootPath, final String packageName, InputStream is, String name, IProject project, java.io.File[] files) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException, CoreException {
		String layoutName = getFileNameFromPath(name);
		ActivityResource activityResource = new ActivityResource(layoutName);
		final String baseName = getBaseName(name);
		String packageActivity = Constants.getActivityPackage(packageName);
		String path = new StringBuilder("src/").append(packageActivity.replace(".", "/")).append("/")
				.append(getActivityFileName(baseName)).toString();
		IFile iFile = project.getFile(path);

		NodeList nodeList = getNodesWithId(is);
		ActivityGenerator activityObject = new ActivityGenerator(typesAdapter, packageName);
		activityObject.setActivityResource(activityResource);
		activityObject.setPackageName(packageActivity);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String id = node.getAttributes().getNamedItem("android:id").getNodeValue();
			String typeName = node.getNodeName();
			WidgetResource widget = new WidgetResource(id);
			try {
				activityObject.addWidget(widget, typeName);
				if ("ListView".equals(typeName)) {
					generateAdapter(rootPath, baseName, files, packageName, name, project);
				}
				log.log(new Status(Status.INFO, Activator.PLUGIN_ID, "Type: " + typeName + " added."));
			} catch (IllegalArgumentException e) {
				log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type: " + typeName + " was not recognized."));
			}
		}
		InputStream generatedCode = new ByteArrayInputStream(activityObject.generate().getBytes());
		iFile.create(generatedCode, false, null);
	}

	private void generateAdapter(final String rootPath, final String baseName, final java.io.File[] files, final String packageName, final String fileName, final IProject project){
		ItemListDialog dialog = new ItemListDialog(null, baseName);
		dialog.setFiles(files);
		dialog.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(String name) {
				java.io.File item = new java.io.File(rootPath + "/res/layout/" + name);
				try {
					InputStream is = new FileInputStream(item);
					NodeList nodeList = getNodesWithId(is);
					log.log(new Status(Status.INFO, Activator.PLUGIN_ID, "NodeList size:" + nodeList.getLength()));
					String packageAdapter = Constants.getAdapterPackage(packageName);
					AdapterGenerator adapterObject = new AdapterGenerator(typesAdapter, getBaseName(fileName), packageName);
					adapterObject.setItemName(item.getName());
					adapterObject.setPackageName(packageAdapter);
					String pathAdapter = new StringBuilder("src/").append(packageAdapter.replace(".", "/"))
							.append("/").append(getAdapterFileName(baseName)).toString();
					IFile iFileAdapter = project.getFile(pathAdapter);
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String id = node.getAttributes().getNamedItem("android:id").getNodeValue();
						String typeName = node.getNodeName();
						WidgetResource widget = new WidgetResource(id);
						try {
							adapterObject.addWidget(widget, typeName);
							InputStream generatedCode = new ByteArrayInputStream(adapterObject.generate().getBytes());
							iFileAdapter.delete(true, null);
							iFileAdapter.create(generatedCode, false, null);
							log.log(new Status(Status.INFO, Activator.PLUGIN_ID, "Type: " + typeName + " added."));
						} catch (IllegalArgumentException e) {
							log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type: " + typeName + " was not recognized."));
						}
					}
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
	
	private NodeList getNodesWithId(InputStream inputStream) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(inputStream);
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath xPath = pathFactory.newXPath();
		XPathExpression expression = xPath.compile("//*[@id]");
		return (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
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

	private String getFileNameFromPath(String filePath) {
		return new java.io.File(filePath).getName().replaceFirst(".xml", "");
	}

	public interface OnItemSelectedListener {

		public void onItemSelected(String name);

	}

}
