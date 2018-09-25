package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.MotivoEvasaoServico;

@ManagedBean
@ViewScoped
public class MotivoEvasaoController extends CrudController {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(MotivoEvasaoServico.getInstance());
		this.nomeFuncionalidade = "Motivo de evasão";
	}
}
