package ru.surfstudio.codegenerator;

import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.serialization.Types;

public class Generator {

	private TypesAdapter typesAdapter;

	private static Generator generator;

	public static Generator getInstance() {
		if (generator == null) {
			generator = new Generator();
		}
		return generator;
	}

	
	private Generator() {
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

	public TypesAdapter getTypesAdapter() {
		return typesAdapter;
	}

}
