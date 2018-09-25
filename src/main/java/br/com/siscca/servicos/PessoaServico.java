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

import br.com.siscca.entidades.Cidade;
import br.com.siscca.entidades.EntidadeCrud;
import br.com.siscca.entidades.Pessoa;
import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;

public class PessoaServico extends SisccaServico implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PessoaServico.class);
	private static PessoaServico instance;

	private PessoaServico() {
	}

	public static synchronized PessoaServico getInstance() {
		if (instance == null) {
			instance = new PessoaServico();
		}
		return instance;
	}

	public List<Pessoa> pesquisar(Pessoa pessoa) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Pessoa> listaPessoas = new ArrayList<Pessoa>();
		
		try {
			connection = ConexaoServico.getConexao();
			sql.append("SELECT DISTINCT(P.ID), ");
			sql.append("P.NOMEPESSOA, ");
			sql.append("P.DATANASCIMENTO, ");
			sql.append("P.SEXO, ");
			sql.append("P.ENDERECO ");
			sql.append("FROM PESSOA P ");
			sql.append("WHERE 1 = 1 ");
			
			if (pessoa.getNomePessoa() != "") {
				sql.append("AND P.NOMEPESSOA LIKE '%" + pessoa.getNomePessoa() + "%'");
			}
			
			if (pessoa.getDataNascimento() != null) {
				sql.append("AND P.DATANASCIMENTO = '" + sdf.format(pessoa.getDataNascimento()) + "'");
			}
			
			ps = connection.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			Pessoa p = null;
			
			while (rs.next()) {
				p = new Pessoa();
				p.setId(rs.getLong("ID"));
				p.setNomePessoa(rs.getString("NOMEPESSOA"));
				p.setDataNascimento(rs.getDate("DATANASCIMENTO"));
				p.setSexo(rs.getString("SEXO"));
				p.setEndereco(rs.getString("ENDERECO"));
				
				listaPessoas.add(p);
			}
			
		} catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		} finally {
			ConexaoServico.fecharConexao(connection);
		}
		
		return listaPessoas;
	}
	
	public Pessoa recuperarPorId(Long id) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Pessoa p = null;
		
		try {
			connection = ConexaoServico.getConexao();
			String sql = "SELECT * FROM PESSOA P WHERE P.ID = ?";
			
			ps = connection.prepareStatement(sql);
			ps.setLong(1, id);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				p = new Pessoa();
				p.setId(rs.getLong("ID"));
				p.setNomePessoa(rs.getString("NOMEPESSOA"));
				p.setSexo(rs.getString("SEXO"));
				p.setNomeMae(rs.getString("NOMEMAE"));
				p.setNomePai(rs.getString("NOMEPAI"));
				p.setFamiliaExtensa(rs.getString("FAMILIAEXTENSA"));
				p.setDataNascimento(rs.getDate("DATANASCIMENTO"));
				p.setCertidaoNascimento(rs.getString("CERTIDAONASCIMENTO"));
				p.setRg(rs.getString("RG"));
				p.setCpf(rs.getString("CPF"));
				
				/* Cidade naturalidade */
				Integer idCidadeNaturalidade = rs.getInt("IDCIDADENATURALIDADE");
				Cidade cidadeNaturalidade = CidadeServico.getInstance().recuperarPorId(idCidadeNaturalidade);
				p.setCidadeNaturalidade(cidadeNaturalidade);
				
				/* Grau de instrucao */
				Long idGrauInstrucao = rs.getLong("IDGRAUINSTRUCAO");
				EntidadeCrud grau = GrauInstrucaoServico.getInstance().findById(idGrauInstrucao);
				p.setGrauInstrucao(grau);
				
				p.setNomeEscola(rs.getString("NOMEESCOLA"));
				p.setRg(rs.getString("RG"));
				p.setCertidaoNascimento(rs.getString("CERTIDAONASCIMENTO"));
				p.setEndereco(rs.getString("ENDERECO"));
				
				/* Cidade endereco */
				Integer idCidadeEndereco = rs.getInt("IDCIDADEENDERECO");
				Cidade cidadeEndereco = CidadeServico.getInstance().recuperarPorId(idCidadeEndereco);
				p.setCidadeEndereco(cidadeEndereco);
				
				/* Rede de referencia */
				Long idRedeReferencia = rs.getLong("IDREDEREFERENCIA"); 
				EntidadeCrud rede = RedeReferenciaServico.getInstance().findById(idRedeReferencia);
				p.setRedeReferencia(rede);
				
				Long idDeficiencia = rs.getLong("IDDEFICIENCIA");
				
				if (idDeficiencia != null) {
					EntidadeCrud deficiencia = DeficienciaServico.getInstance().findById(idDeficiencia);
					p.setDeficiencia(deficiencia);
				}
				
				p.setNumeroOficio(rs.getString("NUMEROOFICIO"));
				p.setNumeroAutos(rs.getString("NUMEROAUTOS"));
				p.setNumeroGuiaAcolhimento(rs.getString("NUMEROGUIAACOLHIMENTO"));
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
	
	public Pessoa salvar(Pessoa p) throws BancoDadosException {
		
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = null;
		Integer count = 1;
		boolean inserirPessoa = p.getId() == null;
				
		try {
			
			connection = ConexaoServico.getConexao();
			
			if (inserirPessoa) {
				sql = recuperarClausulaInsert();
				ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				
			} else {
				sql = recuperarClausulaUpdate();
				ps = connection.prepareStatement(sql.toString());
			}
			
			ps.setString(count++, p.getNomePessoa().toUpperCase());
			ps.setString(count++, p.getSexo() != null ? p.getSexo().toUpperCase() : p.getSexo());
			ps.setString(count++, p.getNomeMae() != null ? p.getNomeMae().toUpperCase() : p.getNomeMae());
			ps.setString(count++, p.getNomePai() != null ? p.getNomePai().toUpperCase() : p.getNomePai());
			ps.setString(count++, p.getFamiliaExtensa() != null ? p.getFamiliaExtensa().toUpperCase() : p.getFamiliaExtensa());
			
			if (p.getDataNascimento() != null) {
				ps.setDate(count++, new Date(p.getDataNascimento().getTime()));
			} else {
				ps.setNull(count++, Types.DATE);
			}
			
			if (p.getCidadeNaturalidade() != null && p.getCidadeNaturalidade().getId() != null) {
				ps.setInt(count++, p.getCidadeNaturalidade().getId());
			} else {
				ps.setNull(count++, Types.INTEGER);
			}

			if (p.getGrauInstrucao() != null) {
				ps.setLong(count++, p.getGrauInstrucao().getId());
			} else {
				ps.setNull(count++, Types.BIGINT);
			}
			
			ps.setString(count++, p.getNomeEscola() != null ? p.getNomeEscola().toUpperCase() : p.getNomeEscola());
			ps.setString(count++, p.getCertidaoNascimento() != null ? p.getCertidaoNascimento().toUpperCase() : p.getCertidaoNascimento());
			ps.setString(count++, p.getRg() != null ? p.getRg().toUpperCase() : p.getRg());
			ps.setString(count++, p.getCpf() != null ? p.getCpf().toUpperCase() : p.getCpf());
			ps.setString(count++, p.getEndereco() != null ? p.getEndereco().toUpperCase() : p.getEndereco());
			
			if (p.getCidadeEndereco() != null && p.getCidadeEndereco().getId() != null) {
				ps.setInt(count++, p.getCidadeEndereco().getId());
			} else {
				ps.setNull(count++, Types.INTEGER);
			}
			
			if (p.getRedeReferencia() != null) {
				ps.setLong(count++, p.getRedeReferencia().getId());
			} else {
				ps.setNull(count++, Types.BIGINT);
			}
			
			if (p.getDeficiencia() != null) {
				ps.setLong(count++, p.getDeficiencia().getId());
			} else {
				ps.setNull(count++, Types.BIGINT);
			}
			
			ps.setString(count++, p.getNumeroOficio());
			ps.setString(count++, p.getNumeroAutos());
			ps.setString(count++, p.getNumeroGuiaAcolhimento());
			
			if (inserirPessoa) {
				ps.execute();
				
				ResultSet rs = ps.getGeneratedKeys();
				Long id = null;
				
				if (rs.next()) {
					id = rs.getLong(1);
				}
				
				//p = recuperarPorId(id);
				p.setId(id);
				
			} else {
				ps.setLong(count++, p.getId());
				ps.executeUpdate();
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
		sql.append("INSERT INTO PESSOA (");
		sql.append("ID, ");
		sql.append("NOMEPESSOA, ");
		sql.append("SEXO, ");
		sql.append("NOMEMAE, ");
		sql.append("NOMEPAI, ");
		sql.append("FAMILIAEXTENSA, ");
		sql.append("DATANASCIMENTO, ");
		sql.append("IDCIDADENATURALIDADE, ");
		sql.append("IDGRAUINSTRUCAO, ");
		sql.append("NOMEESCOLA, ");
		sql.append("CERTIDAONASCIMENTO, ");
		sql.append("RG, ");
		sql.append("CPF, ");
		sql.append("ENDERECO, ");
		sql.append("IDCIDADEENDERECO, ");
		sql.append("IDREDEREFERENCIA, ");
		sql.append("IDDEFICIENCIA, ");
		sql.append("NUMEROOFICIO, ");
		sql.append("NUMEROAUTOS, ");
		sql.append("NUMEROGUIAACOLHIMENTO ");
		sql.append(") ");
		sql.append("VALUES (");
		sql.append("DEFAULT, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
		sql.append("?, ");
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
		sql.append("UPDATE PESSOA SET ");
		sql.append("NOMEPESSOA = ?, ");
		sql.append("SEXO = ?, ");
		sql.append("NOMEMAE = ?, ");
		sql.append("NOMEPAI = ?, ");
		sql.append("FAMILIAEXTENSA = ?, ");
		sql.append("DATANASCIMENTO = ?, ");
		sql.append("IDCIDADENATURALIDADE = ?, ");
		sql.append("IDGRAUINSTRUCAO = ?, ");
		sql.append("NOMEESCOLA = ?, ");
		sql.append("CERTIDAONASCIMENTO = ?, ");
		sql.append("RG = ?, ");
		sql.append("CPF = ?, ");
		sql.append("ENDERECO = ?, ");
		sql.append("IDCIDADEENDERECO = ?, ");
		sql.append("IDREDEREFERENCIA = ?, ");
		sql.append("IDDEFICIENCIA = ?, ");
		sql.append("NUMEROOFICIO = ?, ");
		sql.append("NUMEROAUTOS = ?, ");
		sql.append("NUMEROGUIAACOLHIMENTO = ? ");
		sql.append("WHERE ID = ? ");

		return sql;
	}
}
