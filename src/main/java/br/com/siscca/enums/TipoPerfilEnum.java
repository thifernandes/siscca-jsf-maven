package br.com.siscca.enums;

public enum TipoPerfilEnum {

	ADMINISTRADOR(1, "ADMINISTRADOR"), SUPERVISOR(2, "SUPERVISOR"), CONSULTA(3, "CONSULTA");
	
	private Integer valor;
	private String descricao;
	
	private TipoPerfilEnum(Integer valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public Integer getValor() {
		return this.valor;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
}
