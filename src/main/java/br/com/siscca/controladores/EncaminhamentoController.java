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

import br.com.siscca.entidades.Encaminhamento;
import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.EncaminhamentoServico;
import br.com.siscca.servicos.MotivoAdmissaoServico;
import br.com.siscca.servicos.ProcedenciaServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class EncaminhamentoController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Encaminhamento";
	
	private Prontuario prontuario;
	private Encaminhamento encaminhamento;
	
	private LazyDataModel<Encaminhamento> lista;
	private List<EntidadeCrud> listaMotivosAdmissao;
	private List<EntidadeCrud> listaProcedencias;

	@SuppressWarnings("unchecked")
	private void carregarListas() {
		
		try {
			
			listaMotivosAdmissao = MotivoAdmissaoServico.getInstance().listar();
			listaProcedencias = ProcedenciaServico.getInstance().listar();
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar carregar as listas.", null));
		}
		
	}
	
	public void pesquisar() {
		
		lista = new LazyDataModel<Encaminhamento>() {

			/**
			 * Serial UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Encaminhamento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				List<Encaminhamento> tempList = new ArrayList<Encaminhamento>();
				
				try {
				
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					tempList = EncaminhamentoServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(EncaminhamentoServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar listar os encaminhamentos.", FacesMessage.SEVERITY_FATAL);
				}
				
				return tempList;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formEncaminhamentos:datatableEncaminhamento");
	}
	
	public void cadastrarNovo(AjaxBehaviorEvent event) {
		
		limparDados();
		carregarListas();
		
		// Fecha o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novoEncaminhamento').show();");
	}
	
	public void carregar(Long id) {
		
		try {
			encaminhamento = EncaminhamentoServico.getInstance().recuperarPorId(id);
			carregarListas();
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoEncaminhamento').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o(a) " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			encaminhamento.setUsuarioRegistro(usuarioLogado);
			encaminhamento.setProntuario(prontuario);
			EncaminhamentoServico.getInstance().salvar(encaminhamento);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoEncaminhamento').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			EncaminhamentoServico.getInstance().excluir(encaminhamento.getId());
			
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
		this.setEncaminhamento(new Encaminhamento());
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public List<EntidadeCrud> getListaMotivosAdmissao() {
		return listaMotivosAdmissao;
	}

	public void setListaMotivosAdmissao(List<EntidadeCrud> listaMotivosAdmissao) {
		this.listaMotivosAdmissao = listaMotivosAdmissao;
	}

	public List<EntidadeCrud> getListaProcedencias() {
		return listaProcedencias;
	}

	public void setListaProcedencias(List<EntidadeCrud> listaProcedencias) {
		this.listaProcedencias = listaProcedencias;
	}

	public Encaminhamento getEncaminhamento() {
		return encaminhamento;
	}

	public void setEncaminhamento(Encaminhamento encaminhamento) {
		this.encaminhamento = encaminhamento;
	}

	public LazyDataModel<Encaminhamento> getLista() {
		return lista;
	}

	public void setLista(LazyDataModel<Encaminhamento> lista) {
		this.lista = lista;
	}
}
