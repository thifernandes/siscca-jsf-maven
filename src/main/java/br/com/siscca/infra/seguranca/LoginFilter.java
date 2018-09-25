package br.com.siscca.infra.seguranca;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import br.com.siscca.entidades.Usuario;

public class LoginFilter implements Filter {
	
	private static Logger logger = Logger.getLogger(LoginFilter.class);
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		Usuario usuario = null;
		
		try {
			
			HttpSession sess = ((HttpServletRequest) request).getSession(false);
			
			if (sess != null){
				usuario = (Usuario) sess.getAttribute("usuarioLogado");
			}
			
			// Usuario expirou, redireciona para a pagina de login
			if (usuario == null) {
				String contextPath = ((HttpServletRequest) request).getContextPath();
				HttpServletResponse resp = (HttpServletResponse) response;
				
				if (isAjax((HttpServletRequest) request)) {
					// Redirecionamento temporário (Solicita redirecionamento).
					resp.sendError(307);
				} else {
					// Se nao for requisicao ajax, redireciona direto.
					resp.sendRedirect(contextPath + "/index.xhtml");
				}
				
			} else {
				chain.doFilter(request, response);
			}
			
		} catch (Exception e) {
			logger.error("Erro ao tentar validar usuário logado.", e);
		}
	}
	
	public static boolean isAjax(HttpServletRequest request) {
		boolean isAjax = request != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With")); 
	    return isAjax;
	}

}
