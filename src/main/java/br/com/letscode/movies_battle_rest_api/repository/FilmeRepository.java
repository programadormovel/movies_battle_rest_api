package br.com.letscode.movies_battle_rest_api.repository;

import br.com.letscode.movies_battle_rest_api.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
}
