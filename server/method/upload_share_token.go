package method

import (
	"context"
	"errors"
	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
	"strconv"
)

type UploadShareTokenHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.UpLoadShareTokenRequest
}

func NewUploadShareTokenHandler(ctx context.Context, req *pb_gen.UpLoadShareTokenRequest) *UploadShareTokenHandler {
	return &UploadShareTokenHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *UploadShareTokenHandler) Run() (resp *pb_gen.UpLoadShareTokenResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.UpLoadShareTokenResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("upload shareToken check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	err := db.Transaction(h.ctx, func(ctx context.Context) error {
		token := h.Req.GetShareToken()
		shareToken := &model.ShareToken{
			L:            token.GetL(),
			JId:          token.GetJId(),
			KId:          token.GetKId(),
			FileId:       token.GetFileId(),
			UserId:       token.GetUserId(),
			OwnerId:      token.GetOwnerId(),
			SecretKey:    h.Req.GetSecretKey(),
			FileName:     h.Req.GetFileName(),
			ShareTokenId: strconv.FormatInt(util.GenId(), 10),
			IsReceived:   0,
			IsShare:      h.Req.GetIsShare(),
		}
		if err := db.ShareFileToken.Create(h.ctx, shareToken); err != nil {
			logs.Sugar.Errorf("UploadShareToken create error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		return nil
	})
	if err != nil {
		logs.Sugar.Errorf("Transaction error:%v", err)
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		}
		return
	}
	return resp
}

func (h *UploadShareTokenHandler) checkParams() error {
	if h.Req.GetShareToken() == nil {
		return errors.New("empty ShareToken")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
