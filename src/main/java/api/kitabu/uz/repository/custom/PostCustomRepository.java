package api.kitabu.uz.repository.custom;

import api.kitabu.uz.dto.filter.post.FilterRequest;
import api.kitabu.uz.dto.filter.post.FilterResponse;
import api.kitabu.uz.dto.filter.FilterResultDTO;
import api.kitabu.uz.enums.BookPrintType;
import api.kitabu.uz.enums.ExchangeType;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.service.AttachService;
import api.kitabu.uz.util.MapperUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
/*
 * @author Raufov Ma`ruf Dilshod o`g`li
 */

@Repository
public class PostCustomRepository {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AttachService attachService;

    public FilterResultDTO<FilterResponse> filter(FilterRequest filterRequestDTO,
                                                  Lang lang, int page, int size, boolean isAdmin) {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        builder.append(" where p.visible is true ");
        if (!isAdmin) {
            builder.append(" and status = 'ACTIVE' ");
        }
        if (filterRequestDTO.title() != null && !filterRequestDTO.title().isEmpty()) {
            builder.append(" and lower(p.title) like :title or lower(p.author_name) like :title AND p.visible is true " );
            params.put("title", "%" + filterRequestDTO.title().toLowerCase().concat("%"));
        }
        if (filterRequestDTO.regionId() != null) {
            builder.append(" and p.region_id = :regionId ");
            params.put("regionId", filterRequestDTO.regionId());
        }
        if (filterRequestDTO.exchangeType() != null) {
            builder.append(" and p.exchange_type = :exchangeType ");
            params.put("exchangeType", filterRequestDTO.exchangeType().name());
        }
        if (filterRequestDTO.bookLanguage() != null) {
            builder.append(" and p.book_language = :bookLanguage ");
            params.put("bookLanguage", filterRequestDTO.bookLanguage().name());
        }
        if (filterRequestDTO.bookPrintType() != null) {
            builder.append(" and p.book_print_type = :bookPrintType ");
            params.put("bookPrintType", filterRequestDTO.bookPrintType().name());
        }
        if (filterRequestDTO.conditionType() != null) {
            builder.append(" and p.condition_type = :conditionType ");
            params.put("conditionType", filterRequestDTO.conditionType().name());
        }
        if (filterRequestDTO.genreId() != null) {
            builder.append(" and g.id = :genreId ");
            params.put("genreId", filterRequestDTO.genreId());
        }
        //FOR ADMIN FILTER USED USER_ID
        if (isAdmin && filterRequestDTO.profileId() != null) {
            builder.append(" and p.profile_id = :profileId ");
            params.put("profileId", filterRequestDTO.profileId());
        }
        if (isAdmin && filterRequestDTO.status() != null) {
            builder.append(" and p.status = :status ");
            params.put("status", filterRequestDTO.status().name());
        } else if (isAdmin) {
            builder.append(" and status in ('ACTIVE','IN_REVIEW') ");
        }

        String selectQueryBuilder = " select distinct(p.id), p.exchange_type, p.created_date, " +
                                    " CASE :lang WHEN 'uz' THEN r.name_uz WHEN 'en' THEN r.name_en ELSE r.name_ru END AS region_name, " +
                                    "(SELECT string_agg(\n" +
                                    "                CASE :lang WHEN 'uz' THEN g.title_uz\n" +
                                    "                           WHEN 'en' THEN g.title_en\n" +
                                    "                           ELSE g.title_ru END, ', ')\n" +
                                    "        FROM post_genre pg " +
                                    "        INNER JOIN genre g ON g.id = pg.genre_id\n" +
                                    "        WHERE pg.post_id = p.id) AS genre_names, p.author_name ," +
                                    "(select pa.attach_id from post_attach pa where pa.post_id = p.id order by created_date limit 1) as attachId , p.title ,p.status , p.book_print_type ,p.price " +
                                    "FROM post p " +
                                    "INNER JOIN region r ON p.region_id = r.id " +
                                    "INNER JOIN post_genre pg ON p.id = pg.post_id " +  // post_genre jadvalini qo'shish
                                    "INNER JOIN genre g ON g.id = pg.genre_id " +
                                    builder +
                                    " order by p.created_date desc " +
                                    " limit " + size + " offset " + (page * size) + ";";

        String countQueryBuilder = "SELECT count(distinct p.id) FROM post p " +
                                   " INNER JOIN post_genre pg ON p.id = pg.post_id " +
                                   "INNER JOIN genre g ON g.id = pg.genre_id " + builder;
        params.put("lang", lang.name());

        Query selectQuery = entityManager.createNativeQuery(selectQueryBuilder);
        Query countQuery = entityManager.createNativeQuery(countQueryBuilder);

        for (Map.Entry<String, Object> param : params.entrySet()) {
            selectQuery.setParameter(param.getKey(), param.getValue());
            if (!param.getKey().equals("lang")) {
                countQuery.setParameter(param.getKey(), param.getValue());
            }
        }

        List<Object[]> apartmentList = selectQuery.getResultList();
        Long totalCount = (Long) countQuery.getSingleResult();

        List<FilterResponse> mapperList = new LinkedList<>();
        for (Object[] object : apartmentList) {
            FilterResponse dto = new FilterResponse(
                    MapperUtil.getStringValue(object[0]),
                    ExchangeType.valueOf(MapperUtil.getStringValue(object[1].toString())),
                    MapperUtil.getLocalDateValue(object[2]),
                    MapperUtil.getStringValue(object[3]),
                    MapperUtil.getStringValue(object[4]),
                    MapperUtil.getStringValue(object[5]),
                    attachService.toDTOFilter(MapperUtil.getStringValue(object[6])),
                    MapperUtil.getStringValue(object[7]),
                    GeneralStatus.valueOf(MapperUtil.getStringValue(object[8])),
                    BookPrintType.valueOf(MapperUtil.getStringValue(object[9])),
                    MapperUtil.getDoubleValue(object[10]));
            mapperList.add(dto);
        }
        return new FilterResultDTO<>(mapperList, totalCount);
    }

}
