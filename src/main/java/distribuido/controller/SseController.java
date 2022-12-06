package distribuido.controller;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import distribuido.dto.Alerta;
import distribuido.dto.Convidado;
import distribuido.dto.Evento;
import distribuido.dto.User;
import distribuido.service.EmitterService;
import distribuido.service.GenerateKey;
import distribuido.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/sse")
public class SseController {

	@Autowired
	private GenerateKey generateKey;

	public SseController() {
		super();
	}

	private static List<Evento> eventos = new ArrayList<Evento>();

	@Autowired
	private EmitterService emitterService;

	@Autowired
	private NotificationService notificationService;

	@PostMapping("/user")
	@ApiOperation(value = "Registar novo usuario e retornar a chave puplica")
	public byte[] registrarNovoUsuario(@RequestBody User nome) throws RemoteException {
		log.info("Novo Usuario Cadastrado com sucesso: " + nome);
		KeyPair pair = generateKey.generateRSAKkeyPair();
		return pair.getPublic().getEncoded();
	}

	@GetMapping("/user")
	public SseEmitter subscribeToEvents(@RequestParam String nome) {
		log.info("Subscribing Evento member with id {}", nome);
		return emitterService.createEmitter(nome);
	}

	@GetMapping("/alerta")
	public SseEmitter subscribeToAlerta(@RequestParam String nome) {
		log.info("Subscribing Alerta member with id {}", nome + "_ALERTA");
		return emitterService.createEmitter(nome + "_ALERTA");
	}
	
	@PostMapping("/alerta")
	public ResponseEntity<String> criarAlerta(@RequestParam(value = "nomeEvento", required = false) String nomeEvento,
			@RequestParam(value = "nome", required = false) String nome,
			@RequestParam(value = "data", required = false) String data) {
		log.info("Criando Alerta para user:" +nome+" no evento: "+nomeEvento+" na data: "+data);
		for (Evento evento : eventos) {
			if (evento.getNomeEvento().equals(nomeEvento)) {
				Alerta alerta = new Alerta();
				alerta.setHoraAlerta(data);
				alerta.setNome(nome);
				evento.getAlertas().add(alerta);
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/evento")
	@ApiOperation(value = "Registar novo evento")
	public Evento register(@Valid @RequestBody Evento evento) {
		log.info("Evento Recebido: " + evento);
		eventos.add(evento);
		for (Convidado convidado : evento.getConvidados()) {
			publishEvent(convidado.getNome(), evento);
		}
		return evento;
	}

	@DeleteMapping("/evento")
	@ApiOperation(value = "Registar novo evento")
	public ResponseEntity<String> delete(@RequestParam(value = "nomeEvento", required = false) String nomeEvento,
			@RequestParam(value = "nome", required = false) String nome) {
		log.info("deletando Evento Ou Alerta: " + nomeEvento);
		for (Evento evento : eventos) {
			if (evento.getNomeEvento().equals(nomeEvento)) {
				if (evento.getAnfitriao().equalsIgnoreCase(nome)) {
					log.info("deletando Evento: " + nome);
					eventos.remove(evento);
					return new ResponseEntity<>(HttpStatus.OK);
				}
				else {
					List<Alerta> alertas = evento.getAlertas();
					for (Alerta alerta : alertas) {
						if (alerta.getNome().equalsIgnoreCase(nome)) {
							log.info("deletando Alerta: " + nome);
							alertas.remove(alerta);
							return new ResponseEntity<>(HttpStatus.OK);
						}
					}
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/eventos")
	@ApiOperation(value = "Registar novo evento")
	public List<Evento> listEventosData(@RequestParam(value = "data", required = false) String data) {
		log.info("Lista de eventos : " +eventos);
		if (data == null || data.isBlank() || data.isEmpty())
			return eventos;
		List<Evento> dataEvento = new ArrayList<>();
		for (Evento evento : eventos) {
			if (evento.getDataEvento().equals(data))
				dataEvento.add(evento);
		}
		return dataEvento;
	}

	public void publishEvent(String nome, Evento event) {
		log.info("Publishing event {} for member with id {}", event, nome);
		notificationService.sendNotification(nome, event);
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 1000)
	public void reportCurrentTime() {
		for (Evento evento : eventos) {
			for (Alerta alerta : evento.getAlertas()) {
				if (dateFormat.format(new Date()).equals(alerta.getHoraAlerta())) {
					log.info("Hora do Evento", evento);
					publishEvent(alerta.getNome() + "_ALERTA", evento);
				}
			}
		}
	}
}
