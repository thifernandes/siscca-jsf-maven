package br.com.siscca;

import javax.xml.bind.DatatypeConverter;

import br.com.siscca.utils.StringUtil;

public class TestCriptEncript {

	public static void main(String[] args) {
		
		try {

			String senhaHex = StringUtil.criptografarSenha("12345678");
			System.out.println(senhaHex);
			
			byte[] senhaByte = DatatypeConverter.parseHexBinary(senhaHex);
			String senha = new String(senhaByte);
			System.out.println(senha);
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
//	public static void main(String[] args) {
//		
//		try{
//            byte[] plainBytes = "12345678".getBytes();
//             
//            // Generate the key first
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(128);  // Key size
//            Key key = keyGen.generateKey();
//             
//            // Create Cipher instance and initialize it to encrytion mode
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            //cipher.init(Cipher.ENCRYPT_MODE, key);
//            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("siscca oiuioopoi".getBytes(), "AES"));
//            byte[] cipherBytes = cipher.doFinal(plainBytes);
//            
//            //String criptHexa = DatatypeConverter.printHexBinary(cipherBytes);
//            
//            StringBuilder criptHexa = new StringBuilder();
//    		
//    		for (byte b : cipherBytes) {
//    			criptHexa.append(String.format("%02X", 0xFF & b));
//    		}
//            
//    		System.out.println(criptHexa);
//            
//            // Reinitialize the Cipher to decryption mode
////            cipher.init(Cipher.DECRYPT_MODE,key, cipher.getParameters());
////            byte[] plainBytesDecrypted = cipher.doFinal(cipherBytes);
////             
////            System.out.println("DECRUPTED DATA : "+new String(plainBytesDecrypted));
//            
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//	}

}
