package objstore

import (
	"net/http"
	"net/url"

	"github.com/JackTJC/gmFS_backend/config"
	"github.com/tencentyun/cos-go-sdk-v5"
)

// refer: https://cloud.tencent.com/document/product/436/31215
var client *cos.Client

func InitCOS() {
	conf := config.GetInstance().COS
	u, _ := url.Parse(conf.BucketURL)
	su, _ := url.Parse(conf.ServiceURL)
	b := &cos.BaseURL{BucketURL: u, ServiceURL: su}
	// sid,sk, https://console.cloud.tencent.com/cam/capi
	client = cos.NewClient(b, &http.Client{
		Transport: &cos.AuthorizationTransport{
			SecretID:  conf.SecretID,
			SecretKey: conf.SecretKey,
		},
	})
}
