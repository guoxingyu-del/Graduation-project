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

type SearchFileHandler struct {
	ctx context.Context
	uid uint64
	Req *pb_gen.SearchFileRequest
}

func NewSearchFileHandler(ctx context.Context, req *pb_gen.SearchFileRequest) *SearchFileHandler {
	return &SearchFileHandler{
		ctx: ctx,
		Req: req,
	}
}

// 根据一组id集合返回对应的文件基本信息，不包括文件密钥和文件内容
func (h *SearchFileHandler) Run() (resp *pb_gen.SearchFileResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.SearchFileResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		logs.Sugar.Errorf("search file check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 拉取node
	var searchResultIDList []uint64
	for _, idx := range h.Req.GetNodeId() {
		searchResultIDList = append(searchResultIDList, uint64(idx))
	}
	nodeMap, err := db.Node.MGetByNodeId(h.ctx, searchResultIDList) // 此处默认文件id=节点id
	if err != nil {
		logs.Sugar.Errorf("search file check params error:%v", err)
		resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		return
	}
	// 下发数据
	for _, node := range nodeMap {
		resp.NodeList = append(resp.NodeList, &pb_gen.Node{
			NodeType:   pb_gen.NodeType(node.NodeType),
			NodeId:     int64(node.NodeId),
			NodeName:   node.Name,
			CreateTime: node.CreateTime.Unix(),
			UpdateTime: node.UpdateTime.Unix(),
		})
	}
	return resp
}

func (h *SearchFileHandler) checkParams() error {
	if h.Req.GetNodeId() == nil {
		return errors.New("empty keyword")
	}

	uid, err := cache.Token.GetUID(h.Req.GetBaseReq().GetToken())
	if err != nil {
		return err
	}
	h.uid = uid
	return nil
}
