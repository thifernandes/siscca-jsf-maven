package br.com.siscca.infra.conversores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass=Date.class)
public class ConversorData implements Converter {

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		Date date = null;
		
		try {
			
			if (arg2 != null && !arg2.equals("")) {
				date = sdf.parse(arg2);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		String data = null;
		
		if (arg2 != null) {
			Date date = (Date) arg2;
			data = sdf.format(date);
		}
		
		return data;
	}

}
