package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.exceptions.ImgNotFoundException;
import com.jagt1806.mangareader.model.Img;
import com.jagt1806.mangareader.repository.ImgRepository;
import com.jagt1806.mangareader.http.request.img.ImgRequest;
import com.jagt1806.mangareader.http.response.img.ImgListResponse;
import com.jagt1806.mangareader.service.ImgService;
import com.jagt1806.mangareader.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImgServiceImp implements ImgService {
    private final ImgRepository imgRepository;
    private final MessageUtil messageSource;

    @Override
    public ImgListResponse getAllImg(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        List<Img> img = imgRepository.findAll(pageable).getContent();
        Long total = imgRepository.count();

        ImgListResponse response = new ImgListResponse();
        response.setData(img);
        response.setOffset(offset);
        response.setLimit(limit);
        response.setTotal(total);

        return response;
    }

    @Override
    public Img getById(Long id) {
        return imgRepository.findById(id).orElseThrow(() -> new ImgNotFoundException(null));
    }

    @Override
    public void createImg(ImgRequest request) {
        if(request.getUrl() == null)
            throw new IllegalArgumentException(messageSource.getMessage("img.null"));

        Img img = new Img();
        img.setUrl(request.getUrl());
        imgRepository.save(img);
    }

    @Override
    public void updateImg(Long id, ImgRequest request) {
        if(request.getUrl() == null)
            throw new IllegalArgumentException(messageSource.getMessage("img.null"));

        Img img = imgRepository.findById(id).orElseThrow(() -> new ImgNotFoundException(null));
        img.setUrl(request.getUrl());

        imgRepository.save(img);
    }

    @Override
    public void deleteImg(Long id) {
        imgRepository.deleteById(id);
    }

}
