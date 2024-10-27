package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.entity.ImgUsersEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.repository.ImgUsersRepository;

import java.util.List;

public interface IImgUserService {
    void saveEntity(UsersEntity user, ImgEntity img);
    ImgUsersEntity getEntityById(Long id);
    List<ImgUsersEntity> getAllEntities();
    void deleteEntity(Long id);
    void setImgUsersRepository(ImgUsersRepository imgUsersRepository);
}
