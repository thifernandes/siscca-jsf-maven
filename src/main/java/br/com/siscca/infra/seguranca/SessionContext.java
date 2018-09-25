package br.com.siscca.infra.seguranca;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.siscca.entidades.Usuario;

public class SessionContext {

	private static SessionContext instance;
	
	public static SessionContext getInstance() {
		
		if (instance == null) {
			instance = new SessionContext();
		}
		
		return instance;
	}
	
	private SessionContext() {
		
	}
	
	private ExternalContext currentExternalContext() {
		
		if (FacesContext.getCurrentInstance() == null) {
			throw new RuntimeException("O FacesContext não pode ser chamado fora de uma requisicao HTTP.");
		} else {
			return FacesContext.getCurrentInstance().getExternalContext();
		}
	}
	
	public Usuario getUsuarioLogado() {
		return (Usuario) getAttribute("usuarioLogado");
	}
	
	public void setUsuarioLogado(Usuario usuario) {
		setAttribute("usuarioLogado", usuario);
	}
	
	public Object getAttribute(String nome) {
		return this.currentExternalContext().getSessionMap().get(nome);
	}
	
	public void setAttribute(String nome, Object valor) {
		this.encerrarSessao();
		this.currentExternalContext().getSessionMap().put(nome, valor);
	}
	
	public void encerrarSessao() {
		this.currentExternalContext().invalidateSession();
	}
}
