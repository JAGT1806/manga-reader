package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.dto.favorite.FavoriteDTO;
import com.jagt1806.mangareader.exceptions.FavoriteNotFoundException;
import com.jagt1806.mangareader.exceptions.UniqueException;
import com.jagt1806.mangareader.exceptions.UserNotFoundException;
import com.jagt1806.mangareader.http.request.favorite.FavoriteRequest;
import com.jagt1806.mangareader.http.response.favorite.FavoriteListResponse;
import com.jagt1806.mangareader.model.Favorites;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.FavoritesRepository;
import com.jagt1806.mangareader.repository.UsersRepository;
import com.jagt1806.mangareader.service.FavoriteService;
import com.jagt1806.mangareader.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImp implements FavoriteService {
    private final FavoritesRepository favoritesRepository;
    private final UsersRepository usersRepository;
    private final MessageUtil messageUtil;

    @Override
    public FavoriteListResponse getAllFavorites(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoriteDTO> favorites = favoritesRepository.findAll(pageable).getContent()
                .stream()
                .map(this::toFavoriteDTO)
                .toList();

        return new FavoriteListResponse(favorites, offset, limit, favoritesRepository.count());
    }

    @Override
    public FavoriteListResponse getFavoriteByUserId(Long userId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoriteDTO> favorites = favoritesRepository.findAllByUserId_Id(userId, pageable)
                .orElseThrow(() -> new FavoriteNotFoundException(messageUtil.getMessage("favorites.user.not.found")))
                .stream()
                .map(this::toFavoriteDTO)
                .toList();
        return new FavoriteListResponse(favorites, offset, limit, favoritesRepository.countFavoritesByUserId_Id(userId));
    }

    @Override
    public void addFavorite(Long userId, FavoriteRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(null));

        Favorites favorite = new Favorites(null, request.getIdManga(), user, request.getNameManga(), request.getUrlImage());

        try {
            favoritesRepository.save(favorite);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("unique_user_manga"))
                throw new UniqueException("user.unique");
            throw e;
        }
    }

    @Override
    public void deleteFavoriteByIds(Long userId, String mangaId) {
        favoritesRepository.deleteByUserId_IdAndMangaId(userId, mangaId);
    }

    @Override
    public Boolean existsFavorite(Long userId, String mangaId) {
        return favoritesRepository.existsByUserId_IdAndMangaId(userId, mangaId);
    }

    private FavoriteDTO toFavoriteDTO(Favorites favorites) {
        return new FavoriteDTO(
                favorites.getId(),
                favorites.getMangaId(),
                favorites.getUserId().getId(),
                favorites.getNameManga(),
                favorites.getUrlImage()
        );
    }
}
