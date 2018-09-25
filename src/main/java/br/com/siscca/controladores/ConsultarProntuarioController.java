package br.com.siscca.controladores;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.servicos.ProntuarioServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@ViewScoped
public class ConsultarProntuarioController  extends AbstractSisccaController {

	private static final long serialVersionUID = -4894958403646089594L;
	private static Logger logger = Logger.getLogger(ConsultarProntuarioController.class);
	
	private String nome;
	private String dataNascimento;
	private String numeroProntuario;
	private String numeroOficio;
	private String numeroAutos;
	private String numeroGuiaAcolhimento;
	
	/* Filtro de novo cadastro */ 
	private String nomePai;
	private String nomeMae;
	
	private LazyDataModel<Prontuario> listaProntuarios;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public void pesquisar(AjaxBehaviorEvent event) {
		
		listaProntuarios = new LazyDataModel<Prontuario>() {

			/**
			 * Serial Uid.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Prontuario> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				List<Prontuario> lista = new ArrayList<Prontuario>();
				
				if (validarCamposPreenchidos()) {
					
					try {
						
						// Monta os dados de pesquisa
						Prontuario p = new Prontuario();
						p.setPessoa(new Pessoa());
						p.getPessoa().setNomePessoa(getNome());
						p.getPessoa().setDataNascimento(getDataNascimento() != "" ? sdf.parse(getDataNascimento()) : null);
						p.setNumeroProntuario(getNumeroProntuario());
						p.getPessoa().setNumeroOficio(getNumeroOficio());
						p.getPessoa().setNumeroAutos(getNumeroAutos());
						p.getPessoa().setNumeroGuiaAcolhimento(getNumeroGuiaAcolhimento());
						p.getPessoa().setNomeMae(getNomeMae());
						p.getPessoa().setNomePai(getNomePai());
						
						
						// Monta os dados de filtro da datatable
						FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
						
						// Efetua a pesquisa
						lista = ProntuarioServico.getInstance().pesquisar(p, filtro);
						
						// Efetua uma pesquisa para saber a quantidade total de registros encontrados
						setRowCount(ProntuarioServico.getInstance().pesquisarQuantidadeRegistros(p));
						
						erroPesquisar = false;
						
					} catch (ParseException e) {
						logger.error(e);
						notificarErroPesquisaLazy("Erro de data de nascimento.", FacesMessage.SEVERITY_ERROR);
						
					} catch (BancoDadosException e) {
						notificarErroPesquisaLazy("Erro ao tentar pesquisar.", FacesMessage.SEVERITY_FATAL);
					}
				}
				
				return lista;
			}
		};
	}
	
	private boolean validarCamposPreenchidos() {
		
		if (getNome().equals("") && getDataNascimento().equals("") && getNumeroProntuario().equals("") && getNumeroOficio().equals("") && getNumeroAutos().equals("")
				&& getNumeroGuiaAcolhimento().equals("") && getNomeMae().equals("") && getNomePai().equals("")) {
			notificarErroPesquisaLazy("Informe pelo menos um dos campos de pesquisa.", FacesMessage.SEVERITY_WARN);
			
			return false;
		}
		
		return true;
	}
	
	public void enviar(AjaxBehaviorEvent event) {
		
		try {
			
			String redirect = "";
			
			Prontuario p = new Prontuario();
			p.setPessoa(new Pessoa());
			p.getPessoa().setNomePessoa(getNome().toUpperCase());
			p.getPessoa().setDataNascimento((getDataNascimento() != null && !getDataNascimento().equals("")) ? sdf.parse(getDataNascimento()) : null);
			p.getPessoa().setNomePai(getNomePai());
			p.getPessoa().setNomeMae(getNomeMae());
			p.setNumeroProntuario(getNumeroProntuario());
			p.getPessoa().setNumeroOficio(getNumeroOficio());
			p.getPessoa().setNumeroAutos(getNumeroAutos());
			p.getPessoa().setNumeroGuiaAcolhimento(getNumeroGuiaAcolhimento());
			
			int quantidadeEncontrada = ProntuarioServico.getInstance().pesquisarQuantidadeRegistros(p);
			
			if (quantidadeEncontrada == 0) {
				redirect = "cadastro.xhtml";
			} else {
				redirect = "precadastro.xhtml";
			}
			
			// Seta os parametros na sessao 
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("nomePessoa", p.getPessoa().getNomePessoa());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("dataNascimento", getDataNascimento());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("nomePai", p.getPessoa().getNomePai());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("nomeMae", p.getPessoa().getNomeMae());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("numeroProntuario", p.getNumeroProntuario());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("numeroOficio", p.getPessoa().getNumeroOficio());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("numeroAutos", p.getPessoa().getNumeroAutos());
			FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("numeroGuiaAcolhimento", p.getPessoa().getNumeroGuiaAcolhimento());
			
			// Redireciona para a pagina
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);
			
		} catch (ParseException e) {
			logger.error(e);
			lancarMensagemErro("Erro de data de nascimento.", FacesMessage.SEVERITY_ERROR);
			
		} catch (IOException e) {
			logger.error(e);
			lancarMensagemErro("Erro ao tentar redirecionar a página.", FacesMessage.SEVERITY_ERROR);
			
		} catch (BancoDadosException e) {
			lancarMensagemErro("Erro ao tentar pesquisar.", FacesMessage.SEVERITY_FATAL);
		}
	}
	
	public void limparCamposNovoCadastro(AjaxBehaviorEvent event) {
		this.nome = "";
		this.dataNascimento = "";
		this.nomePai = "";
		this.nomeMae = "";
		this.numeroOficio = "";
		this.numeroAutos = "";
		this.numeroGuiaAcolhimento = "";
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(String numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public LazyDataModel<Prontuario> getListaProntuarios() {
		return listaProntuarios;
	}

	public void setListaProntuarios(LazyDataModel<Prontuario> listaProntuarios) {
		this.listaProntuarios = listaProntuarios;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getNumeroOficio() {
		return numeroOficio;
	}

	public void setNumeroOficio(String numeroOficio) {
		this.numeroOficio = numeroOficio;
	}

	public String getNumeroAutos() {
		return numeroAutos;
	}

	public void setNumeroAutos(String numeroAutos) {
		this.numeroAutos = numeroAutos;
	}

	public String getNumeroGuiaAcolhimento() {
		return numeroGuiaAcolhimento;
	}

	public void setNumeroGuiaAcolhimento(String numeroGuiaAcolhimento) {
		this.numeroGuiaAcolhimento = numeroGuiaAcolhimento;
	}
}
