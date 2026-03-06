-- =========================================
-- FUNDING FINAL SCHEMA (NO SAMPLE DATA)
-- DROP DB -> CREATE DB -> CREATE TABLES
-- =========================================

DROP DATABASE IF EXISTS funding;

CREATE DATABASE funding
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE funding;

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 1) USERS
-- =========================
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  nickname VARCHAR(100) NOT NULL,
  password VARCHAR(255),
  role VARCHAR(20) NOT NULL,                  -- USER / ADMIN
  status VARCHAR(20) NOT NULL,                -- ACTIVE / SUSPENDED / DELETED
  provider VARCHAR(20) NOT NULL,              -- LOCAL / KAKAO / NAVER / GUEST
  provider_id VARCHAR(255),
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  email_verified_at DATETIME,
  profile_image VARCHAR(500),
  last_login_at DATETIME,
  suspended_reason VARCHAR(255),
  deleted_reason VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uq_users_email (email),
  UNIQUE KEY uq_users_nickname (nickname),
  KEY idx_users_email (email)
) ENGINE=InnoDB;

-- =========================
-- 2) PASSWORD RESET TOKENS
-- =========================
CREATE TABLE password_reset_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(255) NOT NULL,
  expired_at DATETIME NOT NULL,
  used_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uq_prt_token (token),
  KEY idx_prt_user_id (user_id),

  CONSTRAINT fk_prt_user
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 3) CATEGORIES
-- =========================
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uq_categories_name (name)
) ENGINE=InnoDB;

-- =========================
-- 4) PROJECTS
-- =========================
CREATE TABLE projects (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT,
  goal_amount BIGINT NOT NULL,
  current_amount BIGINT NOT NULL DEFAULT 0,
  status VARCHAR(30) NOT NULL,                -- DRAFT / REVIEW_REQUESTED / FUNDING / ...
  start_at DATETIME,
  deadline DATETIME NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME,

  KEY idx_projects_title (title),
  KEY idx_projects_category (category_id),
  KEY idx_projects_deadline (deadline),

  CONSTRAINT fk_projects_user
    FOREIGN KEY (user_id) REFERENCES users(id),

  CONSTRAINT fk_projects_category
    FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;

-- =========================
-- 5) PROJECT REVIEWS
-- =========================
CREATE TABLE project_reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,
  admin_id BIGINT,
  status VARCHAR(20) NOT NULL,                -- PENDING / APPROVED / REJECTED
  comment TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  processed_at DATETIME,

  KEY idx_pr_project_id (project_id),
  KEY idx_pr_requester_id (requester_id),
  KEY idx_pr_admin_id (admin_id),

  CONSTRAINT fk_pr_project
    FOREIGN KEY (project_id) REFERENCES projects(id),

  CONSTRAINT fk_pr_requester
    FOREIGN KEY (requester_id) REFERENCES users(id),

  CONSTRAINT fk_pr_admin
    FOREIGN KEY (admin_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 6) PROJECT STATUS LOGS
-- =========================
CREATE TABLE project_status_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  before_status VARCHAR(30) NOT NULL,
  after_status VARCHAR(30) NOT NULL,
  changed_by VARCHAR(20) NOT NULL,            -- USER / ADMIN
  changed_by_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  KEY idx_psl_project_id (project_id),
  KEY idx_psl_changed_by_id (changed_by_id),

  CONSTRAINT fk_psl_project
    FOREIGN KEY (project_id) REFERENCES projects(id),

  CONSTRAINT fk_psl_user
    FOREIGN KEY (changed_by_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 7) DONATIONS
-- =========================
CREATE TABLE donations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  project_id BIGINT NOT NULL,
  amount BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,                -- PENDING / SUCCESS / FAILED / CANCEL / REFUND
  cancel_deadline DATETIME,
  refunded_at DATETIME,
  version BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  KEY idx_donations_project_id (project_id),
  KEY idx_donations_user_id (user_id),

  CONSTRAINT fk_donations_user
    FOREIGN KEY (user_id) REFERENCES users(id),

  CONSTRAINT fk_donations_project
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB;

