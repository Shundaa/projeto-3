package distribuido.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class GenerateKey {

	private KeyPair pair;
	private Signature sign;
	private PrivateKey priKey;
	private PublicKey pubKey;

	public KeyPair generateRSAKkeyPair() {
		if (pair != null)
			return pair;
		try {
			log.info("Iniciando Geracao de Chaves: ");
			SecureRandom secureRandom = new SecureRandom();
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
			keyPairGenerator.initialize(2048, secureRandom);
			pair = keyPairGenerator.generateKeyPair();
			pubKey = pair.getPublic();
			priKey = pair.getPrivate();
		} catch (Exception e) {
			log.error("Erro ao gerar chaves!!!!!!!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Chaves geradas!!!!!!!");
		return pair;
	}
}
