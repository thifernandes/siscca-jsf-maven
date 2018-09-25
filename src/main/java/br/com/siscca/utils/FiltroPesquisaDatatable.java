package br.com.siscca.utils;

import java.io.Serializable;

public class FiltroPesquisaDatatable implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private int primeiroRegistro;
	private int quantidadeRegistros;
	
	public FiltroPesquisaDatatable(int primeiroRegistro, int quantidadeRegistros) {
		this.setPrimeiroRegistro(primeiroRegistro);
		this.setQuantidadeRegistros(quantidadeRegistros);
	}
	
	public int getPrimeiroRegistro() {
		return primeiroRegistro;
	}
	
	public void setPrimeiroRegistro(int primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}
	
	public int getQuantidadeRegistros() {
		return quantidadeRegistros;
	}
	
	public void setQuantidadeRegistros(int quantidadeRegistros) {
		this.quantidadeRegistros = quantidadeRegistros;
	}
}
