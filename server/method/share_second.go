package method

import (
	"context"

	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type ShareSecondHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.ShareSecondRequest
}

func NewShareSecondHandler(ctx context.Context, req *pb_gen.ShareSecondRequest) *ShareSecondHandler {
	return &ShareSecondHandler{
		ctx: ctx,
		Req: req,
	}
}

/*
从数据库中找到分享方的文件索引
*/
func (h *ShareSecondHandler) Run() (resp *pb_gen.ShareSecondResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.ShareSecondResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("shareSecond check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	nodeId := uint64(h.Req.GetNodeId())
	// 开启事务
	err := db.Transaction(h.ctx, func(ctx context.Context) error {
		// 创建文件夹到文件的索引关系
		nodeRel := &model.NodeRel{
			ParentId: uint64(h.Req.GetParentId()),
			ChildId:  nodeId,
		}
		if err := db.NodeRel.Create(ctx, nodeRel); err != nil {
			logs.Sugar.Errorf("CreateNodeRel error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		// db中创建该文件节点
		node := &model.Node{
			NodeId:   nodeId,
			NodeType: uint(pb_gen.NodeType_File),
			Name:     h.Req.GetFileName(),
			IsDelete: 0,
		}
		if err := db.Node.Create(ctx, node); err != nil {
			logs.Sugar.Errorf("Create Node error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		var isShare int
		if h.Req.GetIsShare() {
			isShare = 1
		} else {
			isShare = 0
		}
		// 创建密钥记录
		sk := &model.FileInfo{
			NodeId:     nodeId,
			FileSecret: h.Req.FileSecret,
			IsShare:    isShare,
			Address:    uint64(h.Req.GetAddress()),
		}
		if err := db.FileInfo.Create(ctx, sk); err != nil {
			logs.Sugar.Errorf("Create SK error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		if len(h.Req.GetSearchIndexSecond()) == 0 { // 没有索引，直接返回
			return nil
		}
		// 创建索引
		indexs := make([]*model.SearchIndex, 0, len(h.Req.GetSearchIndexSecond()))
		for _, keyword := range h.Req.GetSearchIndexSecond() {
			indexs = append(indexs, &model.SearchIndex{
				L:   keyword.L,
				IW:  keyword.Iw,
				RW:  keyword.Rw,
				CW:  keyword.Cw,
				IId: keyword.Iid,
				RId: keyword.Rid,
				CId: keyword.Cid,
			})
		}
		if err := db.SearchIndex.MCreate(h.ctx, indexs); err != nil {
			logs.Sugar.Errorf("m create search index error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		// 更新biIndex
		if err := db.User.UpdateBiIndex(h.ctx, h.Req.BaseReq.Token, h.Req.BiIndex); err != nil {
			logs.Sugar.Errorf("m create search index error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		return nil
	})
	if err != nil {
		logs.Sugar.Errorf("shareSecond transaction error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	return resp
}

func (h *ShareSecondHandler) checkParams() error {
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
