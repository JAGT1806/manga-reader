package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.exceptions.ImgNotFoundException;
import com.proyecto.mangareader.app.repository.ImgRepository;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;
import com.proyecto.mangareader.app.service.IImgService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ImgService implements IImgService {
    private final ImgRepository imgRepository;
    private final MessageUtil messageSource;

    @Override
    public void addImg(ImgRequest request) {
        ImgEntity img = new ImgEntity();
        img.setUrl(request.getUrl());
        imgRepository.save(img);
    }

    @Override
    public ListImgResponse getAllImg(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        List<ImgEntity> imgs = imgRepository.findAll(pageable).getContent();
        Long total = imgRepository.count();

        return convertToDto(imgs, offset, limit, total);
    }

    @Override
    public void setImg(Long id, ImgRequest request) {
        if(request.getUrl() == null) {
            throw new IllegalArgumentException(messageSource.getMessage("img.null"));
        }

        ImgEntity img = imgRepository.findById(id).orElseThrow();
        img.setUrl(request.getUrl());

        imgRepository.save(img);
    }

    @Override
    public ImgEntity getImg(Long id) {
        return imgRepository.findById(id).orElseThrow(() -> new ImgNotFoundException(null));
    }

    @Override
    public void deleteImg(Long id) {
        imgRepository.deleteById(id);
    }


    private ListImgResponse convertToDto(List<ImgEntity> imgs, int offset, int limit, Long total) {
        ListImgResponse response = new ListImgResponse();
        response.setData(imgs);
        response.setOffset(offset);
        response.setLimit(limit);
        response.setTotal(total);

        return response;
    }
}
