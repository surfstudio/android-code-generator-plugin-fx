package ru.surfstudio.codegenerator.plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import ru.surfstudio.codegenerator.data.Constants;

@SuppressWarnings("restriction")
public class Convert extends AbstractHandler {
	
	ILog iLog = Activator.getDefault().getLog();
	
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(arg0);
		IStructuredSelection selection = (IStructuredSelection) sel;
		try {
			String rootPath = getRootPath(selection);
			IProject project = ((File) selection.getFirstElement()).getProject();
			String rootPackageName = getPackageName(rootPath);
			XmlLayoutParser layoutParser = new XmlLayoutParser(rootPackageName);
			java.io.File folder = new java.io.File(rootPath + "/res/layout");
			java.io.File[] listOfFiles = folder.listFiles();
			listOfFiles = sortFiles(listOfFiles);
			initDirectories(project, rootPackageName);
			List<Object> objects = new ArrayList<Object>();
			objects.addAll(selection.toList());
			for (Object object : objects) {
				File file = (File) object;
				layoutParser.setLog(iLog);
				layoutParser.generateCode(rootPath, file.getContents(), file.getName(), file.getProject(), listOfFiles);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private java.io.File[] sortFiles(java.io.File[] files){
		ArrayList<java.io.File> filesList = new ArrayList<java.io.File>(Arrays.asList(files));
		Iterator<java.io.File> iterator = filesList.iterator();
		while (iterator.hasNext()){
			java.io.File file = iterator.next();
			if (!file.getName().contains("item")){
				iterator.remove();
			}
		}
		filesList.trimToSize();
		return filesList.toArray(new java.io.File[filesList.size()]);
	}
	
	private String getRootPath(IStructuredSelection selection) {
		File fileProject = (File) selection.getFirstElement();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		java.io.File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		Path p = new Path(workspaceDirectory.getAbsolutePath() + fileProject.getProject().getFullPath());
		return p.toOSString();
	}

	private void initDirectories(IProject project, String packageName) throws IOException, CoreException{
		IFolder activityFolder = (IFolder) project.getFile("src/" + Constants.getActivityPackage(packageName).replace(".", "/") + "/1").getParent();
		IFolder adapterFolder = (IFolder) project.getFile("src/" + Constants.getAdapterPackage(packageName).replace(".", "/") + "/1").getParent();
		prepare(activityFolder);
		prepare(adapterFolder);
	}
	
	public void prepare(IFolder folder) throws CoreException {
	    if (!folder.exists()) {
	        prepare((IFolder) folder.getParent());
	        folder.create(false, false, null);
	    }
	}
	
	private String getPackageName(String rootPath) throws ParserConfigurationException, FileNotFoundException, SAXException,
			IOException {
		Path p = new Path(rootPath + "/AndroidManifest.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new FileInputStream(p.toFile()));
		Element manifest = (Element) doc.getElementsByTagName("manifest").item(0);
		return manifest.getAttribute("package");
	}

}
