package com.example.demo.endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTestController {


    //No sabemos si va a funcionar lo de enviar el correo diario porque
    // salta el error de "Error al enviar correo: " “Authentication failed” y significa que estás usando Gmail con tu contraseña normal, y Google bloquea este tipo de acceso por seguridad.

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-mail")
    public String sendTestMail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("aquiponertucorreo@gmail.com"); //Aquí ponemos el email alque va a ser enviada la información
            message.setSubject("Prueba de correo");
            message.setText("¡Esto es una prueba de envío de correo desde Spring Boot!");

            mailSender.send(message);
            return "Correo enviado correctamente.";
        } catch (Exception e) {
            return "Error al enviar correo: " + e.getMessage();
        }
    }
}
