package model

import (
	"time"
)

// 分享令牌表
type ShareToken struct {
	Id           uint64    `gorm:"column:id;type:bigint(20) unsigned;primary_key;AUTO_INCREMENT;comment:主键id" json:"id"`
	ShareTokenId string    `gorm:"column:share_token_id;type:varchar(128);comment:分享令牌编号;NOT NULL" json:"share_token_id"`
	L            string    `gorm:"column:L;type:varchar(256);NOT NULL" json:"L"`
	JId          string    `gorm:"column:J_id;type:varchar(256);NOT NULL" json:"J_id"`
	KId          string    `gorm:"column:k_id;type:varchar(256);NOT NULL" json:"k_id"`
	FileId       string    `gorm:"column:file_id;type:varchar(256);NOT NULL" json:"file_id"`
	OwnerId      string    `gorm:"column:owner_id;type:varchar(256);comment:分享名;NOT NULL" json:"owner_id"`
	UserId       string    `gorm:"column:user_id;type:varchar(256);comment:文件名;NOT NULL" json:"user_id"`
	SecretKey    string    `gorm:"column:secret_key;type:varchar(256);comment:文件密钥;NOT NULL" json:"secret_key"`
	FileName     string    `gorm:"column:file_name;type:varchar(256);comment:文件名;NOT NULL" json:"file_name"`
	IsReceived   int       `gorm:"column:is_received;type:int(11);comment:是否被接收，1表示被接收，0表示未被接收;NOT NULL" json:"is_received"`
	CreateTime   time.Time `gorm:"column:create_time;type:timestamp;default:CURRENT_TIMESTAMP;comment:创建时间;NOT NULL" json:"create_time"`
	IsShare      string    `gorm:"column:is_share;type:varchar(10);NOT NULL" json:"is_share"`
}

func (m *ShareToken) TableName() string {
	return "share_token"
}
