package db

import (
	"context"
	"errors"
	"time"

	"github.com/JackTJC/gmFS_backend/model"
	"github.com/go-sql-driver/mysql"
)

var (
	ErrSubExist = errors.New("file already exist in directory")
)

var NodeRel *nodeRelDB

type nodeRelDB struct {
}

func (d *nodeRelDB) Create(ctx context.Context, m *model.NodeRel) error {
	conn := getDbConn(ctx)
	m.CreateTime = time.Now()
	m.UpdateTime = time.Now()
	if err := conn.Model(&model.NodeRel{}).Create(m).Error; err != nil {
		var mysqlErr *mysql.MySQLError
		if errors.As(err, &mysqlErr) && mysqlErr.Number == 1062 {
			return ErrSubExist
		}
		return err
	}
	return nil
}
func (d *nodeRelDB) GetByParent(ctx context.Context, parent int64) ([]*model.NodeRel, error) {
	conn := getDbConn(ctx)
	var ret []*model.NodeRel
	err := conn.Model(&model.NodeRel{}).Where("parent_id = ?", parent).Find(&ret).Error
	if err != nil {
		return nil, err
	}
	return ret, nil
}

func (d *nodeRelDB) GetByUserName(ctx context.Context, username string) ([]*model.NodeRel, error) {
	conn := getDbConn(ctx)
	var ret []*model.NodeRel
	err := conn.Model(&model.NodeRel{}).Where("username = ?", username).Find(&ret).Error
	if err != nil {
		return nil, err
	}
	return ret, nil
}
