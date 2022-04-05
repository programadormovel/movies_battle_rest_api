package br.com.letscode.movies_battle_rest_api.controller;

import br.com.letscode.movies_battle_rest_api.model.*;
import br.com.letscode.movies_battle_rest_api.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/partida")
public class PartidaController {

    double errosJogador = 0;
    double pontuacao = 0;
    int primeiraJogada = 0;
    double acertos = 0;
    double contador = 0;
    int logout = 0;

    Filme filme1;
    Filme filme2;
    List<Filme> listaAnterior1;
    List<Filme> listaAnterior2;
    Partida partidaModel;
    Jogada jogada;
    List<Partida> partidasJogadas;
    List<Jogada> jogadasRealizadas;

    @Autowired
    FilmeService filmeService;
    @Autowired
    PartidaService partidaService;
    @Autowired
    JogadaService jogadaService;
    @Autowired
    UserService userService;
    @Autowired
    SSUserDetailService ssUserDetailService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@PathVariable(value = "username") String username,
                                        @PathVariable(value="password") String password) {
        logout = 0;
        User user = new User();
        if(userService.getByUsername(username).getUsername().equals(username)){
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            user = ssUserDetailService.adicionaUsuario(username);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }
//
//    @GetMapping("/admin")
//    public String admin() {
//        return "admin";
//    }

    @GetMapping("/ranking")
    public ResponseEntity<Object> ranking() {
        List<Partida> listaPartidas = partidaService.pesquisarPartidas();
        if (listaPartidas == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partidas não encontradas.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaPartidas);
    }

    @GetMapping("/resultadoFinal")
    public ResponseEntity<Object> resultadoFinal() {
        String pontos = new DecimalFormat("######.##").format(pontuacao);

        errosJogador = 0;
        pontuacao = 0;
        acertos = 0;
        pontuacao = 0;
        contador = 0;
        primeiraJogada = 0;

        if (pontos == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pontuação não encontrada.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(pontos);
    }

    @GetMapping("/partida")
    public ResponseEntity<Object> partida() {
        List<Filme> filmes = new ArrayList<>();
        if(logout==1) //logout();
            System.out.print("logout");

        long f1 = (long) Math.ceil(Math.random() * 10);
        filme1 = filmeService.pesquisarFilme(f1);

        long f2 = (long) Math.ceil(Math.random() * 10);
        filme2 = filmeService.pesquisarFilme(f2);

        if(!filme1.equals(filme2)){
            filmes = List.of(new Filme[]{filme1, filme2});

            if (primeiraJogada == 0) {
                listaAnterior1 = List.of(new Filme[]{filme1, filme2});
                listaAnterior2 = List.of(new Filme[]{filme2, filme1});
                primeiraJogada++;

                // Gravar partida deste usuário
                long idContaLogada = userService
                        .getByUsername(SecurityContextHolder
                                .getContext().getAuthentication().getName()).getId();
                String contaLogada = userService
                        .getByUsername(SecurityContextHolder
                                .getContext().getAuthentication().getName()).getUsername();
                System.out.println(idContaLogada);
                System.out.println(contaLogada);
                partidaModel = new Partida();
                partidaModel.setPontuacao(0.0);
                partidaModel.setDataPartida(LocalDate.now(ZoneId.of("UTC")));
                partidaModel.setUsuario(new User(idContaLogada, contaLogada));
                partidaService.save(partidaModel);

            } else {
                if (!listaAnterior1.equals(filmes) && !listaAnterior2.equals(filmes) && !filme1.equals(filme2)) {
                    listaAnterior1 = List.of(new Filme[]{filme1, filme2});
                    listaAnterior2 = List.of(new Filme[]{filme2, filme1});

                } else {
                    partida();
                }
            }
        }else {
            partida();
        }

        return ResponseEntity.status(HttpStatus.OK).body(filmes);
    }

    @GetMapping(value = "/resultado/{id}")
    public String resultado(@PathVariable("id") Long id,
                            Model model) {
        String resultado = "{";
        contador++;
        if (Double.parseDouble(filme1.getImdbRating()) > Double.parseDouble(filme2.getImdbRating())) {
            resultado += "filmePopular:" + filmeService.pesquisarFilme(filme1.getId()) + ",";
            if (filme1.getId() != id) {
                errosJogador++;
            } else {
                acertos++;
                jogada = new Jogada(Math.round(contador),true, partidaModel, filme1, filme2);
                if (partidaModel != null)
                    jogadaService.save(jogada);
            }
        } else if (Double.parseDouble(filme1.getImdbRating()) < Double.parseDouble(filme2.getImdbRating())) {
            resultado += "filmePopular:" + filmeService.pesquisarFilme(filme2.getId()) + ",";
            if (filme2.getId() != id) {
                errosJogador++;
            } else {
                acertos++;
                jogada = new Jogada(Math.round(contador),true, partidaModel, filme1, filme2);
                if (partidaModel != null)
                    jogadaService.save(jogada);
            }
        } else {
            model.addAttribute("filmePopular", filmeService.pesquisarFilme(id));
            resultado += "filmePopular:" + filmeService.pesquisarFilme(id) + ",";
            acertos++;
            jogada = new Jogada(Math.round(contador),true, partidaModel, filme1, filme2);
            if (partidaModel != null)
                jogadaService.save(jogada);
        }

        resultado += "filmeEscolhido:" + filmeService.pesquisarFilme(id) + "," +
                            "pontuacao:" + acertos + "," +
                            "errosJogador: " + errosJogador + "}";

        if (errosJogador == 3) {
            //TODO - GRAVAR PONTUAÇÃO DO JOGADOR
            pontuacao = (acertos/contador)*contador;
            partidaModel.setPontuacao(pontuacao);
            partidaService.updatePontuacao(pontuacao, partidaModel.getId());

            logout = 1;
            return "redirect:/resultadoFinal";
        }
        Gson gson = new Gson();

        return  gson.toJson(resultado);
    }

    @PostMapping("/process")
    public String processForm(@Valid Filme filme, BindingResult result) {
        if (result.hasErrors()) {
            return "partida";
        }
        return "redirect:/";
    }

    @PostMapping("/processarResultado")
    public String resultadoForm(@Valid Filme filme, BindingResult result) {
        if (result.hasErrors()) {
            return "resultado";
        }
        return "redirect:/partida";
    }
}
