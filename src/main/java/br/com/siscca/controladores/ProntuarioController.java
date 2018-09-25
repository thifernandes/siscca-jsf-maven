package br.com.siscca.controladores;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import br.com.siscca.entidades.Cidade;
import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Uf;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.infra.seguranca.SessionContext;
import br.com.siscca.servicos.CidadeServico;
import br.com.siscca.servicos.DeficienciaServico;
import br.com.siscca.servicos.GrauInstrucaoServico;
import br.com.siscca.servicos.OrgaoServico;
import br.com.siscca.servicos.PessoaServico;
import br.com.siscca.servicos.ProntuarioServico;
import br.com.siscca.servicos.RedeReferenciaServico;
import br.com.siscca.servicos.UfServico;

@ManagedBean
@ViewScoped
public class ProntuarioController extends AbstractSisccaController {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProntuarioController.class);
	
	private Long idProntuario;
	private Prontuario prontuario;
	private boolean dadosCarregados;
	
	private List<Uf> listaUfsNaturalidade;
	private List<Cidade> listaCidadesNaturalidade;
	private List<EntidadeCrud> listaGrausInstrucao;
	private List<EntidadeCrud> listaOrgaos;
	private List<Uf> listaUfsEndereco;
	private List<Cidade> listaCidadesEndereco;
	
	private Boolean possuiDeficiencia;
	private List<EntidadeCrud> listaDeficiencias;
	private List<EntidadeCrud> listaRedesReferencia;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private TabView tabView;
	private Integer indiceTabView;
	
	// Injeta os beans no atributo
	@ManagedProperty(value="#{enderecoController}")
	private EnderecoController enderecoController;
	
	@ManagedProperty(value="#{contatoController}")
	private ContatoController contatoController;
	
	@ManagedProperty(value="#{admissaoController}")
	private AdmissaoController admissaoController;
	
	@ManagedProperty(value="#{encaminhamentoController}")
	private EncaminhamentoController encaminhamentoController;
	
	@ManagedProperty(value="#{referenciaFamiliarController}")
	private ReferenciaFamiliarController referenciaFamiliarController;

	@ManagedProperty(value="#{acessoMenuController}")
	private AcessoMenuController acessoMenuController;
	
	@ManagedProperty(value="#{evasaoController}")
	private EvasaoController evasaoController;
	
	@ManagedProperty(value="#{evolucaoController}")
	private EvolucaoController evolucaoController;
		
	public void init() {
		
		try {
			
			if (getIndiceTabView() != null && getIndiceTabView().equals(1)) {
				
				if (!dadosCarregados) {
					
					carregarUfs();
					carregarGrausInstrucao();
					carregarOrgaos();
					carregarRedesReferencia();
					
					// Consultando/Alterando
					if (getIdProntuario() != null) {
						
						prontuario = ProntuarioServico.getInstance().recuperarPorId(getIdProntuario());
						recuperarListaCidadesNaturalidade(null);
						recuperarListaCidadesEndereco(null);
						
						if (prontuario.getPessoa().getDeficiencia() != null) {
							setPossuiDeficiencia(Boolean.TRUE);
							carregarListaDeficiencias(null);
						}
						
						// Incluindo
					} else {
						
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
					}
					
					dadosCarregados = true;
					RequestContext.getCurrentInstance().update("tb:formDadosPessoais");
				}
			}
				
		} catch (BancoDadosException e) {
			// Nao faz nada pq as mensagens de erro sao lancadas idependentemente por cada metodo.
		}
	}
	
	private void carregarUfs() throws BancoDadosException {
		
		try {
			
			List<Uf> lista = UfServico.getInstance().listar();
			listaUfsNaturalidade = lista;
			listaUfsEndereco = lista;
			
		} catch (BancoDadosException e) {
			lancarMensagemErro("Erro ao tentar recuperar a lista de Ufs.", FacesMessage.SEVERITY_FATAL);
			throw e;
		}
	}
	
	public void recuperarListaCidadesNaturalidade(AjaxBehaviorEvent event) {
		
		try {
			
			List<Cidade> listaCidades = new ArrayList<Cidade>();
			
			if (prontuario.getPessoa().getCidadeNaturalidade() != null && 
					prontuario.getPessoa().getCidadeNaturalidade().getUf() != null && 
					prontuario.getPessoa().getCidadeNaturalidade().getUf().getSiglaUf() != null) {
				
				listaCidades = CidadeServico.getInstance().recuperarPorSiglaUf(prontuario.getPessoa().getCidadeNaturalidade().getUf().getSiglaUf());
			}
			
			listaCidadesNaturalidade = listaCidades;
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar a lista de cidades.", null));
		}
	}
	
	private void carregarGrausInstrucao() throws BancoDadosException {
		
		try {
			
			List<EntidadeCrud> lista = GrauInstrucaoServico.getInstance().listar();
			listaGrausInstrucao = lista;
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar a lista de graus de instrução.", null));
			throw e;
		}
	}
	
	private void carregarOrgaos() throws BancoDadosException {
		
		try {
			
			List<EntidadeCrud> lista = OrgaoServico.getInstance().listar();
			listaOrgaos = lista;
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar a lista de órgãos.", null));
			throw e;
		}
	}
	
	private void carregarRedesReferencia() throws BancoDadosException {
		
		try {
			
			List<EntidadeCrud> lista = RedeReferenciaServico.getInstance().listar();
			listaRedesReferencia = lista;
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar lista de redes de referência.", null));
			throw e;
		}
	}
	
	public void recuperarListaCidadesEndereco(AjaxBehaviorEvent event) {
		
		try {
			
			List<Cidade> listaCidades = new ArrayList<Cidade>();
			
			if (prontuario.getPessoa().getCidadeEndereco() != null && 
					prontuario.getPessoa().getCidadeEndereco().getUf() != null && 
					prontuario.getPessoa().getCidadeEndereco().getUf().getSiglaUf() != null) {
				
				listaCidades = CidadeServico.getInstance().recuperarPorSiglaUf(prontuario.getPessoa().getCidadeEndereco().getUf().getSiglaUf());
			}
			
			listaCidadesEndereco = listaCidades;
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar a lista de cidades.", null));
		}
	}
	
	public void carregarListaDeficiencias(AjaxBehaviorEvent event) throws BancoDadosException {
		
		try {
			
			if (getPossuiDeficiencia()) {
				listaDeficiencias = DeficienciaServico.getInstance().listar();
				
				if (prontuario.getPessoa().getDeficiencia() == null) {
					prontuario.getPessoa().setDeficiencia(new EntidadeCrud());
				}
				
			} else {
				listaDeficiencias = new ArrayList<EntidadeCrud>();
				prontuario.getPessoa().setDeficiencia(null);
			}
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar recuperar a lista de deficiências.", null));
			throw e;
		}
	}
	
	public void salvarDadosPessoais(AjaxBehaviorEvent event) {
		
		try {
			// Salva os dados pessoais
			prontuario.setPessoa(PessoaServico.getInstance().salvar(getProntuario().getPessoa()));
			
			// Salva o prontuario, caso esteja inserindo
			//if (this.getIdProntuario() == null) {
				
				Usuario usuarioLogado = SessionContext.getInstance().getUsuarioLogado();
				prontuario.setUsuarioGerador(usuarioLogado);
			//}
			
			prontuario = ProntuarioServico.getInstance().salvar(getProntuario());
			this.setIdProntuario(prontuario.getId());

			/* Atualiza o objeto prontuario */
			prontuario = ProntuarioServico.getInstance().recuperarPorId(idProntuario);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Dados pessoais salvos com sucesso.", null));
			
		} catch (BancoDadosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_FATAL, "Erro ao tentar salvar os dados pessoais.", null));
		}
		
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("mensagens");
	}
	
	public void voltar(AjaxBehaviorEvent event) {
		try {
			
			FacesContext.getCurrentInstance().getExternalContext().redirect("consultar.xhtml");
			
		} catch (IOException e) {
			logger.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Erro ao tentar redirecionar para a página de consulta.", null));
		}
	}
	
	public void carregarListaTabs(TabChangeEvent event) {
		
		/* Só tenta buscar os dados caso seja de uma pessoa ja cadastrada. 
		 * Se estiver cadastrando um novo usuario nao ha de se falar em pesquisar essas informacoes.
		 */
		if (getIdProntuario() != null) {
			
			/* Faz o controle de qual tab esta sendo acessada. 
			 * So recupera os dados correspondentes a tab acessada.
			 */
			this.setIndiceTabView(event.getComponent().getChildren().indexOf(event.getTab()) + 1);
			
			if (indiceTabView.equals(2)) {
				enderecoController.setProntuario(prontuario);
				enderecoController.pesquisar();
				
			} else if (indiceTabView.equals(3)) {
				contatoController.setPessoa(prontuario.getPessoa());
				contatoController.pesquisar();
				
			} else if (indiceTabView.equals(4)) {
				admissaoController.setProntuario(prontuario);
				admissaoController.pesquisar();
				
			} else if (indiceTabView.equals(5)) {
				encaminhamentoController.setProntuario(prontuario);
				encaminhamentoController.pesquisar();
				
			} else if (indiceTabView.equals(6)) {
				referenciaFamiliarController.setProntuario(prontuario);
				referenciaFamiliarController.pesquisar();
				
			} else if (indiceTabView.equals(7)) {
				evasaoController.setProntuario(prontuario);
				evasaoController.pesquisar();
				
			} else if (indiceTabView.equals(8)) {
				evolucaoController.setProntuario(prontuario);
				evolucaoController.pesquisar();
			}
			
			resetarPaginacaoDatatable("tb:formEnderecos:datatableEndereco");
			resetarPaginacaoDatatable("tb:formContato:datatableContato");
			resetarPaginacaoDatatable("tb:formAdmissoes:datatableAdmissao");
			resetarPaginacaoDatatable("tb:formEncaminhamentos:datatableEncaminhamento");
			resetarPaginacaoDatatable("tb:formReferenciaFamiliar:datatableReferenciaFamiliar");
			resetarPaginacaoDatatable("tb:formEvasoes:datatableEvasao");
			resetarPaginacaoDatatable("tb:formEvolucoes:datatableEvolucao");
		}
	}
	
	private void resetarPaginacaoDatatable(String nomeDatatable) {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(nomeDatatable);
		
		if (dataTable != null) {
			dataTable.reset();
		}
	}
	
	/**
	 * Método que faz o controle dinamico da chamada dos RemoteCommand para verificar se houve algum erro
	 * nas pesquisas.
	 */
	public void verificarErroPesquisa() {
		
		if (indiceTabView != null) {
			
			if (indiceTabView.equals(1)) {
				this.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(2)) {
				enderecoController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(2)) {
				contatoController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(3)) {
				admissaoController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(4)) {
				encaminhamentoController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(5)) {
				referenciaFamiliarController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(6)) {
				evasaoController.verificarErroPesquisaLazy();
				
			} else if (indiceTabView.equals(7)) {
				evolucaoController.verificarErroPesquisaLazy();
			}
			
		} else {
			this.verificarErroPesquisaLazy();
		}
	}

	public Long getIdProntuario() {
		if (idProntuario != null && idProntuario.equals(0L)) {
			idProntuario = null;
		}
		return idProntuario;
	}

	public void setIdProntuario(Long idProntuario) {
		this.idProntuario = idProntuario;
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public List<Uf> getListaUfsNaturalidade() {
		return listaUfsNaturalidade;
	}

	public void setListaUfsNaturalidade(List<Uf> listaUfsNaturalidade) {
		this.listaUfsNaturalidade = listaUfsNaturalidade;
	}

	public List<Cidade> getListaCidadesNaturalidade() {
		return listaCidadesNaturalidade;
	}

	public void setListaCidadesNaturalidade(List<Cidade> listaCidadesNaturalidade) {
		this.listaCidadesNaturalidade = listaCidadesNaturalidade;
	}

	public List<EntidadeCrud> getListaGrausInstrucao() {
		return listaGrausInstrucao;
	}

	public void setListaGrausInstrucao(List<EntidadeCrud> listaGrausInstrucao) {
		this.listaGrausInstrucao = listaGrausInstrucao;
	}

	public List<EntidadeCrud> getListaOrgaos() {
		return listaOrgaos;
	}

	public void setListaOrgaos(List<EntidadeCrud> listaOrgaos) {
		this.listaOrgaos = listaOrgaos;
	}

	public List<Uf> getListaUfsEndereco() {
		return listaUfsEndereco;
	}

	public void setListaUfsEndereco(List<Uf> listaUfsEndereco) {
		this.listaUfsEndereco = listaUfsEndereco;
	}

	public List<Cidade> getListaCidadesEndereco() {
		return listaCidadesEndereco;
	}

	public void setListaCidadesEndereco(List<Cidade> listaCidadesEndereco) {
		this.listaCidadesEndereco = listaCidadesEndereco;
	}

	public Boolean getPossuiDeficiencia() {
		return possuiDeficiencia;
	}

	public void setPossuiDeficiencia(Boolean possuiDeficiencia) {
		this.possuiDeficiencia = possuiDeficiencia;
	}

	public List<EntidadeCrud> getListaDeficiencias() {
		return listaDeficiencias;
	}

	public void setListaDeficiencias(List<EntidadeCrud> listaDeficiencias) {
		this.listaDeficiencias = listaDeficiencias;
	}

	public List<EntidadeCrud> getListaRedesReferencia() {
		return listaRedesReferencia;
	}

	public void setListaRedesReferencia(List<EntidadeCrud> listaRedesReferencia) {
		this.listaRedesReferencia = listaRedesReferencia;
	}

	public EnderecoController getEnderecoController() {
		return enderecoController;
	}

	public void setEnderecoController(EnderecoController enderecoController) {
		this.enderecoController = enderecoController;
	}

	public AdmissaoController getAdmissaoController() {
		return admissaoController;
	}

	public void setAdmissaoController(AdmissaoController admissaoController) {
		this.admissaoController = admissaoController;
	}

	public EncaminhamentoController getEncaminhamentoController() {
		return encaminhamentoController;
	}

	public void setEncaminhamentoController(EncaminhamentoController encaminhamentoController) {
		this.encaminhamentoController = encaminhamentoController;
	}

	public AcessoMenuController getAcessoMenuController() {
		return acessoMenuController;
	}

	public void setAcessoMenuController(AcessoMenuController acessoMenuController) {
		this.acessoMenuController = acessoMenuController;
	}

	public EvasaoController getEvasaoController() {
		return evasaoController;
	}

	public void setEvasaoController(EvasaoController evasaoController) {
		this.evasaoController = evasaoController;
	}

	public EvolucaoController getEvolucaoController() {
		return evolucaoController;
	}

	public void setEvolucaoController(EvolucaoController evolucaoController) {
		this.evolucaoController = evolucaoController;
	}

	public ReferenciaFamiliarController getReferenciaFamiliarController() {
		return referenciaFamiliarController;
	}

	public void setReferenciaFamiliarController(ReferenciaFamiliarController referenciaFamiliarController) {
		this.referenciaFamiliarController = referenciaFamiliarController;
	}

	public Integer getIndiceTabView() {
		Integer indice = tabView.getActiveIndex() + 1;
		return indice;
	}

	public void setIndiceTabView(Integer indiceTabView) {
		this.indiceTabView = indiceTabView;
	}

	public TabView getTabView() {
		return tabView;
	}

	public void setTabView(TabView tabView) {
		this.tabView = tabView;
	}

	public ContatoController getContatoController() {
		return contatoController;
	}

	public void setContatoController(ContatoController contatoController) {
		this.contatoController = contatoController;
	}
}
