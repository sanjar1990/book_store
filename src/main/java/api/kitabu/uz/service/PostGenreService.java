package api.kitabu.uz.service;

import api.kitabu.uz.dto.post.PostGenreDTO;
import api.kitabu.uz.entity.PostGenreEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.repository.PostGenreRepository;
import api.kitabu.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class PostGenreService {
     @Autowired
    private PostGenreRepository postGenreRepository;

     public void create(String postId, List<String> genres){
         genres.forEach(genreId->{
             PostGenreEntity postGenreEntity = new PostGenreEntity();
             postGenreEntity.setPostId(postId);
             postGenreEntity.setGenreId(genreId);
             postGenreRepository.save(postGenreEntity);
         });
     }
    public void merge(String postId, List<String> genres) {
        List<PostGenreEntity> oldGenreList = postGenreRepository.findAllByPostIdAndVisible(postId,true);
        // create new genres
        for (String genreId : genres) {
            if (!containsGenre(oldGenreList, genreId)) {
                create(postId, genreId);
            }
        }
        // remove genre
        for (PostGenreEntity entity : oldGenreList) {
            if (!genres.contains(entity.getGenreId())) {
                postGenreRepository.deleteGenresById(false, LocalDateTime.now(), SpringSecurityUtil.getCurrentUserId(),entity.getId());
            }
        }
    }

    public void create(String postId, String genreId) {
        PostGenreEntity postGenreEntity = new PostGenreEntity();
        postGenreEntity.setPostId(postId);
        postGenreEntity.setGenreId(genreId);
        postGenreRepository.save(postGenreEntity);

    }

   /* public void createForNotFullyReg(String profileId, ProfileRole role) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setRole(role);
        profileRoleEntity.setVisible(false);
        profileRoleRepository.save(profileRoleEntity);
    }



    public void updateVisible(String postId, Boolean visible) {
        postGenreRepository.updateVisible(visible, postId);
    }

    */


    private boolean containsGenre(List<PostGenreEntity> oldPostGenreList, String genreId) {
        for (PostGenreEntity postGenre  : oldPostGenreList) {
            if (postGenre.getGenreId().equals(genreId)) {
                return true;
            }
        }
        return false;
    }


    public void deleteAllByPostId(String postId){
        postGenreRepository.deleteGenreByPost(false,LocalDateTime.now(),SpringSecurityUtil.getCurrentUserId(),postId);

    }


   /* public List<String> getGenresByPostId(String postId) {
        List<PostGenreEntity> allEntity = postGenreRepository.findAllByPostIdAndVisible(postId, true);
        List<String> genres = new LinkedList<>();
        allEntity.forEach(entity->{genres.add(entity.getGenreId());} );
        return genres;
    }

    */
    public List<GenreLangMapper> getAllGenrePostsByPostIdAndLanguage(String postId, AppLanguage language) {
        return postGenreRepository.getPostGenreByLang(language.name(), postId, true);
    }

    public List<PostGenreDTO> getAllGenrePostsByPostId(String postId){
        List<PostGenreEntity> allByPostIdAndVisible = postGenreRepository.findAllByPostIdAndVisible(postId, true);
        List<PostGenreDTO> dtoList = new LinkedList<>();
        allByPostIdAndVisible.forEach(entity->{
            PostGenreDTO dto = new PostGenreDTO();
            dto.setId(entity.getId());
            dto.setPostId(entity.getPostId());
            dto.setGenreId(entity.getGenreId());
            dto.setCreatedDate(entity.getCreatedDate());
            dtoList.add(dto);
        });
        return dtoList;
    }

    /*
     String id,
            String titleLang,
            Integer orderNumber,
            LocalDateTime createdDate
     */
}
