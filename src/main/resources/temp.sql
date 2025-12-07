--- Get profile post list
SELECT p.profile_id                                       as            profileId,
       p.title                                            as            title,
       p.exchange_type                                    as            type,
       p.created_date                                     as            createdDate,
       (select '[' || string_agg(temp_table.body, ',') || ']'
        from (SELECT json_build_object('id', pg.genre_id,
                                       'name', CASE :lang
                                                   WHEN 'uz' THEN g.title_uz
                                                   WHEN 'en' THEN g.title_en
                                                   ELSE g.title_ru END,
                                       'orderNumber', g.order_number) :: TEXT as body
              FROM post_genre pg
                       INNER JOIN genre g ON pg.genre_id = g.id

              WHERE pg.visible = true
                and pg.post_id = p.id
              order by g.order_number asc) as temp_table) as            genreJson,
       (select pa.attach_id from post_attach pa where pa.post_id = p.id limit 1) as attachId,
                   (SELECT CASE :lang  WHEN 'uz' THEN name_uz WHEN 'en' THEN name_en ELSE name_ru END  FROM region as r where r.id = p.region_id) as regionName
FROM post p
WHERE p.visible = true and p.status = :status and p.profile_id = :profileId
order by created_date desc;
---
(SELECT string_agg(CASE 'uz' WHEN 'uz' THEN g.title_uz WHEN 'en' THEN g.title_en ELSE g.title_ru END, ', ')
 FROM post_genre pg
          INNER JOIN genre g ON g.id = pg.genre_id
 WHERE pg.post_id = 'fa308a27-a720-40e2-871a-3b006e20f1c1');


create table test_entity
(
    id           varchar(255) not null,
    created_date timestamp(6),
    deleted_date timestamp(6),
    deleted_id   varchar(255),
    visible      boolean,
    app_language smallint check (app_language between 0 and 2),
    name         varchar(255),
    password     varchar(255),
    phone        varchar(255),
    status       varchar(255) check (status in ('BLOCKED', 'ACTIVE', 'NOT_ACTIVE', 'IN_REVIEW')),
    surname      varchar(255),
    temp_phone   varchar(255),
    primary key (id)
)
