package br.com.siscca.controladores;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;

import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.GrauInstrucao;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.servicos.ServicoCrud;

@ManagedBean
@ViewScoped
public abstract class CrudController extends AbstractSisccaController {
	
	private static final long serialVersionUID = 1L;
	public String nomeFuncionalidade = "";
	
	/* Filtro de pesquisa */
	private String nome;
	
	/* Atributo para exclusao */
	private Long id;
	
	/* Atributos do modal */
	private String nomeModal;
	
	private List<GrauInstrucao> lista;
	
	private ServicoCrud servico;
	
	abstract void carregarServico();
	
	public CrudController() {
		carregarServico();
		pesquisar();
	}
	
	public void limparDados(AjaxBehaviorEvent event) {
		this.nomeModal = "";
		this.id = null;
	}
	
	public void pesquisar() {
		
		try {
			
			lista = getServico().pesquisar(getNome());
			
			// Fecha o modal de load
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('statusDialog').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar pesquisar.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
		}
	}
	
	public void carregarDados(Long id) {
		
		try {
			EntidadeCrud e = getServico().findById(id);
			this.setId(e.getId());
			this.setNomeModal(e.getNome());
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o(a) " + nomeFuncionalidade + " pelo id.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			EntidadeCrud e = new EntidadeCrud();
			e.setId(getId());
			e.setNome(getNomeModal());
			getServico().salvar(e);
			
			// Recupera a lista atualizada
			lista = getServico().listar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('modal').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
		
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			getServico().excluir(getId());
			
			// Recupera a lista atualizada
			lista = getServico().listar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " excluído(a) com sucesso.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar excluir o(a) " + nomeFuncionalidade + ".", null));
		}
		
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
	}

	public Long getId() {
		if (id != null && id.equals(0L)) {
			id = null;
		}
		return id;
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

	public List<GrauInstrucao> getLista() {
		return lista;
	}

	public void setLista(List<GrauInstrucao> lista) {
		this.lista = lista;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ServicoCrud getServico() {
		return servico;
	}

	public void setServico(ServicoCrud servico) {
		this.servico = servico;
	}
}
