package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Admissao;
import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.utils.FiltroPesquisaDatatable;

public class AdmissaoServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AdmissaoServico.class);
	private static AdmissaoServico instance;

	private static String nomeTabela = "ADMISSAO";
	private static StringBuilder clausulaInsert;
	private static StringBuilder clausulaUpdate;
	private static StringBuilder clausulaDelete;
	private static StringBuilder clausulaSelect;
	private static StringBuilder clausulaCount;

	private AdmissaoServico() {
		
		/* Monta a clausula insert */
		clausulaInsert = new StringBuilder();
		clausulaInsert.append("INSERT INTO " + nomeTabela + "(");
		clausulaInsert.append("ID, ");
		clausulaInsert.append("IDMOTIVOADMISSAO, ");
		clausulaInsert.append("DATAREGISTRO, ");
		clausulaInsert.append("IDPROCEDENCIA, ");
		clausulaInsert.append("OBSERVACOES, ");
		clausulaInsert.append("IDPRONTUARIO, ");
		clausulaInsert.append("IDUSUARIOREGISTRO) VALUES (");
		clausulaInsert.append("DEFAULT, ");
		clausulaInsert.append("?, ");
		clausulaInsert.append("?, ");
		clausulaInsert.append("?, ");
		clausulaInsert.append("?, ");
		clausulaInsert.append("?, ");
		clausulaInsert.append("?)");
		
		/* Monta a clausula update */
		clausulaUpdate = new StringBuilder();
		clausulaUpdate.append("UPDATE " + nomeTabela + " SET ");
		clausulaUpdate.append("IDMOTIVOADMISSAO = ?, ");
		clausulaUpdate.append("IDPROCEDENCIA = ?, ");
		clausulaUpdate.append("OBSERVACOES = ?, ");
		clausulaUpdate.append("IDUSUARIOREGISTRO = ? ");
		clausulaUpdate.append("WHERE ID = ?");
		
		/* Monta a clausula delete */
		clausulaDelete = new StringBuilder();
		clausulaDelete.append("DELETE FROM " + nomeTabela);
		clausulaDelete.append(" WHERE ID = ?");
		
		/* Monta a clausula select */
		clausulaSelect = new StringBuilder();
		clausulaSelect.append("SELECT * FROM " + nomeTabela);
		
		/* Monta a clausula count */
		clausulaCount = new StringBuilder();
		clausulaCount.append("SELECT COUNT(*) QUANTIDADE FROM " + nomeTabela);
		clausulaCount.append(" WHERE IDPRONTUARIO = ?");
	}

	public static synchronized AdmissaoServico getInstance() {
		if (instance == null) {
			instance = new AdmissaoServico();
		}
		return instance;
	}
	
	public List<Admissao> pesquisar(Prontuario p, FiltroPesquisaDatatable filtro) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Admissao> lista = new ArrayList<Admissao>();
		
		try {

			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE IDPRONTUARIO = ?");
			sql.append(" ORDER BY DATAREGISTRO DESC");
			sql.append(" LIMIT ");
			sql.append(filtro.getPrimeiroRegistro());
			sql.append(", ");
			sql.append(filtro.getQuantidadeRegistros());
			
			connection = ConexaoServico.getConexao();
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, p.getId());
			
			rs = ps.executeQuery();
			
			Admissao entidade = null;
			
			while (rs.next()) {
				entidade = recuperarAdmissao(rs);
				lista.add(entidade);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return lista;
	}
	
	public int pesquisarQuantidadeRegistros(Prontuario p) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int quantidade = 0;
		
		try {
			
			connection = ConexaoServico.getConexao();
			ps = connection.prepareStatement(clausulaCount.toString());
			ps.setLong(1, p.getId());
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				quantidade = rs.getInt("QUANTIDADE");
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return quantidade;
	}
	
	public Admissao recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Admissao admissao = null;
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE ID = ?");
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				admissao = recuperarAdmissao(rs);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return admissao;
	}
	
	public void salvar(Admissao a) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (a.getId() != null) {
				sql = clausulaUpdate;
			} else {
				sql = clausulaInsert;
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (a.getId() != null) {
				ps.setLong(count++, a.getMotivoAdmissao().getId());
				ps.setLong(count++, a.getProcedencia().getId());
				ps.setString(count++, a.getObservacoes().toUpperCase());
				ps.setLong(count++, a.getUsuarioRegistro().getId());
				
				ps.setLong(count++, a.getId());
				ps.executeUpdate();
				
			} else {
				ps.setLong(count++, a.getMotivoAdmissao().getId());
				ps.setDate(count++, new Date(new java.util.Date().getTime()));
				ps.setLong(count++, a.getProcedencia().getId());
				ps.setString(count++, a.getObservacoes().toUpperCase());
				ps.setLong(count++, a.getProntuario().getId());
				ps.setLong(count++, a.getUsuarioRegistro().getId());
				ps.execute();
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
	}
	
	public void excluir(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = clausulaDelete;
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, id);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
	}
	
	/**
	 * Método que monta a entidade atraves do resultSet.
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Admissao recuperarAdmissao(ResultSet rs) throws SQLException, BancoDadosException {
		
		Long id;
		Admissao entidade = new Admissao();
		
		entidade.setId(rs.getLong("ID"));
		id = new Long(rs.getLong("IDMOTIVOADMISSAO"));
		EntidadeCrud motAdmissao = MotivoAdmissaoServico.getInstance().findById(id);
		entidade.setMotivoAdmissao(motAdmissao);
		
		entidade.setDataRegistro(rs.getDate("DATAREGISTRO"));
		
		id = new Long(rs.getLong("IDPROCEDENCIA"));
		EntidadeCrud procedencia = ProcedenciaServico.getInstance().findById(id);
		entidade.setProcedencia(procedencia);
		
		entidade.setObservacoes(rs.getString("OBSERVACOES"));
		
		id = new Long(rs.getLong("IDPRONTUARIO"));
		Prontuario prontuario = (Prontuario) ProntuarioServico.getInstance().recuperarPorId(id);
		entidade.setProntuario(prontuario);
		
		id = new Long(rs.getLong("IDUSUARIOREGISTRO"));
		Usuario usuario = (Usuario) UsuarioServico.getInstance().recuperarPorId(id);
		entidade.setUsuarioRegistro(usuario);

		return entidade;
	}
}
