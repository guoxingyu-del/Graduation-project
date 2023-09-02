package test

import (
	"github.com/JackTJC/gmFS_backend/dal"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/util"
)

func init() {
	logs.InitLog()
	dal.Init()
	util.Init()
}
