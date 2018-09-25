package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.excecoes.CriptografiaException;
import br.com.siscca.utils.StringUtil;

public class UsuarioServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UsuarioServico.class);
	private static UsuarioServico instance;

	private UsuarioServico() {
	}

	public static synchronized UsuarioServico getInstance() {
		if (instance == null) {
			instance = new UsuarioServico();
		}
		return instance;
	}
	
	public List<Usuario> pesquisar(String login, Integer tipoPerfil, String matricula, String nome, String status) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		List<Usuario> listaUsuarios = new ArrayList<Usuario>();
		
		try {
			connection = ConexaoServico.getConexao();
			sql.append("SELECT ID, LOGIN, MATRICULA, TIPOPERFIL, NOME, STATUS FROM USUARIO WHERE 1 = 1 ");
			
			if (login != null && !login.equals("")) {
				sql.append("AND LOGIN LIKE '%" + login + "%' ");
			}
			
			if (tipoPerfil != null) {
				sql.append("AND TIPOPERFIL = " + tipoPerfil);
			}
			
			if (matricula != null && !matricula.equals("")) {
				sql.append("AND MATRICULA LIKE '%" + matricula + "%' ");
			}
			
			if (nome != null && !nome.equals("")) {
				sql.append("AND NOME LIKE '%" + nome + "%' ");
			}
			
			if (status != null) {
				sql.append("AND STATUS = " + status);
			}
			
			ps = connection.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			Usuario u = null;
			
			while (rs.next()) {
				u = new Usuario();
				u.setId(rs.getLong("ID"));
				u.setLogin(rs.getString("LOGIN"));
				u.setTipoPerfil(rs.getInt("TIPOPERFIL"));
				u.setMatricula(rs.getString("MATRICULA"));
				u.setNome(rs.getString("NOME"));
				u.setStatus(rs.getString("STATUS"));
				
				listaUsuarios.add(u);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaUsuarios;
	}

	public Usuario validarLogin(String login, String senha) throws CriptografiaException, BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Usuario usuarioLogado = null;
		
		try {
			connection = ConexaoServico.getConexao();
			ps = connection.prepareStatement("SELECT ID, LOGIN, TIPOPERFIL, MATRICULA, NOME, STATUS "
					+ "FROM USUARIO WHERE LOGIN = ? AND SENHA = ?");
			ps.setString(1, login);
			ps.setString(2, StringUtil.criptografarSenha(senha));
			rs = ps.executeQuery();
			
			if (rs.next()) {
				usuarioLogado = new Usuario();
				usuarioLogado.setId(rs.getLong("ID"));
				usuarioLogado.setLogin(rs.getString("LOGIN"));
				usuarioLogado.setTipoPerfil(rs.getInt("TIPOPERFIL"));
				usuarioLogado.setMatricula(rs.getString("MATRICULA"));
				usuarioLogado.setNome(rs.getString("NOME"));
				usuarioLogado.setStatus(rs.getString("STATUS"));
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (CriptografiaException e) {
			logger.error(e);
			throw new CriptografiaException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return usuarioLogado;
	}
	
	public List<Usuario> listar() throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Usuario> listaUsuarios = new ArrayList<Usuario>();
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT ID, LOGIN, TIPOPERFIL, MATRICULA, NOME, STATUS FROM USUARIO";
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();
			
			Usuario usr = null;
			
			while (rs.next()) {
				usr = new Usuario();
				usr.setId(rs.getLong("ID"));
				usr.setLogin(rs.getString("LOGIN"));
				usr.setTipoPerfil(rs.getInt("TIPOPERFIL"));
				usr.setMatricula(rs.getString("MATRICULA"));
				usr.setNome(rs.getString("NOME"));
				usr.setStatus(rs.getString("STATUS"));
				
				listaUsuarios.add(usr);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaUsuarios;
	}
	
	public void excluir(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "DELETE FROM USUARIO WHERE ID = ?";
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
	
	public Usuario recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Usuario usuario = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT ID, LOGIN, TIPOPERFIL, MATRICULA, NOME, STATUS FROM USUARIO WHERE ID = ?";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				usuario = new Usuario();
				usuario.setId(rs.getLong("ID"));
				usuario.setLogin(rs.getString("LOGIN"));
				usuario.setTipoPerfil(rs.getInt("TIPOPERFIL"));
				usuario.setMatricula(rs.getString("MATRICULA"));
				usuario.setNome(rs.getString("NOME"));
				usuario.setStatus(rs.getString("STATUS"));
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return usuario;
	}
	
	public String recuperarSenha(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String senha = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT SENHA FROM USUARIO WHERE ID = ?";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				senha = rs.getString("SENHA");
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return senha;
	}
	
	public Boolean verificarLoginExistente(String login) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Boolean loginExistente = false;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT LOGIN FROM USUARIO WHERE LOGIN = ?";
			ps = connection.prepareStatement(sql);
			ps.setString(1, login);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				loginExistente = true;
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return loginExistente;
	}
	
	public void salvar(Usuario usuario) throws CriptografiaException, BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = null;
		Integer count = 1;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (usuario.getId() != null) {
				sql = recuperarClausulaUpdate();
			} else {
				sql = recuperarClausulaInsert();
			}
			
			ps = connection.prepareStatement(sql.toString());
			
			if (usuario.getId() != null) {
				ps.setString(count++, StringUtil.criptografarSenha(usuario.getSenha()));
				ps.setInt(count++, usuario.getTipoPerfil());
				ps.setString(count++, usuario.getMatricula());
				ps.setString(count++, usuario.getNome().toUpperCase());
				ps.setString(count++, usuario.getStatus());
				ps.setLong(count++, usuario.getId());
				ps.executeUpdate();
				
			} else {
				ps.setString(count++, usuario.getLogin().toUpperCase());
				ps.setString(count++, StringUtil.criptografarSenha(usuario.getSenha()));
				ps.setInt(count++, usuario.getTipoPerfil());
				ps.setString(count++, usuario.getMatricula());
				ps.setString(count++, usuario.getNome().toUpperCase());
				ps.setString(count++, usuario.getStatus());
				
				ps.execute();
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (CriptografiaException e) {
			logger.error(e);
			throw new CriptografiaException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
	}
	
	private StringBuilder recuperarClausulaInsert() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO USUARIO (");
		sql.append("ID, ");
		sql.append("LOGIN, ");
		sql.append("SENHA, ");
		sql.append("TIPOPERFIL, ");
		sql.append("MATRICULA, ");
		sql.append("NOME, ");
		sql.append("STATUS ");
		sql.append(") ");
		sql.append("VALUES (");
		sql.append("DEFAULT, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?) ");
		
		return sql;
	}
	
	private StringBuilder recuperarClausulaUpdate() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE USUARIO SET ");
		sql.append("SENHA = ?, ");
		sql.append("TIPOPERFIL = ?, ");
		sql.append("MATRICULA = ?, ");
		sql.append("NOME = ?, ");
		sql.append("STATUS = ? ");
		sql.append("WHERE ID = ? ");
		
		return sql;
	}
	
	public void atualizarSenha(Usuario usuario) throws CriptografiaException, BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "UPDATE USUARIO SET SENHA = ? WHERE ID = ?";
			
			ps = connection.prepareStatement(sql);
			ps.setString(1, StringUtil.criptografarSenha(usuario.getSenha()));
			ps.setLong(2, usuario.getId());
			ps.executeUpdate();
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (CriptografiaException e) {
			logger.error(e);
			throw new CriptografiaException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
	}
}
