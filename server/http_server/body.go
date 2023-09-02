package http_server

import (
	"errors"
	"io/ioutil"
	"net/http"

	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/gin-gonic/gin"
	"github.com/golang/protobuf/jsonpb"
	"github.com/golang/protobuf/proto"
)

type reqFormat int8

const (
	jsonReqFormat   reqFormat = 1
	pbReqFormat     reqFormat = 2
	formatKey                 = "req_format"
	jsonContentType           = "application/json"
	pbContentType             = "application/octet-stream"
)

var (
	marshaler = jsonpb.Marshaler{EmitDefaults: false}
	unmarshal = jsonpb.Unmarshaler{AllowUnknownFields: true}
)

// 反序列化
func readBody(c *gin.Context, msg proto.Message) error {
	format, ok := c.Keys[formatKey].(reqFormat)
	if !ok {
		logs.Sugar.Errorf("type assert error")
		return errors.New("type assert error")
	}
	switch format {
	case jsonReqFormat:
		if err := unmarshal.Unmarshal(c.Request.Body, msg); err != nil {
			logs.Sugar.Errorf("json unmarshal error:%v", err)
			return err
		}
		return nil
	case pbReqFormat:
		bodyData, err := ioutil.ReadAll(c.Request.Body)
		if err != nil {
			logs.Sugar.Errorf("readall error:%v", err)
			return err
		}
		if err := proto.Unmarshal(bodyData, msg); err != nil {
			logs.Sugar.Errorf("protobuf unmarshal error:%v", err)
		}
		return nil
	default:
		return errors.New("unknown req format")
	}
}

// 序列化
func writeBody(c *gin.Context, msg proto.Message) error {
	format, ok := c.Keys[formatKey].(reqFormat)
	if !ok {
		return errors.New("type assert error")
	}
	switch format {
	case jsonReqFormat:
		if err := marshaler.Marshal(c.Writer, msg); err != nil {
			logs.Sugar.Errorf("json marshal error:%v", err)
			return err
		}
		return nil
	case pbReqFormat:
		bodyData, err := proto.Marshal(msg)
		if err != nil {
			logs.Sugar.Errorf("protobuf marshal error:%v", err)
			return err
		}
		c.Data(http.StatusOK, pbContentType, bodyData)
		return nil
	default:
		return errors.New("unknown req format")
	}
}
