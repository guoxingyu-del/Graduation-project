package test

import (
	"context"
	"testing"

	"github.com/JackTJC/gmFS_backend/method"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/kr/pretty"
)

func TestGetNode(t *testing.T) {
	ctx := context.Background()
	req := &pb_gen.GetNodeRequest{
		NodeId: 1517026803300962304,
	}
	h := method.NewGetNodeHandler(ctx, req)
	pretty.Println(h.Run())
}
