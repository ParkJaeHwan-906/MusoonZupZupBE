DROP DATABASE IF EXISTS `ssafy_home`;

CREATE DATABASE `ssafy_home`;
USE `ssafy_home`;

-- 사용자 관련 테이블 -- 
-- 권한 관리 --
CREATE TABLE `roles` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);
-- 사용자 기본 정보 --
CREATE TABLE `members` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(15) NOT NULL UNIQUE,
    `gender` BIGINT NOT NULL,
    `birth_date` DATE NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);
-- 사용자 계정 --
CREATE TABLE `members_account` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_idx` BIGINT NOT NULL,
    `id` VARCHAR(100) NOT NULL UNIQUE,
    `pw` VARCHAR(100) NOT NULL,
    `role` BIGINT DEFAULT 1,
    `ban` BIGINT DEFAULT 0,
    `exit` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_idx`) REFERENCES members(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`role`) REFERENCES roles(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);
-- 사용자 등록 장소 --
CREATE TABLE `member_regist_place` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY, 
    `member_account_idx` BIGINT NOT NULL,
    `alias` VARCHAR(100),
    `address` VARCHAR(255) NOT NULL,
    `detail` VARCHAR(255),
    `geo` POINT NOT NULL,
    `delete` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
); 

-- 커뮤니티 관련 테이블 -- 
-- 커뮤니티 --
CREATE TABLE `community` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `views` BIGINT DEFAULT 0,
    `blind` BIGINT DEFAULT 0,
    `delete` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);
-- 커뮤니티 좋아요/싫어요 --
CREATE TABLE `community_like_dislike` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `community_idx` BIGINT NOT NULL,
    `like` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`community_idx`) REFERENCES `community`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);
-- 커뮤니티 댓글 관리 --
CREATE TABLE `community_comments` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `community_idx` BIGINT NOT NULL,
    `comment` TEXT NOT NULL,
    `blind` BIGINT DEFAULT 0,
    `delete` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`community_idx`) REFERENCES `community`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 공지사항 관련 테이블 --
-- 공지사항 게시글 ---
CREATE TABLE `notice` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `views` BIGINT DEFAULT 0,
    `blind` BIGINT DEFAULT 0,
    `delete` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);
-- 공지사항 댓글 --
CREATE TABLE `notice_comments` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `notice_idx` BIGINT NOT NULL,
    `comment` TEXT NOT NULL,
    `blind` BIGINT DEFAULT 0,
    `delete` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`notice_idx`) REFERENCES `notice`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 청약 관련 테이블 --
-- 주택 코드 --
CREATE TABLE `house_codes` (
	`idx` BIGINT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
	`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);
-- 시/도 코드 --
CREATE TABLE `sido_code` (
	`idx` BIGINT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
	`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);
-- 청약 정보 --
CREATE TABLE `apply_home` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `house_manage_no` BIGINT NOT NULL UNIQUE, 
    `pblanc_no` BIGINT NOT NULL UNIQUE,
    `views` BIGINT DEFAULT 0,
    `blind` BIGINT DEFAULT 0,
    `house_name` VARCHAR(255) NOT NULL,
    `house_code` BIGINT NOT NULL,
    `zip_code` BIGINT NOT NULL,
    `sido_code` BIGINT NOT NULL,
    `house_address` VARCHAR(255) NOT NULL,
	`geo` POINT NULL,
    `suply_count` BIGINT,
    `suply_price` BIGINT,
    `pblanc_date` DATETIME NOT NULL,
    `apply_start_date` DATETIME NOT NULL,
    `apply_end_date` DATETIME NOT NULL,
    `apply_announce_date` DATETIME NOT NULL,
    `business_entity` VARCHAR(255) NOT NULL,
    `business_tel` VARCHAR(100),
    `applyhome_url` VARCHAR(255),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`house_code`) REFERENCES `house_codes`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`sido_code`) REFERENCES `sido_code`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 청약 찜 --
CREATE TABLE `apply_like` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_account_idx` BIGINT NOT NULL,
    `apply_idx` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`apply_idx`) REFERENCES `apply_home`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 청약 분석 (일반 사용자) --
CREATE TABLE `apply_home_comments` (
	`idx` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `apply_idx` BIGINT NOT NULL UNIQUE,
    `comment` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`apply_idx`) REFERENCES `apply_home`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);
-- 청약 분석 (멤버십) --
CREATE TABLE `apply_home_comments_ms`(
	`idx` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `apply_idx` BIGINT NOT NULL,
    `member_account_idx` BIGINT NOT NULL,
    `request` TEXT NOT NULL,
    `comment` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`apply_idx`) REFERENCES `apply_home`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(`member_account_idx`) REFERENCES `members_account`(`idx`) ON DELETE CASCADE ON UPDATE CASCADE
);