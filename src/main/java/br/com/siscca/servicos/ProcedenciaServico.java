package br.com.siscca.servicos;

public class ProcedenciaServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public ProcedenciaServico() {
		this.setNomeTabela("PROCEDENCIA");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof ProcedenciaServico)) {
			instance = new ProcedenciaServico();
		}
		return instance;
	}
}
