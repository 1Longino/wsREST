package mx.com.longino.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.StdDeserializer;
public class DateHandlerDeserializable extends StdDeserializer<Date>{

	protected DateHandlerDeserializable(Class<?> vc) {
		super(vc);
		// TODO Apéndice de constructor generado automáticamente
	}

	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		// TODO Apéndice de método generado automáticamente
		
		Date birthDate = null;
		try {
			String date = jsonParser.getText();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			birthDate =  sdf.parse(date);
			
		} catch (ParseException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
			return null;
		}
		
		return birthDate;
		
	}

}
