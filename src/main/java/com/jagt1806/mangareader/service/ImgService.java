package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.model.Img;
import com.jagt1806.mangareader.http.request.img.ImgRequest;
import com.jagt1806.mangareader.http.response.img.ImgListResponse;

public interface ImgService {
    ImgListResponse getAllImg(int offset, int limit);

    Img getById(Long id);

    void createImg(ImgRequest request);

    void updateImg(Long id, ImgRequest request);

    void deleteImg(Long id);
}
