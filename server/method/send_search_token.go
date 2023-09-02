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

type SendSearchTokenHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.SendSearchTokenRequest
}

func NewSendSearchTokenHandler(ctx context.Context, req *pb_gen.SendSearchTokenRequest) *SendSearchTokenHandler {
	return &SendSearchTokenHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *SendSearchTokenHandler) Run() (resp *pb_gen.SendSearchTokenResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.SendSearchTokenResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("search file check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 搜索该关键词
	searchIdxs, err := db.SearchIndex.SearchByToken(h.ctx, h.Req.GetSearchToken())
	if err != nil {
		logs.Sugar.Errorf("search file search error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 下发数据
	for _, cw := range searchIdxs {
		resp.Cw = append(resp.Cw, cw)
	}
	return resp
}

func (h *SendSearchTokenHandler) checkParams() error {
	if h.Req.GetSearchToken() == nil {
		return errors.New("empty keyword")
	}

	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
