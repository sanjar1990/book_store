package api.kitabu.uz.repository.custom;

import api.kitabu.uz.dto.filter.*;
import api.kitabu.uz.dto.filter.comment.CommentFilterRequest;
import api.kitabu.uz.dto.filter.comment.CommentFilterResponse;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.util.MapperUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/*
 * @author Raufov Ma`ruf Dilshod o`g`li*/

@Repository
public class CommentCustomRepository {
    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<CommentFilterResponse> filter(CommentFilterRequest commentFilterRequest,
                                                         Lang lang, int page, int size, boolean isAdmin) {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        builder.append(" where c.visible is true ");

        if (commentFilterRequest.commentId() != null){
            builder.append(" and c.id = :commentId ");
            params.put("commentId",commentFilterRequest.commentId());
        }
        if (commentFilterRequest.postId() != null){
            builder.append(" and c.post_id = :postId ");
            params.put("postId",commentFilterRequest.postId());
        }
        if (isAdmin && commentFilterRequest.profileId() != null) {
            builder.append(" and c.profile_id = :profileId ");
            params.put("profileId", commentFilterRequest.profileId());
        }
        String selectQueryBuilder ="select c.content , " +
                                   "c.post_id , c.profile_id ," +
                                   " c.created_date  from comment c "+
                                    builder +
                                    " order by c.created_date desc " +
                                    " limit " + size + " offset " + (page * size) + ";";

        String countQueryBuilder = "select count(*) from comment c " + builder;

        Query selectQuery = entityManager.createNativeQuery(selectQueryBuilder);
        Query countQuery = entityManager.createNativeQuery(countQueryBuilder);

        for (Map.Entry<String, Object> param : params.entrySet()) {
            selectQuery.setParameter(param.getKey(), param.getValue());
            countQuery.setParameter(param.getKey(), param.getValue());

        }
        List<Object[]> apartmentList = selectQuery.getResultList();
        Long totalCount = (Long) countQuery.getSingleResult();

        List<CommentFilterResponse> mapperList = new LinkedList<>();
        for (Object[] object : apartmentList) {
            CommentFilterResponse dto = new CommentFilterResponse(
                    MapperUtil.getStringValue(object[0]),
                    MapperUtil.getStringValue(object[1]),
                    MapperUtil.getStringValue(object[2]),
                    MapperUtil.getLocalDateValue(object[3])
            );
            mapperList.add(dto);
        }
        return new FilterResultDTO<>(mapperList, totalCount);
    }

}
