package br.com.siscca.entidades;

import java.io.Serializable;
import java.util.Date;

public class Admissao implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private EntidadeCrud motivoAdmissao;
	private Date dataRegistro;
	private EntidadeCrud procedencia;
	private String observacoes;
	private Prontuario prontuario;
	private Usuario usuarioRegistro;
	
	public Admissao() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EntidadeCrud getMotivoAdmissao() {
		return motivoAdmissao;
	}

	public void setMotivoAdmissao(EntidadeCrud motivoAdmissao) {
		this.motivoAdmissao = motivoAdmissao;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public EntidadeCrud getProcedencia() {
		return procedencia;
	}

	public void setProcedencia(EntidadeCrud procedencia) {
		this.procedencia = procedencia;
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
	
	public String getObservacoesDataTable() {
		if (observacoes != null && observacoes.length() > 250) 
			return observacoes.substring(0, 249);
		return observacoes;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
}
