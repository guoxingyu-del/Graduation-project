package method

import (
	"context"
	"errors"

	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	objstore "github.com/JackTJC/gmFS_backend/dal/obj_store"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type GetNodeHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.GetNodeRequest
}

func NewGetNodeHandler(ctx context.Context, req *pb_gen.GetNodeRequest) *GetNodeHandler {
	return &GetNodeHandler{
		ctx: ctx,
		Req: req,
	}
}

// 获取文件内容以及对应密钥
func (h *GetNodeHandler) Run() (resp *pb_gen.GetNodeResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.GetNodeResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("check params error:%v", err)
		util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	/*
		// 获取当前节点
		nodeMap, err := db.Node.MGetByNodeId(h.ctx, []uint64{uint64(h.Req.GetNodeId())})
		if err != nil {
			logs.Sugar.Errorf("MGetByNodeId error:%v", err)
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			return
		}
		node, ok := nodeMap[uint64(h.Req.GetNodeId())]
		if !ok {
			logs.Sugar.Warnf("node id:%v not exist", h.Req.GetNodeId())
			return
		}
		// 打包节点元数据
		resp.Node = &pb_gen.Node{
			NodeId:   int64(node.NodeId),
			NodeName: node.Name,
			CreateTime: node.CreateTime.Unix(),
			UpdateTime: node.UpdateTime.Unix(),
		}*/
	// 判断文件是否为分享文件
	fileInfo, err := db.FileInfo.GetByNodeID(h.ctx, uint64(h.Req.GetNodeId()))
	fileSecret := fileInfo.FileSecret
	if err != nil {
		return
	}
	isShare := fileInfo.IsShare
	for isShare == 1 {
		fileInfoNew, _ := db.FileInfo.GetByNodeID(h.ctx, uint64(fileInfo.Address))
		isShare = fileInfoNew.IsShare
		// 判断文件是否被删除
		if fileNode, err := db.Node.MGetByNodeId(h.ctx, []uint64{fileInfoNew.NodeId}); err != nil || len(fileNode) == 0 {
			resp.Node = &pb_gen.Node{
				NodeContent: []byte("分享源文件已被删除"),
			}
			return
		}
		fileInfo = fileInfoNew
	}
	// 根据address拿取文件内容和文件密钥
	content, err := objstore.DownloadFile(h.ctx, GenCosFileKey(int64(fileInfo.Address)))
	if err != nil {
		logs.Sugar.Errorf("get node cos download error:%c", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	resp.Node = &pb_gen.Node{
		NodeContent: content,
		SecretKey:   []byte(fileSecret),
	}
	return
	//// 获取子节点
	//subNodeList, err := db.NodeRel.GetByParent(h.ctx, h.Req.GetNodeId())
	//if err != nil {
	//	logs.Sugar.Errorf("GetByParent error:%v", err)
	//	resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
	//	return
	//}
	//subNodeIDs := make([]uint64, 0, len(subNodeList))
	//for _, nodeRel := range subNodeList {
	//	subNodeIDs = append(subNodeIDs, nodeRel.ChildID)
	//}
	//nodeMap, err = db.Node.MGetByNodeId(h.ctx, subNodeIDs)
	//if err != nil {
	//	logs.Sugar.Errorf("MGetByNodeId error:%v", err)
	//	resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
	//	return
	//}
	//// 后面子数据的打包肯定就不能这么搞了
	//// 打包子节点数据
	//pbSubNodes := make([]*pb_gen.Node, 0, len(nodeMap))
	//for _, node := range nodeMap {
	//	pbSubNodes = append(pbSubNodes, &pb_gen.Node{
	//		NodeId:     int64(node.NodeID),
	//		NodeName:   node.Name,
	//		NodeType:   pb_gen.NodeType(node.NodeType),
	//		CreateTime: node.CreateTime.Unix(),
	//		UpdateTime: node.UpdateTime.Unix(),
	//	})
	//}
	//resp.Node.SubNodeList = pbSubNodes
}

func (h *GetNodeHandler) checkParams() error {
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
