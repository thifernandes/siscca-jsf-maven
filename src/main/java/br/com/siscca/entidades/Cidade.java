package br.com.siscca.entidades;

import java.io.Serializable;

public class Cidade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String nome;
	private Uf uf;
	
	public Cidade() {
	}
	
	public Cidade(String id) {
		this.id = Integer.parseInt(id);
	}
	
	public Cidade(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Uf getUf() {
		
		if (uf == null) {
			uf = new Uf();
		}
		
		return uf;
	}
	public void setUf(Uf uf) {
		this.uf = uf;
	}
	
	@Override
	public String toString() {
		return String.format("Cidade[%s, %s]", id, nome);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cidade))
			return false;
		Cidade other = (Cidade) obj;
		if (id == null){
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
