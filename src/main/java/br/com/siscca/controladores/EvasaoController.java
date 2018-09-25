package br.com.siscca.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Evasao;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.EvasaoServico;
import br.com.siscca.servicos.MotivoEvasaoServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class EvasaoController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Evasão";
	
	private Prontuario prontuario;
	private Evasao evasao;
	
	private LazyDataModel<Evasao> lista;
	private List<EntidadeCrud> listaMotivosEvasao;

	@SuppressWarnings("unchecked")
	private void carregarListas() {
		
		try {
			
			listaMotivosEvasao = MotivoEvasaoServico.getInstance().listar();
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar carregar a lista.", null));
		}
		
	}
	
	public void pesquisar() {
		
		lista = new LazyDataModel<Evasao>() {

			/**
			 * Serial UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Evasao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				List<Evasao> listaTemp = new ArrayList<Evasao>();
				
				try {
					
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					listaTemp = EvasaoServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(EvasaoServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar listar as evasões.", FacesMessage.SEVERITY_FATAL);
				}
				
				return listaTemp;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formEvasoes:datatableEvasao");
	}
	
	public void cadastrarNova(AjaxBehaviorEvent event) {
		
		limparDados();
		carregarListas();
		
		// Fecha o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novaEvasao').show();");
	}
	
	public void carregar(Long id) {
		
		try {
			evasao = EvasaoServico.getInstance().recuperarPorId(id);
			carregarListas();
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaEvasao').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o(a) " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			evasao.setUsuarioRegistro(usuarioLogado);
			evasao.setProntuario(prontuario);
			EvasaoServico.getInstance().salvar(evasao);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaEvasao').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			EvasaoServico.getInstance().excluir(evasao.getId());
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " excluído(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar excluir o(a) " + nomeFuncionalidade + ".", null));
		}
	}
	
	private void limparDados() {
		this.setEvasao(new Evasao());
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public Evasao getEvasao() {
		return evasao;
	}

	public void setEvasao(Evasao evasao) {
		this.evasao = evasao;
	}

	public LazyDataModel<Evasao> getLista() {
		return lista;
	}

	public void setLista(LazyDataModel<Evasao> lista) {
		this.lista = lista;
	}

	public List<EntidadeCrud> getListaMotivosEvasao() {
		return listaMotivosEvasao;
	}

	public void setListaMotivosEvasao(List<EntidadeCrud> listaMotivosEvasao) {
		this.listaMotivosEvasao = listaMotivosEvasao;
	}
}
