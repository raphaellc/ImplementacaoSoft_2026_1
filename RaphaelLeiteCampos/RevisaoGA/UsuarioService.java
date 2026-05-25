import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository usuRepo;
    
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuRepo = usuarioRepository;
    }

    public void cadastrarUsuario(Usuario usuario){
        usuRepo.cadastrarUsuario(usuario);
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String email){
        return usuRepo.buscarUsuarioPorEmail(email);       
    }
        

}
