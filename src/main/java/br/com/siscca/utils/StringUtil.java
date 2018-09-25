package br.com.siscca.utils;

import java.security.NoSuchAlgorithmException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;

import br.com.siscca.excecoes.CriptografiaException;

public class StringUtil {

//	private static final String ALGORITMO_MODO_PREENCHIMENTO = "AES/CBC/PKCS5Padding";
//	private static final String ALGORITMO_CRIPTOGRAFIA = "AES";
//	private static final String TIPO_ALGORITMO_CRIPTOGRAFIA = "SHA-256";
//	private static final String TIPO_CODIFICACAO = "UTF-8";

	public static Boolean isNotNull(String valor) {
		if (valor != null && !valor.equals("")) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Metodo que converte a senha para um valor hexadecimal.
	 * 
	 * @param senha
	 * @return
	 * @throws CriptografiaException
	 */
	public static String criptografarSenha(String senha) throws CriptografiaException {
		String senhaHex = DatatypeConverter.printHexBinary(senha.getBytes());
		return senhaHex;
	}
	
	/**
	 * Metodo que converte a senha hexadecimal em binario.
	 * 
	 * @param senhaHex
	 * @return
	 * @throws CriptografiaException
	 */
	public static String decriptografarSenha(String senhaHex) throws CriptografiaException {
		byte[] senhaByte = DatatypeConverter.parseHexBinary(senhaHex);
		String senha = new String(senhaByte);
		
		return senha;
	}
	
	/**
	 * Método que faz a criptografia unidirecional da senha.
	 */
//	public static String criptografarSenha(String senha) throws CriptografiaException {
//		
//		String senhaHex = "";
//		
//		try {
//			
//			MessageDigest algorithm = MessageDigest.getInstance(TIPO_ALGORITMO_CRIPTOGRAFIA);
//			byte messageDigest[] = algorithm.digest(senha.getBytes(TIPO_CODIFICACAO));
//			
//			StringBuilder hexString = new StringBuilder();
//			for (byte b : messageDigest) {
//			  hexString.append(String.format("%02X", 0xFF & b));
//			}
//			
//			senhaHex = hexString.toString();
//			
//		} catch (Exception e) {
//			throw new CriptografiaException(e);
//		}
//		
//		return senhaHex;
//	}
	
	/**
	 * Método que faz a criptografia da senha utilizando a classe Cipher do Java.
	 */
//	public static String criptografarSenha(String senha) throws CriptografiaException {
//		
//		String senhaCripto = "";
//		
//		try {
//			
//			byte[] senhaByte = senha.getBytes();
//			
//			Key key = instanciarChaveCriptografia();
//
//			Cipher cipher = Cipher.getInstance(ALGORITMO_MODO_PREENCHIMENTO);
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            
//            // Criptografa a senha em byte
//            byte[] cipherBytes = cipher.doFinal(senhaByte);
//            
//            // Converte a senha criptografada em bytes para hexadecimal
//            senhaCripto = DatatypeConverter.printHexBinary(cipherBytes);
////            senhaCripto = cipherBytes.toString();
//            
//		} catch (Exception e) {
//			throw new CriptografiaException(e);
//		}
//		
//		return senhaCripto;
//	}
	
	/**
	 * Método que faz a decriptografia da senha utilizando a classe Cipher do Java.
	 * Porém não atende, pois ele gera sempre um valor hash diferente e isso quebra a lógica 
	 * de comparacao de senha utilizada para logar na aplicacao.
	 */
//	public static String decriptografarSenha(String senhaCripto) throws CriptografiaException {
//		
//		String senha = "";
//		
//		try {
//			
//			//byte[] senhaCriptografadaByte = DatatypeConverter.parseHexBinary(senhaCripto);
//			byte[] senhaCriptoByte = senhaCripto.getBytes();
//            
//            Key key = instanciarChaveCriptografia();
//             
//            Cipher cipher = Cipher.getInstance(ALGORITMO_MODO_PREENCHIMENTO);
//            cipher.init(Cipher.DECRYPT_MODE, key, cipher.getParameters());
//            
//            // Decriptografa a senha em bytes
//            byte[] senhaByte = cipher.doFinal(senhaCriptoByte);
//            
//            // Converte os bytes para String
//            senha = DatatypeConverter.printHexBinary(senhaByte);
//			
//		} catch (Exception e) {
//			throw new CriptografiaException(e);
//		}
//		
//		return senha;
//	}
	
	/**
	 * Método que instancia Key para servir como chave de geracao de criptografia.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
//	private static Key instanciarChaveCriptografia() throws NoSuchAlgorithmException {
//
//		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITMO_CRIPTOGRAFIA);
//        keyGen.init(128);
//        Key key = keyGen.generateKey();
//        
//        return key;
//	}
	
	public static Boolean validarSenhaConfirmacaoSenha(String senha, String confirmacaoSenha) {
		
		Boolean senhaValida = true;
		String msgErro = "";
		
		if (senha.length() < 8) {
			senhaValida = false;
			msgErro = "A senha deve conter no mínimo 8 dígitos.";
			
		} else if (!senha.equals(confirmacaoSenha)) {
			senhaValida = false;
			msgErro = "A senha e a Confirmação de Senha não conferem.";
		}
		
		if (!senhaValida) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_WARN, msgErro, null));
		}
		
		return senhaValida;
	}
	
	public static Boolean validarNovaSenhaConfirmacaoNovaSenha(String novaSenha, String confirmacaoNovaSenha, String senhaAtual, 
			String senhaAtualBd) throws CriptografiaException {
		
		Boolean senhaValida = true;
		String msgErro = "";
	
		if (novaSenha.length() < 8) {
			senhaValida = false;
			msgErro = "A Nova Senha deve conter no mínimo 8 dígitos.";
			
		} else if (!novaSenha.equalsIgnoreCase(confirmacaoNovaSenha)) {
			senhaValida = false;
			msgErro = "Nova Senha e Confirmação de Nova Senha não conferem.";
			
		} else if (!senhaAtualBd.equalsIgnoreCase(criptografarSenha(senhaAtual))) {
			senhaValida = false;
			msgErro = "Senha Atual não confere.";
		}
		
		if (!senhaValida) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_WARN, msgErro, null));
		}
		
		return senhaValida;
	}
}
