USE `blog`;

INSERT IGNORE INTO `user` (`username`, `email`, `password`, `nickname`, `avatar`, `role`) VALUES
('test', 'test@example.com', '0192023a7bbd73250516f069df18b500', 'test-user', NULL, 'USER');

INSERT INTO `category` (`name`, `sort`) VALUES
('鎶€鏈?, 1),
('鐢熸椿', 2),
('闅忕瑪', 3),
('鏁欑▼', 4);

INSERT INTO `tag` (`name`) VALUES
('Java'),
('Spring Boot'),
('Vue'),
('MySQL'),
('鍓嶇'),
('鍚庣'),
('鏁版嵁搴?),
('JavaScript'),
('CSS'),
('HTML');

INSERT INTO `article` (`title`, `summary`, `content`, `cover`, `status`, `view_count`, `category_id`, `created_at`) VALUES
('Spring Boot 鍏ラ棬鏁欑▼', '鏈枃浠嬬粛Spring Boot鐨勫熀纭€鐭ヨ瘑鍜屽揩閫熷叆闂?, '# Spring Boot 鍏ラ棬\n\n## 绠€浠媆n\nSpring Boot 鏄竴涓敤浜庣畝鍖朣pring搴旂敤寮€鍙戠殑妗嗘灦銆俓n\n## 鐗圭偣\n\n- 绾﹀畾浼樹簬閰嶇疆\n- 鍐呭祵鏈嶅姟鍣╘n- 鑷姩閰嶇疆\n\n## 蹇€熷紑濮媆n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```', NULL, 1, 100, 1, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('Vue3 缁勫悎寮?API 璇﹁В', '娣卞叆鐞嗚ВVue3鐨凜omposition API', '# Vue3 缁勫悎寮?API\n\n## setup鍑芥暟\n\n```javascript\nimport { ref, reactive } from ''vue''\n\nexport default {\n  setup() {\n    const count = ref(0)\n    return { count }\n  }\n}\n```\n\n## 鍝嶅簲寮忔暟鎹甛n\n- ref: 鐢ㄤ簬鍩烘湰绫诲瀷\n- reactive: 鐢ㄤ簬瀵硅薄绫诲瀷', NULL, 1, 85, 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
('MySQL 绱㈠紩浼樺寲鎸囧崡', 'MySQL鏁版嵁搴撶储寮曠殑鍒涘缓涓庝紭鍖栫瓥鐣?, '# MySQL 绱㈠紩浼樺寲\n\n## 绱㈠紩绫诲瀷\n\n1. 涓婚敭绱㈠紩\n2. 鍞竴绱㈠紩\n3. 鏅€氱储寮昞n4. 鍏ㄦ枃绱㈠紩\n\n## 浼樺寲寤鸿\n\n- 閬垮厤鍦ㄥ垪涓婁娇鐢ㄥ嚱鏁癨n- 浣跨敤瑕嗙洊绱㈠紩\n- 鍚堢悊浣跨敤鑱斿悎绱㈠紩', NULL, 1, 120, 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('鍓嶇鎬ц兘浼樺寲瀹炶返', '鍓嶇鎬ц兘浼樺寲鐨勫绉嶆柟娉曞拰鎶€宸?, '# 鍓嶇鎬ц兘浼樺寲\n\n## 鍔犺浇浼樺寲\n\n- 浠ｇ爜鍒嗗壊\n- 鎳掑姞杞絓n- 鍘嬬缉璧勬簮\n\n## 娓叉煋浼樺寲\n\n- 铏氭嫙鍒楄〃\n- 闃叉姈鑺傛祦\n- 鍑忓皯閲嶆帓閲嶇粯', NULL, 1, 60, 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('JavaScript 寮傛缂栫▼', 'Promise銆乤sync/await 寮傛缂栫▼璇﹁В', '# JavaScript 寮傛缂栫▼\n\n## Promise\n\n```javascript\nconst p = new Promise((resolve, reject) => {\n  resolve(''success'')\n})\n```\n\n## async/await\n\n```javascript\nasync function fetchData() {\n  const res = await fetch(''/api/data'')\n  return res.json()\n}\n```', NULL, 1, 75, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('CSS Grid 甯冨眬瀹屽叏鎸囧崡', 'CSS Grid甯冨眬浠庡叆闂ㄥ埌绮鹃€?, '# CSS Grid 甯冨眬\n\n## 鍩烘湰姒傚康\n\n- 瀹瑰櫒(container)\n- 椤圭洰(item)\n- 琛?row)\n- 鍒?column)\n\n## 甯哥敤灞炴€n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(3, 1fr);\n  gap: 20px;\n}\n```', NULL, 1, 45, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('鐢熸椿闅忕瑪锛氱▼搴忓憳鐨勬棩甯?, '璁板綍绋嬪簭鍛樻棩甯哥敓娲讳腑鐨勭偣婊?, '# 绋嬪簭鍛樼殑鏃ュ父\n\n浠婂ぉ鍙堟槸鍐欎唬鐮佺殑涓€澶┿€俓n\n鏃╀笂璧锋潵锛屽厛鍠濇澂鍜栧暋锛岀劧鍚庢墦寮€IDE寮€濮嬪伐浣溿€俓n\n## 浠婃棩浠诲姟\n\n- 淇bug\n- 浠ｇ爜review\n- 鍐欐枃妗n\n鐢熸椿灏辨槸杩欐牱锛岀畝鍗曡€屽厖瀹炪€?, NULL, 1, 30, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('鎴戠殑2024骞村害鎬荤粨', '鍥為【2024锛屽睍鏈?025', '# 2024骞村害鎬荤粨\n\n## 宸ヤ綔鏂归潰\n\n浠婂勾瀹屾垚浜嗗緢澶氶」鐩紝瀛﹀埌浜嗗緢澶氭柊鎶€鏈€俓n\n## 瀛︿範鏂归潰\n\n- 瀛︿範浜咷o璇█\n- 娣卞叆浜嗚В浜咾ubernetes\n- 鎺屾彙浜嗗井鏈嶅姟鏋舵瀯\n\n## 2025鐩爣\n\n缁х画瀛︿範锛屾寔缁繘姝ワ紒', NULL, 1, 25, 2, NOW());

INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, 1),
(1, 2),
(1, 6),
(2, 3),
(2, 5),
(2, 8),
(3, 4),
(3, 7),
(4, 5),
(4, 8),
(4, 9),
(5, 8),
(5, 5),
(6, 9),
(6, 10),
(7, 2),
(8, 2);

INSERT INTO `visit_log` (`article_id`, `ip`, `user_agent`, `created_at`) VALUES
(1, '192.168.1.1', 'Mozilla/5.0 Chrome/120.0', DATE_SUB(NOW(), INTERVAL 29 DAY)),
(1, '192.168.1.2', 'Mozilla/5.0 Firefox/121.0', DATE_SUB(NOW(), INTERVAL 28 DAY)),
(2, '192.168.1.3', 'Mozilla/5.0 Safari/17.0', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(3, '192.168.1.1', 'Mozilla/5.0 Chrome/120.0', DATE_SUB(NOW(), INTERVAL 19 DAY)),
(4, '192.168.1.4', 'Mozilla/5.0 Edge/120.0', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(5, '192.168.1.2', 'Mozilla/5.0 Firefox/121.0', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(6, '192.168.1.5', 'Mozilla/5.0 Chrome/120.0', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(7, '192.168.1.3', 'Mozilla/5.0 Safari/17.0', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(8, '192.168.1.1', 'Mozilla/5.0 Chrome/120.0', NOW());

INSERT INTO `article_comment` (`article_id`, `user_id`, `content`, `status`, `created_at`) VALUES
(1, 1, '欢迎来到新的增长闭环版本。', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 2, '这篇文章的结构很清晰，适合入门。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, 'Vue 3 的 setup 用起来确实更顺手。', 1, NOW());

INSERT INTO `article_like` (`article_id`, `user_id`, `created_at`) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 2, NOW());

INSERT INTO `subscription` (`email`, `source_page`, `status`, `created_at`) VALUES
('reader1@example.com', '/', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('reader2@example.com', '/article/1', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO `growth_event` (`event_type`, `user_id`, `article_id`, `event_data`, `created_at`) VALUES
('user_registered', 2, NULL, '{"channel":"seed"}', DATE_SUB(NOW(), INTERVAL 6 DAY)),
('user_logged_in', 1, NULL, '{"channel":"seed"}', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('user_logged_in', 2, NULL, '{"channel":"seed"}', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('article_commented', 1, 1, '{"source":"seed"}', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('article_commented', 2, 1, '{"source":"seed"}', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('article_liked', 1, 1, '{"source":"seed"}', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('article_liked', 2, 1, '{"source":"seed"}', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('article_liked', 2, 2, '{"source":"seed"}', NOW()),
('subscription_created', NULL, NULL, '{"sourcePage":"/"}', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('subscription_created', NULL, NULL, '{"sourcePage":"/article/1"}', DATE_SUB(NOW(), INTERVAL 1 DAY));
