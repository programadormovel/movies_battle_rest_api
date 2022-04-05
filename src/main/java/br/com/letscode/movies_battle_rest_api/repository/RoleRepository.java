package br.com.letscode.movies_battle_rest_api.repository;

import br.com.letscode.movies_battle_rest_api.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRole(String role);

}
