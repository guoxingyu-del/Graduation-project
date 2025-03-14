// Code generated by sql2gorm. DO NOT EDIT.
package model

import (
	"time"
)

// 文件节点引用表
type NodeRel struct {
	Id         uint64    `gorm:"column:id;type:bigint(20) unsigned;primary_key;AUTO_INCREMENT;comment:主键id" json:"id"`
	ParentId   uint64    `gorm:"column:parent_id;type:bigint(20) unsigned;comment:父节点id;NOT NULL" json:"parent_id"`
	ChildId    uint64    `gorm:"column:child_id;type:bigint(20) unsigned;comment:子节点id;NOT NULL" json:"child_id"`
	CreateTime time.Time `gorm:"column:create_time;type:timestamp;default:CURRENT_TIMESTAMP;comment:创建时间;NOT NULL" json:"create_time"`
	UpdateTime time.Time `gorm:"column:update_time;type:timestamp;default:CURRENT_TIMESTAMP;comment:更新时间;NOT NULL" json:"update_time"`
}

func (m *NodeRel) TableName() string {
	return "node_rel"
}
