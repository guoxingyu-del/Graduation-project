package util

import (
	"runtime/debug"

	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
)

func RecoverFromPanic() {
	if err := recover(); err != nil {
		logs.Sugar.Fatalf("panic:%v, stack:%v", err, string(debug.Stack()))
	}
}

func BuildBaseResp(code pb_gen.StatusCode) *pb_gen.BaseResp {
	var msg string
	switch code {
	case pb_gen.StatusCode_Success:
		msg = "success"
	case pb_gen.StatusCode_CommonErr:
		msg = "internal server error"
	case pb_gen.StatusCode_WrongPasswd:
		msg = "password is error"
	case pb_gen.StatusCode_UserNotFound:
		msg = "user not found"
	}
	return &pb_gen.BaseResp{
		StatusCode: code,
		Message:    msg,
	}
}
