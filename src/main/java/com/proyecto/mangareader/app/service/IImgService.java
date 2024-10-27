package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.ImgEntity;

import java.util.List;

public interface IImgService {
    void saveImg(ImgEntity img);
    List<ImgEntity> getAllImg();
    ImgEntity getImg(Long id);
    void deleteImg(Long id);
    public void setImg(ImgEntity img);
}
