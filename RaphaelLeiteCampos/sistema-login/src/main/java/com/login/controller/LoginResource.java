package com.login.controller;

import com.login.model.Usuario;
import com.login.model.UsuarioRepository;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/login")
public class LoginResource {
    private final UsuarioRepository repository;

    public LoginResource() throws SQLException {
        this.repository = new UsuarioRepository();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticar(Usuario usuario) throws SQLException {
        if (repository.validar(usuario.login(), usuario.senha())) {
            return Response.ok("{\"status\": \"Sucesso\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity("{\"status\": \"Falha\"}").build();
    }
}