import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


class UsuarioRepositoryMock implements UsuarioRepository {
    List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void cadastrarUsuario(Usuario usuario){
        if(usuario != null)
            usuarios.add(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios(){
        return usuarios;
    }
    public Optional<Usuario> buscarUsuarioPorEmail(String email){
        for(Usuario usuario : usuarios){
            if(usuario.email().equals(email)){
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
    
}
