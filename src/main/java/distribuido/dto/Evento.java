package distribuido.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Evento {
	public Evento() {
		super();
		convidados= new ArrayList<>();
		alertas= new ArrayList<>();
	}

	
	private String anfitriao;
	private String nomeEvento;
	private String dataEvento;
	private String horarioEvento;
	private List<Convidado> convidados;
	private List<Alerta> alertas;
	
}
