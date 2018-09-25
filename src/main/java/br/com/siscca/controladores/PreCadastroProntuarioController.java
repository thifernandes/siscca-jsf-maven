package br.com.siscca.controladores;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.servicos.ProntuarioServico;
import br.com.siscca.utils.FiltroPesquisaDatatable;

@ManagedBean
@SessionScoped
public class PreCadastroProntuarioController implements Serializable {

	private static final long serialVersionUID = -4894958403646089594L;
	private static Logger logger = Logger.getLogger(PreCadastroProntuarioController.class);
	
	private String nomePessoa;
	private String dataNascimento;
	private String nomePai;
	private String nomeMae;
	
	private LazyDataModel<Prontuario> listaProntuarios; 
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public void init() {
		
		listaProntuarios = new LazyDataModel<Prontuario>() {
		
			/**
			 * Serial Uid.
			 */
			private static final long serialVersionUID = 1L;
			
			List<Prontuario> lista = new ArrayList<Prontuario>();

			@Override
			public List<Prontuario> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				
				try {
					
					// Monta os dados de pesquisa
					Prontuario prontuario = new Prontuario();
					prontuario = new Prontuario();
					prontuario.setPessoa(new Pessoa());
					prontuario.getPessoa().setNomePessoa((String) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("nomePessoa"));
					
					try {
						String dataNascimento = (String) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("dataNascimento");
						prontuario.getPessoa().setDataNascimento((dataNascimento != null && !dataNascimento.equals("")) ? sdf.parse(dataNascimento) : null);
						
					} catch (ParseException e) {
						logger.error(e);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Erro de data de nascimento.", null));
					}
					
					prontuario.getPessoa().setNomePai((String) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("nomePai"));
					prontuario.getPessoa().setNomeMae((String) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("nomeMae"));
					
					// Monta os dados de filtro da datatable
					FiltroPesquisaDatatable filtro = new FiltroPesquisaDatatable(first, pageSize);
					
					// Efetua a pesquisa
					lista = ProntuarioServico.getInstance().pesquisar(prontuario, filtro);
					
					// Efetua uma pesquisa para saber a quantidade total de registros encontrados
					setRowCount(ProntuarioServico.getInstance().pesquisarQuantidadeRegistros(prontuario));
					
				} catch (BancoDadosException e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_FATAL, "Erro ao tentar pesquisar.", null));
				}
				
				return lista;
			}
			
		};
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
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

	public LazyDataModel<Prontuario> getListaProntuarios() {
		return listaProntuarios;
	}

	public void setListaProntuarios(LazyDataModel<Prontuario> listaProntuarios) {
		this.listaProntuarios = listaProntuarios;
	}
}
