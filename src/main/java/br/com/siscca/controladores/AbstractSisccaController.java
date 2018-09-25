package br.com.siscca.controladores;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public abstract class AbstractSisccaController implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/* Controle para lancar mensagem em caso de erro nas pesquisas do tipo LazyDataModel. */
	public boolean erroPesquisar = false;
	private String msgErro;
	private Severity severidade;
	
	public void notificarErroPesquisaLazy(String msgErro, Severity severidade) {
		this.erroPesquisar = true;
		this.msgErro = msgErro;
		this.severidade = severidade;
	}
	
	public void verificarErroPesquisaLazy() {
		if (erroPesquisar) {
			lancarMensagemErro(msgErro, severidade);
		}
	}
	
	public void lancarMensagemErro(String msgErro, Severity severidade) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, msgErro, null));
	}
}
