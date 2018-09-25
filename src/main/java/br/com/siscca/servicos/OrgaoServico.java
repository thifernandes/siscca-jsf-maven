package br.com.siscca.servicos;

public class OrgaoServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public OrgaoServico() {
		this.setNomeTabela("ORGAO");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof OrgaoServico)) {
			instance = new OrgaoServico();
		}
		return instance;
	}
}
