package br.com.siscca.infra.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.siscca.entidades.EntidadeCrud;

@FacesConverter(forClass=EntidadeCrud.class)
public class ConversorEntidadeCrud implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		EntidadeCrud entidade = new EntidadeCrud(arg2);
		return entidade;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		String id = null;
		
		if (arg2 != null) {
			EntidadeCrud entidade = (EntidadeCrud) arg2;
			id = entidade.getId().toString();
		}
		
		return id;
	}

}
