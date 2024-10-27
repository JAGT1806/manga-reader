package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.entity.ImgUsersEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.repository.ImgUsersRepository;
import com.proyecto.mangareader.app.service.IImgUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ImgUserService implements IImgUserService {
    ImgUsersRepository imgUsersRepository;

    @Override
    public void saveEntity(UsersEntity user, ImgEntity img) {
        ImgUsersEntity imgUsersEntity = new ImgUsersEntity();
        imgUsersEntity.setUser(user);
        imgUsersEntity.setImg(img);
        imgUsersRepository.save(imgUsersEntity);
    }

    @Override
    public ImgUsersEntity getEntityById(Long id) {
        return imgUsersRepository.findById(id).orElse(null);
    }

    @Override
    public List<ImgUsersEntity> getAllEntities() {
        return imgUsersRepository.findAll();
    }

    @Override
    public void deleteEntity(Long id) {
        imgUsersRepository.deleteById(id);
    }

    @Override
    public void setImgUsersRepository(ImgUsersRepository imgUsersRepository) {
        this.imgUsersRepository = imgUsersRepository;
    }
}
