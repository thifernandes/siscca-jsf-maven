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

import br.com.siscca.entidades.Admissao;
import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.AdmissaoServico;
import br.com.siscca.servicos.MotivoAdmissaoServico;
import br.com.siscca.servicos.ProcedenciaServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class AdmissaoController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Admissão";
	
	private Prontuario prontuario;
	private Admissao admissao;
	
	private LazyDataModel<Admissao> listaAdmissoes;
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
		
		listaAdmissoes = new LazyDataModel<Admissao>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Admissao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
			
			List<Admissao> lista = new ArrayList<Admissao>();
				
				try {
					
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					lista = AdmissaoServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(AdmissaoServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar pesquisar as admissões.", FacesMessage.SEVERITY_FATAL);
				}
				
				return lista;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formAdmissoes:datatableAdmissao");
	}
	
	public void cadastrarNova(AjaxBehaviorEvent event) {
		
		limparDados();
		carregarListas();
		
		// Fecha o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novaAdmissao').show();");
	}
	
	public void carregarAdmissao(Long id) {
		
		try {
			admissao = AdmissaoServico.getInstance().recuperarPorId(id);
			carregarListas();
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaAdmissao').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o(a) " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			admissao.setUsuarioRegistro(usuarioLogado);
			admissao.setProntuario(prontuario);
			AdmissaoServico.getInstance().salvar(admissao);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novaAdmissao').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			AdmissaoServico.getInstance().excluir(admissao.getId());
			
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
		this.setAdmissao(new Admissao());
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public LazyDataModel<Admissao> getListaAdmissoes() {
		return listaAdmissoes;
	}

	public void setListaAdmissoes(LazyDataModel<Admissao> listaAdmissoes) {
		this.listaAdmissoes = listaAdmissoes;
	}

	public Admissao getAdmissao() {
		return admissao;
	}

	public void setAdmissao(Admissao admissao) {
		this.admissao = admissao;
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
}
