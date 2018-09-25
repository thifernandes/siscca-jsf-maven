package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;
import br.com.siscca.utils.FiltroPesquisaDatatable;

public class ProntuarioServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProntuarioServico.class);
	private static ProntuarioServico instance;

	private ProntuarioServico() {
	}

	public static synchronized ProntuarioServico getInstance() {
		if (instance == null) {
			instance = new ProntuarioServico();
		}
		return instance;
	}
	
	public List<Prontuario> pesquisar(Prontuario prontuario, FiltroPesquisaDatatable filtro) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Prontuario> listaProntuarios = new ArrayList<Prontuario>();
		
		try {
			connection = ConexaoServico.getConexao();
			sql.append("SELECT DISTINCT(P.ID) IDPRONTUARIO, ");
			sql.append("P.NUMEROPRONTUARIO, ");
			sql.append("PESS.ID AS IDPESSOA, ");
			sql.append("PESS.NOMEPESSOA, ");
			sql.append("PESS.DATANASCIMENTO, ");
			sql.append("PESS.NOMEPAI, ");
			sql.append("PESS.NOMEMAE ");
			sql.append("FROM PRONTUARIO P ");
			sql.append("INNER JOIN PESSOA PESS ON PESS.ID = P.IDPESSOA ");
			sql.append("WHERE 1 = 1 ");
			
			if (prontuario.getId() != null) {
				sql.append(" AND P.ID = " + prontuario.getId());
			}
			
			if (prontuario.getPessoa().getNomePessoa() != "") {
				sql.append(" AND PESS.NOMEPESSOA LIKE '%" + prontuario.getPessoa().getNomePessoa() + "%'");
			}
			
			if (prontuario.getPessoa().getDataNascimento() != null) {
				sql.append(" AND PESS.DATANASCIMENTO = '" + sdf.format(prontuario.getPessoa().getDataNascimento()) + "'");
			}
			
			if (prontuario.getPessoa().getNomePai() != null && !prontuario.getPessoa().getNomePai().equals("")) {
				sql.append(" AND PESS.NOMEPAI LIKE '%" + prontuario.getPessoa().getNomePai() + "%'");
			}
			
			if (prontuario.getPessoa().getNomeMae() != null && !prontuario.getPessoa().getNomeMae().equals("")) {
				sql.append(" AND PESS.NOMEMAE LIKE '%" + prontuario.getPessoa().getNomeMae() + "%'");
			}
			
			if (prontuario.getNumeroProntuario() != null && !prontuario.getNumeroProntuario().equals("")) {
				sql.append(" AND P.NUMEROPRONTUARIO LIKE '%" + prontuario.getNumeroProntuario() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroOficio() != null && !prontuario.getPessoa().getNumeroOficio().equals("")) {
				sql.append(" AND PESS.NUMEROOFICIO LIKE '%" + prontuario.getPessoa().getNumeroOficio() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroAutos() != null && !prontuario.getPessoa().getNumeroAutos().equals("")) {
				sql.append(" AND PESS.NUMEROAUTOS LIKE '%" + prontuario.getPessoa().getNumeroAutos() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroGuiaAcolhimento() != null && !prontuario.getPessoa().getNumeroGuiaAcolhimento().equals("")) {
				sql.append(" AND PESS.NUMEROGUIAACOLHIMENTO LIKE '%" + prontuario.getPessoa().getNumeroGuiaAcolhimento() + "%'");
			}
			
			sql.append(" LIMIT ");
			sql.append(filtro.getPrimeiroRegistro());
			sql.append(", ");
			sql.append(filtro.getQuantidadeRegistros());
			
			ps = connection.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			Prontuario pront = null;
			Pessoa p = null;
			
			while (rs.next()) {
				
				p = new Pessoa();
				p.setId(rs.getLong("IDPESSOA"));
				p.setNomePessoa(rs.getString("NOMEPESSOA"));
				p.setDataNascimento(rs.getDate("DATANASCIMENTO"));
				p.setNomePai(rs.getString("NOMEPAI"));
				p.setNomeMae(rs.getString("NOMEMAE"));
				
				pront = new Prontuario();
				pront.setId(rs.getLong("IDPRONTUARIO"));
				pront.setNumeroProntuario(rs.getString("NUMEROPRONTUARIO"));
				pront.setPessoa(p);
				
				listaProntuarios.add(pront);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaProntuarios;
	}
	
	public int pesquisarQuantidadeRegistros(Prontuario prontuario) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int quantidade = 0;
		
		try {
			connection = ConexaoServico.getConexao();
			sql.append("SELECT COUNT(*) QUANTIDADE ");
			sql.append("FROM PRONTUARIO P ");
			sql.append("INNER JOIN PESSOA PESS ON PESS.ID = P.IDPESSOA ");
			sql.append("WHERE 1 = 1 ");
			
			if (prontuario.getId() != null) {
				sql.append(" AND P.ID = " + prontuario.getId());
			}
			
			if (prontuario.getPessoa().getNomePessoa() != null && !prontuario.getPessoa().getNomePessoa().equals("")) {
				sql.append(" AND PESS.NOMEPESSOA LIKE '%" + prontuario.getPessoa().getNomePessoa() + "%'");
			}
			
			if (prontuario.getPessoa().getDataNascimento() != null) {
				sql.append(" AND PESS.DATANASCIMENTO = '" + sdf.format(prontuario.getPessoa().getDataNascimento()) + "'");
			}
			
			if (prontuario.getPessoa().getNomePai() != null && !prontuario.getPessoa().getNomePai().equals("")) {
				sql.append(" AND PESS.NOMEPAI LIKE '%" + prontuario.getPessoa().getNomePai() + "%'");
			}
			
			if (prontuario.getPessoa().getNomeMae() != null && !prontuario.getPessoa().getNomeMae().equals("")) {
				sql.append(" AND PESS.NOMEMAE LIKE '%" + prontuario.getPessoa().getNomeMae() + "%'");
			}
			
			if (prontuario.getNumeroProntuario() != null && !prontuario.getNumeroProntuario().equals("")) {
				sql.append(" AND P.NUMEROPRONTUARIO LIKE '%" + prontuario.getNumeroProntuario() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroOficio() != null && !prontuario.getPessoa().getNumeroOficio().equals("")) {
				sql.append(" AND PESS.NUMEROOFICIO LIKE '%" + prontuario.getPessoa().getNumeroOficio() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroAutos() != null && !prontuario.getPessoa().getNumeroAutos().equals("")) {
				sql.append(" AND PESS.NUMEROAUTOS LIKE '%" + prontuario.getPessoa().getNumeroAutos() + "%'");
			}
			
			if (prontuario.getPessoa().getNumeroGuiaAcolhimento() != null && !prontuario.getPessoa().getNumeroGuiaAcolhimento().equals("")) {
				sql.append(" AND PESS.NUMEROGUIAACOLHIMENTO LIKE '%" + prontuario.getPessoa().getNumeroGuiaAcolhimento() + "%'");
			}
			
			ps = connection.prepareStatement(sql.toString());
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
	
	public List<Prontuario> recuperarPorIdPessoa(Long idPessoa) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Prontuario> listaProntuarios = new ArrayList<Prontuario>();
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT P.ID, P.DESCRICAO, P.DATAGERACAO FROM PRONTUARIO P " +
					"WHERE P.IDPESSOA = ? ";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, idPessoa);
			rs = ps.executeQuery();
			
			Prontuario pront = null;
			
			while (rs.next()) {
				pront = new Prontuario();
				pront.setId(rs.getLong("ID"));
				pront.setDataGeracao(rs.getDate("DATAGERACAO"));
								
				listaProntuarios.add(pront);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaProntuarios;
	}
	
	public Prontuario recuperarPorId(Long idProntuario) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Prontuario pront = null;
		Pessoa p = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM PRONTUARIO WHERE ID = ?";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, idProntuario);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				pront = new Prontuario();
				pront.setId(rs.getLong("ID"));
				pront.setDataGeracao(rs.getDate("DATAGERACAO"));
				pront.setNumeroProntuario(rs.getString("NUMEROPRONTUARIO"));
				
				Long idUsuario = rs.getLong("IDUSUARIOGERADOR");
				Usuario usuario = UsuarioServico.getInstance().recuperarPorId(idUsuario);
				pront.setUsuarioGerador(usuario);
				
				Long idPessoa = rs.getLong("IDPESSOA");
				p = PessoaServico.getInstance().recuperarPorId(idPessoa);
				pront.setPessoa(p);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return pront;
	}
	
	public Prontuario salvar(Prontuario p) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = null;
		Integer count = 1;
	
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (p.getId() != null) {
				sql = recuperarClausulaUpdate();
			} else {
				sql = recuperarClausulaInsert();
			}
			
			if (p.getId() != null) {
				ps = connection.prepareStatement(sql.toString());
				ps.setString(count++, p.getNumeroProntuario());
				ps.setLong(count++, p.getUsuarioGerador().getId());
				ps.setLong(count++, p.getId());
				ps.executeUpdate();
				
			} else {
				ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setLong(count++, p.getPessoa().getId());
				ps.setDate(count++, new Date(new java.util.Date().getTime()));
				ps.setLong(count++, p.getUsuarioGerador().getId());
				
				if (p.getNumeroProntuario() != null) {
					ps.setString(count++, p.getNumeroProntuario());
					
				} else {
					ps.setNull(count++, Types.BIGINT);
				}
				
				ps.execute();
				
				ResultSet rs = ps.getGeneratedKeys();
				Long id = null;
				
				if (rs.next()) {
					id = rs.getLong(1);
				}
				
				//p = recuperarPorId(id);
				p.setId(id);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return p;
	}
	
	private StringBuilder recuperarClausulaInsert() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRONTUARIO (");
		sql.append("ID, ");
		sql.append("IDPESSOA, ");
		sql.append("DATAGERACAO, ");
		sql.append("IDUSUARIOGERADOR, ");
		sql.append("NUMEROPRONTUARIO ");
		sql.append(") ");
		sql.append("VALUES (");
		sql.append("DEFAULT, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?) ");
		
		return sql;
	}
	
	private StringBuilder recuperarClausulaUpdate() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE PRONTUARIO SET ");
		sql.append("NUMEROPRONTUARIO = ?, ");
		sql.append("IDUSUARIOGERADOR = ? ");
		sql.append("WHERE ID = ? ");
		
		return sql;
	}
	
	public void excluirProntuario(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "DELETE FROM PRONTUARIO WHERE ID = ?";
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
}
