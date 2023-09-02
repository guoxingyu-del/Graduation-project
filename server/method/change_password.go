package method

import (
	"context"
	"errors"

	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type ChangePasswordHandler struct {
	ctx context.Context
	Req *pb_gen.ChangePasswordRequest
}

func NewChangePasswordHandler(ctx context.Context, req *pb_gen.ChangePasswordRequest) *ChangePasswordHandler {
	return &ChangePasswordHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *ChangePasswordHandler) Run() (resp *pb_gen.ChangePasswordResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.ChangePasswordResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 更新hashId
	if err := db.User.ChangePassword(h.ctx, h.Req.OldHashId, h.Req.NewHashId, h.Req.Key1, h.Req.Key2); err != nil {
		logs.Sugar.Errorf("m create search index error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	return
}

func (h *ChangePasswordHandler) checkParams() error {
	if len(h.Req.GetOldHashId()) == 0 {
		return errors.New("empty old hashId")
	}
	if len(h.Req.GetNewHashId()) == 0 {
		return errors.New("empty new hashId")
	}
	return nil
}
