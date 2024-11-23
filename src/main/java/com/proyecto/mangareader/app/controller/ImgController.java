package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.service.imp.ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/img")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ImgController {
    private final ImgService imgService;

    @GetMapping
    public ResponseEntity<ListImgResponse> getImgs(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        ListImgResponse response = imgService.getAllImg(offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ImgEntity getImg(@PathVariable Long id) {
        return imgService.getImg(id);
    }

    @PostMapping
    public ResponseEntity<OkResponse> addImg(@RequestBody ImgRequest request) {
        imgService.addImg(request);
        return ResponseEntity.ok(new OkResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OkResponse> updateImg(@PathVariable Long id, @RequestBody ImgRequest request) {
        imgService.setImg(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> deleteImg(@PathVariable Long id) {
        imgService.deleteImg(id);
        return ResponseEntity.ok(new OkResponse());
    }

}
