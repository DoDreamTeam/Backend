package com.dodream.study.service;

import com.dodream.study.domain.QueCommentRequest;
import com.dodream.study.domain.QueCommentResponse;
import com.dodream.study.domain.QueCommentUpdateRequest;
import com.dodream.study.domain.QueCommentUpdateResponse;
import com.dodream.study.entity.QueComment;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueCommentService {

    QueCommentResponse addQueComment(Long id, User user, QueCommentRequest queCommentRequest);

    Page<QueCommentResponse> getQueCommentList(Pageable pageable, Long id, User user, boolean isSortByLikes);

    void deleteQueComment(Long commentId, User user);

    QueCommentUpdateResponse updateQueComment(Long commentId, User user, QueCommentUpdateRequest queCommentUpdateRequest);

}
