package br.com.letscode.movies_battle_rest_api.repository;

import br.com.letscode.movies_battle_rest_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository2 extends JpaRepository<User, Long> {
    User getByUsername(String username);
}
