package br.com.siscca;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import br.com.siscca.entidades.Pessoa;
import br.com.siscca.entidades.Prontuario;
import br.com.siscca.entidades.Usuario;
import br.com.siscca.servicos.PessoaServico;
import br.com.siscca.servicos.ProntuarioServico;
import br.com.siscca.servicos.UsuarioServico;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class ImportacaoPlanilha {

	public static void main(String[] args) {

		// objeto relativo ao arquivo excel
		Workbook workbook = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {

			// Carrega planilha
			WorkbookSettings config = new WorkbookSettings();

			// configura acentuação
			config.setEncoding("Cp1252");

			// recupera arquivo desejado
			workbook = Workbook.getWorkbook(new File("D:/Thiago/Projetos/Dalmo/Sistemas/siscca/src/br/com/siscca/unacExcel2003.xls"), config);

			// recupera pagina/planilha/aba do arquivo
			Sheet sheet = workbook.getSheet(1);

			// recupera numero de linhas
			int linhas = sheet.getRows();
			
			Prontuario pront = null;
			Pessoa p = null;
			String celula = null;
			
			// percorre todas as linhas da planilha comecando da linha 5
			for (int row = 4; row < 5300; row++) {

				pront = new Prontuario();
				p = new Pessoa();
				
				// Coluna 1 (B) - Prontuario
				celula = sheet.getCell(1, row).getContents();
				
				if (celula != null && !celula.equals("")) {
					pront.setNumeroProntuario(celula);
				}
				
				// Coluna 2 (C) - Nome Crianca/Adolescente
				celula = sheet.getCell(2, row).getContents();
				
				if (celula != null && !celula.equals("")) {
					p.setNomePessoa(celula.toUpperCase());
				}
				
				// Coluna 3 (D) - Filiacao
				celula = sheet.getCell(3, row).getContents();
				
				if (celula != null && !celula.equals("")) {
					String nomePai = "";
					String nomeMae = "";
					
					// Se contem o ' e ' suponho que contenha nome de pai e mae.
					if (celula.contains(" e ")) {
						
						StringTokenizer st = new StringTokenizer(celula, "\\se\\s");
						if (st.hasMoreTokens()) {
							nomePai = st.nextToken();
							nomeMae = st.nextToken();
						}
					} 
					// Senao, so nome da mae.
					else {
						nomeMae = celula;
					}
					
					p.setNomeMae(nomeMae != "" ? nomeMae.toUpperCase() : nomeMae);
					p.setNomePai(nomePai != "" ? nomePai.toUpperCase() : nomePai);
				}
				
				// Coluna 4 (E) - Data Nascimento
				celula = sheet.getCell(4, row).getContents();
				
				if (celula != null && !celula.equals("")) {
					Date dataNascimento = null;
					
					try {
						
						dataNascimento = sdf.parse(celula);
						
					} catch (ParseException e) {
					}
					
					p.setDataNascimento(dataNascimento);
				}
				
				pront.setPessoa(p);
				
				if (pront.getPessoa().getNomePessoa() != null) {
					
					// Insere a pessoa
					Pessoa pessoa = PessoaServico.getInstance().salvar(p);
					pront.setPessoa(pessoa);
					
					// Recupera um usuario padrao de carga do banco de dados
					Usuario u = UsuarioServico.getInstance().validarLogin("BD", "12345678");
					pront.setUsuarioGerador(u);
					
					// Isere o prontuario
					ProntuarioServico.getInstance().salvar(pront);
					
					System.out.println("pessoa id: " + pessoa.getId());
					System.out.println("prontuario id: " + pront.getId());
					System.out.println("Inseridos com sucesso!");
					System.out.println("-----------------------------------------------");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// fechar
			if (workbook != null)
				workbook.close();
		}
	}
}
