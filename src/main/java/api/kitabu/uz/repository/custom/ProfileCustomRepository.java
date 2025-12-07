package api.kitabu.uz.repository.custom;

import api.kitabu.uz.dto.filter.FilterResultDTO;
import api.kitabu.uz.dto.filter.post.FilterRequest;
import api.kitabu.uz.dto.filter.post.FilterResponse;
import api.kitabu.uz.dto.filter.user.UserFilterRequest;
import api.kitabu.uz.dto.filter.user.UserFilterResponse;
import api.kitabu.uz.enums.ExchangeType;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.service.AttachService;
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
 * @author Raufov Ma`ruf Dilshod o`g`li
 */

@Repository
public class ProfileCustomRepository {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AttachService attachService;

    public FilterResultDTO<UserFilterResponse> filter(UserFilterRequest filterRequestDTO,
                                                      int page, int size) {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        builder.append(" where p.visible is true ");

        if (filterRequestDTO.nameOrSurname() != null && !filterRequestDTO.nameOrSurname().isEmpty()) {
            builder.append(" and lower(p.name) like :nameOrSurname or lower(p.surname) like :nameOrSurname ");
            params.put("nameOrSurname", "%" + filterRequestDTO.nameOrSurname().toLowerCase().concat("%"));
        }
        if (filterRequestDTO.phone() != null && !filterRequestDTO.phone().isEmpty()) {
            builder.append(" and lower(p.phone) like :phone ");
            params.put("phone", "%" + filterRequestDTO.phone().toLowerCase().concat("%"));
        }
        if (filterRequestDTO.role() != null) {
            builder.append(" and pr.role = :role ");
            params.put("role", filterRequestDTO.role().name());
        }

        String selectQueryBuilder = "select distinct(p.id) , p.photo_id , p.name , p.surname ,  " +
                                    "p.phone , p.status , p.created_date from profile p " +
                                    "inner join profile_role pr on p.id = pr.profile_id " +
                                    builder +
                                    " order by p.created_date desc " +
                                    " limit " + size + " offset " + (page * size) + ";";

        String countQueryBuilder = "SELECT count (distinct(p.id)) FROM profile p " +
                                   " inner join profile_role pr on p.id = pr.profile_id " +
                                   builder;


        System.out.println("Select Query: " + selectQueryBuilder);
        System.out.println("Count Query: " + countQueryBuilder);
        System.out.println("Params: " + params);

        Query selectQuery = entityManager.createNativeQuery(selectQueryBuilder);
        Query countQuery = entityManager.createNativeQuery(countQueryBuilder);

        for (Map.Entry<String, Object> param : params.entrySet()) {
            selectQuery.setParameter(param.getKey(), param.getValue());
            countQuery.setParameter(param.getKey(), param.getValue());
        }

        List<Object[]> response = selectQuery.getResultList();
        Long totalCount = (Long) countQuery.getSingleResult();

        List<UserFilterResponse> mapperList = new LinkedList<>();
        for (Object[] object : response) {
            UserFilterResponse dto = UserFilterResponse.builder()
                    .id(MapperUtil.getStringValue(object[0]))
                    .imageUrl(MapperUtil.getStringValue(object[1]))
                    .name(MapperUtil.getStringValue(object[2]))
                    .surname(MapperUtil.getStringValue(object[3]))
                    .phone(MapperUtil.getStringValue(object[4]))
                    .status(MapperUtil.getStringValue(object[5]))
                    .createdDate(MapperUtil.getLocalDateValue(object[6]))
                    .build();
            mapperList.add(dto);
        }
        return new FilterResultDTO<>(mapperList, totalCount);
    }

}
