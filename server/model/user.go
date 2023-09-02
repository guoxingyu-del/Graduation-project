package model

import (
	"time"
)

// 用户信息表
type UserInfo struct {
	UserId     uint64    `gorm:"column:user_id;type:bigint(20) unsigned;primary_key;AUTO_INCREMENT;comment:user id" json:"user_id"`
	UserName   string    `gorm:"column:user_name;type:varchar(128);comment:用户名字;NOT NULL" json:"user_name"`
	HashId     string    `gorm:"column:hash_id;type:varchar(1024);comment:用户身份标识;NOT NULL" json:"hash_id"`
	Email      string    `gorm:"column:email;type:varchar(256);comment:用户邮箱;NOT NULL" json:"email"`
	BiIndex    string    `gorm:"column:bi_index;type:longtext;comment:用户双向索引链表;NOT NULL" json:"bi_index"`
	RootNodeId uint64    `gorm:"column:root_node_id;type:bigint(20) unsigned;comment:用户网盘根节点id;NOT NULL" json:"root_node_id"`
	Key1       string    `gorm:"column:key1;type:varchar(1024);comment:用户密钥1;NOT NULL" json:"key1"`
	Key2       string    `gorm:"column:key2;type:varchar(1024);comment:用户密钥2;NOT NULL" json:"key2"`
	CreateTime time.Time `gorm:"column:create_time;type:timestamp;default:CURRENT_TIMESTAMP;comment:创建时间;NOT NULL" json:"create_time"`
	UpdateTime time.Time `gorm:"column:update_time;type:timestamp;default:CURRENT_TIMESTAMP;comment:更新时间;NOT NULL" json:"update_time"`
}

func (m *UserInfo) TableName() string {
	return "user_info"
}
