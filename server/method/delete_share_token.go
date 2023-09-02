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

type DeleteShareTokensHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.DeleteShareTokenRequest
}

func NewDeleteShareTokensHandler(ctx context.Context, req *pb_gen.DeleteShareTokenRequest) *DeleteShareTokensHandler {
	return &DeleteShareTokensHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *DeleteShareTokensHandler) Run() (resp *pb_gen.DeleteShareTokenResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.DeleteShareTokenResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("delete shareToken check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	err := db.ShareFileToken.UpdateReceivedStateById(h.ctx, h.Req.GetShareTokenId())
	if err != nil {
		logs.Sugar.Errorf("DeleteShareToken create error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	return resp
}
func (h *DeleteShareTokensHandler) checkParams() error {
	if h.Req.GetShareTokenId() == "" {
		return errors.New("empty ShareTokenId")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
