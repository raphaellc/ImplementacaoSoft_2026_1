import java.util.Optional;

public class UsuarioController {
    UsuarioService usuario_service;
    public UsuarioController(UsuarioService usuario_service){
        this.usuario_service = usuario_service;
    }

    public void cadastrarUsuario(Usuario usuario){
        usuario_service.cadastrarUsuario(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        Optional<Usuario> usuario = usuario_service.buscarUsuarioPorEmail(email);
        if(usuario.isPresent()){
            return usuario.get();
        }else{
            return null;
        }
    }
}
