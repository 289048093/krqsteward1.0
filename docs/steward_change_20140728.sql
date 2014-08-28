-- ----------------------------
-- Table structure for `t_repository_charger`
-- ----------------------------
DROP TABLE IF EXISTS `t_repository_charger`;
CREATE TABLE `t_repository_charger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `repository_id` int(11) DEFAULT NULL,
  `charger_id` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
