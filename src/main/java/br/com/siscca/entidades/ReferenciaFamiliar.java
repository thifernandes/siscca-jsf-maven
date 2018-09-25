package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class ReferenciaFamiliar implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private Date dataNascimento;
	private EntidadeCrud tipoParentesco;
	private String observacoes;
	private Usuario usuarioRegistro;
	private Prontuario prontuario;
	
	public ReferenciaFamiliar() {
		
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EntidadeCrud getTipoParentesco() {
		return tipoParentesco;
	}

	public void setTipoParentesco(EntidadeCrud tipoParentesco) {
		this.tipoParentesco = tipoParentesco;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Prontuario getProntuario() {
		return prontuario;
	}

	public void setProntuario(Prontuario prontuario) {
		this.prontuario = prontuario;
	}
}
