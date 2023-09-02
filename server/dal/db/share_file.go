package db

import (
	"context"
	"errors"
	"time"

	"github.com/JackTJC/gmFS_backend/model"
	"gorm.io/gorm"
)

var ShareFile *shareFileDB

type ShareFileStatus uint8

const (
	NotProcess  ShareFileStatus = 1
	HaveProcess ShareFileStatus = 2
)

var (
	ErrEmptyShareFile = errors.New("empty share file")
)

type shareFileDB struct {
}

func (d *shareFileDB) Create(ctx context.Context, shareFile *model.ShareFile) error {
	shareFile.CreateTime = time.Now()
	shareFile.UpdateTime = time.Now()
	conn := getDbConn(ctx)
	return conn.Model(shareFile).Create(shareFile).Error
}

// 使用被分享者id查询收到且未处理的文件列表
func (d *shareFileDB) GetByDstUID(ctx context.Context, to uint64) ([]*model.ShareFile, error) {
	conn := getDbConn(ctx)
	var ret []*model.ShareFile
	if err := conn.Model(&model.ShareFile{}).Where("`to` = ? AND status = ?", to, NotProcess).Find(&ret).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, ErrEmptyShareFile
		}
		return nil, err
	}
	return ret, nil
}

// 将分享记录标记为已处理，即惰性删除
func (d *shareFileDB) LazyDel(ctx context.Context, id int64) error {
	conn := getDbConn(ctx)
	return conn.Model(&model.ShareFile{}).Where("id = ? AND status = ?", id, NotProcess).Update("status", HaveProcess).Error
}
