package distribuido.service;

import distribuido.dto.Alerta;
import distribuido.dto.Evento;

public interface NotificationService {

    void sendNotification(String memberId, Evento event);
    void sendNotification(String memberId, Alerta event);
}
