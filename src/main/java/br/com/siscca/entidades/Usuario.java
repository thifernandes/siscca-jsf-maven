package br.com.siscca.entidades;

import java.io.Serializable;

import br.com.siscca.enums.TipoPerfilEnum;

public class Usuario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String login;
	private String senha;
	private Integer tipoPerfil;
	private String matricula;
	private String nome;
	private String status;
	
	public Usuario(Long id, String login, String senha, Integer tipoPerfil, String matricula, String nome, String status) {
		this.id = id;
		this.login = login;
		this.senha = senha;
		this.tipoPerfil = tipoPerfil;
		this.matricula = matricula;
		this.nome = nome;
		this.status = status;
	}
	
	public Usuario() {
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Integer getTipoPerfil() {
		return tipoPerfil;
	}

	public void setTipoPerfil(Integer tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}
	
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
	public String getTipoPerfilDescricao() {
		String descricao = "";
		
		for (TipoPerfilEnum tpEnum : TipoPerfilEnum.values()) {
			if (tpEnum.getValor().equals(tipoPerfil)) {
				descricao = tpEnum.getDescricao();
			}
		}
		
		return descricao;
	}
	
	public String getNomeMatricula() {
		return getNome() + " - Matrícula: " + getMatricula();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getStatusDescricao() {
		return status.equals("1") ? "ATIVO" : "INATIVO";
	}
	
	public Boolean getUsuarioAtivo() {
		return status.equals("1");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
