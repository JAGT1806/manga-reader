package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;

import java.util.List;

public interface IImgService {
    void addImg(ImgRequest img);
    ListImgResponse getAllImg(int offset, int limit);
    ImgEntity getImg(Long id);
    void deleteImg(Long id);
    public void setImg(Long id, ImgRequest img);
}
