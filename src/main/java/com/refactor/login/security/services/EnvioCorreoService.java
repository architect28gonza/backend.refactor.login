package com.refactor.login.security.services;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.refactor.login.util.GenerarNumRandom;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@Service
@AllArgsConstructor
public class EnvioCorreoService {

    private static final String ORIGEN_ENVIO = "axcelsoftrepositorio@gmail.com";
    private static final String BODY = "Muy buenas señor usuario, para recuperar su contraseña se ha realizado el envio del siguiente codigo de : ";

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    private Destination getDestino(String recipiente) {
        Destination destino = new Destination();
        destino.setBccAddresses(Arrays.asList(recipiente));
        return destino;
    }

    private Content getContenido(String contenidoEmail) {
        Content contenido = new Content();
        contenido.setData(contenidoEmail);
        return contenido;
    }

    private Body getCuerpo(Content cuerpoEmail) {
        Body cuerpo = new Body();
        cuerpo.setText(cuerpoEmail);
        return cuerpo;
    }

    private Message getMensaje(Content asunto, Body cuerpo) {
        Message mensaje = new Message();
        mensaje.setBody(cuerpo);
        mensaje.setSubject(asunto);
        return mensaje;
    }

    private SendEmailRequest getPeticionEnvio(
            Destination destino,
            Message mensaje,
            String correo) {

        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setDestination(destino);
        sendEmailRequest.setMessage(mensaje);
        sendEmailRequest.setSource(correo);
        return sendEmailRequest;
    }

    public String setEnviarCorreo(String correo) {
        Destination destino = this.getDestino(correo);
        Content contenido = this.getContenido(BODY.concat(String.valueOf(GenerarNumRandom.getNumeroRandom())));
        Content asunto = this.getContenido("RECUPERAR CONTRASEÑA - CRED.");
        Body cuerpo = this.getCuerpo(contenido);
        Message mensaje = this.getMensaje(asunto, cuerpo);
        SendEmailRequest peticionEnvio = this.getPeticionEnvio(destino, mensaje, ORIGEN_ENVIO);

        try {
            amazonSimpleEmailService.sendEmail(peticionEnvio);
            return "Solictud de correo enviada correctamente.";
        } catch (SnsException e) {
            log.error("Error, No se puede realizar el envio del correo electronico : ".concat(e.getMessage()), e);
        }
        return "";
    }
}
