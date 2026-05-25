
class Main{    
    public static void main(String[] args) {
        UsuarioRepository usuarioRepository = new UsuarioRepositoryMock();
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        UsuarioController usuarioController = new UsuarioController(usuarioService);        
        System.out.println("Hello, World!");
    }
}