package br.com.siscca.servicos;

public class TipoParentescoServico extends ServicoCrud {

	/**
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 1L;

	public TipoParentescoServico() {
		this.setNomeTabela("TIPOPARENTESCO");
		this.carregarQuerys();
	}
	
	public static synchronized ServicoCrud getInstance() {
		if (instance == null || !(instance instanceof TipoParentescoServico)) {
			instance = new TipoParentescoServico();
		}
		return instance;
	}
}
