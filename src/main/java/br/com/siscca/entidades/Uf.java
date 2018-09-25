package br.com.siscca.entidades;

import java.io.Serializable;

public class Uf implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private String siglaUf;
	private String nomeUf;

	public Uf() {
	}
	
	public Uf(String siglaUf) {
		this.siglaUf = siglaUf;
	}
	
	public String getSiglaUf() {
		return siglaUf;
	}
	public void setSiglaUf(String siglaUf) {
		this.siglaUf = siglaUf;
	}
	public String getNomeUf() {
		return nomeUf;
	}
	public void setNomeUf(String nomeUf) {
		this.nomeUf = nomeUf;
	}
	
	@Override
	public String toString() {
		return String.format("Uf[%s, %s]", siglaUf, nomeUf);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Uf))
			return false;
		Uf other = (Uf) obj;
		if (siglaUf == null){
			if (other.siglaUf != null)
				return false;
		} else if (!siglaUf.equals(other.siglaUf))
			return false;
		return true;
	}
}
