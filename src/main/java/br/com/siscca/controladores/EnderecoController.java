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

import br.com.siscca.entidades.Endereco;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.EnderecoServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class EnderecoController extends AbstractSisccaController {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	public String nomeFuncionalidade = "Endereço";
	
	private LazyDataModel<Endereco> listaEnderecos;
	private Prontuario prontuario;
	private Endereco endereco;
	
	public void pesquisar() {
		
		listaEnderecos = new LazyDataModel<Endereco>() {

			/**
			 * Serial UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Endereco> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				List<Endereco> lista = new ArrayList<Endereco>();

				try {
					
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					lista = EnderecoServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(EnderecoServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
					erroPesquisar = false;
					
				} catch (BancoDadosException e) {
					notificarErroPesquisaLazy("Erro ao tentar listar os endereços.", FacesMessage.SEVERITY_FATAL);
				}
				
				return lista;
			}
		};
		
		RequestContext.getCurrentInstance().update("tb:formEnderecos:datatableEndereco");
	}
	
	public void cadastrarNovo(AjaxBehaviorEvent event) {
		
		limparDados();
		
		// Abre o modal
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('novoEndereco').show();");
	}
	
	public void carregarEndereco(Long id) {
		
		try {
			endereco = EnderecoServico.getInstance().recuperarPorId(id);
			
			// Abre o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoEndereco').show()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar o " + nomeFuncionalidade + " pelo id.", null));
		}
	}
	
	public void salvar(AjaxBehaviorEvent event) {
		
		try {
			
			Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
			endereco.setUsuarioRegistro(usuarioLogado);
			endereco.setProntuario(prontuario);
			EnderecoServico.getInstance().salvar(endereco);
			
			// Recupera a lista atualizada
			pesquisar();
			
			// Dispara mensagem de sucesso
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, nomeFuncionalidade + " salvo(a) com sucesso.", null));
			FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
			
			// Fecha o modal
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('novoEndereco').hide()");
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados do(a) " + nomeFuncionalidade, null));
		}
	}
	
	public void excluir(AjaxBehaviorEvent event) {
		
		try {
			EnderecoServico.getInstance().excluir(endereco.getId());
			
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
		this.setEndereco(new Endereco());
	}

	public LazyDataModel<Endereco> getListaEnderecos() {
		return listaEnderecos;
	}

	public void setListaEnderecos(LazyDataModel<Endereco> listaEnderecos) {
		this.listaEnderecos = listaEnderecos;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}
}
