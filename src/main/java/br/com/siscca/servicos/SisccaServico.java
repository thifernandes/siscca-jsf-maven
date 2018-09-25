package br.com.siscca.servicos;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.siscca.excecoes.BancoDadosException;
import br.com.siscca.excecoes.ConexaoException;


public class SisccaServico implements Serializable {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 6801837922028409086L;
	private static Logger logger = Logger.getLogger(SisccaServico.class);

	public List<Object> findAll(Object T) throws BancoDadosException {
        
		List<Object> listaObjetos = null;
        Class cl = T.getClass();//AQUI EU SEI QUAL CLASSE É. COMO INSTANCIO ESSA CLASSSE.
        Field[] campos = cl.getDeclaredFields();
        Object[] obj = new Object[campos.length];
        
        try {
            listaObjetos = new ArrayList<Object>();
            Connection conn = ConexaoServico.getConexao();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + cl.getSimpleName() + "");
            
            while (rs.next()) {
            	
            	//obj = cl.newInstance();
            	
                for (int i = 0; i < campos.length; i++) {
                    obj[i] = rs.getObject(campos[i].getName());
                }
                listaObjetos.add(obj);
            }
        } catch (SQLException e) {
			logger.error(e);
			throw new BancoDadosException();
			
		} catch (ConexaoException e) {
			throw new BancoDadosException();
			
		}
        return listaObjetos;
    }
}