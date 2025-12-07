package api.kitabu.uz.service;

import api.kitabu.uz.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostScheduler {
    private final PostService postService;

    @Scheduled(cron = "0 0 0 * * *")
    public void blockInactivePosts() {
        log.info("Scheduled  is working");
        List<PostEntity> posts = postService.getInactivePosts();
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (PostEntity post : posts) {
            long diff = ChronoUnit.DAYS.between(post.getCreatedDate(), currentDateTime);
            if (diff >= 90) {
                postService.blockPost(post);
            }
        }
    }
  /* @Scheduled(fixedRate = 60000) // Har bir minutda ishlaydi
   public void blockInactivePosts() {
       List<PostEntity> posts = postService.getAllPosts();
       LocalDateTime currentDateTime = LocalDateTime.now();
       for (PostEntity post : posts) {
           long diffInMinutes = ChronoUnit.MINUTES.between(post.getCreatedDate(), currentDateTime);
           if (diffInMinutes >= 1) { // Agar bir minutdan ko'p o'tgan bo'lsa
               postService.blockPost(post); // Bloklash metodini chaqirish
           }
       }
   }*/
}