package br.com.siscca.controladores;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.siscca.entidades.Usuario;
import br.com.siscca.enums.TipoPerfilEnum;
import br.com.siscca.infra.seguranca.SessionContext;

@ManagedBean
@SessionScoped
public class AcessoMenuController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Usuario usuario;
	private Boolean perfilConsulta;
	
	public AcessoMenuController() {
		usuario = SessionContext.getInstance().getUsuarioLogado();
	}
	
	public boolean perfilAdministrador() {
		return usuario.getTipoPerfil().equals(TipoPerfilEnum.ADMINISTRADOR.getValor());
	}
	
	private void verificarPerfilConsulta() {
		boolean valor = usuario.getTipoPerfil().equals(TipoPerfilEnum.CONSULTA.getValor());
		perfilConsulta = valor;
	}

	public Boolean getPerfilConsulta() {
		verificarPerfilConsulta();
		return perfilConsulta;
	}

	public void setPerfilConsulta(Boolean perfilConsulta) {
		this.perfilConsulta = perfilConsulta;
	}
}