-- =========================
-- 8) LIKES (composite PK)
-- =========================
CREATE TABLE likes (
  user_id BIGINT NOT NULL,
  project_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (user_id, project_id),

  CONSTRAINT fk_likes_user
    FOREIGN KEY (user_id) REFERENCES users(id),

  CONSTRAINT fk_likes_project
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB;

-- =========================
-- 9) FOLLOWS (composite PK)
-- =========================
CREATE TABLE follows (
  follower_id BIGINT NOT NULL,
  following_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (follower_id, following_id),

  CONSTRAINT fk_follows_follower
    FOREIGN KEY (follower_id) REFERENCES users(id),

  CONSTRAINT fk_follows_following
    FOREIGN KEY (following_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 10) TAGS
-- =========================
CREATE TABLE tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  normalized_name VARCHAR(100) NOT NULL,      -- lowercase/정규화 값 저장 추천
  UNIQUE KEY uq_tags_normalized (normalized_name)
) ENGINE=InnoDB;

-- =========================
-- 11) PROJECT_TAGS (composite PK)
-- =========================
CREATE TABLE project_tags (
  project_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,

  PRIMARY KEY (project_id, tag_id),

  CONSTRAINT fk_pt_project
    FOREIGN KEY (project_id) REFERENCES projects(id),

  CONSTRAINT fk_pt_tag
    FOREIGN KEY (tag_id) REFERENCES tags(id)
) ENGINE=InnoDB;

-- =========================
-- 12) PROJECT IMAGES
-- =========================
CREATE TABLE project_images (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE,

  KEY idx_pi_project_id (project_id),

  CONSTRAINT fk_pi_project
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB;

-- =========================
-- 13) PROJECT FILES
-- =========================
CREATE TABLE project_files (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  file_url VARCHAR(500) NOT NULL,
  file_size BIGINT NOT NULL,                  -- 20MB 제한은 앱 로직/검증에서 처리

  KEY idx_pf_project_id (project_id),

  CONSTRAINT fk_pf_project
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB;

-- =========================
-- 14) ADMIN ACTION LOGS
-- =========================
CREATE TABLE admin_action_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,           -- PROJECT / DONATION / USER
  target_id BIGINT NOT NULL,
  before_status VARCHAR(30),
  after_status VARCHAR(30),
  reason VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  KEY idx_aal_admin_id (admin_id),

  CONSTRAINT fk_aal_admin
    FOREIGN KEY (admin_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 15) PROJECT DELETE REQUESTS
-- =========================
CREATE TABLE project_delete_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,                -- PENDING / APPROVED / REJECTED
  admin_id BIGINT,
  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  approved_at DATETIME,

  KEY idx_pdr_project_id (project_id),
  KEY idx_pdr_requester_id (requester_id),
  KEY idx_pdr_admin_id (admin_id),

  CONSTRAINT fk_pdr_project
    FOREIGN KEY (project_id) REFERENCES projects(id),

  CONSTRAINT fk_pdr_requester
    FOREIGN KEY (requester_id) REFERENCES users(id),

  CONSTRAINT fk_pdr_admin
    FOREIGN KEY (admin_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- =========================
-- 16) DAILY STATISTICS (PK=DATE)
-- =========================
CREATE TABLE daily_statistics (
  stat_date DATE PRIMARY KEY,
  total_donation_amount BIGINT NOT NULL DEFAULT 0,
  project_count INT NOT NULL DEFAULT 0,
  success_project_count INT NOT NULL DEFAULT 0,
  user_count INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- =========================
-- 17) PROJECT DAILY VIEWS
-- =========================
CREATE TABLE project_daily_views (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  view_date DATE NOT NULL,
  view_count INT NOT NULL DEFAULT 0,

  UNIQUE KEY uq_pdv_project_date (project_id, view_date),

  CONSTRAINT fk_pdv_project
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB;

-- =========================
-- 18) EMAIL BLACKLISTS
-- =========================
CREATE TABLE email_blacklists (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  reason VARCHAR(255),
  banned_by BIGINT NOT NULL,                  -- 처리 관리자
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uq_eb_email (email),
  KEY idx_eb_email (email),

  CONSTRAINT fk_eb_admin
    FOREIGN KEY (banned_by) REFERENCES users(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;