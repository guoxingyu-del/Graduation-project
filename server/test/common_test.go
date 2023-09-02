package test

import (
	"runtime"
	"testing"

	"github.com/JackTJC/gmFS_backend/util"
)

func TestCaller(t *testing.T) {
	_, b, _, _ := runtime.Caller(0)
	t.Log(b)
}

func TestIdGen(t *testing.T) {
	id := util.GenUUID()
	t.Log(id)
}
