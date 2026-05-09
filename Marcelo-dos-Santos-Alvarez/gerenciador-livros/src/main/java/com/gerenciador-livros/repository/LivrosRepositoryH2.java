package com.gerenciadorlivros.repository;

import com.gerenciadorlivros.model.Livro;
import com.gerenciadorlivros.util.DatabaseConnectionH2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivrosRepositoryH2 implements LivrosRepository {
	public LivrosRepositoryH2() {
		criarTabelaSeNaoExistir();
	}

	private void criarTabelaSeNaoExistir() {
		String sql = "CREATE TABLE IF NOT EXISTS livros (" +
				"id INT AUTO_INCREMENT PRIMARY KEY, " +
				"nome VARCHAR(255) NOT NULL)";

		try (Connection conn = DatabaseConnectionH2.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inicializar banco de dados H2", e);
		}
	}

	@Override
	public void adicionarLivro(String nome) {
		String sql = "INSERT INTO livros (nome) VALUES (?)";

		try (Connection conn = DatabaseConnectionH2.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nome);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao adicionar livro", e);
		}
	}

	@Override
	public List<Livro> listarLivros() {
		List<Livro> livros = new ArrayList<>();
		String sql = "SELECT * FROM livros";

		try (Connection conn = DatabaseConnectionH2.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				livros.add(new Livro(rs.getInt("id"), rs.getString("nome")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar livros", e);
		}

		return livros;
	}

	@Override
	public Optional<Livro> buscarPorId(int id) {
		String sql = "SELECT * FROM livros WHERE id = ?";

		try (Connection conn = DatabaseConnectionH2.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return Optional.of(new Livro(rs.getInt("id"), rs.getString("nome")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar livro", e);
		}

		return Optional.empty();
	}

	@Override
	public boolean removerLivro(int id) {
		String sql = "DELETE FROM livros WHERE id = ?";

		try (Connection conn = DatabaseConnectionH2.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, id);
			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Erro ao remover livro", e);
		}
	}
}
