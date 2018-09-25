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

import br.com.siscca.entidades.Contato;
import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.ContatoServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class ContatoController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Contato";
	
	private LazyDataModel<Contato> listaContatos;
	private Pessoa pessoa;
	private Contato contato;
	private Integer tipoContato;
	private String mascaraTelefone;
	
	public void pesquisar() {
		
		listaContatos = new LazyDataModel<Contato>() {

			/**
			 * Serial UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Contato> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				List<Contato> lista = new ArrayList<Contato>();

				try {
					
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					lista = ContatoServico.getInstance().pesquisar(pessoa, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(ContatoServico.getInstance().pesquisarQuantidadeRegistros(pessoa));
					
					erroPesquisar = false;
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar listar os contatos.", FacesMessage.SEVERITY_FATAL);
				}
				
				return lista;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formContatos:datatableContato");
	}
	
	public void cadastrarNovo(AjaxBehaviorEvent event) {
		
		limparDados();
		
		this.setMascaraTelefone("(99) 9999-9999");
		
		// Abre o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novoContato').show();");
	}
	
	public void carregarContato(Long id) {
		
		try {
			contato = ContatoServico.getInstance().recuperarPorId(id);
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoContato').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			contato.setUsuarioRegistro(usuarioLogado);
			contato.setPessoa(pessoa);
			ContatoServico.getInstance().salvar(contato);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoContato').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			ContatoServico.getInstance().excluir(contato.getId());
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " excluído com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar excluir o " + nomeFuncionalidade + ".", null));
		}
	}
	
	private void limparDados() {
		this.setContato(new Contato());
	}

	public String getNomeFuncionalidade() {
		return nomeFuncionalidade;
	}

	public void setNomeFuncionalidade(String nomeFuncionalidade) {
		this.nomeFuncionalidade = nomeFuncionalidade;
	}

	public LazyDataModel<Contato> getListaContatos() {
		return listaContatos;
	}

	public void setListaContatos(LazyDataModel<Contato> listaContatos) {
		this.listaContatos = listaContatos;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Contato getContato() {
		return contato;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}

	public Integer getTipoContato() {
		return tipoContato;
	}

	public void setTipoContato(Integer tipoContato) {
		this.tipoContato = tipoContato;
		
		if (tipoContato == 1) {
			this.setMascaraTelefone("(99) 9999-9999");
		} else {
			this.setMascaraTelefone("(99) 99999-9999");
		}
	}

	public String getMascaraTelefone() {
		return mascaraTelefone;
	}

	public void setMascaraTelefone(String mascaraTelefone) {
		this.mascaraTelefone = mascaraTelefone;
	}
}
