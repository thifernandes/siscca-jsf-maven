package br.com.siscca.controladores;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;

import br.com.siscca.entidades.Usuario;
import br.com.siscca.enums.TipoPerfilEnum;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.CriptografiaException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.UsuarioServico;
import br.com.siscca.utils.StringUtil;

@ManagedBean
@ViewScoped
public class UsuarioController extends AbstractSisccaController {
	
	private static final long serialVersionUID = 1L;
	
	/* Filtro de pesquisa */
	private String login;
	private Integer tipoPerfil;
	private String matricula;
	private String nome;
	private String status;
	
	/* Atributo para exclusao */
	private Long idUsuario;
	
	/* Atributos do modal */
	private String loginModal;
	private String senha;
	private String confirmacaoSenha;
	private Integer tipoPerfilModal;
	private String matriculaModal;
	private String nomeModal;
	private String statusModal;
	
	private List<TipoPerfilEnum> listaTiposPerfil;
	private List<Usuario> listaUsuarios;
	
	public UsuarioController() {
		pesquisar();
	}
	
	public void limparDados(AjaxBehaviorEvent event) {
		this.loginModal = "";
		this.senha = "";
		this.confirmacaoSenha = "";
		this.tipoPerfilModal = null;
		this.idUsuario = null;
		this.matriculaModal = "";
		this.nomeModal = "";
		this.statusModal = null;
	}
	
	public void pesquisar() {
		
		try {
			
			listaUsuarios = UsuarioServico.getInstance().pesquisar(getLogin(), getTipoPerfil(), getMatricula(), getNome(), getStatus());
			
			// Fecha o modal de load
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('statusDialog').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar pesquisar.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
		}
	}
	
	public void carregarDadosUsuario(Long idUsuario) {
		
		limparDados(null);
		
		try {
			Usuario usuario = UsuarioServico.getInstance().recuperarPorId(idUsuario);
			this.setIdUsuario(usuario.getId());
			this.setNomeModal(usuario.getNome());
			this.setLoginModal(usuario.getLogin());
			this.setTipoPerfilModal(usuario.getTipoPerfil());
			this.setMatriculaModal(usuario.getMatricula());
			this.setStatusModal(usuario.getStatus());
			
			// Recupera a senha criptografada do BD e decriptografa para setar na tela de alteração do usuário.
			String senhaHexa = UsuarioServico.getInstance().recuperarSenha(idUsuario);
			String senha = StringUtil.decriptografarSenha(senhaHexa);
			
			this.setSenha(senha);
			this.setConfirmacaoSenha(senha);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o usuário pelo id.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
		}
	}
	
	public void salvar() {
		
		try {
			
			if (StringUtil.validarSenhaConfirmacaoSenha(getSenha(), getConfirmacaoSenha())) {
				
				Usuario usuario = new Usuario(getIdUsuario(), getLoginModal(), getSenha(), getTipoPerfilModal(), 
						getMatriculaModal(), getNomeModal(), getStatusModal());
				
				Boolean loginExistente = false;
				
				if (usuario.getId() == null) {
					loginExistente = UsuarioServico.getInstance().verificarLoginExistente(usuario.getLogin());
				}
				
				if (!loginExistente) {
					
					// Salva o usuario
					UsuarioServico.getInstance().salvar(usuario);
					
					// Recupera a lista atualizada
					listaUsuarios = UsuarioServico.getInstance().listar();
					
					// Dispara mensagem de sucesso
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Usuário salvo com sucesso.", null));
					
					// Fecha o modal
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.execute("PF('usuarioModal').hide()");
					
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_WARN, "Já existe um usuário cadastrado com o login informado.", null));
				}
			}
			
		} catch (CriptografiaException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro ao tentar salvar os dados do usuário.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do usuário.", null));
		}
		
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			UsuarioServico.getInstance().excluir(getIdUsuario());
			
			// Recupera a lista atualizada
			listaUsuarios = UsuarioServico.getInstance().listar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Usuário excluído com sucesso.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar excluir usuário.", null));
		}
	}
	
	public String getUsuarioLogado() {
		Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
		return usuario.getNomeMatricula();
	}

	public Long getIdUsuario() {
		if (idUsuario != null && idUsuario.equals(0L)) {
			idUsuario = null;
		}
		return idUsuario;
	}
	
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public List<TipoPerfilEnum> getListaTiposPerfil() {
		this.listaTiposPerfil = Arrays.asList(TipoPerfilEnum.values());
		return listaTiposPerfil;
	}

	public void setListaTiposPerfil(List<TipoPerfilEnum> listaTiposPerfil) {
		this.listaTiposPerfil = listaTiposPerfil;
	}

	public List<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public void setListaUsuarios(List<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Integer getTipoPerfil() {
		if (tipoPerfil != null && tipoPerfil.equals(0)) {
			tipoPerfil = null;
		}
		return tipoPerfil;
	}

	public void setTipoPerfil(Integer tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getLoginModal() {
		return loginModal;
	}

	public void setLoginModal(String loginModal) {
		this.loginModal = loginModal;
	}

	public Integer getTipoPerfilModal() {
		return tipoPerfilModal;
	}

	public void setTipoPerfilModal(Integer tipoPerfilModal) {
		this.tipoPerfilModal = tipoPerfilModal;
	}

	public String getMatriculaModal() {
		return matriculaModal;
	}

	public void setMatriculaModal(String matriculaModal) {
		this.matriculaModal = matriculaModal;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeModal() {
		return nomeModal;
	}

	public void setNomeModal(String nomeModal) {
		this.nomeModal = nomeModal;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public String getStatus() {
		if (status != null && status.equals("")) {
			status = null;
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusModal() {
		return statusModal;
	}

	public void setStatusModal(String statusModal) {
		this.statusModal = statusModal;
	}
}
