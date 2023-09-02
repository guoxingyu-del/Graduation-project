package util

import (
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/bwmarrin/snowflake"
	"github.com/google/uuid"
)

var node *snowflake.Node

func initIDGen() {
	var err error
	node, err = snowflake.NewNode(1)
	if err != nil {
		logs.Sugar.Fatalf("snowflake init error:%v", err)
		panic(err)
	}
}

// 获取64bit id
func GenId() int64 {
	id := node.Generate()
	return id.Int64() / 10000 // div 10000 解决js的问题
}

func GenUUID() string {
	return uuid.New().String()
}
