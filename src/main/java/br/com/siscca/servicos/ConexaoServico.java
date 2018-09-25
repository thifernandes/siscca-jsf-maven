package br.com.siscca.servicos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import br.com.siscca.excecoes.ConexaoException;

public class ConexaoServico {
	
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION_BD = "jdbc:mysql://localhost/SISCCA";
	
	private static final String USER = "SISCCA";
	private static final String PASS = "sisCCARoot2017";
	
	private static Logger logger = Logger.getLogger(ConexaoServico.class);
	
	
	public static Connection getConexao() throws ConexaoException {
		
		Connection connection = null;
		
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(CONNECTION_BD, USER, PASS);
			
		} catch (ClassNotFoundException e) {
			logger.error("Drive de conexao com o banco de dados não encontrado.", e);
			throw new ConexaoException();
			
		} catch (CommunicationsException e) {
			logger.error("Não foi possível estabelecer conexao com o banco de dados.", e);
			throw new ConexaoException();
			
		} catch (Exception e) {
			logger.error(e);
			throw new ConexaoException();
		}
		
		return connection;
	}
	
	public static void fecharConexao(Connection conn) {
		
		try {
			
			if (conn != null) {
				conn.close();
			}
			
		} catch (SQLException e) {
			logger.error(e);
		}
	}
}
