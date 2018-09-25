package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Contato;
import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.utils.FiltroPesquisaDatatable;

public class ContatoServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ContatoServico.class);
	private static ContatoServico instance;
	
	private static String nomeTabela = "CONTATO";
	private static String clausulaInsert = "INSERT INTO " + nomeTabela + " (ID, TELEFONE, NOMECONTATO, OBSERVACOES, IDPESSOA, IDUSUARIOREGISTRO) "
			+ "VALUES (DEFAULT, ?, ?, ?, ?, ?)";
	private static String clausulaUpdate = "UPDATE " + nomeTabela + " SET TELEFONE = ?, NOMECONTATO = ?, OBSERVACOES = ?, IDUSUARIOREGISTRO = ? WHERE ID = ?";
	private static String clausulaDelete = "DELETE FROM " + nomeTabela + " WHERE ID = ?";
	private static String clausulaSelect = "SELECT * FROM " + nomeTabela;
	private static String clausulaCount = "SELECT COUNT(*) QUANTIDADE FROM " + nomeTabela + " WHERE IDPESSOA = ?";

	private ContatoServico() {
	}

	public static synchronized ContatoServico getInstance() {
		if (instance == null) {
			instance = new ContatoServico();
		}
		return instance;
	}
	
	public List<Contato> pesquisar(Pessoa p, FiltroPesquisaDatatable filtro) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Contato> lista = new ArrayList<Contato>();
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE IDPESSOA = ?");
			sql.append(" ORDER BY ID DESC");
			sql.append(" LIMIT ");
			sql.append(filtro.getPrimeiroRegistro());
			sql.append(", ");
			sql.append(filtro.getQuantidadeRegistros());
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, p.getId());
			
			rs = ps.executeQuery();
			
			Contato cont = null;
			
			while (rs.next()) {
				cont = recuperarEntidade(rs);
				lista.add(cont);
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
	
	public int pesquisarQuantidadeRegistros(Pessoa p) throws BancoDadosException {
		
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
	
	public Contato recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Contato cont = null;
		
		try {
			connection = ConexaoServico.getConexao();
			StringBuilder sql = new StringBuilder(clausulaSelect);
			sql.append(" WHERE ID = ?");
			
			ps = connection.prepareStatement(sql.toString());
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				cont = recuperarEntidade(rs);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return cont;
	}
	
	public void salvar(Contato cont) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		String sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (cont.getId() != null) {
				sql = clausulaUpdate;
			} else {
				sql = clausulaInsert;
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (cont.getId() != null) {
				ps.setString(count++, cont.getTelefone());
				ps.setString(count++, cont.getNomeContato().toUpperCase());
				ps.setString(count++, cont.getObservacoes().toUpperCase());
				ps.setLong(count++, cont.getUsuarioRegistro().getId());
				ps.setLong(count++, cont.getId());
				ps.executeUpdate();
				
			} else {
				ps.setString(count++, cont.getTelefone());
				ps.setString(count++, cont.getNomeContato().toUpperCase());
				ps.setString(count++, cont.getObservacoes().toUpperCase());
				ps.setLong(count++, cont.getPessoa().getId());
				ps.setLong(count++, cont.getUsuarioRegistro().getId());
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
	private Contato recuperarEntidade(ResultSet rs) throws SQLException, BancoDadosException {
		
		Contato entidade = new Contato();
		entidade.setId(rs.getLong("ID"));
		entidade.setTelefone(rs.getString("TELEFONE"));
		entidade.setNomeContato(rs.getString("NOMECONTATO"));
		entidade.setObservacoes(rs.getString("OBSERVACOES"));
		
		Long id;
		id = new Long(rs.getLong("IDPESSOA"));
		Pessoa p = PessoaServico.getInstance().recuperarPorId(id);
		entidade.setPessoa(p);
		
		id = new Long(rs.getLong("IDUSUARIOREGISTRO"));
		Usuario usuario = (Usuario) UsuarioServico.getInstance().recuperarPorId(id);
		entidade.setUsuarioRegistro(usuario);
		
		return entidade;
	}
}
