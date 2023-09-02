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

type ShareFirstHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.ShareFirstRequest
}

func NewShareFirstHandler(ctx context.Context, req *pb_gen.ShareFirstRequest) *ShareFirstHandler {
	return &ShareFirstHandler{
		ctx: ctx,
		Req: req,
	}
}

/*
	从数据库中找到分享方的文件索引 CId实际加密的是关键字
*/
func (h *ShareFirstHandler) Run() (resp *pb_gen.ShareFirstResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.ShareFirstResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("shareFirst check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	//logs.Sugar.Errorf("h.Req.L:%v", h.Req.L)
	CIds, err := db.SearchIndex.MulGetCIdByLAndJId(h.ctx, h.Req.L, h.Req.GetJId())
	if err != nil {
		logs.Sugar.Errorf("shareFirst getCIdByLAndJId error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	for _, CId := range CIds {
		resp.S = append(resp.S, CId)
	}
	//fmt.Print(len(resp.S), "------------------------------------------")
	return resp
}

func (h *ShareFirstHandler) checkParams() error {
	if h.Req.L == "" {
		return errors.New("empty index L")
	}
	if h.Req.JId == "" {
		return errors.New("empty Jid")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
