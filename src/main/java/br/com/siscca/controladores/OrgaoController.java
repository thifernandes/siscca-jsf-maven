package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.OrgaoServico;

@ManagedBean
@ViewScoped
public class OrgaoController extends CrudController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(OrgaoServico.getInstance());
		this.nomeFuncionalidade = "Órgão";
	}
}
