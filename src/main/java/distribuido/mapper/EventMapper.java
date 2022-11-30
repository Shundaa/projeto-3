package distribuido.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import distribuido.dto.Alerta;
import distribuido.dto.Evento;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(Evento event) {
        return SseEmitter.event()
//                .id(RandomStringUtils.randomAlphanumeric(12))
//                .name(event.getNomeEvento())
                .data(event);
    }
    public SseEmitter.SseEventBuilder toSseEventBuilder(Alerta alerta) {
        return SseEmitter.event()
//                .id(RandomStringUtils.randomAlphanumeric(12))
//                .name(alerta.getNome())
                .data(alerta);
    }
}