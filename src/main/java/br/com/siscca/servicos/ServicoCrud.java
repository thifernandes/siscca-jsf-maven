package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;

public class ServicoCrud extends SisccaServico implements Serializable {

	protected static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(ServicoCrud.class);
	protected static ServicoCrud instance;
	
	/**
	 * Tabela do banco de dados.
	 * 
	 */
	private String nomeTabela = "";
	
	/**
	 * Campos do banco de dados.
	 * 
	 */
	private static String identificador = "ID";
	private static String nome = "NOME";
	
	/**
	 * Clausulas genericas.
	 * 
	 */
	private String clausulaSelect;
	private String clausulaDelete;
	private String clausulaInsert;
	private String clausulaUpdate;
	
	protected void carregarQuerys() {
		clausulaSelect = "SELECT * FROM " + getNomeTabela();
		clausulaDelete = "DELETE FROM " + getNomeTabela() + " WHERE ID = ?";
		clausulaInsert = "INSERT INTO " + getNomeTabela() + " (ID, NOME) VALUES (DEFAULT, ?)";
		clausulaUpdate = "UPDATE " + getNomeTabela() + " SET NOME = ? WHERE ID = ?";
	}
	
	public List listar() throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EntidadeCrud> lista = new ArrayList<EntidadeCrud>();
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = clausulaSelect;
			
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();
			
			EntidadeCrud e = null;
			
			while (rs.next()) {
				e = new EntidadeCrud();
				e.setId(rs.getLong(identificador));
				e.setNome(rs.getString(nome));
				lista.add(e);
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
	
	public EntidadeCrud findById(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		EntidadeCrud ec = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = clausulaSelect;
			sql += " WHERE " + identificador + " = ?";
			
			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				ec = new EntidadeCrud();
				ec.setId(rs.getLong(identificador));
				ec.setNome(rs.getString(nome));
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return ec;
	}

	public List pesquisar(String nomeParam) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		List<EntidadeCrud> lista = new ArrayList<EntidadeCrud>();
		
		try {
			connection = ConexaoServico.getConexao();
			sql.append(getClausulaSelect());
			sql.append(" WHERE 1 = 1 ");
			
			if (nomeParam != null && !nomeParam.equals("")) {
				sql.append("AND " + nome + " LIKE '%" + nomeParam + "%' ");
			}
			
			ps = connection.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			EntidadeCrud e = null;
			
			while (rs.next()) {
				e = new EntidadeCrud();
				e.setId(rs.getLong(identificador));
				e.setNome(rs.getString(nome));
				
				lista.add(e);
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
	
	public void excluir(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = clausulaDelete;
			
			ps = connection.prepareStatement(sql);
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
	
	public void salvar(EntidadeCrud ec) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		String sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (ec.getId() != null) {
				sql = clausulaUpdate;
			} else {
				sql = clausulaInsert;
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (ec.getId() != null) {
				ps.setString(count++, ec.getNome().toUpperCase());
				ps.setLong(count++, ec.getId());
				ps.executeUpdate();
				
			} else {
				ps.setString(count++, ec.getNome().toUpperCase());
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

	public String getNomeTabela() {
		return nomeTabela;
	}

	public void setNomeTabela(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}

	public String getClausulaSelect() {
		return clausulaSelect;
	}

	public void setClausulaSelect(String clausulaSelect) {
		this.clausulaSelect = clausulaSelect;
	}

	public String getClausulaDelete() {
		return clausulaDelete;
	}

	public void setClausulaDelete(String clausulaDelete) {
		this.clausulaDelete = clausulaDelete;
	}

	public String getClausulaInsert() {
		return clausulaInsert;
	}

	public void setClausulaInsert(String clausulaInsert) {
		this.clausulaInsert = clausulaInsert;
	}

	public String getClausulaUpdate() {
		return clausulaUpdate;
	}

	public void setClausulaUpdate(String clausulaUpdate) {
		this.clausulaUpdate = clausulaUpdate;
	}
}
