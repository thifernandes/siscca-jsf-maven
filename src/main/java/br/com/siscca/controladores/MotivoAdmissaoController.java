package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.MotivoAdmissaoServico;

@ManagedBean
@ViewScoped
public class MotivoAdmissaoController extends CrudController {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(MotivoAdmissaoServico.getInstance());
		this.nomeFuncionalidade = "Motivo de Admissão";
	}
}
