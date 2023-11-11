package com.refactor.login.security.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolResponse;

@Slf4j
@Service
@AllArgsConstructor
public class RedSocialService {

    private static final String GRUPO_USUARIO = "capacitacion-axcelsoft";

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    private String setCreateGrupoUsuario(String nombreGrupoUsuario) {
        try {
            CreateUserPoolResponse response = cognitoIdentityProviderClient.createUserPool(
                    CreateUserPoolRequest.builder().poolName(nombreGrupoUsuario).build());
            log.info("Grupo de usuario creado con exito");
            return response.userPool().id();

        } catch (CognitoIdentityProviderException e) {
            log.error("Error, No fue posible crear el grupo de usuario : ".concat(e.getMessage()), e);
        }
        return "";
    }


    public String iniciarSesionRedSocial() {
        return setCreateGrupoUsuario(GRUPO_USUARIO);
    }

}
