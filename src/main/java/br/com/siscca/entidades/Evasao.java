package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class Evasao implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private EntidadeCrud motivoEvasao;
	private Date dataRegistro;
	private String observacoes;
	private Prontuario prontuario;
	private Usuario usuarioRegistro;
	
	public Evasao() {
		
	}
	
	public String getObservacoesDataTable() {
		if (observacoes != null && observacoes.length() > 250) 
			return observacoes.substring(0, 249);
		return observacoes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EntidadeCrud getMotivoEvasao() {
		return motivoEvasao;
	}

	public void setMotivoEvasao(EntidadeCrud motivoEvasao) {
		this.motivoEvasao = motivoEvasao;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
}
