package db

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/JackTJC/gmFS_backend/config"
	"github.com/JackTJC/gmFS_backend/logs"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

var globalDB *gorm.DB

func InitDB() {
	newLogger := logger.New(
		log.New(os.Stdout, "\r\n", log.LstdFlags), // io writer
		logger.Config{
			SlowThreshold: time.Second, // 慢 SQL 阈值
			LogLevel:      logger.Info, // Log level
			Colorful:      true,        // 禁用彩色打印
		},
	)
	var err error
	conf := config.GetInstance()
	dsn := fmt.Sprintf("%v:%v@tcp(%v:%v)/gm_fs?charset=utf8mb4&parseTime=True&loc=Local",
		conf.MySQL.User, conf.MySQL.Passwd, conf.MySQL.Host, conf.MySQL.Port)
	globalDB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{
		Logger: newLogger,
	})
	if err != nil {
		logs.Sugar.Errorf("open db error:%v", err)
		panic(err)
	}
}

type ctxTransactionKey struct {
}

// 执行事务
func Transaction(ctx context.Context, fc func(ctx context.Context) error) error {
	db := globalDB.WithContext(ctx)
	return db.Transaction(func(tx *gorm.DB) error {
		txctx := context.WithValue(ctx, ctxTransactionKey{}, tx)
		return fc(txctx)
	})
}

// 如果ctx带有事务，从ctx中获取事务所在的db
// 如果没有事务，直接返回全局 db
func getDbConn(ctx context.Context) *gorm.DB {
	v := ctx.Value(ctxTransactionKey{})
	if v != nil {
		tx, ok := v.(*gorm.DB)
		if !ok {
			logs.Sugar.Fatalf("transaction assert error")
		}
		return tx
	}
	return globalDB.WithContext(ctx)
}
