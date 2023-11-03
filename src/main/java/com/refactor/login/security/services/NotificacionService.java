package com.refactor.login.security.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class NotificacionService {

    private static final String NOMBRE_TOPICO = "MyAxcelSNS"; /* Nombre del topico quemada, modo prueba */

    private final AmazonSNS amazonSNS;
    private final SnsClient snsCliente;

    public int addSubcripcion(String numeroTelefono) {
        boolean procesoSubcripcion = true;
        if (!isExisteTopico(NOMBRE_TOPICO)) {
            final String topicoCreado = this.setCrearTopico(NOMBRE_TOPICO);
            procesoSubcripcion = !topicoCreado.equals("");
            return procesoSubcripcion ? this.setProcesoSubcripcion("sns", numeroTelefono, NOMBRE_TOPICO) : -1;
        }
        return -1;
    }

    public void setEnviarSMS(String telefono) {
        try {
            Map<String, MessageAttributeValue> smsAtributos = new HashMap<>();
            smsAtributos.put("AWS.SNS.SMS.SenderID", this.getMessageAttributeValue(NOMBRE_TOPICO));
            smsAtributos.put("AWS.SNS.SMS.SMSType", this.getMessageAttributeValue("Transactional"));

            final String mensaje = "Envio de servicio de sns - AxcelSoftware";
            PublishResult result = amazonSNS.publish(this.getPublishRequestSMS(mensaje, telefono, smsAtributos));
            log.info("El id recibido es : " + result.getMessageId());
        } catch (SnsException e) {
            log.error("Error, No se pudo realizar el completado de envio : ".concat(e.getMessage()), e);
        }
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

    private int setProcesoSubcripcion(String tipoProtocolo, String tipoEndpoint, String topico) {
        try {
            SubscribeRequest request = this.getSubscribeRequest(tipoProtocolo, tipoEndpoint, topico);
            SubscribeResponse result = snsCliente.subscribe(request);
            return result.sdkHttpResponse().statusCode();
        } catch (SnsException e) {
            log.error("Error, No se ha podido realiar la subcripcion con dicho numero : ".concat(e.getMessage()), e);
        }
        return -1;
    }

    private boolean isExisteTopico(String nombreTopico) {
        return amazonSNS.listTopics().getTopics().stream()
                .anyMatch(topic -> topic.getTopicArn().contains(nombreTopico));
    }

    private SubscribeRequest getSubscribeRequest(String tipoProtocolo, String tipoEndpoint, String topico) {
        return SubscribeRequest.builder()
                .protocol(tipoProtocolo)
                .endpoint(tipoEndpoint)
                .returnSubscriptionArn(true)
                .topicArn(topico)
                .build();
    }
}
