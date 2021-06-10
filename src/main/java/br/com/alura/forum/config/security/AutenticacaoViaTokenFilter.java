package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter{
	
		private TokenService tokenService;
		private UsuarioRepository repository;
		//private UsuarioRepository br.com.alura.forum.repository;

		public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository repository) {
			this.tokenService = tokenService;
			this.repository = repository;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
				throws ServletException, IOException {
			
			String token = recuperarToken(request);
			
			boolean valido = tokenService.isTokenValido(token);
			if(valido) {
				autenticarCliente(token);
			}
			
			filterChain.doFilter(request, response);
		}

		private void autenticarCliente(String token) {
			Long IdUsuario = tokenService.getIdUsuario(token);
			Usuario usuario = repository.findById(IdUsuario).get();
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null,usuario.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}

		private String recuperarToken (HttpServletRequest request)	{
			String token = request.getHeader("Authorization");
			if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
				return null;
			}
			return token.substring(7, token.length());
		}
}
