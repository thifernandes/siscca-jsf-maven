package br.com.siscca.servicos;

public class RedeReferenciaServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public RedeReferenciaServico() {
		this.setNomeTabela("REDEREFERENCIA");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof RedeReferenciaServico)) {
			instance = new RedeReferenciaServico();
		}
		return instance;
	}
}
