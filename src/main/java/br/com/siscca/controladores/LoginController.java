package br.com.siscca.controladores;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.CriptografiaException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.UsuarioServico;

@ManagedBean
@RequestScoped
public class LoginController implements Serializable {

	private static final long serialVersionUID = -4894958403646089594L;
	private static Logger logger = Logger.getLogger(LoginController.class);
	
	private String login;
	private String senha;

	public void logar() {
		
		try {
			
			Usuario usuario = UsuarioServico.getInstance().validarLogin(login, senha);
			
			if (usuario == null) {
				FacesContext.getCurrentInstance().validationFailed();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Usuário ou senha inválidos.", null));
			} else {
				
				if (usuario.getUsuarioAtivo()) {
					SessionContext.getInstance().setAttribute("usuarioLogado", usuario);
					FacesContext.getCurrentInstance().getExternalContext().redirect("paginas/paginaPrincipal.xhtml");
					
				} else {
					FacesContext.getCurrentInstance().validationFailed();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Usuário sem permissão de acesso. Favor, procure o administrador do sistema.", null));
				}
			}
			
		} catch (CriptografiaException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar logar.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar logar.", null));
			
		} catch (IOException e) {
			logger.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro ao tentar redirecionar a página.", null));
		}
	}
	
	public void sair() {
		
		try {
		    
		    SessionContext.getInstance().encerrarSessao();
		    
		    HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		    FacesContext.getCurrentInstance().getExternalContext().redirect(req.getContextPath() + "/index.xhtml");
			
		} catch (IOException e) {
			logger.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro ao tentar redirecionar a página.", null));
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
