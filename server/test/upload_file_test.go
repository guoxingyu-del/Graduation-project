package test

import (
	"context"
	"testing"

	"github.com/JackTJC/gmFS_backend/method"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/kr/pretty"
)

func TestUplooadFile(t *testing.T) {
	ctx := context.Background()
	req := &pb_gen.UploadFileRequest{
		FileName: "tianjincai's test file",
		Content:  []byte("this is test file content"),
	}
	h := method.NewUploadFileHandler(ctx, req)
	pretty.Println(h.Run())
}

func TestUploadFileToDir(t *testing.T) {
	ctx := context.Background()
	req := &pb_gen.UploadFileRequest{
		FileName: "test file under in root",
		Content:  []byte("test file content"),
		ParentId: 1517026803300962304,
	}
	h := method.NewUploadFileHandler(ctx, req)
	pretty.Println(h.Run())
}
