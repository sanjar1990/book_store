package api.kitabu.uz.telegramBot.dto;

import api.kitabu.uz.entity.PostEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PostFullData {
    List<PostEntity> postEntityList = new ArrayList<>();
    Map<String,List<String>> images = new HashMap<>();
}
