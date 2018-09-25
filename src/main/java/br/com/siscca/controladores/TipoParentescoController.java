package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.siscca.servicos.TipoParentescoServico;

@ManagedBean
@ViewScoped
public class TipoParentescoController extends CrudController {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	void carregarServico() {
		this.setServico(TipoParentescoServico.getInstance());
		this.nomeFuncionalidade = "Tipo de Parentesco";
	}
}
