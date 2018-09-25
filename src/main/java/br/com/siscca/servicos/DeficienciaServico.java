package br.com.siscca.servicos;

public class DeficienciaServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public DeficienciaServico() {
		this.setNomeTabela("DEFICIENCIA");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof DeficienciaServico)) {
			instance = new DeficienciaServico();
		}
		return instance;
	}
}
