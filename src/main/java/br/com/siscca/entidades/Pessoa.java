package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class Pessoa implements Serializable {

	/**
	 * Serial UID 
	 */
	private static final long serialVersionUID = 1L;
	
	public Pessoa() {
		
	}
	
	public Pessoa(Long id) {
		this.id = id;
	}
	
	private Long id;
	private String nomePessoa;
	private String sexo;
	private String nomeMae;
	private String nomePai;
	private String familiaExtensa;
	private Date dataNascimento;
	private Cidade cidadeNaturalidade;
	private EntidadeCrud grauInstrucao;
	private String nomeEscola;
	private String certidaoNascimento;
	private String rg;
	private String cpf;
	private String endereco;
	private Cidade cidadeEndereco;
	private EntidadeCrud redeReferencia;
	private EntidadeCrud deficiencia;
	private String numeroOficio;
	private String numeroAutos;
	private String numeroGuiaAcolhimento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
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

	public String getFamiliaExtensa() {
		return familiaExtensa;
	}

	public void setFamiliaExtensa(String familiaExtensa) {
		this.familiaExtensa = familiaExtensa;
	}	

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Cidade getCidadeNaturalidade() {
		
		if (cidadeNaturalidade == null) {
			cidadeNaturalidade = new Cidade();
		}
		
		return cidadeNaturalidade;
	}

	public void setCidadeNaturalidade(Cidade cidadeNaturalidade) {
		this.cidadeNaturalidade = cidadeNaturalidade;
	}

	public EntidadeCrud getGrauInstrucao() {
		return grauInstrucao;
	}

	public void setGrauInstrucao(EntidadeCrud grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	public String getNomeEscola() {
		return nomeEscola;
	}

	public void setNomeEscola(String nomeEscola) {
		this.nomeEscola = nomeEscola;
	}	

	public String getCertidaoNascimento() {
		return certidaoNascimento;
	}

	public void setCertidaoNascimento(String certidaoNascimento) {
		this.certidaoNascimento = certidaoNascimento;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Cidade getCidadeEndereco() {
		
		if (cidadeEndereco == null) {
			cidadeEndereco = new Cidade();
		}
		
		return cidadeEndereco;
	}

	public void setCidadeEndereco(Cidade cidadeEndereco) {
		this.cidadeEndereco = cidadeEndereco;
	}

	public EntidadeCrud getRedeReferencia() {
		return redeReferencia;
	}

	public void setRedeReferencia(EntidadeCrud redeReferencia) {
		this.redeReferencia = redeReferencia;
	}

	public EntidadeCrud getDeficiencia() {
		return deficiencia;
	}

	public void setDeficiencia(EntidadeCrud deficiencia) {
		this.deficiencia = deficiencia;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
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
