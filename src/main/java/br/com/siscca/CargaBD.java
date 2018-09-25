package br.com.siscca;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

public class CargaBD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		carregarDadosPessoa();
	}
	
	private static void carregarDadosPessoa() {
		
		Connection connectionAccess = null;
		PreparedStatement psAccess = null;
		ResultSet rs = null;
		
		Connection connectionMysql = null;
		PreparedStatement psMysql = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//1996-03-14 00:00:00
		
		try {
			// Conecta no access
			connectionAccess = getConexaoAccess();
			psAccess = connectionAccess.prepareStatement("select * from CadPrincipal where nome is not null order by prontuario");
			rs = psAccess.executeQuery();
			
			// Conecta no mysql
			connectionMysql = getConexaoMysql();
			StringBuilder builder = new StringBuilder();
			builder.append("insert into siscca.pessoa (idpessoa,");
			builder.append("nomepessoa, ");
			builder.append("datanascimento, ");
			builder.append("sexo, ");
			builder.append("iddeficiencia, ");
			builder.append("siglaufnaturalidade, ");
			builder.append("idcidadenaturalidade, ");
			builder.append("endereco, ");
			builder.append("siglaufendereco, ");
			builder.append("idcidadeendereco, ");
			builder.append("bairroendereco, ");
			builder.append("nomemae, ");
			builder.append("nomepai, ");
			builder.append("nomeresponsavel ");
			builder.append(") values (default,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?,");
			builder.append("?)");
			
			psMysql = connectionMysql.prepareStatement(builder.toString());
			
			while (rs.next()) {
				
				Integer count = 1;
				
				// Nome
				String nome = rs.getString("nome");
				if (nome != null) {
					psMysql.setString(count++, nome);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Data de nascimento
				String dataNasc = rs.getString("dtnascimento");
				if (dataNasc != null) {
					Date data = new Date(sdf.parse(dataNasc).getTime());
					psMysql.setDate(count++, data);
				} else {
					psMysql.setNull(count++, Types.DATE);
				}
				
				// Sexo
				String sexo = rs.getString("sexo");
				if (sexo != null) {
					psMysql.setString(count++, sexo);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Deficiencia
				Integer idDeficiencia = recuperarIdDeficiencia(connectionMysql, rs.getString("tipodef"));
				if (idDeficiencia != null) {
					psMysql.setInt(count++, idDeficiencia);
				} else {
					psMysql.setNull(count++, Types.INTEGER);
				}
				
				// Uf e Cidade Naturalidade
				count = setarIdUfIdCidadePorNomeCidade(connectionMysql, psMysql, count, rs.getString("naturalidade"));
				
				// Endereco
				String endereco = rs.getString("endresp");
				if (endereco != null) {
					psMysql.setString(count++, endereco);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Uf e Cidade Endereco
				count = setarIdUfIdCidadePorNomeCidade(connectionMysql, psMysql, count, rs.getString("cidaderesp"));
				
				// Bairro
				String bairro = rs.getString("bairroresp");
				if (bairro != null) {
					psMysql.setString(count++, bairro);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Nome mae
				String mae = rs.getString("mae");
				if (mae != null) {
					psMysql.setString(count++, mae);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Nome pai
				String pai = rs.getString("pai");
				if (pai != null) {
					psMysql.setString(count++, pai);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				// Responsavel
				String resp = rs.getString("resp");
				if (resp != null) {
					psMysql.setString(count++, resp);
				} else {
					psMysql.setNull(count++, Types.VARCHAR);
				}
				
				psMysql.execute();
				connectionMysql.commit();
				psMysql.clearParameters();
			}
			
		} catch (Exception e) {
			System.out.println(e);
			
			try {
				connectionMysql.rollback();
			} catch (SQLException e2) {
				System.out.println("Erro ao efetuar rollback. \n" + e2);
			}
			
		} finally {
			fecharConexao(connectionAccess);
			fecharConexao(connectionMysql);
		}
	}
	
	private static Integer recuperarIdDeficiencia(Connection conn, String nomeDeficiencia) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer idRetorno = null;
		
		try {
			
			ps = conn.prepareStatement("select iddeficiencia from deficiencia where nomedeficiencia = ?");
			ps.setString(1, nomeDeficiencia);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				idRetorno = rs.getInt("iddeficiencia");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return idRetorno;
	}
	
	private static Integer setarIdUfIdCidadePorNomeCidade(Connection conn, PreparedStatement psInsert, Integer countPs, String nomeCidade) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer idCidade = null;
		String siglaUf = null;
		
		try {
			
			ps = conn.prepareStatement("select idcidade, siglauf from cidade where nomecidade = ?");
			ps.setString(1, nomeCidade);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				
				siglaUf = rs.getString("siglaUf");
				idCidade = rs.getInt("idcidade");
				
				if (siglaUf != null) {
					psInsert.setString(countPs++, siglaUf);
				} else {
					psInsert.setNull(countPs++, Types.CHAR);
				}
				
				if (idCidade != null) {
					psInsert.setInt(countPs++, idCidade);
				} else {
					psInsert.setNull(countPs++, Types.INTEGER);
				}
				
			} else {
				psInsert.setNull(countPs++, Types.CHAR);
				psInsert.setNull(countPs++, Types.INTEGER);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return countPs;
	}
	
	private static Connection getConexaoAccess() {
		
		Connection connection = null;
		
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			connection = DriverManager.getConnection("jdbc:odbc:unac", "", "");
			
		} catch (ClassNotFoundException e) {
			System.out.println("erro: " + e);
			
		} catch (SQLException e) {
			System.out.println("erro: " + e);
		} catch (Exception e) {
			System.out.println("erro: " + e);
		}
		
		return connection;
	}
	
	private static Connection getConexaoMysql() {
		
		Connection connection = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/siscca", "root", "root");
			connection.setAutoCommit(false);
			
		} catch (ClassNotFoundException e) {
			System.out.println("erro: " + e);
			
		} catch (SQLException e) {
			System.out.println("erro: " + e);
		} catch (Exception e) {
			System.out.println("erro: " + e);
		}
		
		return connection;
	}
	
	private static void fecharConexao(Connection conn) {
		
		try {
			
			if (conn != null) {
				conn.close();
			}
			
		} catch (SQLException e) {
			System.out.println("erro: " + e);
		}
	}
}
