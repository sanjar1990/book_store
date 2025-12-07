package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.entity.AttachEntity;
import api.kitabu.uz.entity.PostAttachEntity;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.AttachRepository;
import api.kitabu.uz.repository.PostAttachRepository;
import api.kitabu.uz.util.SpringSecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
/*
 * @author Raufov Ma`ruf
 * */

@Service
@Transactional
public class PostAttachService {
    @Autowired
    private PostAttachRepository postAttachRepository;
    private final AttachService attachService;

    @Autowired
    public PostAttachService(@Lazy AttachService attachService) {
        this.attachService = attachService;
    }
    public void create(String postId, List<String> attachIdList) {
        for (String attachId : attachIdList) {
            var entity = new PostAttachEntity();
            entity.setAttachId(attachId);
            entity.setPostId(postId);
            postAttachRepository.save(entity);
        }
    }

    public void merge(String postId, List<String> newList) {
        List<String> oldList = postAttachRepository.findAllByPostId(postId);
        // create
        for (String item : newList) {
            if (!oldList.contains(item)) {
                create(postId, item);
            }
        }
        // remove
        for (String item : oldList) {
            if (!newList.contains(item)) {
                postAttachRepository.delete(postId, item);
            }
        }
    }


    public void create(String postId, String attachId) {
        var entity = new PostAttachEntity();
        entity.setAttachId(attachId);
        entity.setPostId(postId);
        postAttachRepository.save(entity);
    }

    public List<FileResponse> getPostAttachList(String postId) {
        List<String> attachIdList = postAttachRepository.finAllByPostId(postId);
        List<FileResponse> attachList = new LinkedList<>();
        for (String attachId : attachIdList) {
            attachList.add(attachService.toDTO(attachId));
        }
        return attachList;
    }
    public List<String> getPostImageIdList(String postId) {
        return  postAttachRepository.finAllByPostId(postId);
    }
    public FileResponse getPostAttachLimitOne(String postId) {
      var fileResponse = postAttachRepository.finAllByPostIdLimit(postId);
       if (fileResponse == null){
           throw new APIException("getPostAttachLimitOne error");
       }
       return attachService.toDTO(fileResponse);
    }

    public void deleteAllAttachesByPostId(String postId){
        postAttachRepository.deleteAttachesByPost(false, LocalDateTime.now(), SpringSecurityUtil.getCurrentUserId(),postId);
    }

    public void deletePostAttachByAttachId(String attachId){
        postAttachRepository.deleteAttachesByAttachId(attachId);
    }


}
