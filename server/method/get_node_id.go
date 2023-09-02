package method

import (
	"context"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type GetNodeIdHandler struct {
	ctx context.Context
	Req *pb_gen.GetNodeIdRequest
}

func NewGetNodeIdHandler(ctx context.Context, req *pb_gen.GetNodeIdRequest) *GetNodeIdHandler {
	return &GetNodeIdHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *GetNodeIdHandler) Run() (resp *pb_gen.GetNodeIdResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.GetNodeIdResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	// 生成节点id
	resp.NodeId = util.GenId()
	return
}
