package main;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.bharatthippireddy.patient.Patient;


public class JAXBMarshallUnmarshall {
	
	public static void main(String[] args) {
		
		try {
			
			JAXBContext context = JAXBContext.newInstance(Patient.class);
			
			System.out.println("//**************************MARSHALL:: JAVA  a XML*********************************//////");
			Marshaller marshaller = context.createMarshaller();
			
			//Se crea objeto tipo Patient
			Patient patient = new Patient();
			patient.setId(123);
			patient.setName("Luis");
			patient.setAge(26);
			
			//Se convierte objeto tipo java a XML
			StringWriter writer = new StringWriter();
			marshaller.marshal(patient, writer);
			
			System.out.println(writer.toString());
			
			
			System.out.println("." +"\n"+
					"." +"\n"+
					"." +"\n"+
					".");
			
			System.out.println(
			"///**************************MARSHALL:: XML a JAVA*********************************//////");
			Unmarshaller unMarshaller = context.createUnmarshaller();
			Patient patientUnmarshal = (Patient) unMarshaller.unmarshal(new StringReader(writer.toString()));
			System.out.println(patientUnmarshal.getId());
			System.out.println(patientUnmarshal.getName());
			System.out.println(patientUnmarshal.getAge());
			
		} catch (JAXBException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
	}
}
