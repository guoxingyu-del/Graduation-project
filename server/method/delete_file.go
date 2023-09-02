package method

import (
	"context"
	"errors"
	"fmt"

	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type DeleteFileHandler struct {
	ctx context.Context
	Req *pb_gen.DeleteFileRequest
}

func NewDeleteFileHandler(ctx context.Context, req *pb_gen.DeleteFileRequest) *DeleteFileHandler {
	return &DeleteFileHandler{
		ctx: ctx,
		Req: req,
	}
}

// 根据request中的信息，将dir成功的存入到结点中，同时保存相关的结点关系
// 相比之前的差别就是nodeId的生成被放置到了前端
func (h *DeleteFileHandler) Run() (resp *pb_gen.DeleteFileResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.DeleteFileResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	fmt.Printf("%v\n", h.Req.DelNodeID)
	var idList []uint64
	for _, id := range h.Req.DelNodeID {
		idList = append(idList, uint64(id))
	}
	fmt.Printf("%v\n", idList)
	// 这个地方没用事务，可能会出现问题
	if err := db.Node.DeleteFile(h.ctx, idList); err != nil {
		logs.Sugar.Errorf("create node error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	return
}

func (h *DeleteFileHandler) checkParams() error {
	if len(h.Req.GetDelNodeID()) == 0 {
		return errors.New("empty dir name")
	}
	return nil
}
