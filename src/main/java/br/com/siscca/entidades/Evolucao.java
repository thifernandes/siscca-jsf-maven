package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class Evolucao implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Date dataRegistro;
	private String descricao;
	private Prontuario prontuario;
	private Usuario usuarioRegistro;
	
	public Evolucao() {
		
	}
	
	public String getDescricaoDataTable() {
		if (descricao != null && descricao.length() > 250) 
			return descricao.substring(0, 249);
		return descricao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}
}
