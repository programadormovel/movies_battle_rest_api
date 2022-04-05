package br.com.letscode.movies_battle_rest_api.repository;

import br.com.letscode.movies_battle_rest_api.model.Jogada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JogadaRepository extends JpaRepository<Jogada, Long> {
}
