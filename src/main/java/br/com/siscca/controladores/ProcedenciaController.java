package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.ProcedenciaServico;

@ManagedBean
@ViewScoped
public class ProcedenciaController extends CrudController {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(ProcedenciaServico.getInstance());
		this.nomeFuncionalidade = "Procedência";
	}
}
