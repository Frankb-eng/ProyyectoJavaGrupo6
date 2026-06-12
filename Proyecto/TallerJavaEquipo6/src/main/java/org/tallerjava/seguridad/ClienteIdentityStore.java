package org.tallerjava.seguridad;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import org.mindrot.jbcrypt.BCrypt;
import org.tallerjava.ModuloCliente.Interface.local.InterfaceLocalCliente;
import org.tallerjava.ModuloCliente.dominio.Cliente;

import java.util.Set;

@ApplicationScoped
public class ClienteIdentityStore implements IdentityStore {

    @Inject
    InterfaceLocalCliente interfaceLocalCliente;  // se usa la interfaz local y no el repositorio directamente para no exponer todas las funciones internas del modulo cliente hacia afuera y respetar la arquitectura modular

    @Override
    public CredentialValidationResult validate(Credential credential) {

        UsernamePasswordCredential cred = (UsernamePasswordCredential) credential;
        String cedula = cred.getCaller();
        String contrasena = cred.getPasswordAsString();

        Cliente cliente = interfaceLocalCliente.buscarPorCedula(cedula);
        if (cliente == null) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        if (!BCrypt.checkpw(contrasena, cliente.getContrasena())) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        return new CredentialValidationResult(cedula, Set.of("CLIENTE"));
    }
}