package br.com.siscca.controladores;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;

import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.CriptografiaException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.UsuarioServico;
import br.com.siscca.utils.StringUtil;

@ManagedBean
@ViewScoped
public class AlteracaoSenhaController extends AbstractSisccaController {
	
	private static final long serialVersionUID = 1L;
	
	private Long idUsuario;
	private String login;
	private String senhaAtual;
	private String novaSenha;
	private String confirmacaoNovaSenha;
	
	public void init() {
		
		limparDados();
				
		Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
		this.setIdUsuario(usuario.getId());
		this.setLogin(usuario.getLogin());
		
		// Abre o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('alteracaoSenha').show()");
		
		// Atualiza o form.
		RequestContext.getCurrentInstance().update("form");
	}
	
	public void limparDados() {
		this.idUsuario = null;
		this.login = "";
		this.senhaAtual = "";
		this.novaSenha = "";
		this.confirmacaoNovaSenha = "";
	}
	
	public void alterar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuario = UsuarioServico.getInstance().recuperarPorId(getIdUsuario());
			String senhaAtualBd = UsuarioServico.getInstance().recuperarSenha(getIdUsuario());
			
			if (StringUtil.validarNovaSenhaConfirmacaoNovaSenha(getNovaSenha(), getConfirmacaoNovaSenha(), getSenhaAtual(), senhaAtualBd)) {
				
				usuario.setSenha(getNovaSenha());
				UsuarioServico.getInstance().atualizarSenha(usuario);
				
				// Dispara mensagem de sucesso
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Senha alterada com sucesso.", null));
				FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
				
				// Fecha o modal
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("PF('alteracaoSenha').hide()");
			}
			
		} catch (CriptografiaException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro ao tentar alterar a senha.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar atualizar a senha.", null));
		}
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmacaoNovaSenha() {
		return confirmacaoNovaSenha;
	}

	public void setConfirmacaoNovaSenha(String confirmacaoNovaSenha) {
		this.confirmacaoNovaSenha = confirmacaoNovaSenha;
	}
}
