package ru.surfstudio.codegenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.surfstudio.codegenerator.data.ActivityResource;
import ru.surfstudio.codegenerator.data.Constants;
import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.data.WidgetResource;
import ru.surfstudio.codegenerator.logic.ActivityGenerator;
import ru.surfstudio.codegenerator.logic.AdapterGenerator;
import ru.surfstudio.codegenerator.serialization.Types;

public class Generator {

	private String basePackage;
	private TypesAdapter typesAdapter;

	private ActivityGenerator activityGenerator;
	private AdapterGenerator adapterGenerator;

	private static Generator generator;

	public static Generator getInstance(String basePackage) {
		if (generator == null) {
			generator = new Generator(basePackage);
		}
		return generator;
	}

	private Generator(String basePackage) {
		this.basePackage = basePackage;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Types.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			InputStream is = getClass().getClassLoader().getResourceAsStream("resources/types.xml");
			typesAdapter = new TypesAdapter((Types) unmarshaller.unmarshal(is));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ActivityGenerator getActivityGenerator() {
		activityGenerator = new ActivityGenerator(typesAdapter, basePackage);
		return activityGenerator;
	}

	public AdapterGenerator getAdapterGenerator(String adapterName) {
		adapterGenerator = new AdapterGenerator(typesAdapter, adapterName, basePackage);
		return adapterGenerator;
	}

	public InputStream generateActivity(InputStream is, String fileName, OnListViewDetectedListener listener) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		NodeList nodeList = getNodesWithId(is);
		String layoutName = getFileNameFromPath(fileName);
		String packageActivity = Constants.getActivityPackage(basePackage);
		ActivityResource activityResource = new ActivityResource(layoutName);
		ActivityGenerator activityObject = generator.getActivityGenerator();
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
					listener.onListViewDetected();
				}
			} catch (IllegalArgumentException e) {
			}
		}
		return new ByteArrayInputStream(activityObject.generate().getBytes());
	}

	public InputStream generateAdapter(InputStream is, String itemName, String baseName) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		NodeList nodeList = getNodesWithId(is);
		String packageAdapter = Constants.getAdapterPackage(basePackage);
		AdapterGenerator adapterObject = generator.getAdapterGenerator(baseName);
		adapterObject.setItemName(itemName);
		adapterObject.setPackageName(packageAdapter);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String id = node.getAttributes().getNamedItem("android:id").getNodeValue();
			String typeName = node.getNodeName();
			WidgetResource widget = new WidgetResource(id);
			adapterObject.addWidget(widget, typeName);
		}
		return new ByteArrayInputStream(adapterObject.generate().getBytes());
	}

	private String getFileNameFromPath(String filePath) {
		return new java.io.File(filePath).getName().replaceFirst(".xml", "");
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

	public interface OnListViewDetectedListener {

		public void onListViewDetected();

	}

}
