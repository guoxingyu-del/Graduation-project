package test

import (
	"context"
	"testing"

	"github.com/JackTJC/gmFS_backend/method"
	"github.com/JackTJC/gmFS_backend/pb_gen"
)

func TestCreateDir(t *testing.T) {
	ctx := context.Background()
	req := &pb_gen.CreateDirRequest{
		DirName:  "dir under root",
		ParentId: 1517026803300962304,
	}
	h := method.NewCreateDirHandler(ctx, req)
	resp := h.Run()
	if resp.GetBaseResp().GetStatusCode() != pb_gen.StatusCode_Success {
		t.Log("error")
		t.FailNow()
	}
	t.Log("success")
}
