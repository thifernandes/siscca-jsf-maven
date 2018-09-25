package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.GrauInstrucaoServico;

@ManagedBean
@ViewScoped
public class GrauInstrucaoController extends CrudController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(GrauInstrucaoServico.getInstance());
		this.nomeFuncionalidade = "Grau de instrução";
	}
	
	
}
