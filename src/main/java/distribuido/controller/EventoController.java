package distribuido.controller;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import distribuido.dto.Alerta;
import distribuido.dto.Evento;
import distribuido.dto.User;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;


@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("")
public class EventoController {

	private KeyPair pair;
	private Signature sign;
	private PrivateKey priKey;
	private PublicKey pubKey;
	private WebClient client;

	public EventoController() {
		super();

		client = WebClient.create("http://localhost:8080");
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("DSA");
			SecureRandom secureRan = new SecureRandom();
			kpg.initialize(512, secureRan);
			KeyPair kp = kpg.generateKeyPair();
			pubKey = kp.getPublic();
			priKey = kp.getPrivate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<Evento> eventos = new ArrayList<Evento>();
	private static List<String> clientes = new ArrayList<String>();

	@PostMapping("/register/user")
	@ApiOperation(value = "Registar novo usuario e retornar a chave puplica")
	public String registrarNovoUsuario(@RequestBody User nome) throws RemoteException {
		clientes.add(nome.getNome());
		System.out.println("Novo Usuario Cadastrado com sucesso: " + nome);
		System.out.println("Clientes: " + clientes);
		return "OK";
	}

	@PostMapping("/register/evento")
	@ApiOperation(value = "Registar novo evento")
	public Evento register(@Valid @RequestBody Evento eventoDto) {
		log.info("Evento Recebido: " + eventoDto);
		eventos.add(eventoDto);
		return eventoDto;
	}

	@DeleteMapping("/register")
	@ApiOperation(value = "Remover Compromisso ou alerta")
	public void delete(@RequestParam String nome, @RequestParam String nomeEvento) {

		for (Evento evento : eventos) {
			if (evento.getNomeEvento().equalsIgnoreCase(nomeEvento)) {
				if (evento.getAnfitriao().equalsIgnoreCase(nome)) {
					eventos.remove(evento);
				} else {
					for (Alerta alerta : evento.getAlertas()) {
						if (nome.equalsIgnoreCase(alerta.getNome())) {
							evento.getAlertas().remove(alerta);
						}
					}
				}
			}
		}
		return;
	}

	@DeleteMapping("/alerta")
	@ApiOperation(value = "Remover Compromisso ou alerta")
	public void deleteAlerta(@RequestParam String nome, @RequestParam String nomeEvento) {

		for (Evento evento : eventos) {
			if (evento.getNomeEvento().equalsIgnoreCase(nomeEvento)) {
				for (Alerta alerta : evento.getAlertas()) {
					if (nome.equalsIgnoreCase(alerta.getNome())) {
						evento.getAlertas().remove(alerta);
					}
				}
			}
		}
		return;
	}

	@GetMapping("/eventos")
	@ApiOperation(value = "Register a new Customer and his adresses")
	public List<Evento> find() {
		log.info("Lista de eventos: " + eventos);
		return eventos;
	}

	@GetMapping("/stream")
	@ApiOperation(value = "Register a new Customer and his adresses")
	public void teste() {

		Flux<Evento> eventoFlux = client.get().uri("/api/v1/eventos").retrieve().bodyToFlux(Evento.class);
		eventoFlux.subscribe(System.out::println);
	}

	@GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> streamFlux() {
		return Flux.interval(Duration.ofSeconds(1)).map(sequence -> "Flux - " + LocalTime.now().toString());
	}

	@GetMapping("/stream-sse")
	public Flux<ServerSentEvent<String>> streamEvents(@RequestParam String nome) {
		log.info("SSE NOME: " + nome);

		Flux.fromIterable(eventos);
//        .flatMap(prepaidClient::sendBalanceAlertData);

		return Flux.interval(Duration.ofSeconds(1))
				.map(sequence -> ServerSentEvent.<String>builder().id(String.valueOf(sequence)).event("periodic-event")
						.data("SSE - " + LocalTime.now().toString()).build());
	}

	private SseEmitter emitter;

	@GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	SseEmitter createConnection() {
		emitter = new SseEmitter();
		return emitter;
	}

	@GetMapping(path = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	void send() {
		sendEvents();
	}

	void sendEvents() {
		try {
			emitter.send("Alpha");
			emitter.send("Omega");

//			emitter.complete();
		} catch (Exception e) {
			emitter.completeWithError(e);
		}
	}

	@GetMapping(value = "/stream/teste", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Evento> streamAllUsers() {
		return Flux.fromIterable(eventos)
				.flatMap(user -> Flux
						.zip(Flux.interval(Duration.ofSeconds(2)), Flux.fromStream(Stream.generate(() -> user)))
						.map(Tuple2::getT2));

	}

	public void consumeServerSentEvent() {
		WebClient client = WebClient.create("http://localhost:8080/sse-server");
		ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
		};

		Flux<ServerSentEvent<String>> eventStream = client.get().uri("/stream-sse").retrieve().bodyToFlux(type);

		eventStream.subscribe(
				content -> log.info("Time: {} - event: name[{}], id [{}], content[{}] ", LocalTime.now(),
						content.event(), content.id(), content.data()),
				error -> log.error("Error receiving SSE: {}", error), () -> log.info("Completed!!!"));
	}

	@GetMapping("/stream-sse-mvc")
	public SseEmitter streamSseMvc() {
		SseEmitter emitter = new SseEmitter();
		ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
		sseMvcExecutor.execute(() -> {
			try {
				for (int i = 0; true; i++) {
					SseEventBuilder event = SseEmitter.event().data("SSE MVC - " + LocalTime.now().toString())
							.id(String.valueOf(i)).name("sse event - mvc");
					emitter.send(event);
					Thread.sleep(1000);
				}
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
	}

}
