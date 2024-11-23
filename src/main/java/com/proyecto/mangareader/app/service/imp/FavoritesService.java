package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.favorites.FavoriteDTO;
import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException;
import com.proyecto.mangareader.app.exceptions.UniqueException;
import com.proyecto.mangareader.app.exceptions.UserNotFoundException;
import com.proyecto.mangareader.app.repository.FavoritesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;
import com.proyecto.mangareader.app.service.IFavoritesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService implements IFavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final UsersRepository usersRepository;
    private final MessageUtil messageSource;

    @Override
    public ListFavoritesResponse getAllFavorites(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoritesEntity> favorites = favoritesRepository.findAll(pageable).getContent();

        ListFavoritesResponse response = new ListFavoritesResponse();

        List<FavoriteDTO> dto = favorites.stream().map(this::setFavoriteDTO).toList();

        long total = favoritesRepository.count();

        response.setData(dto);
        response.setTotal(total);
        response.setOffset(offset);
        response.setLimit(limit);

        return response;
    }

    private FavoriteDTO setFavoriteDTO(FavoritesEntity favorites) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorites.getIdFavorites());
        dto.setUserId(favorites.getUserId().getIdUser());
        dto.setIdManga(favorites.getIdManga());
        dto.setNameManga(favorites.getNameManga());
        dto.setUrlImage(favorites.getUrlImage());

        return dto;
    }

    @Override
    public FavoritesEntity getFavoriteById(Long id) {
        return favoritesRepository.findById(id).orElseThrow(() -> new FavoriteNotFoundException(messageSource.getMessage("favorite.not.found")));
    }

    @Override
    public ListFavoritesResponse getFavoritesByUserId(Long userId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoritesEntity> favorites = favoritesRepository.findAllByUserId_IdUser(userId, pageable).orElseThrow(() -> new FavoriteNotFoundException(messageSource.getMessage("favorites.user.not.found")));

        ListFavoritesResponse response = new ListFavoritesResponse();

        List<FavoriteDTO> dto = favorites.stream().map(this::setFavoriteDTO).toList();



        Long total = favoritesRepository.countFavoritesByUserId_IdUser(userId);

        response.setData(dto);
        response.setTotal(total);
        response.setOffset(offset);
        response.setLimit(limit);

        return response;
    }

    @Override
    public FavoritesEntity createFavorite(FavoritesRequest request) {
        try {
            FavoritesEntity favorite = new FavoritesEntity();
            favorite.setIdManga(request.getIdManga());
            UsersEntity user = usersRepository.findById(request.getUserId()).orElseThrow( () -> new UserNotFoundException(messageSource.getMessage("user.not.found")) );
            favorite.setUserId(user);
            favorite.setNameManga(request.getNameManga());
            favorite.setUrlImage(request.getUrlImage());

            return favoritesRepository.save(favorite);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("unique_user_manga")) {
                throw new UniqueException(messageSource.getMessage("user.unique"));
            }
            throw e;

        }
    }

    @Override
    public void deleteFavoriteByIds(Long id, String mangaId) {
        favoritesRepository.deleteByUserId_IdUserAndIdManga(id, mangaId);
    }

    @Override
    public Boolean existsFavorite(Long userId, String mangaId) {
        return favoritesRepository.existsByUserIdIdUserAndIdManga(userId, mangaId);
    }

}
