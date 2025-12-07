
INSERT INTO profile(id,name,surname,phone,password,status,created_date,visible)
values ('f69801ad-5679-4a63-b236-ab8d15c7c3a0','Nadm','Sadm','975187161','827ccb0eea8a706c4c34a16891f84e7b','ACTIVE',now(),true);

INSERT INTO profile_role(id,  role ,profile_id,created_date, visible)
values ('f69801ad-5679-4a63-b236-ab8d15c7c3a2','ROLE_ADMIN','f69801ad-5679-4a63-b236-ab8d15c7c3a0', now(), true) on conflict  (id) do nothing;
