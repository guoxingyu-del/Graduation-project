package method

import (
	"context"
	"errors"

	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type CreateDirHandler struct {
	ctx context.Context
	Req *pb_gen.CreateDirRequest
}

func NewCreateDirHandler(ctx context.Context, req *pb_gen.CreateDirRequest) *CreateDirHandler {
	return &CreateDirHandler{
		ctx: ctx,
		Req: req,
	}
}

// 根据request中的信息，将dir成功的存入到结点中，同时保存相关的结点关系
// 相比之前的差别就是nodeId的生成被放置到了前端
func (h *CreateDirHandler) Run() (resp *pb_gen.CreateDirResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.CreateDirResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	dirID := util.GenId()
	err := db.Transaction(h.ctx, func(ctx context.Context) error {
		dirNode := &model.Node{
			NodeType: uint(pb_gen.NodeType_Dir),
			NodeId:   uint64(dirID),
			Name:     h.Req.GetDirName(),
			IsDelete: 0,
		}
		if err := db.Node.Create(ctx, dirNode); err != nil {
			logs.Sugar.Errorf("create node error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		rel := &model.NodeRel{
			ParentId: uint64(h.Req.GetParentId()),
			ChildId:  uint64(dirID),
		}
		if err := db.NodeRel.Create(ctx, rel); err != nil {
			logs.Sugar.Errorf("create rel error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		return nil
	})
	if err != nil {
		logs.Sugar.Errorf("transaction error:%v", err)
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		}
		return
	}
	return
}

func (h *CreateDirHandler) checkParams() error {
	if len(h.Req.GetDirName()) == 0 {
		return errors.New("empty dir name")
	}
	if h.Req.GetParentId() <= 0 {
		return errors.New("illegal parent id")
	}
	return nil
}
