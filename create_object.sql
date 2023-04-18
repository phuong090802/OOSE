create database create_object;
use create_object;

drop table if  exists role;
create table role(
    role_id int auto_increment,
    role_name varchar(10) not null,
    constraint PK_ROLE primary key (role_id)
);

drop table if  exists users;
create table users(
    user_id int auto_increment,
    first_name varchar(50) not null,
    last_name  varchar(50) not null,
    user_name varchar(50) not null,
    password varchar(100) not null,
    date_of_birth date,
    phone varchar(10),
    email varchar(100),
    stautus bit default true,
    role_id int not null,
    constraint PK_USER primary key (user_id),
    constraint FK_USER_ROLE foreign key (role_id) references role(role_id)
);

drop table if exists genres;
create table genres(
    genre_id int auto_increment,
    genre_name varchar(50) not null,
    constraint PK_GENRE primary key (genre_id)
);

drop table if exists lightnovels;
create table lightnovels(
    light_novel_id int auto_increment,
    light_novel_name varchar(50) not null,
    genre_id int not null,
    author_name varchar(100) not null,
    user_id int,
    constraint PK_LIGHT_NOVEL primary key (light_novel_id),
    constraint FK_LIGHT_NOVEL_GENRE foreign key (genre_id) references genres(genre_id),
    constraint FK_LIGHT_NOVEL_USER foreign key (user_id) references users(user_id)
);

drop table if exists chapters;
create table chapters(
    chapter_id int auto_increment,
    chater_number int,
    chapter_content text not null,
    light_novel_id int not null,
    constraint PK_CHAPTER primary key (chapter_id),
    constraint FK_CHAPTER_LIGHT_NOVEL foreign key (light_novel_id) references lightnovels(light_novel_id)
);

drop table if exists comments;
create table comments(
    comment_id int auto_increment,
    user_id int not null,
    chapter_id int not null,
    time_comment datetime not null,
    content_comment text not null,
    constraint PK_COMMENT primary key (comment_id),
    constraint FK_COMMENT_USER foreign key (user_id) references users(user_id),
    constraint FK_COMMENT_CHAPTER foreign key (chapter_id) references chapters(chapter_id)
);

drop table if exists new_contents;
create table new_contents(
    new_content_id int auto_increment,
    chapter_id int not null,
    constraint PK_NEWCONTENT primary key (new_content_id),
    constraint FK_NEWCONTENT_CHAPTER foreign key (chapter_id) references chapters(chapter_id)
);

drop table if exists reported_contents;
create table reported_contents(
    reported_content_id int auto_increment,
    chapter_id int not null,
    constraint PK_REPORTED_NEWCONTENT primary key (reported_content_id),
    constraint FK_REPORTED_ONTENT_CHAPTER foreign key (chapter_id) references chapters(chapter_id)
);
-- Bảng trả lời bình luận 
drop table if exists reply_comments;
create table reply_comments(
    reply_comments_id int auto_increment,
    reply_comments_content text not null,
    time_reply_comment datetime not null,
    comment_id int not null,
    user_id int not null,
    constraint PK_REPLY_COMMENT primary key (reply_comments_id),
    constraint FK_REPLY_COMMENT_COMMENT foreign key (comment_id) references comments(comment_id),
    constraint FK_REPLY_COMMENT_USER foreign key (user_id) references users(user_id)
);


drop table if exists reported_comments;
create table reported_comments(
    reported_comment_id int auto_increment,
    comment_id int not null, 
    constraint PK_REPORTED_COMMENT_ID primary key (reported_comment_id),
    constraint FK_REPORTED_COMMNET_COMMENT foreign key (comment_id) references comments(comment_id)
);


-- Bảng tủ truyện 
drop table if exists light_novel_cabinets;
create table light_novel_cabinets(
    light_novel_cabinet_id int auto_increment,
    light_novel_id int not null, 
    constraint PK_LIGHT_NOVEL_CABINET primary key (light_novel_cabinet_id),
    constraint FK_LIGHT_NOVEL_CABINET_LIGHT_NOVEL foreign key (light_novel_id) references lightnovels(light_novel_id)
);

