package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class Prontuario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Pessoa pessoa;
	private Date dataGeracao;
	private Usuario usuarioGerador;
	private String numeroProntuario;
	
	public Prontuario() {
		
	}
	
	public Prontuario(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Usuario getUsuarioGerador() {
		return usuarioGerador;
	}

	public void setUsuarioGerador(Usuario usuarioGerador) {
		this.usuarioGerador = usuarioGerador;
	}

	public String getNumeroProntuario() {
		if (numeroProntuario != null) {
			numeroProntuario = numeroProntuario.toUpperCase();
		}
		return numeroProntuario;
	}

	public void setNumeroProntuario(String numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
}
