package com.example.demo.endpoints;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class CorreoController {

    //No sabemos si va a funcionar lo de enviar el correo diario porque
    // salta el error de "Error al enviar correo: " “Authentication failed” y significa que estás usando Gmail con tu contraseña normal, y Google bloquea este tipo de acceso por seguridad.
    //Hemos hecho otra clase MailTestController para ver si se enviaba correctamente el gmail

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/enviar-correo")
    public ResponseEntity<String> enviarCorreo(@RequestParam("archivo") MultipartFile archivo,
                                               @RequestParam("destinatario") String destinatario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            helper.setTo(destinatario);
            helper.setSubject("Informe de Stock Bajo");
            helper.setText("Adjunto el archivo Excel con los productos con stock bajo.");

            helper.addAttachment(archivo.getOriginalFilename(), archivo);

            mailSender.send(mensaje);
            return ResponseEntity.ok("Correo enviado correctamente.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo: " + e.getMessage());
        }
    }
}
