package br.com.siscca.servicos;

public class MotivoAdmissaoServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public MotivoAdmissaoServico() {
		this.setNomeTabela("MOTIVOADMISSAO");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof MotivoAdmissaoServico)) {
			instance = new MotivoAdmissaoServico();
		}
		return instance;
	}
}
