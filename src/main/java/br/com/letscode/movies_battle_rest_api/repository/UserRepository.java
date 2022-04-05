package br.com.letscode.movies_battle_rest_api.repository;

import br.com.letscode.movies_battle_rest_api.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <User, Long> {
    User findByUsername(String username);

}
