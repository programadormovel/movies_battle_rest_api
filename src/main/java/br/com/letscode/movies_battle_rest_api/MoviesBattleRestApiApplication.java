package br.com.letscode.movies_battle_rest_api;

import br.com.letscode.movies_battle_rest_api.controller.FilmeController;
import br.com.letscode.movies_battle_rest_api.service.FilmeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoviesBattleRestApiApplication {

    public MoviesBattleRestApiApplication(FilmeController filmeController,
                                   FilmeService filmeService) {
        try {
            filmeService.saveAll(filmeController.carregarFilmes());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MoviesBattleRestApiApplication.class, args);
    }

}
