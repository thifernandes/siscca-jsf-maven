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

import br.com.siscca.entidades.Endereco;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.utils.FiltroPesquisaDatatable;

public class EnderecoServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(EnderecoServico.class);
	private static EnderecoServico instance;
	
	private static String nomeTabela = "ENDERECO";
	private static String clausulaInsert = "INSERT INTO " + nomeTabela + " (ID, ENDERECO, DATAREGISTRO, IDPRONTUARIO, IDUSUARIOREGISTRO) "
			+ "VALUES (DEFAULT, ?, ?, ?, ?)";
	private static String clausulaUpdate = "UPDATE " + nomeTabela + " SET ENDERECO = ?, IDUSUARIOREGISTRO = ? WHERE ID = ?";
	private static String clausulaDelete = "DELETE FROM " + nomeTabela + " WHERE ID = ?";
	private static String clausulaSelect = "SELECT * FROM " + nomeTabela;
	private static String clausulaCount = "SELECT COUNT(*) QUANTIDADE FROM " + nomeTabela + " WHERE IDPRONTUARIO = ?";

	private EnderecoServico() {
	}

	public static synchronized EnderecoServico getInstance() {
		if (instance == null) {
			instance = new EnderecoServico();
		}
		return instance;
	}
	
	public List<Endereco> pesquisar(Prontuario p, FiltroPesquisaDatatable filtro) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Endereco> lista = new ArrayList<Endereco>();
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE IDPRONTUARIO = ?");
			sql.append(" ORDER BY DATAREGISTRO DESC");
			sql.append(" LIMIT ");
			sql.append(filtro.getPrimeiroRegistro());
			sql.append(", ");
			sql.append(filtro.getQuantidadeRegistros());
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, p.getId());
			
			rs = ps.executeQuery();
			
			Endereco end = null;
			
			while (rs.next()) {
				end = recuperarEntidade(rs);
				lista.add(end);
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
	
	public Endereco recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Endereco end = null;
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE ID = ?");
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				end = recuperarEntidade(rs);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return end;
	}
	
	public void salvar(Endereco end) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		String sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (end.getId() != null) {
				sql = clausulaUpdate;
			} else {
				sql = clausulaInsert;
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (end.getId() != null) {
				ps.setString(count++, end.getEndereco().toUpperCase());
				ps.setLong(count++, end.getUsuarioRegistro().getId());
				ps.setLong(count++, end.getId());
				ps.executeUpdate();
				
			} else {
				ps.setString(count++, end.getEndereco().toUpperCase());
				ps.setDate(count++, new Date(new java.util.Date().getTime()));
				ps.setLong(count++, end.getProntuario().getId());
				ps.setLong(count++, end.getUsuarioRegistro().getId());
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
	
	/**
	 * Método que monta a entidade atraves do resultSet.
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Endereco recuperarEntidade(ResultSet rs) throws SQLException, BancoDadosException {
		
		Long id;
		Endereco entidade = new Endereco();
		entidade.setId(rs.getLong("ID"));
		entidade.setEndereco(rs.getString("ENDERECO"));
		entidade.setDataRegistro(rs.getDate("DATAREGISTRO"));
		
		id = new Long(rs.getLong("IDPRONTUARIO"));
		Prontuario prontuario = (Prontuario) ProntuarioServico.getInstance().recuperarPorId(id);
		entidade.setProntuario(prontuario);
		
		id = new Long(rs.getLong("IDUSUARIOREGISTRO"));
		Usuario usuario = (Usuario) UsuarioServico.getInstance().recuperarPorId(id);
		entidade.setUsuarioRegistro(usuario);

		return entidade;
	}
}
