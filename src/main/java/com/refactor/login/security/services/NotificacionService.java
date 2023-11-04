package com.refactor.login.security.services;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.refactor.login.util.GenerarNumRandom;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class NotificacionService {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class TopicArn {
        private String arn;
        private boolean existeTopic;
    }

    private static final String NOMBRE_TOPICO = "MyAxcelSNS"; /* Nombre del topico quemada, modo prueba */

    private final AmazonSNS amazonSNS;
    private final SnsClient snsCliente;

    public int addSubcripcion(String numeroTelefono) {
        TopicArn objTopico = this.setBuscarTopico(NOMBRE_TOPICO);
        final boolean isExisteTopico = objTopico.isExisteTopic();
        final String arn = objTopico.getArn();
        if (!isExisteTopico) {
            final String arnResultado = this.setCrearTopico(NOMBRE_TOPICO);
            return !arnResultado.equals("")
                    ? setProcesoSubcripcion("sms", numeroTelefono, arnResultado)
                    : -1;
        }
        return setProcesoSubcripcion("sms", numeroTelefono, arn);
    }

    public String setEnviarSMS(String telefono) {
        try {
            Map<String, MessageAttributeValue> smsAtributos = new HashMap<>();
            smsAtributos.put("AWS.SNS.SMS.SenderID", this.getMessageAttributeValue(NOMBRE_TOPICO));
            smsAtributos.put("AWS.SNS.SMS.SMSType", this.getMessageAttributeValue("Transactional"));

            final String mensaje = "Su codigo para recuperar la contraseña es : "
                    .concat(String.valueOf(GenerarNumRandom.getNumeroRandom()));
            PublishResult result = amazonSNS.publish(this.getPublishRequestSMS(mensaje, telefono, smsAtributos));
            return result.getMessageId();
        } catch (SnsException e) {
            log.error("Error, No se pudo realizar el completado de envio : ".concat(e.getMessage()), e);
        }
        return "";
    }

    private MessageAttributeValue getMessageAttributeValue(String valor) {
        return new MessageAttributeValue().withStringValue(valor).withDataType("String");
    }

    private PublishRequest getPublishRequestSMS(String mensaje, String telefono,
            Map<String, MessageAttributeValue> smsAtributos) {
        return new PublishRequest()
                .withMessage(mensaje)
                .withPhoneNumber(telefono)
                .withMessageAttributes(smsAtributos);
    }

    private String setCrearTopico(String nombreTopico) {
        try {
            CreateTopicRequest request = CreateTopicRequest.builder().name(nombreTopico).build();
            return snsCliente.createTopic(request).topicArn();
        } catch (SnsException e) {
            log.error("Error, No se puede crear el topico para SNS : ".concat(e.getMessage()), e);
        }
        return "";
    }

    private int setProcesoSubcripcion(String tipoProtocolo, String tipoEndpoint, String arn) {
        try {
            SubscribeRequest request = this.getSubscribeRequest(tipoProtocolo, tipoEndpoint, arn);
            SubscribeResponse result = snsCliente.subscribe(request);
            return result.sdkHttpResponse().statusCode();
        } catch (SnsException e) {
            log.error("Error, No se ha podido realiar la subcripcion con dicho numero : ".concat(e.getMessage()), e);
        }
        return -1;
    }

    private TopicArn setBuscarTopico(String nombreTopico) {
        return amazonSNS.listTopics().getTopics().stream()
                .filter(topic -> topic.getTopicArn().contains(nombreTopico))
                .map(topic -> new TopicArn(topic.getTopicArn(), true))
                .findFirst()
                .orElse(new TopicArn(null, false));
    }

    private SubscribeRequest getSubscribeRequest(String tipoProtocolo, String tipoEndpoint, String arn) {
        return SubscribeRequest.builder()
                .protocol(tipoProtocolo)
                .endpoint(tipoEndpoint)
                .returnSubscriptionArn(true)
                .topicArn(arn)
                .build();
    }
}
