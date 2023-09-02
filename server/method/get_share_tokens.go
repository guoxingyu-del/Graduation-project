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

type GetShareTokensHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.GetShareTokensRequest
}

func NewGetShareTokensHandler(ctx context.Context, req *pb_gen.GetShareTokensRequest) *GetShareTokensHandler {
	return &GetShareTokensHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *GetShareTokensHandler) Run() (resp *pb_gen.GetShareTokensResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.GetShareTokensResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("getShareTokens check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	shareTokens, err := db.ShareFileToken.MulGetShareTokenByUserId(h.ctx, h.Req.GetUserId())
	if err != nil {
		logs.Sugar.Errorf("getShareTokens error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 将数据库中存储的shareToken以合适的格式进行返回
	for _, shareToken := range shareTokens {
		resp.ShareMesssages = append(resp.ShareMesssages, &pb_gen.ShareMesssage{
			ShareToken: &pb_gen.ShareToken{
				L:       shareToken.L,
				JId:     shareToken.JId,
				KId:     shareToken.KId,
				FileId:  shareToken.FileId,
				OwnerId: shareToken.OwnerId,
				UserId:  shareToken.UserId,
			},
			SecretKey:    shareToken.SecretKey,
			FileName:     shareToken.FileName,
			ShareTokenId: shareToken.ShareTokenId,
			IsShare:      shareToken.IsShare,
			CreateTime:   shareToken.CreateTime.Unix(),
		})
	}
	return resp
}

func (h *GetShareTokensHandler) checkParams() error {
	if h.Req.GetUserId() == "" {
		return errors.New("empty UserId")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
