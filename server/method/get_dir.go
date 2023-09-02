package method

import (
	"context"
	"errors"

	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type GetDirHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.GetDirRequest
}

func NewGetDirHandler(ctx context.Context, req *pb_gen.GetDirRequest) *GetDirHandler {
	return &GetDirHandler{
		ctx: ctx,
		Req: req,
	}
}

// 根据文件夹id返回其下的目录列表id集合
func (h *GetDirHandler) Run() (resp *pb_gen.GetDirResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.GetDirResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("check params error:%v", err)
		util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	subNodes, err := db.NodeRel.GetByParent(h.ctx, h.Req.NodeId)
	if err != nil {
		logs.Sugar.Errorf("GetByParent error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	var nodeList []int64
	for _, subNode := range subNodes {
		nodeList = append(nodeList, int64(subNode.ChildId))
	}
	resp.NodeIdList = nodeList
	return
}

func (h *GetDirHandler) checkParams() error {
	if h.Req.GetNodeId() <= 0 {
		return errors.New("illegal node id")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
