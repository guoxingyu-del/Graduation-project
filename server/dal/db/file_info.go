package db

import (
	"context"
	"errors"
	"time"

	"github.com/JackTJC/gmFS_backend/model"
	"github.com/go-sql-driver/mysql"
)

var (
	ErrFileEixst = errors.New("user already have this file")
)

var FileInfo *FileInfoDB

type FileInfoDB struct {
}

func (d *FileInfoDB) Create(ctx context.Context, sk *model.FileInfo) error {
	sk.CreateTime = time.Now()
	sk.UpdateTime = time.Now()
	conn := getDbConn(ctx)
	if err := conn.Model(sk).Create(sk).Error; err != nil {
		var mysqlErr *mysql.MySQLError
		if errors.As(err, &mysqlErr) && mysqlErr.Number == 1062 {
			return ErrFileEixst
		}
		return err
	}
	return nil
}

func (d *FileInfoDB) GetByNodeID(ctx context.Context, fileID uint64) (*model.FileInfo, error) {
	var ret *model.FileInfo
	conn := getDbConn(ctx)
	err := conn.Model(&model.FileInfo{}).Where("node_id = ?", fileID).Find(&ret).Error

	if err != nil {
		return nil, err
	}
	return ret, nil
}

/*func (d *FileInfoDB) GetByUID(ctx context.Context, uid uint64) ([]*model.FileInfo, error) {
	var ret []*model.FileInfo
	conn := getDbConn(ctx)
	err := conn.Model(ret).Where("user_id = ?", uid).Find(&ret).Error
	if err != nil {
		return nil, err
	}
	return ret, nil
}*/
