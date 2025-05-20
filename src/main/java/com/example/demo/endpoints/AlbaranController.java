package com.example.demo.endpoints;

import com.example.demo.entities.Albaran;
import com.example.demo.repositories.AlbaranRepository;
import com.example.demo.repositories.ProveedorRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/albaranes")
public class AlbaranController {

    private final AlbaranRepository albaranRepository;
    private final ProveedorRepository proveedorRepository;

    public AlbaranController(AlbaranRepository albaranRepository, ProveedorRepository proveedorRepository) {
        this.albaranRepository = albaranRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public List<Albaran> getAllAlbaranes() {
        return albaranRepository.findAll();
    }

    @GetMapping("/proveedor/{proveedorId}")
    public List<Albaran> getByProveedor(@PathVariable Long proveedorId) {
        return albaranRepository.findByProveedorId(proveedorId);
    }

    @PostMapping
    public Albaran createAlbaran(@RequestBody Albaran albaran) {
        if (albaran.getProveedor() != null && albaran.getProveedor().getCif() != null) {
            proveedorRepository.findByCif(albaran.getProveedor().getCif())
                    .ifPresent(albaran::setProveedor);
        }
        return albaranRepository.save(albaran);
    }


    @PutMapping("/{id}/pagar")
    public Albaran markAsPaid(@PathVariable Long id) {
        return albaranRepository.findById(id).map(albaran -> {
            albaran.setPagado(true);
            albaran.setFechaPago(LocalDate.now());
            return albaranRepository.save(albaran);
        }).orElse(null);
    }

    @PostMapping("/{id}/foto")
    public Albaran subirFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return albaranRepository.findById(id).map(albaran -> {
            String url = "uploads/" + file.getOriginalFilename();
            albaran.setFotoUrl(url);
            return albaranRepository.save(albaran);
        }).orElse(null);
    }
}
