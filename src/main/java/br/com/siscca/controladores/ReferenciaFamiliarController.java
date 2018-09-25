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
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.ReferenciaFamiliar;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.ReferenciaFamiliarServico;
import br.com.siscca.servicos.TipoParentescoServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class ReferenciaFamiliarController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Referência Familiar";
	
	private Prontuario prontuario;
	private ReferenciaFamiliar referenciaFamiliar;
	
	private LazyDataModel<ReferenciaFamiliar> lista;
	private List<EntidadeCrud> listaTiposParentesco;
	
	@SuppressWarnings("unchecked")
	private void carregarListas() {
		
		try {
			
			listaTiposParentesco = TipoParentescoServico.getInstance().listar();
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar carregar as listas.", null));
		}
		
	}
	
	public void pesquisar() {
		
		lista = new LazyDataModel<ReferenciaFamiliar>() {

			/**
			 * Serial UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<ReferenciaFamiliar> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

					List<ReferenciaFamiliar> listaTemp = new ArrayList<ReferenciaFamiliar>();
				
					try {
					
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					listaTemp = ReferenciaFamiliarServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(ReferenciaFamiliarServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar listar as referências familiares.", FacesMessage.SEVERITY_FATAL);
				}
					
				return listaTemp;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formReferenciaFamiliar:datatableReferenciaFamiliar");
	}
	
	public void cadastrarNova(AjaxBehaviorEvent event) {
		
		limparDados();
		carregarListas();
		
		// Fecha o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novaReferenciaFamiliar').show();");
	}
	
	public void carregar(Long id) {
		
		try {
			referenciaFamiliar = ReferenciaFamiliarServico.getInstance().recuperarPorId(id);
			carregarListas();
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaReferenciaFamiliar').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o(a) " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			referenciaFamiliar.setUsuarioRegistro(usuarioLogado);
			referenciaFamiliar.setProntuario(prontuario);
			ReferenciaFamiliarServico.getInstance().salvar(referenciaFamiliar);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaReferenciaFamiliar').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			ReferenciaFamiliarServico.getInstance().excluir(referenciaFamiliar.getId());
			
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
		this.setReferenciaFamiliar(new ReferenciaFamiliar());
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public ReferenciaFamiliar getReferenciaFamiliar() {
		return referenciaFamiliar;
	}

	public void setReferenciaFamiliar(ReferenciaFamiliar referenciaFamiliar) {
		this.referenciaFamiliar = referenciaFamiliar;
	}

	public LazyDataModel<ReferenciaFamiliar> getLista() {
		return lista;
	}

	public void setLista(LazyDataModel<ReferenciaFamiliar> lista) {
		this.lista = lista;
	}

	public List<EntidadeCrud> getListaTiposParentesco() {
		return listaTiposParentesco;
	}

	public void setListaTiposParentesco(List<EntidadeCrud> listaTiposParentesco) {
		this.listaTiposParentesco = listaTiposParentesco;
	}
}
