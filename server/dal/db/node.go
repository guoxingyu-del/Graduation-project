package db

import (
	"context"
	"time"

	"github.com/JackTJC/gmFS_backend/model"
)

var Node *nodeDB

type nodeDB struct {
}

func (d *nodeDB) Create(ctx context.Context, node *model.Node) error {
	conn := getDbConn(ctx)
	node.CreateTime = time.Now()
	node.UpdateTime = time.Now()
	return conn.Model(node).Create(node).Error
}

func (d *nodeDB) MGetByNodeId(ctx context.Context, nodeIds []uint64) (map[uint64]*model.Node, error) {
	conn := getDbConn(ctx)
	var nodeList []*model.Node
	if err := conn.Model(&model.Node{}).Where("node_id IN (?) AND is_delete = 0", nodeIds).Find(&nodeList).Error; err != nil {
		return nil, err
	}
	ret := make(map[uint64]*model.Node)
	for _, node := range nodeList {
		ret[node.NodeId] = node
	}
	return ret, nil
}

func (d *nodeDB) DeleteFile(ctx context.Context, nodeIds []uint64) error {
	conn := getDbConn(ctx)
	if err := conn.Model(&model.Node{}).Where("node_id IN (?)", nodeIds).Update("is_delete", 1).Error; err != nil {
		return err
	}
	return nil
}
