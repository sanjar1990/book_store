
create or replace function get_admin_dashboard_data()
    returns text
    language plpgsql
as
$$
declare
user_count        bigint;
    post_count        bigint;
    genre_count       bigint;
    free_count        bigint;
    sell_count        bigint;
    exchange_count    bigint;
    temporarily_count bigint;
    response          text;
begin

    -- user count
select count(*)
into user_count
from profile as p
where p.visible = true
  and p.status = 'ACTIVE';

-- post count

select count(*)
into post_count
from post as p
where p.visible = true
  and p.status = 'ACTIVE';

-- genre count
select count(*)
into genre_count
from genre as g
where g.visible = true;

-- post free count
select count(*)
into free_count
from post as p
where p.visible = true
  and p.status = 'ACTIVE'
  and p.exchange_type = 'FREE';

-- post sell count
select count(*)
into sell_count
from post as p
where p.visible = true
  and p.status = 'ACTIVE'
  and p.exchange_type = 'SELL';

-- post exchange count
select count(*)
into exchange_count
from post as p
where p.visible = true
  and p.status = 'ACTIVE'
  and p.exchange_type = 'EXCHANGE';


-- post temporarily count
select count(*)
into temporarily_count
from post as p
where p.visible = true
  and p.status = 'ACTIVE'
  and p.exchange_type = 'TEMPORARILY';

-- return result
response := json_build_object(
            'userCount', user_count,
            'genreCount', genre_count,
            'postCount', post_count,
            'freeCount', free_count,
            'sellCount', sell_count,
            'exchangeCount', exchange_count,
            'temporarilyCount', temporarily_count
                ):: TEXT;


return response;
end;
$$