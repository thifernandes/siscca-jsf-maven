package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Uf;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;

public class UfServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UfServico.class);
	private static UfServico instance;

	private UfServico() {
	}

	public static synchronized UfServico getInstance() {
		if (instance == null) {
			instance = new UfServico();
		}
		return instance;
	}
	
	public List<Uf> listar() throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Uf> listaUfs = new ArrayList<Uf>();
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM UF";
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();
			
			Uf uf = null;
			
			while (rs.next()) {
				uf = new Uf();
				uf.setSiglaUf(rs.getString("SIGLAUF"));
				uf.setNomeUf(rs.getString("NOMEUF"));
				listaUfs.add(uf);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaUfs;
	}
	
	public Uf recuperarPorSiglaUf(String siglaUf) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Uf uf = null;

		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM UF WHERE SIGLAUF = ?";

			ps = connection.prepareStatement(sql);
			ps.setString(1, siglaUf);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				uf = new Uf();
				uf.setSiglaUf(rs.getString("SIGLAUF"));
				uf.setNomeUf(rs.getString("NOMEUF"));
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return uf;
	}
}
