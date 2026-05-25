import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    public void cadastrarUsuario(Usuario usuario);
    public List<Usuario> listarUsuarios();
    public Optional<Usuario> buscarUsuarioPorEmail(String email);
} 
