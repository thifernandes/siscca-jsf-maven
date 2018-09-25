package br.com.siscca.servicos;

public class MotivoEvasaoServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public MotivoEvasaoServico() {
		this.setNomeTabela("MOTIVOEVASAO");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof MotivoEvasaoServico)) {
			instance = new MotivoEvasaoServico();
		}
		return instance;
	}
}
