package distribuido.service;

import java.io.IOException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import distribuido.dto.Alerta;
import distribuido.dto.Evento;
import distribuido.mapper.EventMapper;
import distribuido.repository.EmitterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Primary
@AllArgsConstructor
@Slf4j
public class SseNotificationService implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;
    
    @Override
    public void sendNotification(String memberId, Alerta event) {
        if (event == null) {
            log.debug("No server event to send to device.");
            return;
        }
        doSendNotification(memberId, event);
    }
    
    @Override
    public void sendNotification(String memberId, Evento event) {
        if (event == null) {
            log.debug("No server event to send to device.");
            return;
        }
        doSendNotification(memberId, event);
    }

    private void doSendNotification(String memberId, Evento event) {
        emitterRepository.get(memberId).ifPresentOrElse(sseEmitter -> {
            try {
                log.debug("Sending event: {} for member: {}", event, memberId);
                sseEmitter.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                log.debug("Error while sending event: {} for member: {} - exception: {}", event, memberId, e);
                emitterRepository.remove(memberId);
            }
        }, () -> log.debug("No emitter for member {}", memberId));
    }
    private void doSendNotification(String memberId, Alerta alerta) {
        emitterRepository.get(memberId).ifPresentOrElse(sseEmitter -> {
            try {
                log.debug("Sending event: {} for member: {}", alerta, memberId);
                sseEmitter.send(eventMapper.toSseEventBuilder(alerta));
            } catch (IOException | IllegalStateException e) {
                log.debug("Error while sending event: {} for member: {} - exception: {}", alerta, memberId, e);
                emitterRepository.remove(memberId);
            }
        }, () -> log.debug("No emitter for member {}", memberId));
    }
}