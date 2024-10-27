package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.repository.ImgRepository;
import com.proyecto.mangareader.app.service.IImgService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ImgService implements IImgService {
    private final ImgRepository imgRepository;

    @Override
    public void saveImg(ImgEntity img) {
        imgRepository.save(img);
    }

    @Override
    public List<ImgEntity> getAllImg() {
        return imgRepository.findAll();
    }

    @Override
    public void setImg(ImgEntity img) {
        imgRepository.save(img);
    }

    @Override
    public ImgEntity getImg(Long id) {
        ImgEntity img = imgRepository.findById(id).orElse(null);
        return img;
    }

    @Override
    public void deleteImg(Long id) {
        imgRepository.deleteById(id);
    }


}
