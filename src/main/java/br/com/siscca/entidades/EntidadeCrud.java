package br.com.siscca.entidades;

import java.io.Serializable;

public class EntidadeCrud implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	
	public EntidadeCrud() {
		
	}
	
	public EntidadeCrud(String id) {
		this.id = Long.parseLong(id);
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
	
	@Override
	public String toString() {
		return String.format("EntidadeCrud[%s, %s]", id, nome);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntidadeCrud))
			return false;
		EntidadeCrud other = (EntidadeCrud) obj;
		if (id == null){
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
