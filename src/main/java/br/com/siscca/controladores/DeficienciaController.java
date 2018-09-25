package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.DeficienciaServico;

@ManagedBean
@ViewScoped
public class DeficienciaController extends CrudController {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(DeficienciaServico.getInstance());
		this.nomeFuncionalidade = "Deficiência";
	}
	
}
