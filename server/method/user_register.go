package method

import (
	"context"
	"errors"

	"github.com/JackTJC/gmFS_backend/dal/db"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/JackTJC/gmFS_backend/util"
)

type UserRegisterHandler struct {
	ctx context.Context
	Req *pb_gen.UserRegisterRequest
}

func NewUserRegisterHandler(ctx context.Context, req *pb_gen.UserRegisterRequest) *UserRegisterHandler {
	return &UserRegisterHandler{
		ctx: ctx,
		Req: req,
	}
}

func (h *UserRegisterHandler) Run() (resp *pb_gen.UserRegisterResponse) {
	defer func() {
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_Success)
		}
		logs.Sugar.Infof("req = %+v, resp = %+v", h.Req, resp)
	}()
	resp = &pb_gen.UserRegisterResponse{
		BaseResp: util.BuildBaseResp(pb_gen.StatusCode_Success),
	}
	if err := h.checkParams(); err != nil {
		return
	}
	rootNodeID := util.GenId()
	err := db.Transaction(h.ctx, func(ctx context.Context) error {
		rootNode := &model.Node{
			NodeId:   uint64(rootNodeID),
			NodeType: uint(pb_gen.NodeType_Dir),
			Name:     h.Req.GetUserName(), // 根节点Name属性为用户名，其余节点Name属性为文件名
			IsDelete: 0,
		}
		err := db.Node.Create(ctx, rootNode) // 创建该用户的根节点
		if err != nil {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			logs.Sugar.Errorf("CreateNode error:%v", err)
			return err
		}
		user := &model.UserInfo{
			UserName:   h.Req.GetUserName(),
			HashId:     h.Req.GetHashId(),
			RootNodeId: uint64(rootNodeID),
			Email:      h.Req.GetEmail(),
			BiIndex:    h.Req.GetBiIndex(),
			Key1:       h.Req.GetKey1(),
			Key2:       h.Req.GetKey2(),
		}
		if err := db.User.Create(ctx, user); err != nil { // 创建用户
			if err == db.ErrUserExist {
				resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_UserExist)
				logs.Sugar.Errorf("user:%v have exist", h.Req.GetUserName())
				return err
			}
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
			logs.Sugar.Errorf("create user error:%v", err)
			return err
		}
		return nil
	})
	if err != nil {
		logs.Sugar.Errorf("transaction error:%v", err)
		if resp.GetBaseResp().GetStatusCode() == pb_gen.StatusCode_Success {
			resp.BaseResp = util.BuildBaseResp(pb_gen.StatusCode_CommonErr)
		}
		return
	}
	return
}

func (h *UserRegisterHandler) checkParams() error {
	if h.Req.GetUserName() == "" || h.Req.GetHashId() == "" {
		return errors.New("参数非法")
	}
	return nil
}
