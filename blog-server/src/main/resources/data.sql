USE `blog`;

INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `avatar`) VALUES
('test', '0192023a7bbd73250516f069df18b500', '测试用户', NULL);

INSERT INTO `category` (`name`, `sort`) VALUES
('技术', 1),
('生活', 2),
('随笔', 3),
('教程', 4);

INSERT INTO `tag` (`name`) VALUES
('Java'),
('Spring Boot'),
('Vue'),
('MySQL'),
('前端'),
('后端'),
('数据库'),
('JavaScript'),
('CSS'),
('HTML');

INSERT INTO `article` (`title`, `summary`, `content`, `cover`, `status`, `view_count`, `category_id`, `created_at`) VALUES
('Spring Boot 入门教程', '本文介绍Spring Boot的基础知识和快速入门', '# Spring Boot 入门\n\n## 简介\n\nSpring Boot 是一个用于简化Spring应用开发的框架。\n\n## 特点\n\n- 约定优于配置\n- 内嵌服务器\n- 自动配置\n\n## 快速开始\n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```', NULL, 1, 100, 1, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('Vue3 组合式 API 详解', '深入理解Vue3的Composition API', '# Vue3 组合式 API\n\n## setup函数\n\n```javascript\nimport { ref, reactive } from ''vue''\n\nexport default {\n  setup() {\n    const count = ref(0)\n    return { count }\n  }\n}\n```\n\n## 响应式数据\n\n- ref: 用于基本类型\n- reactive: 用于对象类型', NULL, 1, 85, 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
('MySQL 索引优化指南', 'MySQL数据库索引的创建与优化策略', '# MySQL 索引优化\n\n## 索引类型\n\n1. 主键索引\n2. 唯一索引\n3. 普通索引\n4. 全文索引\n\n## 优化建议\n\n- 避免在列上使用函数\n- 使用覆盖索引\n- 合理使用联合索引', NULL, 1, 120, 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('前端性能优化实践', '前端性能优化的多种方法和技巧', '# 前端性能优化\n\n## 加载优化\n\n- 代码分割\n- 懒加载\n- 压缩资源\n\n## 渲染优化\n\n- 虚拟列表\n- 防抖节流\n- 减少重排重绘', NULL, 1, 60, 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('JavaScript 异步编程', 'Promise、async/await 异步编程详解', '# JavaScript 异步编程\n\n## Promise\n\n```javascript\nconst p = new Promise((resolve, reject) => {\n  resolve(''success'')\n})\n```\n\n## async/await\n\n```javascript\nasync function fetchData() {\n  const res = await fetch(''/api/data'')\n  return res.json()\n}\n```', NULL, 1, 75, 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('CSS Grid 布局完全指南', 'CSS Grid布局从入门到精通', '# CSS Grid 布局\n\n## 基本概念\n\n- 容器(container)\n- 项目(item)\n- 行(row)\n- 列(column)\n\n## 常用属性\n\n```css\n.container {\n  display: grid;\n  grid-template-columns: repeat(3, 1fr);\n  gap: 20px;\n}\n```', NULL, 1, 45, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('生活随笔：程序员的日常', '记录程序员日常生活中的点滴', '# 程序员的日常\n\n今天又是写代码的一天。\n\n早上起来，先喝杯咖啡，然后打开IDE开始工作。\n\n## 今日任务\n\n- 修复bug\n- 代码review\n- 写文档\n\n生活就是这样，简单而充实。', NULL, 1, 30, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('我的2024年度总结', '回顾2024，展望2025', '# 2024年度总结\n\n## 工作方面\n\n今年完成了很多项目，学到了很多新技术。\n\n## 学习方面\n\n- 学习了Go语言\n- 深入了解了Kubernetes\n- 掌握了微服务架构\n\n## 2025目标\n\n继续学习，持续进步！', NULL, 1, 25, 2, NOW());

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
