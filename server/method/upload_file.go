package method

import (
	"context"
	"errors"
	"fmt"

	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	objstore "github.com/JackTJC/gmFS_backend/dal/obj_store"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type UploadFileHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.UploadFileRequest
}

func NewUploadFileHandler(ctx context.Context, req *pb_gen.UploadFileRequest) *UploadFileHandler {
	return &UploadFileHandler{
		ctx: ctx,
		Req: req,
	}
}

// 这部分的主要修改就是将node_rel进行了修改
func (h *UploadFileHandler) Run() (resp *pb_gen.UploadFileReponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.UploadFileReponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("checkParams error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 生成节点id
	// nodeID := uint64(util.GenId())
	nodeID := uint64(h.Req.GetNodeId())
	// 对象存储保存文件
	if err := objstore.UploadFile(h.ctx, GenCosFileKey(int64(nodeID)), h.Req.Content); err != nil {
		logs.Sugar.Errorf("upload file cos upload error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 开启事务
	err := db.Transaction(h.ctx, func(ctx context.Context) error {
		// 创建文件夹到文件的索引关系
		nodeRel := &model.NodeRel{
			ParentId: uint64(h.Req.GetParentId()),
			ChildId:  nodeID,
		}
		if err := db.NodeRel.Create(ctx, nodeRel); err != nil {
			logs.Sugar.Errorf("CreateNodeRel error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		// db中创建该文件节点
		node := &model.Node{
			NodeId:   nodeID,
			NodeType: uint(pb_gen.NodeType_File),
			Name:     h.Req.GetFileName(),
			IsDelete: 0,
		}
		if err := db.Node.Create(ctx, node); err != nil {
			logs.Sugar.Errorf("Create Node error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		// 创建密钥记录
		sk := &model.FileInfo{
			NodeId:     nodeID,
			FileSecret: h.Req.FileSecret,
			IsShare:    0,
			Address:    nodeID,
		}
		if err := db.FileInfo.Create(ctx, sk); err != nil {
			logs.Sugar.Errorf("Create SK error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return err
		}
		if len(h.Req.GetIndexList()) == 0 { // 没有索引，直接返回
			return nil
		}
		// 创建索引
		indexs := make([]*model.SearchIndex, 0, len(h.Req.GetIndexList()))
		for _, keyword := range h.Req.GetIndexList() {
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
		logs.Sugar.Errorf("Transaction error:%v", err)
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		}
		return
	}
	return
}

func (h *UploadFileHandler) checkParams() error {
	if len(h.Req.GetFileName()) == 0 {
		return errors.New("empty file name")
	}
	if len(h.Req.GetContent()) == 0 {
		return errors.New("empty file content")
	}
	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}

// 生成 file/{node_id}形式的key
func GenCosFileKey(fileId int64) string {
	return fmt.Sprintf("file/%v", fileId)
}
