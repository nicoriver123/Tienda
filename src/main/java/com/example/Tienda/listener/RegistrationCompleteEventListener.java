/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.listener;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Service.Auth.AuthServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author USUARIO
 */
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    // Si por alguna razón @Slf4j no funciona, puedes usar esto:
    private static final Logger log = LoggerFactory.getLogger(RegistrationCompleteEventListener.class);

    @Autowired
    private AuthServiceImpl userService;

    @Autowired
    private JavaMailSender mailSender;
    private Usuario theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. Obtener el usuario recién registrado a partir del evento.
        theUser = event.getUser();

        // 2. Crear un token de verificación único para el usuario.
        String verificationToken = UUID.randomUUID().toString();

        // 3. Guardar el token de verificación asociado al usuario en la base de datos.
        userService.saveUserVerificationToken(theUser, verificationToken);

        // 4. Construir la URL de verificación que será enviada al usuario para confirmar su registro.
        // La URL contiene el token de verificación como parámetro.
        String url = "http://localhost:8080/api/auth/verifyEmail?token=" + verificationToken;

        // 5. Enviar el correo electrónico con el enlace de verificación.
        try {
            sendVerificationEmail(url); // Método para enviar el correo con el enlace.
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Si ocurre un error al enviar el correo, se lanza una excepción.
            throw new RuntimeException(e);
        }
        // 6. Loguear el enlace de verificación generado para que el administrador pueda revisarlo si es necesario.
        log.info("Haga clic en el enlace para verificar su registro:  {}", url);
    }
    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        // Asunto del correo electrónico.
        String subject = "Verificación de Correo Electrónico";
        // Nombre del remitente del correo.
        String senderName = "Servicio de Registro de Usuarios";
        // Contenido del correo electrónico en formato HTML.
        String mailContent = "<p>Hola, " + theUser.getNombre() + ",</p>"
                + "<p>Gracias por registrarte con nosotros.</p>"
                + "<p>Por favor, haz clic en el enlace a continuación para completar tu registro:</p>"
                + "<a href=\"" + url + "\">Verifica tu correo electrónico para activar tu cuenta</a>"
                + "<p>Gracias,<br>Servicio de Registro de Usuarios</p>";

        // Crear el mensaje de correo electrónico.
        MimeMessage message = mailSender.createMimeMessage();
        // Asistente para configurar el contenido del mensaje.
        var messageHelper = new MimeMessageHelper(message);

        // Configurar remitente, destinatario, asunto y contenido.
        messageHelper.setFrom("26@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true); // true indica que el contenido es HTML.
        // Enviar el correo electrónico.
        mailSender.send(message);
    }
}
