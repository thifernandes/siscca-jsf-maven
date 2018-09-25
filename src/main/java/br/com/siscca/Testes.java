package br.com.siscca;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Testes {

	public static void main(String[] args) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		try {
			date = sdf.parse("19979-02-21");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(date);
	}

}
