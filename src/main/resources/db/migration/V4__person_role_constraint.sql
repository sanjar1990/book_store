alter table profile_role drop constraint if exists profile_role_role_check;
alter table post drop constraint if exists post_book_language_check;
alter table post drop constraint if exists post_book_print_type_check;
alter table post drop constraint if exists post_condition_type_check;
alter table post drop constraint if exists post_exchange_type_check;
alter table post drop constraint if exists post_status_check;
alter table profile drop constraint if exists profile_app_language_check;
