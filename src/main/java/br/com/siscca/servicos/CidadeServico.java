package br.com.siscca.servicos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Cidade;
import br.com.siscca.entidades.Uf;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;

public class CidadeServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(CidadeServico.class);
	
	private static CidadeServico instance;

	private CidadeServico() {
	}

	public static synchronized CidadeServico getInstance() {
		if (instance == null) {
			instance = new CidadeServico();
		}
		return instance;
	}
	
	public List<Cidade> recuperarPorSiglaUf(String siglaUf) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Cidade> listaCidades = new ArrayList<Cidade>();
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM CIDADE WHERE SIGLAUF = ?";

			ps = connection.prepareStatement(sql);
			ps.setString(1, siglaUf);
			rs = ps.executeQuery();
			
			Cidade cid = null;
			Uf uf = null;
			
			while (rs.next()) {
				cid = new Cidade();
				cid.setId(rs.getInt("ID"));
				cid.setNome(rs.getString("NOME"));
				
				uf = new Uf();
				uf.setSiglaUf(rs.getString("SIGLAUF"));
				cid.setUf(uf);
				
				listaCidades.add(cid);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaCidades;
	}
	
	public Cidade recuperarPorId(Integer id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Cidade cid = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM CIDADE WHERE ID = ?";
			
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				cid = new Cidade();
				cid.setId(rs.getInt("ID"));
				cid.setNome(rs.getString("NOME"));
				
				String siglaUf = rs.getString("SIGLAUF"); 
				Uf uf = UfServico.getInstance().recuperarPorSiglaUf(siglaUf);
				cid.setUf(uf);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return cid;
	}
}
