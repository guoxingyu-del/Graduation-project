package http_server

import (
	"fmt"
	"net/http"

	"github.com/JackTJC/gmFS_backend/method"
	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/gin-gonic/gin"
)

// 设置路由
func setRoute(r *gin.Engine) {
	r.POST("/ping", ping) // 路径与对应的处理函数
	r.POST("/user/login", userLogin)
	r.POST("/user/register", userRegister)
	r.POST("/dir/create", createDir)
	r.POST("/file/upload", uploadFile)
	r.POST("/file/search", searchFile)
	r.POST("/dir/get", getDir)
	r.POST("/node/get", getNode)
	r.POST("/node/getId", getNodeId)
	r.POST("/file/sendSearchToken", sendSearchToken)
	r.POST("/user/changePwd", changePassword)
	r.POST("/file/seadSearchToken", sendSearchToken)
	r.POST("/file/uploadShareToken", uploadShareToken)
	r.POST("/file/getShareTokens", getShareTokens)
	r.POST("/file/shareFirst", shareFirst)
	r.POST("/file/shareSecond", shareSecond)
	r.POST("/node/delete", deleteFile)
	r.POST("/node/shareToken", deleteShareToken)
}

// template
func ping(c *gin.Context) {
	req := &pb_gen.PingRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewPingHandler(c.Request.Context(), req).Run() // 业务逻辑
	if err := writeBody(c, resp); err != nil {                    // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func userLogin(c *gin.Context) {
	// := 表示声明变量并赋值
	req := &pb_gen.UserLoginRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	fmt.Println(req)
	resp := method.NewUserLoginHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}
func userRegister(c *gin.Context) {
	req := &pb_gen.UserRegisterRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewUserRegisterHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}
func createDir(c *gin.Context) {
	req := &pb_gen.CreateDirRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewCreateDirHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func uploadFile(c *gin.Context) {
	req := &pb_gen.UploadFileRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewUploadFileHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func searchFile(c *gin.Context) {
	req := &pb_gen.SearchFileRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewSearchFileHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func getNode(c *gin.Context) {
	req := &pb_gen.GetNodeRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewGetNodeHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func getDir(c *gin.Context) {
	req := &pb_gen.GetDirRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewGetDirHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

/*
func registerFile(c *gin.Context) {
	req := &pb_gen.RegisterFileRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewRegisterFileHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}*/

/*func shareFile(c *gin.Context) {
	req := &pb_gen.ShareFileRequest{}
	if err := readBody(c, req); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewShareFileHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil {
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}*/

/*func getRecvFile(c *gin.Context) {
	req := &pb_gen.GetRecvFileRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewGetRecvFileHandler(c.Request.Context(), req).Run() // 业务逻辑
	if err := writeBody(c, resp); err != nil {                           // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}*/

func getNodeId(c *gin.Context) {
	req := &pb_gen.GetNodeIdRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewGetNodeIdHandler(c.Request.Context(), req).Run() // 业务逻辑
	if err := writeBody(c, resp); err != nil {                         // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func sendSearchToken(c *gin.Context) {
	req := &pb_gen.SendSearchTokenRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewSendSearchTokenHandler(c.Request.Context(), req).Run() // 业务逻辑
	if err := writeBody(c, resp); err != nil {                               // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func changePassword(c *gin.Context) {
	req := &pb_gen.ChangePasswordRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewChangePasswordHandler(c.Request.Context(), req).Run() // 业务逻辑
	if err := writeBody(c, resp); err != nil {                              // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

/*
上传shareToken
*/
func uploadShareToken(c *gin.Context) {
	req := &pb_gen.UpLoadShareTokenRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewUploadShareTokenHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func getShareTokens(c *gin.Context) {
	req := &pb_gen.GetShareTokensRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewGetShareTokensHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}
func shareFirst(c *gin.Context) {
	req := &pb_gen.ShareFirstRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewShareFirstHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func shareSecond(c *gin.Context) {
	req := &pb_gen.ShareSecondRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewShareSecondHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func deleteFile(c *gin.Context) {
	req := &pb_gen.DeleteFileRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewDeleteFileHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}

func deleteShareToken(c *gin.Context) {
	req := &pb_gen.DeleteShareTokenRequest{}
	if err := readBody(c, req); err != nil { // 从http request body解析初请求
		c.Status(http.StatusInternalServerError)
		return
	}
	resp := method.NewDeleteShareTokensHandler(c.Request.Context(), req).Run()
	if err := writeBody(c, resp); err != nil { // 响应写回http response
		c.Status(http.StatusInternalServerError)
		return
	}
	c.Status(http.StatusOK)
}
