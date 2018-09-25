package br.com.siscca.servicos;

public class GrauInstrucaoServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public GrauInstrucaoServico() {
		this.setNomeTabela("GRAUINSTRUCAO");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof GrauInstrucaoServico)) {
			instance = new GrauInstrucaoServico();
		}
		return instance;
	}
}
