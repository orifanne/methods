package methods;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import defaults.TextLinkDefaults;

public class Utils {
	public static Document openXML(String path) {
		DocumentBuilderFactory f = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			builder = f.newDocumentBuilder();
		}

		catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		try {
			doc = builder.parse(Utils.class.getResource(path).openStream());
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
		}

		return doc;
	}

	public static Document openXMLAbsolutePath(String path) {
		DocumentBuilderFactory f = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			builder = f.newDocumentBuilder();
		}

		catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		try {
			doc = builder.parse(new File(path));
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
		}

		return doc;
	}

	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Utils.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Set<Component> getAllComponents(Component component) {
		Set<Component> children = new HashSet<Component>();
		children.add(component);
		if (component instanceof Container) {
			Container container = (Container) component;
			Component[] components = container.getComponents();
			for (int i = 0; i < components.length; i++) {
				children.addAll(getAllComponents(components[i]));
			}
		}
		return children;
	}

	public static Object createObject(Constructor constructor, Object[] arguments) {
		Object object = null;

		try {
			object = constructor.newInstance(arguments);
			return object;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Document openXMLFromString(String xml) {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = f.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			try {
				doc = builder.parse(is);
				String message = doc.getDocumentElement().getTextContent();
				System.out.println(message);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		return doc;
	}

	public static String getHDDSerialNumber() {
		String result = "";
		try {
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "";
			Scanner in = new Scanner(Utils.class.getResource("resources/text/script.txt").openStream());
			while (in.hasNext())
				vbs += in.nextLine() + "\r\n";
			in.close();

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.trim();
	}

	public static String getFilePath() {
		return new File("").getAbsolutePath();
	}

	public static String getLicenceKey() {
		Document doc = Utils.openXMLAbsolutePath(Utils.getFilePath() + "/config");
		NodeList n = doc.getElementsByTagName("key");
		return n.item(0).getTextContent();
	}

	public static String getLicenceUserName() {
		Document doc = Utils.openXMLAbsolutePath(Utils.getFilePath() + "/config");
		NodeList n = doc.getElementsByTagName("username");
		return n.item(0).getTextContent();
	}

	public static String getVersion() {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.VERSION));
		NodeList n = doc.getElementsByTagName("version");
		return n.item(0).getTextContent();
	}

	public static String getVersionDate() {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.VERSION));
		NodeList n = doc.getElementsByTagName("date");
		return n.item(0).getTextContent();
	}

	public static String getAppServer() {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.SERVER));
		NodeList n = doc.getElementsByTagName("app");
		return n.item(0).getTextContent();
	}

	public static Boolean getCheckUpdatesAuto() {
		Document doc = Utils.openXMLAbsolutePath(Utils.getFilePath() + "/config");
		NodeList n = doc.getElementsByTagName("checkUpdatesAuto");
		if (n.item(0).getTextContent().equals("true"))
			return true;
		else
			return false;
	}

	public static String readFile(String path) {
		byte[] encoded = null;
		String s = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			s = new String(encoded, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return s;
	}

	public static void writeFile(String contents, String path) {
		File file = new File(path);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(contents);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}