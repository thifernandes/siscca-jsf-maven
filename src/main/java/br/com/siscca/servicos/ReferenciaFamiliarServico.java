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

import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.ReferenciaFamiliar;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.utils.FiltroPesquisaDatatable;

public class ReferenciaFamiliarServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ReferenciaFamiliarServico.class);
	private static ReferenciaFamiliarServico instance;
	
	private static String nomeTabela = "REFERENCIAFAMILIAR";
	private static StringBuilder clausulaInsert;
	private static StringBuilder clausulaUpdate;
	private static StringBuilder clausulaDelete;
	private static StringBuilder clausulaSelect;
	private static StringBuilder clausulaCount;

	private ReferenciaFamiliarServico() {
		
		/* Monta a clausula insert */
		clausulaInsert = new StringBuilder();
		clausulaInsert.append("INSERT INTO " + nomeTabela + "(");
		clausulaInsert.append("ID, ");
		clausulaInsert.append("NOME, ");
		clausulaInsert.append("DATANASCIMENTO, ");
		clausulaInsert.append("IDTIPOPARENTESCO, ");
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
		clausulaUpdate.append("NOME = ?, ");
		clausulaUpdate.append("DATANASCIMENTO = ?, ");
		clausulaUpdate.append("IDTIPOPARENTESCO = ?, ");
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

	public static synchronized ReferenciaFamiliarServico getInstance() {
		if (instance == null) {
			instance = new ReferenciaFamiliarServico();
		}
		return instance;
	}
	
	public List<ReferenciaFamiliar> pesquisar(Prontuario p, FiltroPesquisaDatatable filtro) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ReferenciaFamiliar> lista = new ArrayList<ReferenciaFamiliar>();
		
		try {
			
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE IDPRONTUARIO = ?");
			sql.append(" ORDER BY ID DESC");
			sql.append(" LIMIT ");
			sql.append(filtro.getPrimeiroRegistro());
			sql.append(", ");
			sql.append(filtro.getQuantidadeRegistros());
			
			connection = ConexaoServico.getConexao();
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, p.getId());
			rs = ps.executeQuery();
			
			ReferenciaFamiliar entidade = null;
			
			while (rs.next()) {
				entidade = recuperarEntidade(rs);
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
	
	public ReferenciaFamiliar recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ReferenciaFamiliar entidade = null;
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE ID = ?");
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				entidade = recuperarEntidade(rs);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return entidade;
	}
	
	public void salvar(ReferenciaFamiliar rf) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (rf.getId() != null) {
				sql = clausulaUpdate;
			} else {
				sql = clausulaInsert;
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (rf.getId() != null) {
				ps.setString(count++, rf.getNome().toUpperCase());
				ps.setDate(count++, new Date(rf.getDataNascimento().getTime()));
				ps.setLong(count++, rf.getTipoParentesco().getId());
				ps.setString(count++, rf.getObservacoes().toUpperCase());
				ps.setLong(count++, rf.getUsuarioRegistro().getId());
				
				ps.setLong(count++, rf.getId());
				ps.executeUpdate();
				
			} else {
				ps.setString(count++, rf.getNome().toUpperCase());
				ps.setDate(count++, new Date(rf.getDataNascimento().getTime()));
				ps.setLong(count++, rf.getTipoParentesco().getId());
				ps.setString(count++, rf.getObservacoes().toUpperCase());
				ps.setLong(count++, rf.getProntuario().getId());
				ps.setLong(count++, rf.getUsuarioRegistro().getId());
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
	private ReferenciaFamiliar recuperarEntidade(ResultSet rs) throws SQLException, BancoDadosException {
		
		Long id;
		ReferenciaFamiliar entidade = new ReferenciaFamiliar();
		entidade.setId(rs.getLong("ID"));
		entidade.setNome(rs.getString("NOME"));
		entidade.setDataNascimento(rs.getDate("DATANASCIMENTO"));
		entidade.setObservacoes(rs.getString("OBSERVACOES"));
		
		id = new Long(rs.getLong("IDTIPOPARENTESCO"));
		EntidadeCrud tipoParentesco = TipoParentescoServico.getInstance().findById(id);
		entidade.setTipoParentesco(tipoParentesco);
		
		id = new Long(rs.getLong("IDPRONTUARIO"));
		Prontuario prontuario = (Prontuario) ProntuarioServico.getInstance().recuperarPorId(id);
		entidade.setProntuario(prontuario);
		
		id = new Long(rs.getLong("IDUSUARIOREGISTRO"));
		Usuario usuario = (Usuario) UsuarioServico.getInstance().recuperarPorId(id);
		entidade.setUsuarioRegistro(usuario);

		return entidade;
	}
}
