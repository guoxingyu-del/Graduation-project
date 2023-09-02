package db

import (
	"context"
	"errors"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/go-sql-driver/mysql"
	"time"
)

var ShareFileToken *shareFileTokenDB

type shareFileTokenDB struct {
}

func (d *shareFileTokenDB) Create(ctx context.Context, sk *model.ShareToken) error {
	sk.CreateTime = time.Now()
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

func (d *shareFileTokenDB) MulGetShareTokenByUserId(ctx context.Context, userid string) ([]*model.ShareToken, error) {
	conn := getDbConn(ctx)
	var ret []*model.ShareToken
	err := conn.Model(&model.ShareToken{}).Where("user_id = ?", userid).Where("is_received", 0).Find(&ret).Error
	if err != nil {
		return nil, err
	}
	return ret, nil
}

func (d *shareFileTokenDB) UpdateReceivedStateById(ctx context.Context, shareTokenId string) error {
	conn := getDbConn(ctx)
	err := conn.Model(&model.ShareToken{}).Where("share_token_id", shareTokenId).Update("is_received", 1).Error
	if err != nil {
		return err
	}
	return nil
}
